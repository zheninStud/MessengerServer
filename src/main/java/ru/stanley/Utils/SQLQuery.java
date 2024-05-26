package ru.stanley.Utils;

public enum SQLQuery {

    CREATE_TABLE_USER(
            "CREATE TABLE IF NOT EXISTS User (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "userId VARCHAR(255) UNIQUE NOT NULL," +
                    "userName VARCHAR(255) UNIQUE NOT NULL," +
                    "passwordHash VARCHAR(255) NOT NULL," +
                    "salt VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) UNIQUE NOT NULL," +
                    "phone VARCHAR(20) UNIQUE NOT NULL" +
                    ")"
    ),

    CREATE_TABLE_USER_PUBLIC_KEY(
            "CREATE TABLE IF NOT EXISTS UserPublicKey (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_sender VARCHAR(255) NOT NULL," +
                    "user_receiver VARCHAR(255) NOT NULL," +
                    "message TEXT NOT NULL," +
                    "FOREIGN KEY (user_sender) REFERENCES User(userId)," +
                    "FOREIGN KEY (user_receiver) REFERENCES User(userId)" +
                    ")"
    ),

    CREATE_TABLE_PENDING_MESSAGE(
            "CREATE TABLE IF NOT EXISTS PendingMessages (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "senderId VARCHAR(255)," +
                    "recipientId VARCHAR(255)," +
                    "message TEXT," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");"
    ),

    INSERT_USER(
            "INSERT INTO User (userId, userName, passwordHash, salt, email, phone) VALUES (?, ?, ?, ?, ?, ?)"
    ),

    INSERT_USER_PUBLIC_KEY(
            "INSERT INTO UserPublicKey (user_sender, user_receiver, message) VALUES (?, ?, ?)"
    ),

    INSERT_PENDING_MESSAGE(
            "INSERT INTO PendingMessages (senderId, recipientId, message) VALUES (?, ?, ?);"
    ),

    SELECT_USER_PUBLIC_KEY(
            "SELECT * FROM UserPublicKey WHERE user_receiver = ?"
    ),

    SELECT_PENDING_MESSAGE(
            "SELECT * FROM PendingMessages WHERE recipientId = ?"
    ),

    SELECT_USER_SALT(
            "SELECT salt FROM User WHERE userName = ?"
    ),

    SELECT_USER(
            "SELECT * FROM User WHERE userName = ?"
    ),

    SELECT_CHECK_NEWUSER_USERNAME(
            "SELECT * FROM User where userName = ?"
    ),

    SELECT_CHECK_NEWUSER_PHONE(
            "SELECT * FROM User where phone = ?"
    ),

    SELECT_CHECK_NEWUSER_EMAIL(
            "SELECT * FROM User where email = ?"
    );

    private final String mysql;

    SQLQuery(String mysql) {
        this.mysql = mysql;
    }

    @Override
    public String toString() {
        return mysql;
    }

}
