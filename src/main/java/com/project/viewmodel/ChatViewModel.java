package com.project.viewmodel;

import com.project.dao.MessageDAO;
import com.project.dao.NotificationDAO;
import com.project.dao.UserDAO;
import com.project.model.Message;
import com.project.model.Notification;
import com.project.model.User;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.ui.util.Toast;
import org.zkoss.zul.Textbox;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatViewModel {


    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private NotificationDAO notificationDAO;


    private User currentUser;
    private User selectedUser;
    private String welcomeMessage;
    private EventQueue<Event> chatQueue;


    private List<Message> messages;
    private List<User> userList;
    private List<Notification> notifications;
    private Map<Integer, String> userMap;


    @Init
    @NotifyChange({"currentUser", "userList", "messages", "notifications"})
    public void init() throws Exception {
        // Retrieve currentUser from the session
        currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");

        if (currentUser == null) {
            Executions.sendRedirect("/login.zul");
            return;
        }

//        Sessions.getCurrent().setAttribute("username", currentUser.getUsername());

        // Initialize DAOs
        userDAO = new UserDAO();
        messageDAO = new MessageDAO();
        notificationDAO = new NotificationDAO();
        userMap = new HashMap<>();

        // Load users and create the userMap
        for (User user : userDAO.getAllUsers()) {
            userMap.put(user.getId(), user.getUsername());
        }

        // Load initial data
        updateWelcomeMessage();
        listUser();
        loadMessages();
        loadNotifications();
        updateNotificationIndicators();

        chatQueue = EventQueues.lookup("chatQueue", EventQueues.APPLICATION, true);
        chatQueue.subscribe(event -> {
            // Check if the event pertains to the current user and selected chat
            if (event.getName().equals("newMessage") && event.getData() instanceof Message) {
                Message newMessage = (Message) event.getData();
                if (newMessage.getReceiverId() == currentUser.getId() || newMessage.getSenderId() == currentUser.getId()) {
                    loadMessages();// Refresh messages
                    loadNotifications();
                    updateNotificationIndicators();
                    BindUtils.postNotifyChange(null, null, this, "messages");
                    BindUtils.postNotifyChange(null, null, this, "userList");
                }
                if (currentUser.getId() == newMessage.getReceiverId()) {
                    Toast.show("Notification from " + newMessage.getSenderUsername());
                }
            }
        });
    }


    @Command
    @NotifyChange({"messages"})
    public void sendMessage(@BindingParam("messageText") String messageText) {

        if (messageText == null || messageText.trim().isEmpty()) {
            Messagebox.show("Message cannot be empty!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        Message message = new Message();
        message.setSenderId(currentUser.getId());
        message.setReceiverId(selectedUser.getId());
        message.setSenderUsername(currentUser.getUsername());
        message.setMessageText(messageText);
        message.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()).toString());
        message.setSeen(0);
        messageDAO.addMessage(message);

        // Notify recipient
        Notification notification = new Notification();
        notification.setUserId(selectedUser.getId());
        notification.setSenderId(currentUser.getId());
        notification.setMessageId(message.getId());
        System.out.println(message.getId());
        notification.setNotificationTime(new java.sql.Timestamp(System.currentTimeMillis()).toString());
        notificationDAO.addNotification(notification);

        chatQueue.publish(new Event("newMessage", null, message));
        loadMessages();
    }

    @Command
    @NotifyChange({"notifications"})
    public void loadNotifications() {
        if (currentUser != null) {
            notifications = notificationDAO.getNotificationsByUserId(currentUser.getId());
        }
    }

    @Command
    @NotifyChange({"messages"})
    public void loadMessages() {
        if (currentUser != null && selectedUser != null) {
            messages = messageDAO.getMessagesByUserIds(currentUser.getId(), selectedUser.getId());
        }
    }

    @Command("userList")
    public void listUser() {
        userDAO = new UserDAO();
        userList = userDAO.getAllUsers();
        if (currentUser != null) {
            userList = userList.stream().filter(user -> !user.getUsername().equals(currentUser.getUsername())).collect(Collectors.toList());
        }
    }

    @Command
    @NotifyChange({"selectedUser", "messages", "userList"})
    public void selectUser(@BindingParam("user") User user) {
        this.selectedUser = user;
        if (user != null) {
            Messagebox.show("Let's chat with " + selectedUser.getUsername());

            System.out.println("Thisi me ");
            notificationDAO.deleteNotificationsByUserId(currentUser.getId(), selectedUser.getId());

            loadMessages();

            loadNotifications();

            updateNotificationIndicators();

        } else {
            System.out.println("User is null");
        }
    }

    @Command
    public String getSenderUsername(int senderId) {
        return userMap.getOrDefault(senderId, "Unknown User");
    }

    @Command
    private void updateWelcomeMessage() {
        if (currentUser != null) {
            this.welcomeMessage = "Chat Application - Welcome back " + currentUser.getUsername();
        } else {
            this.welcomeMessage = "Chat Application - Welcome back";
        }
    }

    @Command
    @NotifyChange({"userList"})
    public void updateNotificationIndicators() {
        if (currentUser != null) {
            List<Integer> usersWithNotifications = notificationDAO.getUsersWithNotificationsFrom(currentUser.getId());
            System.out.println(usersWithNotifications);

            for (User user : userList) {
                user.setNotificationIndicator(usersWithNotifications.contains(user.getId()));
            }
        }
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
