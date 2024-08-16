package com.project.dao;

import com.project.config.DBConnection;
import com.project.model.ChatRoom;
import com.project.model.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomDAO {

    public void createChatRoom(int user1Id, int user2Id) {
        String sql = "INSERT INTO ChatRooms(user1_id, user2_id) VALUES(?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user1Id);
            pstmt.setInt(2, user2Id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating chat room: " + e.getMessage());
        }
    }

    public boolean chatRoomExists(int user1Id, int user2Id) {
        String sql = "SELECT COUNT(*) FROM ChatRooms WHERE (user1_id = ? AND user2_id = ?) OR (user2_id = ? AND user1_id = ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user1Id);
            pstmt.setInt(2, user2Id);
            pstmt.setInt(3, user2Id);
            pstmt.setInt(4, user1Id);

            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Error checking chat room existence: " + e.getMessage());
            return false;
        }
    }

    public List<ChatRoom> getChatRoomsByUserId(int userId) {
        String sql = "SELECT * FROM ChatRooms WHERE user1_id = ?";
        List<ChatRoom> chatRooms = new ArrayList<>();

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setId(rs.getInt("id"));
                chatRoom.setUser1Id(rs.getInt("user1_id"));
                chatRoom.setUser2Id(rs.getInt("user2_id"));

                chatRooms.add(chatRoom);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching chat rooms: " + e.getMessage());
        }
        return  chatRooms;
    }

}
