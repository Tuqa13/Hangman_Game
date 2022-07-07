import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

// Runnable is implemented on a class whose instances will be executed by a thread.
public class ClientHandler implements Runnable {

    // Array list of all the threads handling clients so each message can be sent to the client the thread is handling.
    private Game game = new Game();
    private ArrayList<Game> games = new ArrayList<>();

    int num = 0;
    // Socket for a connection, buffer reader and writer for receiving and sending data respectively.
    private BufferedReader buf1;
    private PrintWriter wri1;
    private BufferedReader buf2;
    private PrintWriter wri2;
    HashMap<String, String> items;

    public static Game getGame() {
        return game;
    }

    // Creating the client handler from the socket the server passes.
    public ClientHandler(BufferedReader buf1, PrintWriter wri1) {
        this.buf1 = buf1;
        this.wri1 = wri1;
        try {
            wri1.println("Enter your username for the Hangman game:");
            String username1 = buf1.readLine();
            wri1.println("1");
            Player p1 = new Player(username1, "1", buf1, wri1);
            game.player1 = p1;
            addPlayer(p1);
            num++;
        } catch (IOException e) {
            closeEverything(buf1, buf2, wri1, wri2);
            e.printStackTrace();
        }
    }
    public void addClient(BufferedReader buf2, PrintWriter wri2){
        try {
            // When a client connects their username is sent.
            this.buf2 = buf2;
            this.wri2 = wri2;
            wri2.println("Enter your username for the Hangman game:");
            String username2 = buf2.readLine();
            wri2.println("2");
            Player p2 = new Player(username2, "2", buf2, wri2);
            game.player2 = p2;
            addPlayer(p2);
            num++;
        } catch (IOException e) {
            closeEverything(buf1, buf2, wri1, wri2);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if(game.isReady()){
            game.player1.wri.println("2");
            games.add(game);
            items = Items.getInstance();
            game = new Game();
            games.get(games.size()-1).chooseItem(items);
            games.get(games.size()-1).startGame();
        }
    }

    public void addPlayer(Player player){
        broadcastMessage("GAME: " + player.getName() + " has entered the game!");
        // Add the new client handler to the array, so they can receive messages from others.
        if(game.isReady()){
            broadcastMessage("Game Has Started..");

        }
        else
            broadcastMessage("Waiting for another player to connect..");
    }

    // Send a message through each client handler thread so that everyone gets the message.
    public void broadcastMessage(String messageToSend) {
        wri1.println(messageToSend);
        if(wri2 != null)
        wri2.println(messageToSend);
    }

    // Helper method to close everything, so you don't have to repeat yourself.
    public void closeEverything(BufferedReader buf1, BufferedReader buf2, PrintWriter wri1, PrintWriter wri2) {
        try {
            if (buf1 != null) {
                buf1.close();
            }
            if (wri1 != null) {
                wri1.close();
            }
            if (buf2 != null) {
                buf2.close();
            }
            if (wri2 != null) {
                wri2.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
