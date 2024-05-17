package ru.stanley.Utils;

public enum SQLQuery {

    CREATE_TABLE_USER(
            "CREATE TABLE IF NOT EXISTS User (" +
                    "userId INT AUTO_INCREMENT PRIMARY KEY," +
                    "userName VARCHAR(255) UNIQUE NOT NULL," +
                    "passwordHash VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) UNIQUE NOT NULL," +
                    "phone VARCHAR(20) UNIQUE NOT NULL" +
                    ")"
    ),

    INSERT_USER(
            "INSERT INTO User (userName, passwordHash, email, phone) VALUES (?, ?, ?, ?)"
    ),

    SELECT_CHECK_USER(
            "SELECT * FROM User where userName = ?"
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
