
public class ChatClientLauncher {
    public static void main(String[] args) {
        String serverAddress = IO.readln("Enter server address: "); // Get server address from user input
        int serverPort = Integer.parseInt(IO.readln("Enter server port: ")); // Get server port from user input
        ChatClient client = new ChatClient(serverAddress, serverPort, "User" + (int)(Math.random() * 1000)); // Random username for demonstration
        client.start();
    }
}
