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
        String sql = "INSERT INTO Notifications(user_id, message_id, notification_time) VALUES(?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notification.getUserId());
            pstmt.setInt(2, notification.getMessageId());
            pstmt.setString(3, notification.getNotificationTime());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding notification: " + e.getMessage());
        }
    }

    public List<Notification> getNotificationsByUserId(int userId) {
        String sql = "SELECT * FROM Notifications WHERE user_id = ?";
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

}
