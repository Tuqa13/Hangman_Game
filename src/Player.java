import java.io.BufferedReader;
import java.io.PrintWriter;

public class Player {
    String name;
    int remainingLives = 5;
    String num;
    BufferedReader buf;
    PrintWriter wri;

    public Player(String name, String num, BufferedReader buf, PrintWriter wri) {
        this.name = name;
        this.num = num;
        this.wri = wri;
        this.buf = buf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getRemainingLives() {
        return remainingLives;
    }

    public void setRemainingLives(int remainingLives) {
        this.remainingLives = remainingLives;
    }

    public void decreaseLives(){
        remainingLives--;
    }
}
