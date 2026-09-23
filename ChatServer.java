import module java.base;

public class ChatServer {
    private static int PORT;
    private static final Set<ClientHandler> activeClients = Collections.newSetFromMap(new ConcurrentHashMap<>()); //Keep track of Clients

    public static void main(String[] args) {
        PORT = Integer.parseInt(IO.readln("Enter port: "));

        IO.println("Attempting server host on port " + PORT +".");

        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            IO.println("Server started on port " + PORT + ". Waiting for clients to connect...");
            while (true) { 
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread.startVirtualThread(clientHandler);
                IO.println("New client connected: " + clientSocket.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            IO.println("Server error: " + e.getMessage());
        }
    }

    public static void broadcast(Message message) throws IOException {
        for (ClientHandler client : activeClients) {
            client.oos.writeObject(message);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final ObjectOutputStream oos;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            oos = new ObjectOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            activeClients.add(this);
            try (oos; var ois = new ObjectInputStream(socket.getInputStream())) {
                while (true) {
                    Message msg = (Message) ois.readObject();
                    if ("/quit".equals(msg.payload())) {
                        IO.println("Client disconnected: " + socket.getRemoteSocketAddress());
                        break;
                    }

                    IO.println("Received message from " + socket.getRemoteSocketAddress() + ": " + msg.payload());

                    Message message = new Message(socket.getRemoteSocketAddress().toString(), msg.payload(), msg.instant());
                    IO.println(message);
                    broadcast(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                IO.println("Error handling client: " + e.getMessage());
            } finally {
                activeClients.remove(this);
                IO.println("Cleaned up connection for: " + socket.getRemoteSocketAddress());
            }
        }
    }

}
