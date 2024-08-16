package com.project.viewmodel;


import com.project.dao.UserDAO;
import com.project.model.User;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;

public class LoginViewModel {

    private UserDAO userDAO;
    private String username;
    private String password;


    public LoginViewModel() {
        userDAO = new UserDAO();
    }

    @Command
    public void login() {
        User user = userDAO.getUserByUsernameAndPassword(username, password);

        if (user != null) {
            // Set the current user in the session
            Executions.getCurrent().getSession().setAttribute("currentUser", user);
            // Redirect to the chat page
            Executions.sendRedirect("/userView.zul");
        } else {
            Messagebox.show("Invalid username or password", "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

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
}
