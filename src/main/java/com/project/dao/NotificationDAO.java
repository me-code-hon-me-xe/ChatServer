package com.project.dao;

import com.project.config.DBConnection;
import com.project.model.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public void addNotification(Notification notification) {
        String sql = "INSERT INTO Notifications(user_id, sender_id, message_id, notification_time) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notification.getUserId());
            pstmt.setInt(2, notification.getSenderId());
            pstmt.setInt(3, notification.getMessageId());
            pstmt.setString(4, notification.getNotificationTime());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding notification: " + e.getMessage());
        }
    }

    public void markNotificationAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET seen = 1 WHERE id = ?";
        try (Connection connection = DBConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, notificationId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteNotificationsByUserId(int userId, int senderId) {
        String sql = "DELETE FROM Notifications WHERE user_id = ? AND sender_id = ?";
        try (Connection connection = DBConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, senderId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getUsersWithNotificationsFrom(int currentUserId) {
        List<Integer> userIds = new ArrayList<>();
        String query = "SELECT * FROM Notifications WHERE user_id = ? AND seen = 0";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("sender_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }


    public List<Notification> getNotificationsByUserId(int userId) {
        String sql = "SELECT * FROM Notifications WHERE user_id = ? AND seen = 0";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setMessageId(rs.getInt("message_id"));
                notification.setNotificationTime(rs.getString("notification_time"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
        }
        return notifications;
    }

    public void markAsRead(int userId, int senderId) {
        String query = "UPDATE Notifications SET seen = 1 WHERE user_Id = ? AND sender_Id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, senderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
