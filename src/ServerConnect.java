import java.io.*;
import java.net.Socket;

public class ServerConnect extends Thread {

    private Socket socket;
    private ObjectInputStream in;
    private BufferedWriter out;

    ServerConnect(Socket socket) throws IOException {
        this.socket = socket;
        in = new ObjectInputStream(socket.getInputStream());
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        String word;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                word = in.readLine();

                if (word.equals("!disconnect")) {
                    System.out.println(this.socket.getInetAddress() + " disconnected from the server");
                    this.downServer();
                    break;
                }
                for (ServerConnect vr : Server.serverList) {
                    vr.send(word);
                }

            } catch (IOException e) {
                System.out.println("Loss of connection to one of the clients");
                e.printStackTrace();
                break;
            }
        }
    }

    public void send(String word) {
        try {
            out.write(word + '\n');
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private void downServer() {
        try {
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


}
