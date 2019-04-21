package cloudServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {

    public MyServer() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(8091);
            System.out.println("Server ON!");

            while (true) {
                socket = serverSocket.accept();
                System.out.println("Client ONLINE!");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
