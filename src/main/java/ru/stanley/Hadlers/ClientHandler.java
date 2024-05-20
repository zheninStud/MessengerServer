package ru.stanley.Hadlers;

import org.json.JSONObject;
import ru.stanley.Database.DatabaseConnection;
import ru.stanley.Models.Message;
import ru.stanley.Models.User;
import ru.stanley.Server;
import ru.stanley.Utils.MessageType;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;

public class ClientHandler extends Thread {

    private SSLSocket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static final DatabaseConnection database = Server.getDatabaseConnection();

    public ClientHandler(SSLSocket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            MessageType outMessageType;
            JSONObject jsonMessage;
            User user;

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Сообщение от клиента: " + inputLine);

                Message message = Message.fromJSON(inputLine);
                String messageType = message.getType();

                switch (messageType) {
                    case "AUTH":
                        String username = message.getData().getString("username");
                        String password = message.getData().getString("passwordHash");

                        user = database.isValidUser(username, password);
                        if (user != null) {
                            outMessageType = MessageType.AUTH_SUCCESS;
                            jsonMessage = outMessageType.createJsonObject();

                            jsonMessage.getJSONObject("data").put("userId", user.getUserId());
                            jsonMessage.getJSONObject("data").put("username", user.getUserName());
                            jsonMessage.getJSONObject("data").put("email", user.getEmail());
                            jsonMessage.getJSONObject("data").put("phone", user.getPhone());

                            out.println(outMessageType.createMessage(jsonMessage).toJSON());
                        } else {
                            out.println(new Message(MessageType.AUTH_FAIL.createJsonObject()).toJSON());
                        }
                        break;
                    case "REGISTER":
                        String newUsername = message.getData().getString("username");
                        String newPasswordHash = message.getData().getString("passwordHash");
                        String saltRegister = message.getData().getString("salt");
                        String newEmail = message.getData().getString("email");
                        String newPhone = message.getData().getString("phone");

                        outMessageType = MessageType.REGISTER_FAIL;
                        jsonMessage = outMessageType.createJsonObject();

                        if (database.isUserExists(newUsername)) {
                            jsonMessage.getJSONObject("data").put("errorCode", "Username");
                            out.println(outMessageType.createMessage(jsonMessage).toJSON());
                            return;
                        }

                        if (database.isEmailExists(newEmail)) {
                            jsonMessage.getJSONObject("data").put("errorCode", "Email");
                            out.println(outMessageType.createMessage(jsonMessage).toJSON());
                            return;
                        }

                        if (database.isPhoneExists(newPhone)) {
                            jsonMessage.getJSONObject("data").put("errorCode", "Phone");
                            out.println(outMessageType.createMessage(jsonMessage).toJSON());
                            return;
                        }

                        if (!database.registerUser(newUsername, newPasswordHash, saltRegister, newEmail, newPhone)) {
                            jsonMessage.getJSONObject("data").put("errorCode", "MySQL");
                            out.println(outMessageType.createMessage(jsonMessage).toJSON());
                            return;
                        }

                        out.println(new Message(MessageType.REGISTER_SUCCESS.createJsonObject()).toJSON());
                        break;
                    case "GET_SALT":
                        String usernameSalt = message.getData().getString("username");
                        String salt = database.getUserSalt(usernameSalt);
                        if (salt != null) {
                            outMessageType = MessageType.SET_SALT;
                            jsonMessage = outMessageType.createJsonObject();

                            jsonMessage.getJSONObject("data").put("salt", salt);

                            out.println(outMessageType.createMessage(jsonMessage).toJSON());
                        }
                        break;
                    case "GET_USER":
                        String usernameSearch = message.getData().getString("username");
                        user = database.searchUser(usernameSearch);
                        if (user != null) {
                            outMessageType = MessageType.USER_SUCCESS;
                            jsonMessage = outMessageType.createJsonObject();

                            jsonMessage.getJSONObject("data").put("userId", user.getUserId());
                            jsonMessage.getJSONObject("data").put("username", user.getUserName());
                            jsonMessage.getJSONObject("data").put("email", user.getEmail());
                            jsonMessage.getJSONObject("data").put("phone", user.getPhone());

                            out.println(outMessageType.createMessage(jsonMessage).toJSON());
                        } else {
                            out.println(new Message(MessageType.USER_FAIL.createJsonObject()).toJSON());
                        }
                }
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
