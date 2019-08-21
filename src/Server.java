import com.company.LoginSet;
import com.company.ServerMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Server {

    private static final int PORT = 255;
    static Database database = new Database();
    static Set<ServerConnect> serverList = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        database.connect();
        try {
            while (true) {
                Socket socket = server.accept();
                System.out.println(socket.getInetAddress() + " connected");
                try {
                    ServerConnect serverConnect = new ServerConnect(socket);
                    serverConnect.start();
                    serverList.add(serverConnect);
                    serverConnect.registerCallback(Server::onReceive);
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

    private static void onReceive(ServerMessage message, ServerConnect server){
        switch (message.getCommand()) {
            case ServerCommands.SEND_MESSAGE:
                for (ServerConnect vr : Server.serverList) {
                    vr.send(message);
                }
                break;

            case ServerCommands.REGISTER:
                try {
                    LoginSet loginSet = (LoginSet) message.getData();
                    ResultSet resultSet = Server.database.newQuery(String.format(
                            "SELECT * FROM REGISTERUSERS WHERE login = '%s'",
                            loginSet.getLogin()
                    ));
                    if (!resultSet.next()){
                        server.send(new ServerMessage(ServerCommands.REGISTER_SUCCESS, null));
                        Server.database.newQuery(String.format(
                                "INSERT INTO registerusers (LOGIN, USER_PASSWORD) VALUES ('%s',%s)",
                                loginSet.getLogin(),
                                loginSet.getPassword()
                        ));
                    } else {
                        server.send(new ServerMessage(ServerCommands.REGISTER_FAILED, null));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case ServerCommands.LOGIN:
                try {
                    LoginSet loginSet = (LoginSet) message.getData();
                    ResultSet resultSet = Server.database.newQuery(String.format(
                            "SELECT * FROM REGISTERUSERS WHERE login = '%s'",
                            loginSet.getLogin()
                    ));

                    if (resultSet.next()) {
                        if (resultSet.getString("user_password").equals(loginSet.getPassword())){
                            server.send(new ServerMessage(ServerCommands.LOGIN_SUCCESS, loginSet));
                        }else {
                            server.send(new ServerMessage(ServerCommands.INCORRECT_PASSWORD, null));
                        }
                    }else {
                        server.send(new ServerMessage(ServerCommands.INCORRECT_LOGIN, null));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        }

    }
}

