package ru.stanley.Hadlers;

import org.json.JSONObject;
import ru.stanley.Database.DatabaseConnection;
import ru.stanley.Models.Message;
import ru.stanley.Server;
import ru.stanley.Utils.MessageType;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Сообщение от клиента: " + inputLine);
                out.println("Сервер получил ваше сообщение: " + inputLine);

                Message message = Message.fromJSON(inputLine);
                String messageType = message.getType();

                switch (messageType) {
                    case "AUTH":
//                        // Обработка авторизации
//                        String username = message.getData().getString("username");
//                        String password = message.getData().getString("password");
//                        if (database.isValidUser(username, password)) {
//                            out.println(new Message("AUTH_SUCCESS", new JSONObject()).toJSON());
//                        } else {
//                            out.println(new Message("AUTH_FAIL", new JSONObject()).toJSON());
//                        }
                        break;
                    case "REGISTER":
                        // Получаем данные из сообщения
                        String newUsername = message.getData().getString("username");
                        String newPassword = message.getData().getString("password");
                        String newEmail = message.getData().getString("email");
                        String newPhone = message.getData().getString("phone");

                        // Проверяем, существует ли пользователь с таким именем или электронной почтой
//                        if (!database.isUserExists(newUsername) && !database.isEmailExists(newEmail)) {
//                            // Регистрируем пользователя
//                            database.registerUser(newUsername, newPassword, newEmail, newPhone);
//
//                            // Отправляем сообщение об успешной регистрации
//                            out.println(MessageType.REGISTER_SUCCESS.createJsonObject());
//                        } else {
//                            // Отправляем сообщение о неудачной регистрации
//                            out.println(MessageType.REGISTER_FAIL.createJsonObject());
//                        }
                        break;
                }
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
