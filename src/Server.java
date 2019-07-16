import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {

    private static final int PORT = 255;
    static Set<ServerConnect> serverList = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        try {
            while (true) {
                Socket socket = server.accept();
                System.out.println(socket.getInetAddress() + " connected");
                try {
                    ServerConnect serverConnect = new ServerConnect(socket);
                    serverConnect.start();
                    serverList.add(serverConnect);
                } catch (IOException e) {
                    socket.close();
                }
                if (!socket.isConnected()) {
                    break;
                }
            }
        } finally {
            server.close();
        }
    }

}

