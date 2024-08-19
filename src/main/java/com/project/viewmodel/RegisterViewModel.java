package com.project.viewmodel;

import com.project.dao.UserDAO;
import com.project.model.User;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;

public class RegisterViewModel {
    private String username;
    private String password;
    private String status;
    private UserDAO userDAO = new UserDAO();

    // Getters and Setters for username, password, confirmPassword, and status
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Command
    @NotifyChange({"username", "password"})
    public void register() {

        User existingUser = userDAO.getUserByUsername(username);
        if (existingUser != null) {
            // Display error message
            org.zkoss.zk.ui.util.Clients.showNotification("Username already exists!");
            return;
        }

        // Create a new user and set its fields
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);


        // Add the new user to the database
        userDAO.addUser(newUser);
        Messagebox.show("Register successfully!");
    }
}
