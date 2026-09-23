import java.util.Scanner;

public class ChatClientLauncher {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter server address: ");
        String serverAddress = scanner.nextLine(); // Get server address from user input
        System.out.print("Enter server port: ");
        int serverPort = scanner.nextInt(); // Get server port from user input
        ChatClient client = new ChatClient(serverAddress, serverPort, "User" + (int)(Math.random() * 1000)); // Random username for demonstration
        client.start();
    }
}
