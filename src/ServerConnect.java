import java.io.*;
import java.net.Socket;

public class ServerConnect extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    ServerConnect(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        String word;
        while (true) {
            try {
                word = in.readLine();

                if (word.equals("!disconnect")) {
                    this.downServer();
                    break;
                }
                for (ServerConnect vr : Server.serverList) {
                    vr.send(word);
                }

            } catch (IOException e) {
                e.printStackTrace();
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
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerConnect vr : Server.serverList) {
                    if(vr.equals(this)) vr.interrupt();
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }


}
