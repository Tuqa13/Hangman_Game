import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// A client sends messages to the server, the server spawns a thread to communicate with the client.
public class Client {

    // A client has a socket to connect to the server and a reader and writer to receive and send messages respectively.
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter pWriter;

    private BufferedReader scan;
    private String num;
    private boolean gameIsDone = false;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.pWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.scan = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(bufferedReader.readLine()); // Enter your username for the Hangman game:
            String clientName = scan.readLine();
            pWriter.println(clientName); // Sending name
            num = bufferedReader.readLine(); // Reading player's num
            addPLayer();
            while(!gameIsDone)
                if(num.equals("1")){
                    String n = bufferedReader.readLine();
                    if(n.equals("2")){
                        startGame();
                    }
                }
                else if(num.equals("2")){
                    startGame();
            }

        } catch (IOException e) {
            // Gracefully close everything.
            closeEverything(socket, bufferedReader, pWriter);
        }
    }

    public void addPLayer(){
        receiveMessage(); // Player # has entered the game
        receiveMessage();
    }

    public void startGame() throws IOException {
        receiveMessage(); // Genre:
        receiveMessage(); // Word - Empty blanks
            try {
                String rNum = bufferedReader.readLine(); // RandomNumber 1|2.
                if(rNum.equals(num)) {
                    while(!gameIsDone){
                        turn();
                        notTurn();}
            }
                else{
                while(!gameIsDone){
                    notTurn();
                    turn();
                }}
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void turn() throws IOException{
        receiveMessage(); // It's your turn
        receiveMessage(); // Do you want to choose a letter or make a guess?
        String response = scan.readLine();
        pWriter.println(response);
        if(response.equals("1")){ // Choose letter
            receiveMessage(); // Choose letter please
            String letter = scan.readLine();
            pWriter.println(letter); // Sending the letter

            boolean c1 = Boolean.parseBoolean(bufferedReader.readLine()); //This letter has been chosen, it's wrong
            boolean c2 = Boolean.parseBoolean(bufferedReader.readLine()); // Correct answer
            if(c1){
                receiveMessage(); //This letter has been chosen, it's wrong
//                receiveMessage();
                turn();
            }
            else if(c2){
                receiveMessage(); // Correct answer
                updateWord();
            }
            else{
                receiveMessage(); //In Correct answer

                boolean c3 = Boolean.parseBoolean(bufferedReader.readLine()); // pTurn.getRemainingLives()==0
                if(c3){
                    receiveMessage(); //The word is:
                    receiveMessage(); // You lose
                }
                updateWord();
            }
            receiveMessage(); // Do you want to make a guess? Y|N
            String r = scan.readLine();
            pWriter.println(r);
            if(r.toLowerCase().equals("y") || r.toLowerCase().equals("yes")){
                makeGuess();
            }

        }
        else if(response.equals("2")){
            makeGuess();
        }
        else{
            receiveMessage();
            turn();
        }
    }
    public void notTurn() throws IOException {
        receiveMessage(); // It's player 2 turn
        String r1 = bufferedReader.readLine(); // Getting the response.
        if(r1.equals("1")){
            boolean c1 = Boolean.parseBoolean(bufferedReader.readLine()); // This letter has been chosen, it's wrong
            boolean c2 = Boolean.parseBoolean(bufferedReader.readLine()); // correct answer
            if(c1){
                receiveMessage();
                notTurn();
            }
            else if(c2){
                // update word..
                receiveMessage();
                boolean c3 = Boolean.parseBoolean(bufferedReader.readLine());
                if(c3){
                    receiveMessage();
                    receiveMessage();
                    return;
                }
                receiveMessage();
                receiveMessage();
                receiveMessage();
            }
            else{
                receiveMessage();
                boolean c4 = Boolean.parseBoolean(bufferedReader.readLine());
                if(c4){
                    receiveMessage(); // The word is:
                    receiveMessage(); // Player # Lose, you WIN
                }
                // update word..
                boolean c5 = Boolean.parseBoolean(bufferedReader.readLine());
                if(c5){
                    receiveMessage();
                    receiveMessage();
                    return;
                }
                receiveMessage();
                receiveMessage();
                receiveMessage();
            }
            boolean c6 = Boolean.parseBoolean(bufferedReader.readLine());
            if(c6) {
                // make guess
                receiveMessage();
                receiveMessage();
            }
        }
        else if(r1.equals("2")){
            boolean c7 = Boolean.parseBoolean(bufferedReader.readLine());
            if(c7) {
                // make guess
                receiveMessage();
                receiveMessage();
            }
        }
    }

    public void makeGuess() throws IOException {
        receiveMessage(); //Make your guess
        String guess = scan.readLine();
        pWriter.println(guess); // Sending the guess
        boolean c = Boolean.parseBoolean(bufferedReader.readLine()); // Guessed word is equal to the word in game
        if(c){
            receiveMessage(); // Correct, the word is:
            receiveMessage(); // YOU WIN 🎆🎆
            gameIsDone = true;
        }
    }

    public void updateWord() throws IOException {
        boolean c1 = Boolean.parseBoolean(bufferedReader.readLine()); // all letters are guessed
        if(c1){
            receiveMessage(); // Correct the word is:
            receiveMessage(); // You win
            gameIsDone = true;
            return;
        }
        receiveMessage(); // printing the genre and the word with blanks.
        receiveMessage();
        receiveMessage(); // Incorrect letters
    }

    public void receiveMessage(){
        String msg;
        // While there is still a connection with the server, continue to listen for messages on a separate thread.
            try {
                // Get the messages sent from other users and print it to the console.
                msg = bufferedReader.readLine();
                System.out.println(msg);
            } catch (IOException e) {
                // Close everything gracefully.
                closeEverything(socket, bufferedReader, pWriter);
    }}

    public void closeEverything(Socket socket, BufferedReader bufferedReader, PrintWriter pWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (pWriter != null) {
                pWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Run the program.
    public static void main(String[] args) throws IOException {
        // Create a socket to connect to the server.
        Socket socket = new Socket("localhost", 1234);

        // Pass the socket
        new Client(socket);
    }
}
