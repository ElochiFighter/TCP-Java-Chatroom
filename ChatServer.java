import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {
    private static final Scanner scanner = new Scanner(System.in);
    private static final int port = scanner.nextInt();
    private static final Set<PrintWriter> clientWriters = new HashSet<>(); //Keep track of Clients

    public static void main(String[] args) {
        System.out.println("Attempting server host on port " + port +".");

        try (ServerSocket serverSocket = new ServerSocket(port)){
            while (true) { 
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
                System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public static void broadcast(Message message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            synchronized (clientWriters) {
                clientWriters.add(writer);
            }

            String messageText;
            while ((messageText = reader.readLine()) != null) {
                if (messageText.equalsIgnoreCase("/quit")) {
                    System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
                    break;
                }

                System.out.println("Received message from " + socket.getRemoteSocketAddress() + ": " + messageText);

                Message message = new Message(socket.getRemoteSocketAddress().toString(), messageText);
                broadcast(message);
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            synchronized (clientWriters) {
                clientWriters.remove(writer);
            }
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
            System.out.println("Cleaned up connection for: " + socket.getRemoteSocketAddress());
        }
    }
}

}
