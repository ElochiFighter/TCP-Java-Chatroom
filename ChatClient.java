import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private final String serverAddress;
    private final int serverPort;
    private final String username;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ChatClient(String username) {
        this.serverAddress = "localhost";
        this.serverPort = 12345;
        this.username = username;
    }

    public ChatClient(String serverAddress, int serverPort, String username) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
    }

    public void start() {
        try {
            socket = new Socket(serverAddress, serverPort);
            reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Start a thread to listen for messages from the server
            new Thread(new ServerListener()).start();

            // Read messages from the console and send them to the server
            BufferedReader consoleReader = new BufferedReader(new java.io.InputStreamReader(System.in));
            String messageText;
            while ((messageText = consoleReader.readLine()) != null) {
                if (messageText.equalsIgnoreCase("/quit")) {
                    System.out.println("Disconnecting from the chat...");
                    break;
                }
                Message message = new Message(messageText, username);
                writer.println(message.toString());
            }
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            String messageText;
            try {
                while ((messageText = reader.readLine()) != null) {
                    System.out.println(messageText);
                }
            } catch (IOException e) {
                System.out.println("Error reading from server: " + e.getMessage());
            }
        }
    }
}