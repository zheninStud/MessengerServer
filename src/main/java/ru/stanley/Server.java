package ru.stanley;

import ru.stanley.Database.DatabaseConnection;
import ru.stanley.Hadlers.ClientHandler;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.SQLException;

public class Server {

    private static Server instance;
    private static int PORT;
    private static DatabaseConnection databaseConnection;

    public void start() throws SQLException {
        PORT = 12345;

        databaseConnection = new DatabaseConnection();

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            char[] password = "testserver".toCharArray();
            keyStore.load(new FileInputStream("server_keystore.jks"), password);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);
            System.out.println("Сервер запущен на порту " + PORT);

            databaseConnection.isUserExists("Stanley000");

            while (true) {
                SSLSocket clientSocket = (SSLSocket) sslServerSocket.accept();
                System.out.println("Новое подключение: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }

        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        } finally {
            if (databaseConnection != null) {
                try {
                    databaseConnection.disconnect();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        instance = new Server();
        instance.start();
    }

    public static DatabaseConnection getDatabaseConnection() { return databaseConnection; }
    public static Server getInstance() {
        return instance;
    }
}

