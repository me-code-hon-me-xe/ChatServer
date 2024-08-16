package com.project.viewmodel;

import com.mysql.cj.Session;
import com.project.dao.ChatRoomDAO;
import com.project.dao.MessageDAO;
import com.project.dao.NotificationDAO;
import com.project.dao.UserDAO;
import com.project.model.ChatRoom;
import com.project.model.Message;
import com.project.model.Notification;
import com.project.model.User;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatViewModel {



	private UserDAO userDAO;
	private MessageDAO messageDAO;
	private ChatRoomDAO chatRoomDAO;
	private NotificationDAO notificationDAO;


	private User currentUser;
	private User selectedUser;
	private String welcomeMessage;


	private List<Message> messages;
	private List<User> userList;
	private List<Notification> notifications;
	private Map<Integer, String> userMap;
	private Map<Integer, Integer> unreadMessagesCount = new HashMap<>();


	private Session websocketSession;

	@Init
	@NotifyChange({"currentUser", "userList", "messages", "notifications"})
	public void init() throws Exception {
		// Retrieve currentUser from the session
		currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");

		if(currentUser == null){
			Executions.sendRedirect("/login.zul");
		}

		// Initialize DAOs
		userDAO = new UserDAO();
		messageDAO = new MessageDAO();
		chatRoomDAO = new ChatRoomDAO();
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



	}



	@Command
	@NotifyChange({"messages"})
	public void sendMessage(@BindingParam("messageText") String messageText){
		Message message = new Message();
		message.setSenderId(currentUser.getId());
		message.setReceiverId(selectedUser.getId());
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



		if(messageText != null) {
			System.out.println("Sender:" + currentUser + " Receiver:" + selectedUser + " Message:" + messageText);
		}else{
			System.out.println("Message Text is null");
		}
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
		if(currentUser != null){
			userList = userList.stream().filter(user -> !user.getUsername().equals(currentUser.getUsername())).collect(Collectors.toList());
		}
	}

	@Command
	@NotifyChange({"selectedUser", "messages"})
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
