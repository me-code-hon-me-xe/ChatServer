# Chat Server Application

## Table of Contents

- [Introduction](#introduction)
- [Key Features](#key-features)
- [Technologies Used](#technologies-used)
- [How to Run the Application](#how-to-run-the-application)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

## Introduction

Hello everyone, this is my project of **Chat Server Application**. This is a lightweight, real-time messaging platform designed to facilitate communication between users. Built using the ZK framework and JDBC for database interactions, this application allows users to chat with each other, manage their accounts, and receive real-time notifications for incoming messages.

## Key Features

- **Real-Time Messaging**: Users can send and receive messages in real-time, with instant updates to the chat view.
- **Notifications**: Notifications are displayed when a new message is received, ensuring that users never miss a message.
- **User Authentication**: A secure login system ensures that only authenticated users can access the chat functionality.
- **SQLite Database**: The chat data is stored locally using SQLite, with JDBC handling the database operations.
- **ZK Framework**: The application uses the ZK framework for the frontend, providing a responsive and interactive user interface.

## Technologies Used

- **ZK Framework**: For building the user interface.
- **JDBC**: For database operations and connecting to SQLite.
- **SQLite**: As the database for storing user and message data.
- **Java**: The primary programming language used in the project.

## How to Run the Application

1. **Clone the Repository**:
   ```bash
   https://github.com/me-code-hon-me-xe/ChatServer.git
2. **Open folder**: Extract the dowloaded folder and open it in IntelliJ IDEA.
   <img width="1440" alt="Screen Shot 2024-08-16 at 11 03 44 PM" src="https://github.com/user-attachments/assets/c9170a2c-00c3-43fe-8549-d015289a8265">
2. **Configure the Database:**:
   Opening the **DBConnection** in the project to set up SQLite database and configure the JDBC connection to the project.
   <img width="1440" alt="Screen Shot 2024-08-16 at 10 52 49 PM" src="https://github.com/user-attachments/assets/cc14ccd8-7140-4a16-9339-1c3723be5dfd">
4. **Edit the configuration**:
   Open Current **File > Edit Configuration > Add new > Maven**. In the **Run** type **jetty:run**.Then click on **Apply**
   <img width="1440" alt="Screen Shot 2024-08-16 at 11 11 36 PM" src="https://github.com/user-attachments/assets/27ed7ae3-5428-4fb1-8530-b0108de74d2b">
5. **Run the project**
6. **Access the Application**:
   - **Register page**: http://localhost:8080/ChatServer/register.zul
   - **Login page**: http://localhost:8080/ChatServer/login.zul
   - **Chat page**: http://localhost:8080/ChatServer/userView.zul

   


