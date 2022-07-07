import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Items {
    private static HashMap<String, String> items;

    private Items() {
        items = new HashMap<>();
        deserializeFile();
    }

    public static HashMap<String, String> getInstance(){
        if(items == null){
            new Items();
        }
        return items;
    }

    private void deserializeFile(){ // Deserialize hangman.out file
        try{
            // Open file Input stream
            FileInputStream fileIn = new FileInputStream("hangman.out");
            ObjectInputStream oin = new ObjectInputStream(fileIn);
            // Read the file
            ArrayList<String> arr = (ArrayList) oin.readObject();
            // remove the un-needed sign
            for (String i : arr) {
                String[] item = i.split("-");
                items.put(item[1], item[0]);
            }
            // Close the files
            oin.close();
            fileIn.close();
        }
        catch (FileNotFoundException fnf) {
            System.out.println("ERROR : File not found");
            fnf.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ioe) {
            System.out.println("ERROR");
            ioe.printStackTrace();
        }
    }

}
