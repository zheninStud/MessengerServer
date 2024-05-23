package ru.stanley.Utils;

public enum SQLQuery {

    CREATE_TABLE_USER(
            "CREATE TABLE IF NOT EXISTS User (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "userId INT UNIQUE NOT NULL," +
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
                    "user_sender INT NOT NULL," +
                    "user_receiver INT NOT NULL," +
                    "public_key VARCHAR(255) NOT NULL," +
                    "FOREIGN KEY (user_sender) REFERENCES User(id)," +
                    "FOREIGN KEY (user_receiver) REFERENCES User(id)" +
                    ")"
    ),

    CREATE_TABLE_PENDING_MESSAGE(
            "CREATE TABLE IF NOT EXISTS PendingMessages (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "senderId INT," +
                    "recipientId INT," +
                    "message TEXT," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");"
    ),

    INSERT_USER(
            "INSERT INTO User (userId, userName, passwordHash, salt, email, phone) VALUES (?, ?, ?, ?, ?)"
    ),

    INSERT_USER_PUBLIC_KEY(
            "INSERT INTO UserPublicKey (user_sender, user_receiver, public_key) VALUES (?, ?, ?)"
    ),

    INSERT_PENDING_MESSAGE(
            "INSERT INTO PendingMessages (senderId, recipientId, message) VALUES (?, ?, ?);"
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

    SELECT_USER_WITH_USERID(
            "SELECT * FROM User WHERE userId = ?"
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
