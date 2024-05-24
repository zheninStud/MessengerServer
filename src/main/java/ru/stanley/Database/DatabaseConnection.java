package ru.stanley.Database;

import ru.stanley.Hadlers.ClientHandler;
import ru.stanley.Manager.ClientManager;
import ru.stanley.Models.User;
import ru.stanley.Server;
import ru.stanley.Utils.SQLQuery;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import java.sql.*;

public class DatabaseConnection {

    private static final Server application = Server.getInstance();
    private RowSetFactory factory;
    private Connection connection;

    private String url = "jdbc:mysql://localhost:3306/mydatabase";
    private String user = "username";
    private String password = "password";

    public DatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Соединение с mysql базой данных установлено.");

        executeStatement(SQLQuery.CREATE_TABLE_USER);
        executeStatement(SQLQuery.CREATE_TABLE_USER_PUBLIC_KEY);
        executeStatement(SQLQuery.CREATE_TABLE_PENDING_MESSAGE);
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public void executeStatement(SQLQuery sql, Object... params) {
        executeStatement(sql.toString(), false, params);
    }

    public ResultSet executeResultStatement(SQLQuery sql, Object... params) {
        return executeStatement(sql.toString(), true, params);
    }

    public void executeBatchStatement(SQLQuery sql, Object[] array, Object... params) {
        executeBatchStatement(sql.toString(), array, params);
    }

    public int executeUpdateStatement(SQLQuery sql, Object... params) {
        return executeUpdateStatement(sql.toString(), params);
    }

    private ResultSet executeStatement(String sql, boolean result, Object... params) {

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(statement, null, params);

            if (result) {
                CachedRowSet results = createCachedRowSet();
                results.populate(statement.executeQuery());
                return results;
            }

            statement.execute();

        } catch (SQLException ex) {
            System.out.println("Произошла ошибка SQL:");
            System.out.println("SQL State: " + ex.getSQLState());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            System.out.println("Произошла ошибка NullPointer:");
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }

    private int executeUpdateStatement(String sql, Object... params) {

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(statement, null, params);
            return statement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Произошла ошибка SQL:");
            System.out.println("SQL State: " + ex.getSQLState());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            System.out.println("Произошла ошибка NullPointer:");
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        }

        return 0;
    }

    private void executeBatchStatement(String sql, Object[] array, Object... params) {

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Object object : array) {
                setParameters(statement, object, params);
                statement.addBatch();
            }

            statement.executeBatch();

        } catch (SQLException ex) {
            System.out.println("Произошла ошибка SQL:");
            System.out.println("SQL State: " + ex.getSQLState());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            System.out.println("Произошла ошибка NullPointer:");
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    private void setParameters(PreparedStatement statement, Object element, Object... params) throws SQLException {
        if (params != null) {
            if (element == null) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            } else {
                statement.setObject(1, element);

                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 2, params[i]);
                }

            }
        }
    }

    private CachedRowSet createCachedRowSet() throws SQLException {
        if (factory == null) {
            factory = RowSetProvider.newFactory();
        }
        return factory.createCachedRowSet();
    }

    public User isValidUser(String username, String passwordHash) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_USER, username);
        if (resultSet.next()) {
            String userId = String.valueOf(resultSet.getString("userId"));
            String userName = resultSet.getString("userName");
            String dataPasswordHash = resultSet.getString("passwordHash");
            String email = resultSet.getString("email");
            String phone = resultSet.getString("phone");

            if (dataPasswordHash.equals(passwordHash)) {
                return new User(userId, userName, email, phone);
            }
        }
        return null;
    }

    public User searchUser(String username) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_USER, username);
        if (resultSet.next()) {
            String userId = String.valueOf(resultSet.getString("userId"));
            String userName = resultSet.getString("userName");
            String email = resultSet.getString("email");
            String phone = resultSet.getString("phone");

            return new User(userId, userName, email, phone);
        }

        return null;
    }

    public boolean isUserExists(String newUsername) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_CHECK_NEWUSER_USERNAME, newUsername);
        return resultSet.next();
    }

    public boolean isEmailExists(String newEmail) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_CHECK_NEWUSER_EMAIL, newEmail);
        return resultSet.next();
    }

    public boolean isPhoneExists(String newPhone) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_CHECK_NEWUSER_PHONE, newPhone);
        return resultSet.next();
    }

    public String getUserSalt(String username) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_USER_SALT, username);
        if (resultSet.next()) {
            return resultSet.getString("salt");
        }
        return null;
    }

    public boolean insertUserPublicKey(String userIdSender, String userIdReceiver, String publicKey) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_USER_WITH_USERID, userIdSender);
        if (resultSet.next()) {
            ResultSet resultSet2 = executeResultStatement(SQLQuery.SELECT_USER_WITH_USERID, userIdReceiver);

            if (resultSet2.next()) {
                int result = executeUpdateStatement(SQLQuery.INSERT_USER_PUBLIC_KEY, resultSet.getInt("id"), resultSet2.getInt("id"), publicKey);
                return result > 0;
            }

            return false;
        }

        return false;
    }

    public boolean registerUser(String newUsername, String newPassword, String salt, String newEmail, String newPhone) {
        String userId = generateUserId(newUsername, newPhone, newEmail);
        int result = executeUpdateStatement(SQLQuery.INSERT_USER, userId, newUsername, newPassword, salt, newEmail, newPhone);

        return result > 0;
    }

    public static String generateUserId(String username, String phone, String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = username + phone + email;
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insertPendingMessage(String userIdSender, String userIdReceiver, String json) {
        int result = executeUpdateStatement(SQLQuery.INSERT_PENDING_MESSAGE, userIdSender, userIdReceiver, json);

        return result > 0;
    }

    public boolean selectPendingMessage(String userId) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_PENDING_MESSAGE, userId);

        if (resultSet.next()){
            String recipientId = resultSet.getString("recipientId");
            String message = resultSet.getString("message");

            ClientHandler client = ClientManager.getClient(recipientId);

            client.sendMessage(message);
        }

        return false;
    }
}
