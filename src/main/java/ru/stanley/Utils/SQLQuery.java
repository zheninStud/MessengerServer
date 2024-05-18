package ru.stanley.Utils;

public enum SQLQuery {

    CREATE_TABLE_USER(
            "CREATE TABLE IF NOT EXISTS User (" +
                    "userId INT AUTO_INCREMENT PRIMARY KEY," +
                    "userName VARCHAR(255) UNIQUE NOT NULL," +
                    "passwordHash VARCHAR(255) NOT NULL," +
                    "salt VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) UNIQUE NOT NULL," +
                    "phone VARCHAR(20) UNIQUE NOT NULL" +
                    ")"
    ),

    INSERT_USER(
            "INSERT INTO User (userName, passwordHash, salt, email, phone) VALUES (?, ?, ?, ?, ?)"
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
