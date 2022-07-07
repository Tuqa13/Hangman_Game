import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Game{
    Player player1 = null;
    Player player2 = null;
    String word;
    String genre;
    boolean gameIsDone = false;
    ArrayList<Character> trueCharArr = new ArrayList<>();
    ArrayList<Character> guessedLetters = new ArrayList<>();
    ArrayList<Character> inCorrectLetters = new ArrayList<>();

    public ArrayList<Character> getTrueCharArr() {
        return trueCharArr;
    }

    public ArrayList<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public ArrayList<Character> getInCorrectLetters() {
        return inCorrectLetters;
    }

    public void chooseItem(HashMap<String, String> items){
        int randomNumber = (int) (Math.random() * items.size());
        String itemName = (String) items.keySet().toArray()[randomNumber];
        String itemType = items.get(itemName);
        this.setGenre(itemType);
        this.setWord(itemName);
        char [] carr = word.toCharArray();
        // Adding the characters in the selected word to an array.
        for (char c : carr) {
            if (!trueCharArr.contains(c)) {
                trueCharArr.add(c);
            }
        }
    }
    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    public boolean isReady(){
        return(player1 != null && player2 != null);
    }

    public void startGame(){
        // Continue to listen for messages while a connection with the client is still established.
        while (!gameIsDone) {
            try {
                // Make the chosen word invisible..
                StringBuilder gWord = new StringBuilder("");
                char[] arr = word.toCharArray();
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] != ' ') {
                        gWord.append("_");
                    } else
                        gWord.append(" ");
                }
                broadcastMessage("Genre: " + genre);
                broadcastMessage(String.valueOf(gWord));

                // Choose the player who start first:
                String randomNumber = String.valueOf((int) (Math.random() * 2) + 1);
                // random number using Math.random will give us either 0 or 1, and because we want 1 or 2 I added one.
                // If 1: PLayer1 turn comes first, if 2: player2 turn comes first.
                // I used String not integer to avoid errors.
                player1.wri.println(randomNumber);
                player2.wri.println(randomNumber);
                if(randomNumber.equals(player1.num)){
                    while (!gameIsDone) {
                        turn(player1, player2);
                        turn(player2, player1);
                    }
                }
                else{
                    while(!gameIsDone) {
                        turn(player2, player1);
                        turn(player1, player2);
                    }}

            } catch (IOException e) {
                // Close everything gracefully.
//                closeEverything(socket, getBufferedReader(), getPWriter());
                break;
            }
        }
    }
    public void turn(Player pTurn, Player nTurn) throws IOException {
        //Sending message for both players
        broadcastMessageForOne("It's your turn", pTurn);
        broadcastMessageForOne("It's " + pTurn.getName()+"'s turn.", nTurn);

        // i turn:
        broadcastMessageForOne("Do you want to choose a letter or to make a guess? 1|2", pTurn);
        String response = pTurn.buf.readLine();
        System.out.println(response);
        broadcastMessageForOne(response, nTurn);
        if(response.equals("1")){ // choose a letter
            broadcastMessageForOne("Choose a letter Please:", pTurn);
            String c = pTurn.buf.readLine();

            broadcastMessageForOne(String.valueOf(inCorrectLetters.contains(c)), pTurn);
            broadcastMessageForOne(String.valueOf(inCorrectLetters.contains(c)), nTurn);

            broadcastMessageForOne(String.valueOf(trueCharArr.contains(c.toCharArray()[0])), pTurn);
            broadcastMessageForOne(String.valueOf(trueCharArr.contains(c.toCharArray()[0])), nTurn);

            if(inCorrectLetters.contains(c)){
                broadcastMessageForOne("This letter has been chosen, it's wrong, Choose another one", pTurn);
                broadcastMessageForOne("Other player write chosen letter, will try it again", nTurn);
                turn(pTurn, nTurn);
            }
            else if(trueCharArr.contains(c.toCharArray()[0])){
                broadcastMessageForOne("Correct answer 👍", pTurn);
                broadcastMessageForOne(pTurn.getName()+ " answered correct.", nTurn);
                guessedLetters.add(c.toCharArray()[0]);
                this.updateWord(pTurn, nTurn);
            }
            else{
                broadcastMessageForOne("In Correct answer 👎", pTurn);
                inCorrectLetters.add(c.toCharArray()[0]);
                pTurn.remainingLives--;
                broadcastMessageForOne(pTurn.getName()+ " answered Incorrect.", nTurn);
                broadcastMessageForOne(String.valueOf(pTurn.getRemainingLives()==0), pTurn);
                broadcastMessageForOne(String.valueOf(pTurn.getRemainingLives()==0), nTurn);
                if(pTurn.getRemainingLives()==0){
                    broadcastMessage("The word is: "+ word);
                    broadcastMessageForOne("YOU LOSE", pTurn);
                    broadcastMessageForOne(pTurn.getName().toUpperCase() + " LOSE, you WIN ", nTurn);

                }
                this.updateWord(pTurn, nTurn);
            }
            broadcastMessageForOne("Do you want to make a guess? Y|N", pTurn);
            String r = pTurn.buf.readLine();
            boolean x = r.toLowerCase().equals("y") || r.toLowerCase().equals("yes");
            broadcastMessageForOne(String.valueOf(x), nTurn);
            if(x){
                this.makeGuess(pTurn, nTurn);
            }
        }
        else if(response.equals("2")){ // make a guess
            this.makeGuess(pTurn, nTurn);
        }
        else{
            broadcastMessageForOne("Choose 1 or 2 please", pTurn);
            turn(pTurn, nTurn);
        }

    }
    public void makeGuess(Player pTurn, Player nTurn) throws IOException {
        broadcastMessageForOne("Make your guess", pTurn);
        String c = pTurn.buf.readLine();
        broadcastMessageForOne(String.valueOf(c.equals(word)), pTurn);
        broadcastMessageForOne(String.valueOf(c.equals(word)), nTurn);
        if(c.equals(word)){
            broadcastMessage("Correct, the word is: "+ word);
            broadcastMessageForOne("YOU WIN 🎆🎆", pTurn);
            broadcastMessageForOne(pTurn.getName().toUpperCase() + " WIN.\n HARD LUCK 🤝 ", nTurn);
            gameIsDone = true;
        }
        else{
            broadcastMessage("InCorrect guess");
        }
    }
    public void updateWord(Player pTurn, Player nTurn){
        StringBuilder gWord = new StringBuilder("");
        char[] arr = word.toCharArray();
        for (int k = 0; k < arr.length; k++) {
            if(guessedLetters.contains(arr[k])){
                gWord.append(arr[k]);
            }
            else if (arr[k] != ' ') {
                gWord.append("_");
            } else
                gWord.append(" ");
        }
        broadcastMessageForOne(String.valueOf(word.equals(gWord.toString())), pTurn);
        broadcastMessageForOne(String.valueOf(word.equals(gWord.toString())), nTurn);
        if(word.equals(gWord.toString())){
            // The game must be ended
            broadcastMessage("Correct, the word is: "+ word);
            broadcastMessageForOne("YOU WIN 🎆🎆", pTurn);
            broadcastMessageForOne(pTurn.getName().toUpperCase() + " WIN.\n HARD LUCK 🤝 ", nTurn);
            gameIsDone = true;
            return;
        }
        broadcastMessage("Genre: " + genre);
        broadcastMessage(String.valueOf(gWord));
        broadcastMessage("Incorrect letters: "+inCorrectLetters.toString());
    }
    public void broadcastMessage(String messageToSend) {
        player1.wri.println(messageToSend);
        player2.wri.println(messageToSend);
    }

    public void broadcastMessageForOne(String messageToSend, Player player){
                // You don't want to broadcast the message to the user who sent it.
                player.wri.println(messageToSend);


        }
    }

