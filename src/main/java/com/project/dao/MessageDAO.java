package com.project.dao;

import com.project.config.DBConnection;
import com.project.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public void addMessage(Message message) {
        String sql = "INSERT INTO Messages(sender_id, receiver_id, message_text, seen) VALUES(?, ?, ?, ?)";
        String[] generatedColumns = {"id"}; // Specify the column to retrieve generated key

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, generatedColumns)) {

            pstmt.setInt(1, message.getSenderId());
            pstmt.setInt(2, message.getReceiverId());
            pstmt.setString(3, message.getMessageText());
            pstmt.setInt(4, message.getSeen());

            pstmt.executeUpdate();

            // Retrieve the generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setId(generatedKeys.getInt(1)); // Set the ID to the Message object
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding message: " + e.getMessage());
        }
    }

    public List<Message> getMessagesByUserIds(int user1Id, int user2Id) {
        String sql = "SELECT * FROM Messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
        List<Message> messages = new ArrayList<>();

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user1Id);
            pstmt.setInt(2, user2Id);
            pstmt.setInt(3, user2Id);
            pstmt.setInt(4, user1Id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setReceiverId(rs.getInt("receiver_id"));
                message.setMessageText(rs.getString("message_text"));
                message.setTimestamp(rs.getString("timestamp"));
                message.setSeen(rs.getInt("seen"));
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching messages: " + e.getMessage());
        }
        return messages;
    }

    public void updateMessageSeenStatus(int messageId, boolean seen) {
        String sql = "UPDATE Messages SET seen = ? WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, seen);
            pstmt.setInt(2, messageId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating message status: " + e.getMessage());
        }
    }

}
