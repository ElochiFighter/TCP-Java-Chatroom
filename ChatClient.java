import module java.base;

public class ChatClient {
    private final String serverAddress;
    private final int serverPort;
    private final String username;
    private ObjectInputStream ois;

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
        try (var socket = new Socket(serverAddress, serverPort)) {
            ois = new ObjectInputStream(socket.getInputStream());
            var oos = new ObjectOutputStream(socket.getOutputStream());

            // Start a thread to listen for messages from the server
            Thread.startVirtualThread(new ServerListener());

            // Read messages from the console and send them to the server
            String messageText;
            while ((messageText = IO.readln()) != null) {
                if ("/quit".equals(messageText)) {
                    IO.println("Disconnecting from the chat...");
                    break;
                }
                Message message = new Message(username, messageText);
                oos.writeObject(message);
            }
        } catch (IOException e) {
            IO.println("Client error: " + e.getMessage());
        }
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    IO.println(ois.readObject());
                }
            } catch (IOException | ClassNotFoundException e) {
                IO.println("Error reading from server: " + e.getMessage());
            }
        }
    }
}