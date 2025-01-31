import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// instance of the class which implements runnable runs separate thread
public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); // blocking method : program halt here until client connect
                System.out.println("A new Client has connected !");

                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler); // run new thread for each client
                thread.start();
            }
        } catch (IOException e) {
            closedServerSocket();
        }
    }

    public void closedServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("waiting for clients to join...");
        ServerSocket serverSocket1 = new ServerSocket(5959);
        Server server = new Server(serverSocket1);
        server.startServer();
    }
}
