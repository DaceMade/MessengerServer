import com.company.LoginSet;
import com.company.ServerMessage;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ServerConnect extends Thread {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Set<Callback> callbacks = new HashSet<>();

    public ServerConnect(Socket socket) throws IOException {
        this.socket = socket;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ServerMessage word = (ServerMessage) in.readObject();
                for (Callback callback : callbacks) {
                    callback.onReceive(word);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Loss of connection to one of the clients");
                e.printStackTrace();
                downServer();
                break;
            }
        }
    }


    public void registerCallback(Callback callback) {
        callbacks.add(callback);
    }

    public void unregisterCallback(Callback callback) {
        callbacks.remove(callback);
    }

    public void send(ServerMessage word) {
        try {
            out.writeObject(word);
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private void downServer() {
        try {
            callbacks.clear();
            if (!socket.isClosed()) {
                in.close();
                out.close();
                socket.close();
                for (ServerConnect vr : Server.serverList) {
                    if (equals(vr)) vr.interrupt();
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {
        }
    }


    public interface Callback {
        void onReceive(ServerMessage message);
    }
}
