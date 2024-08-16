package com.project.config;

import java.sql.Connection;
import java.sql.Statement;

public class DBInitialize {

    public static void initialize() {

        String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "status TEXT DEFAULT 'offline'," +
                "notification INTEGER DEFAULT 0);";

        String createMessagesTable = "CREATE TABLE IF NOT EXISTS Messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sender_id INTEGER NOT NULL," +
                "receiver_id INTEGER NOT NULL," +
                "message_text TEXT NOT NULL," +
                "timestamp TEXT DEFAULT CURRENT_TIMESTAMP," +
                "seen INTEGER DEFAULT 0," +
                "FOREIGN KEY (sender_id) REFERENCES \"Users\"," +
                "FOREIGN KEY (receiver_id) REFERENCES \"Users\");";

        String createChatRoomsTable = "CREATE TABLE IF NOT EXISTS ChatRooms (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user1_id INTEGER NOT NULL," +
                "user2_id INTEGER NOT NULL," +
                "FOREIGN KEY (user1_id) REFERENCES \"Users\"," +
                "FOREIGN KEY (user2_id) REFERENCES \"Users\");";

        String createNotificationsTable = "CREATE TABLE IF NOT EXISTS Notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "sender_id INTEGER NOT NULL," +
                "message_id INTEGER NOT NULL," +
                "seen INTEGER DEFAULT 0," +
                "notification_time TEXT DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES \"Users\"," +
                "FOREIGN KEY (message_id) REFERENCES \"Messages\");";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {
            // Execute the SQL statements to create the tables
            stmt.execute(createUsersTable);
            stmt.execute(createMessagesTable);
            stmt.execute(createChatRoomsTable);
            stmt.execute(createNotificationsTable);

            System.out.println("Database tables created successfully.");
        } catch (Exception e) {
            System.out.println("Error initializing the database: " + e.getMessage());
        }

    }

}
