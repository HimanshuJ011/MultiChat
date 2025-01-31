import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // keep track of all clients and send msg to all client
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // wrapping byte stream to character stream bcoz we want to send character , much better
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine(); // user give name
            clientHandlers.add(this); // user added to list
            broadcastMessage("Server : " + clientUserName + " has entered the chat !");

        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    @Override
    public void run() {  // everything in this run separate thread, listen messages
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine(); // will read
                broadcastMessage(messageFromClient);

            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                break; // loop brk
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        // each clientHandler
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUserName.equals(clientUserName)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine(); //
                    clientHandler.bufferedWriter.flush(); // mannual flush to buffer after sending data

                }
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }

    public void removeClientHandler() {

        clientHandlers.remove(this);
        broadcastMessage("Server : " + clientUserName + " has left the chat !");

    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClientHandler(); //
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.getMessage();
        }

    }

}
