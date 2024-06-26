package ru.stanley.Hadlers;

import org.json.JSONObject;
import ru.stanley.Database.DatabaseConnection;
import ru.stanley.Manager.ClientManager;
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

    private final SSLSocket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private User user;
    private static final DatabaseConnection database = Server.getDatabaseConnection();

    public ClientHandler(SSLSocket socket) {
        this.clientSocket = socket;
    }

    public void sendMessage(Message message) {
        out.println(message.toJSON());
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            MessageType outMessageType;
            JSONObject jsonMessage;
            User user;
            ClientHandler client;

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
                            this.user = user;
                            ClientManager.addClient(user.getUserId(), this);
                            outMessageType = MessageType.AUTH_SUCCESS;
                            jsonMessage = outMessageType.createJsonObject();

                            jsonMessage.getJSONObject("data").put("userId", user.getUserId());
                            jsonMessage.getJSONObject("data").put("userName", user.getUserName());
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
                        } else {
                            out.println(new Message(MessageType.GET_SALT_FALSE.createJsonObject()).toJSON());
                        }
                        break;
                    case "GET_USER":
                        String usernameSearch = message.getData().getString("username");
                        user = database.searchUser(usernameSearch);
                        if (user != null) {
                            outMessageType = MessageType.USER_SUCCESS;
                            jsonMessage = outMessageType.createJsonObject();

                            jsonMessage.getJSONObject("data").put("userId", user.getUserId());
                            jsonMessage.getJSONObject("data").put("userName", user.getUserName());
                            jsonMessage.getJSONObject("data").put("email", user.getEmail());
                            jsonMessage.getJSONObject("data").put("phone", user.getPhone());

                            out.println(outMessageType.createMessage(jsonMessage).toJSON());
                        } else {
                            out.println(new Message(MessageType.USER_FAIL.createJsonObject()).toJSON());
                        }
                        break;
                    case "REGUEST_FRIEND":
                        String userIdRequest = message.getData().getString("userId");
                        String publicKeyRequest = message.getData().getString("publicKey");

                        client = ClientManager.getClient(userIdRequest);

                        outMessageType = MessageType.REGUEST_FRIEND_CLIENT;
                        jsonMessage = outMessageType.createJsonObject();

                        jsonMessage.getJSONObject("data").put("userId", this.user.getUserId());
                        jsonMessage.getJSONObject("data").put("userName", this.user.getUserName());
                        jsonMessage.getJSONObject("data").put("email", this.user.getEmail());
                        jsonMessage.getJSONObject("data").put("phone", this.user.getPhone());
                        jsonMessage.getJSONObject("data").put("publicKey", publicKeyRequest);

                        if (client == null) {
                            if (database.insertUserPublicKey(this.user.getUserId(), userIdRequest, outMessageType.createMessage(jsonMessage).toJSON())) {
                                MessageType outMessageType2 = MessageType.REGUEST_FRIEND_SERVER;
                                JSONObject jsonMessage2 = outMessageType2.createJsonObject();

                                jsonMessage2.getJSONObject("data").put("userId", userIdRequest);

                                out.println(outMessageType2.createMessage(jsonMessage2).toJSON());
                            }
                        } else {
                            client.sendMessage(outMessageType.createMessage(jsonMessage));
                        }
                        break;
                    case "REGUEST_FRIEND_CLIENT_TAKEN":
                        String userIdTaken = message.getData().getString("userId");

                        client = ClientManager.getClient(userIdTaken);

                        outMessageType = MessageType.REGUEST_FRIEND_CLIENT_TAKEN_CLIENT;
                        jsonMessage = outMessageType.createJsonObject();

                        jsonMessage.getJSONObject("data").put("userId", this.user.getUserId());

                        if (client == null) {
                            if (database.insertPendingMessage(this.user.getUserId(), userIdTaken, outMessageType.createMessage(jsonMessage).toJSON())) {
                                out.println(new Message(MessageType.REGUEST_FRIEND_CLIENT_TAKEN_SERVER.createJsonObject()).toJSON());
                            }
                        } else {
                            client.sendMessage(outMessageType.createMessage(jsonMessage));
                        }
                        break;
                    case "REGUEST_FRIEND_CLIENT_SUCCESS":
                        String userIdClientSuccess = message.getData().getString("userId");
                        String publicKeyClientSuccess = message.getData().getString("publicKey");

                        client = ClientManager.getClient(userIdClientSuccess);

                        outMessageType = MessageType.REGUEST_FRIEND_CLIENT_SUCCESS_CLIENT;
                        jsonMessage = outMessageType.createJsonObject();

                        jsonMessage.getJSONObject("data").put("userId", this.user.getUserId());
                        jsonMessage.getJSONObject("data").put("publicKey", publicKeyClientSuccess);

                        if (client == null) {
                            if (database.insertPendingMessage(this.user.getUserId(), userIdClientSuccess, outMessageType.createMessage(jsonMessage).toJSON())) {

                                MessageType outMessageType2 = MessageType.REGUEST_FRIEND_CLIENT_SUCCESS_SERVER;
                                JSONObject jsonMessage2 = outMessageType2.createJsonObject();

                                jsonMessage2.getJSONObject("data").put("userId", userIdClientSuccess);

                                out.println(outMessageType2.createMessage(jsonMessage2).toJSON());
                            }
                        } else {

                            client.sendMessage(outMessageType.createMessage(jsonMessage));
                        }

                        break;
                    case "MESSAGE_SENT":
                        String userIdSenderMessage = message.getData().getString("sender");
                        String userIdReceiverMessage = message.getData().getString("recipient");
                        String text = message.getData().getString("message");

                        client = ClientManager.getClient(userIdReceiverMessage);

                        outMessageType = MessageType.MESSAGE_SENT_CLIENT;
                        jsonMessage = outMessageType.createJsonObject();

                        jsonMessage.getJSONObject("data").put("sender", userIdSenderMessage);
                        jsonMessage.getJSONObject("data").put("recipient", userIdReceiverMessage);
                        jsonMessage.getJSONObject("data").put("message", text);

                        if (client == null) {
                            if (database.insertPendingMessage(userIdSenderMessage, userIdReceiverMessage, outMessageType.createMessage(jsonMessage).toJSON())) {

                                out.println(new Message(MessageType.MESSAGE_SENT_SERVER.createJsonObject()).toJSON());
                            }
                        } else {

                            client.sendMessage(outMessageType.createMessage(jsonMessage));
                        }
                        break;
                    case "CHECK_MESSAGE":
                        String userIdCheck = message.getData().getString("userId");
                        checkPendingMessage(userIdCheck);
                        checkPublicKey(userIdCheck);
                        break;
                    case "REGUEST_FRIEND_CLIENT_DENY":
                        String userIdDeny = message.getData().getString("userId");

                        client = ClientManager.getClient(userIdDeny);

                        outMessageType = MessageType.REGUEST_FRIEND_CLIENT_DENY_CLIENT;
                        jsonMessage = outMessageType.createJsonObject();

                        jsonMessage.getJSONObject("data").put("userId", this.user.getUserId());

                        if (client == null) {
                            database.insertPendingMessage(this.user.getUserId(), userIdDeny, outMessageType.createMessage(jsonMessage).toJSON());
                        } else {
                            client.sendMessage(outMessageType.createMessage(jsonMessage));
                        }
                        break;
                    case "DISCONNECT":
                        ClientManager.removeClient(this.user.getUserId());
                        clientSocket.close();
                        break;
                }
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkPublicKey(String userId) throws SQLException {
        database.selectPublicKey(userId);
    }

    private void checkPendingMessage(String userId) throws SQLException {
        database.selectPendingMessage(userId);
    }
}
