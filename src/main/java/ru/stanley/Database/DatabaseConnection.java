package ru.stanley.Database;

import ru.stanley.Server;
import ru.stanley.Utils.SQLQuery;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import java.sql.*;
import java.net.URL;

public class DatabaseConnection {

    private static final Server application = Server.getInstance();
    private String pathDb;
    private RowSetFactory factory;
    private Connection connection;

    public DatabaseConnection() throws SQLException {

        checkPath();
        // connection = DriverManager.getConnection("jdbc:sqlite:" + pathDb);
        connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
        System.out.println("Соединение с локальной базой данных установлено.");

        executeStatement(SQLQuery.CREATE_TABLE_USER);


    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    private void checkPath() {
        URL resourceUrl = application.getClass().getResource("databases/mydatabase.db");

        if (resourceUrl != null) {
            pathDb = resourceUrl.getPath();
            System.out.println("Resource: " + pathDb);
        } else {
            System.out.println("Resource not found");
        }
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

    public boolean isUserExists(String newUsername) {

        return true;
    }

    public boolean isEmailExists(String newEmail) {

        return true;
    }

    public void registerUser(String newUsername, String newPassword, String newEmail, String newPhone) {
    }
}
