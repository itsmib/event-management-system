# Event Management System

This is a full-stack event management platform built during my internship as part of a collaborative development team. After the internship, I migrated the project to my personal workspace, refactored components, and continued improving the system independently.

The application allows users to register, log in, manage credentials securely, and view events, while admins can create and manage event listings. It is designed to be scalable, secure, and aligned with real-world event workflows.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| **User Authentication** | Registration & login with secure validations |
| **OTP-Based Password Reset** | Email verification for account recovery |
| **Admin Event Management** | Create, update, and manage events |
| **Role-Based Access** | Separate flows for users & admins |
| **Database-Driven Architecture** | Persistent storage for events & users |
| **Thymeleaf UI Layer** | Server-side rendered pages with Spring MVC |

### Future Enhancements

- Online ticket booking  
- Payment integration  
- Notification service  
- Cloud deployment

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Spring Boot (Spring MVC, Spring Web) |
| **Frontend** | Thymeleaf, HTML, CSS |
| **Database** | Oracle SQL |
| **Security / Auth** | JavaMailSender for OTP |
| **Build Tool** | Maven |
| **Version Control** | Git & GitHub |

---

## 👤 Contributors

| Name | GitHub Username |
|------|----------------|
| Noorul Misbah | [@itsmib](https://github.com/itsmib) |
| Meghraj Yelwande | [@meghraj-yelwande](https://github.com/meghraj-yelwande) |
| Sameed Irfan | [@SameedIrfan7](https://github.com/SameedIrfan7) |

---

## 📌 Project Context

> Originally developed as a **team project during my internship**, where I contributed to core backend modules, authentication flow, database schema design, and security features. The version hosted here is my continued development and maintenance of the codebase with additional enhancements and cleanup.

---

## 🚀 Getting Started

### Clone the repository

```sh
git clone https://github.com/itsmib/event-management-system.git
```

### Run the application

```sh
mvn spring-boot:run
```

---

## 🗄 Database Setup

Update connection credentials in:

```
src/main/resources/application.properties
```

Ensure tables are created using SQL scripts or ORM auto-generation.

---

## 📂 Folder Structure

```
src/
 └── main/
      ├── java/
      │    └── com.example.event
      └── resources/
           ├── templates/
           └── application.properties
```

---

## 📄 License

This repository is for learning and portfolio purposes.  
Please request permission before using commercially.
