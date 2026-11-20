# Campus Lost & Found Management System

A complete full-stack web application developed for **CSE 3rd Year / 5th Semester Mini Project** using **Java 17**, **Spring Boot**, **Spring Data JPA**, **Spring Security**, **MySQL**, and **Thymeleaf**.

---

## 📌 Project Overview
The **Campus Lost & Found Management System** provides a centralized, transparent platform for college students, faculty, and administrative staff to report lost personal belongings, submit found items, and safely verify and reclaim lost property.

### Key Objectives:
- Eliminate physical lost-and-found notice boards with real-time digital tracking.
- Enable photographic proof and item status tracking (`OPEN`, `CLAIMED`, `CLOSED`).
- Prevent fraudulent claims through structured ownership verification and admin moderation.
- Implement strict Role-Based Access Control (**Student** vs **Admin**).

---

## 🛠️ Technology Stack (Strictly Implemented)

| Component | Technology / Library |
|---|---|
| **Programming Language** | Java 17 (LTS) |
| **Backend Framework** | Spring Boot 3.2.5 |
| **Web Layer** | Spring MVC |
| **Security Layer** | Spring Security 6 (BCrypt Password Hashing, Role-Based Access Control) |
| **Database ORM** | Spring Data JPA + Hibernate 6 |
| **Database** | MySQL 8.0+ |
| **Build & Dependency Tool** | Apache Maven |
| **Template Engine** | Thymeleaf 3 + Thymeleaf Extras Spring Security 6 |
| **Frontend UI & Styling** | HTML5, CSS3, JavaScript, Bootstrap 5.3, Bootstrap Icons |

> **Note:** Strictly built without React, Node.js, Firebase, or MongoDB as specified.

---

## 🏛️ System Architecture

The project strictly follows the standard **Spring Boot Layered Enterprise Architecture**:

```
src/main/java/com/campus/lostfound/
│
├── config/              # Security filter chain, WebMvc resource handler, DataInitializer
├── controller/          # Spring MVC Web Controllers (Home, Auth, Student, Admin, Item)
├── dto/                 # Data Transfer Objects & Form validation beans
├── entity/              # JPA Relational Entities & Enums (User, ItemReport, ClaimRequest, Category)
├── repository/          # Spring Data JPA Repository interfaces with custom queries
├── security/            # Custom UserDetailsService and Role-based Authentication Success Handler
└── service/             # Business Logic interfaces and implementations (UserService, ItemReportService, etc.)
```

---

## 🗄️ Database Schema & Entities

The relational database (`campus_lostfound_db`) consists of 4 main tables:

1. **`users`**
   - `id` (PK, Auto Increment)
   - `full_name`, `email` (Unique), `password` (BCrypt encoded)
   - `student_id` (Roll No), `phone`, `role` (`ROLE_STUDENT`, `ROLE_ADMIN`), `enabled`, `created_at`

2. **`categories`**
   - `id` (PK, Auto Increment)
   - `name` (Unique), `description`, `icon` (Bootstrap Icons class)

3. **`item_reports`**
   - `id` (PK, Auto Increment)
   - `title`, `category_id` (FK -> categories.id), `description`, `location`, `item_date`
   - `image_url` (Path to uploaded item picture)
   - `report_type` (`LOST`, `FOUND`)
   - `status` (`OPEN`, `CLAIMED`, `CLOSED`)
   - `posted_by_user_id` (FK -> users.id)
   - `created_at`, `updated_at`

4. **`claim_requests`**
   - `id` (PK, Auto Increment)
   - `item_report_id` (FK -> item_reports.id)
   - `claimer_user_id` (FK -> users.id)
   - `claim_description` (Explanation of ownership)
   - `proof_details` (Distinctive marks, serial number, lock screen hint, pouch contents)
   - `contact_info` (Phone/email for handover)
   - `status` (`PENDING`, `APPROVED`, `REJECTED`)
   - `admin_remarks`
   - `created_at`, `updated_at`

---

## 🚀 Pre-seeded Accounts & Demo Credentials

When the application boots up for the first time, `DataInitializer.java` automatically initializes default categories, admin and student accounts, and realistic sample lost and found reports:

| Role | Email | Password | Access Level |
|---|---|---|---|
| **System Admin** | `admin@campus.edu` | `admin123` | Full admin dashboard, claim verification, status controls, user management |
| **Student (Demo)** | `student@campus.edu` | `student123` | Student portal, report items, submit ownership claims, track reports |

