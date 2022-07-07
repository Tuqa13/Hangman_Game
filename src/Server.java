import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        while (!serverSocket.isClosed()) {
            // Listen for connections (clients to connect) on port 1234.
            try {
                // Will be closed in the Client Handler.
                Socket s1 = serverSocket.accept();
                s1.setSoTimeout(120 * 1000);
                BufferedReader buf1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
                PrintWriter wri1= new PrintWriter(new OutputStreamWriter(s1.getOutputStream()), true);
                ClientHandler clientHandler = new ClientHandler(buf1, wri1);

                Socket s2 = serverSocket.accept();
                s2.setSoTimeout(120 * 1000);

                BufferedReader buf2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
                PrintWriter wri2= new PrintWriter(new OutputStreamWriter(s2.getOutputStream()), true);
                clientHandler.addClient(buf2, wri2);

                Thread thread = new Thread(clientHandler);
                // The start method begins the execution of a thread.
                thread.start();
            } catch (IOException e) {
                closeServerSocket();
            }
        }
    }

    // Close the server socket gracefully.
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Run the program.
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        System.out.println("Server has connected...");
        server.startServer();
    }

}