---

## 💻 Step-by-Step Windows VS Code Setup Instructions

### 1. Prerequisites
Ensure you have the following installed on your Windows machine:
1. **Java Development Kit (JDK) 17**:
   - Download and install [Eclipse Adoptium Temurin 17](https://adoptium.net/) or Oracle JDK 17.
   - Verify in terminal: `java -version`
2. **MySQL Server**:
   - Install MySQL Server via MySQL Installer or XAMPP.
   - Make sure the MySQL service is running on default port `3306`.
3. **VS Code Extensions**:
   - Open VS Code and install the **Extension Pack for Java** (by Microsoft).
   - Install the **Spring Boot Extension Pack** (by VMware).

### 2. Configure MySQL Database Credentials
Open `src/main/resources/application.properties` and verify your MySQL password:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_lostfound_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```
*(If your local MySQL root password is empty or different, change `spring.datasource.password` accordingly).*

### 3. (Optional) Run Database Setup SQL
The application will automatically create all tables using Hibernate (`ddl-auto=update`), but you can also execute `database_setup.sql` in MySQL Workbench or phpMyAdmin if needed.

### 4. Running the Application in VS Code
You have two easy ways to run the project:

#### Method A: Using VS Code Java Runner (Recommended)
1. Open this project folder in VS Code (`File` -> `Open Folder...`).
2. Navigate to `src/main/java/com/campus/lostfound/CampusLostFoundApplication.java`.
3. Click the **Run** button (or press `F5` / click `Run Java` above the `main` method).

#### Method B: Using Maven in Terminal
Open the VS Code integrated terminal (`Ctrl + ~`) in the project directory and run:
```bash
mvn spring-boot:run
```
*(Or use `.\mvnw spring-boot:run`)*

### 5. Access the Web Application
Once the console displays:
```
Tomcat started on port 8080 (http) with context path '/'
Started CampusLostFoundApplication in ... seconds
```
Open your web browser and navigate to:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🎯 Viva & Demonstration Walkthrough

When presenting this project to your college evaluator, follow this flow:

1. **Homepage (`/`)**:
   - Show live statistics counters (Total Reports, Lost Items, Found Items, Reclaimed).
   - Demonstrate category cards and recent reports grid.
2. **Student Module**:
   - Register a new student at `/register`.
   - Log in using the student credentials.
   - Access the **Student Dashboard** (`/student/dashboard`) showing personal stats.
   - Click **Report Lost Item** (`/items/report-lost`), fill the form, upload an item image, and submit.
   - Demonstrate the real-time image preview and input validation.
   - Search and filter items on `/items` by keyword, category, campus location, and report type.
3. **Claim Request**:
   - Find a listed found item and click **View Details & Claim**.
   - Submit a claim request with specific ownership proof (serial number, distinctive markings).
   - View the claim under **My Claims** (`/student/my-claims`) marked as `PENDING REVIEW`.
4. **Admin Module**:
   - Log out and sign in with `admin@campus.edu` / `admin123`.
   - The system automatically redirects the administrator to `/admin/dashboard`.
   - View campus metrics and pending claims.
   - Go to **Manage Claims** (`/admin/claims`), review the student's proof details, add admin remarks, and click **Approve Claim**.
   - Notice how approving the claim automatically updates the item's status to `CLAIMED` across the entire database!
   - Demonstrate **Manage Reports** (`/admin/reports`) to change item statuses or delete inappropriate posts.
   - Demonstrate **Manage Users** (`/admin/users`) to view registered students and toggle account access.
   - Demonstrate **Manage Categories** (`/admin/categories`) to create custom campus classification tags.

---

## 🔒 Security Highlights
- **BCrypt Password Hashing**: Passwords are never stored in plaintext.
- **CSRF Protection**: Form submissions are protected by Spring Security CSRF tokens.
- **Role-Based URL Authorization**: `/admin/**` endpoints are strictly protected with `hasRole('ROLE_ADMIN')`.
- **Ownership Verification**: Students can only edit or delete items that they personally reported.

---

## 🎓 Academic Details
- **Course**: Bachelor of Engineering / Technology (CSE)
- **Semester**: 5th Semester / 3rd Year
- **Subject**: Java Frameworks / Web Technology Mini Project
- **Application**: Campus Lost & Found Management System
#   L o s t _ a n d _ F o u n d  
 