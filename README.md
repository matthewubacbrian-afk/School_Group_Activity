# School Database Manager

A Java Swing desktop application connected to a PostgreSQL database. Manage students, subjects, and enrollments through a tabbed GUI — built with Java, Maven, and JDBC.

---

## Requirements

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 11 or higher | https://www.oracle.com/java/technologies/downloads/ |
| Maven | 3.6 or higher | https://maven.apache.org/download.cgi |
| PostgreSQL | 13 or higher | https://www.postgresql.org/download |
| VS Code | Any recent version | https://code.visualstudio.com |

**VS Code Extensions needed:**

- [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) — by Microsoft (includes Language Support, Debugger, Maven, and Project Manager for Java)

> **Note:** You need both Java JDK and Maven installed on your system before opening the project in VS Code.

---

## Installation Guide

### Step 1 — Install Java JDK

1. Go to https://adoptium.net
2. Download the installer for your OS (Windows / Mac / Linux)
3. Run the installer — check **"Set JAVA_HOME"** during setup
4. Open a new terminal and verify:

```bash
java -version
```

Expected output:
```
openjdk version "11.0.x" ...
```

---

### Step 2 — Install Maven

1. Go to https://maven.apache.org/download.cgi
2. Download the **Binary zip archive** (e.g. `apache-maven-3.9.x-bin.zip`)
3. Extract it to a folder, for example: `C:\Program Files\Apache\maven`
4. Add Maven to your system PATH:
   - Open **Start** → search **Environment Variables** → click **Edit the system environment variables**
   - Under **System Variables**, find `Path` → click **Edit** → click **New**
   - Add the path to Maven's `bin` folder, e.g.: `C:\Program Files\Apache\maven\bin`
5. Open a new terminal and verify:

```bash
mvn -version
```

Expected output:
```
Apache Maven 3.9.x ...
```

---

### Step 3 — Install & Set Up PostgreSQL

1. Go to https://www.postgresql.org/download
2. Download and run the installer for your OS
3. During installation, **remember the password** you set for the `postgres` user
4. After installation, open **SQL Shell (psql)** from your Start Menu
5. Press Enter through all prompts, then enter your password:

```
Server [localhost]:      ← press ENTER
Database [postgres]:     ← press ENTER
Port [5432]:             ← press ENTER
Username [postgres]:     ← press ENTER
Password:                ← type your password
```

6. Create the database:

```sql
CREATE DATABASE school_db;
```

7. Verify it was created:

```sql
\l
```

> **Note:** The tables (`students`, `subjects`, `enrollments`) are created automatically when you first run the app.

---

### Step 4 — Get the Project

**Clone via Git:**

```bash
git clone <repository-url>
cd school_db_app
```
---

### Step 5 — Open in VS Code

1. Open VS Code
2. Go to **File → Open Folder** and select the `school_db_app` folder
3. VS Code will detect it as a Maven project — click **Yes** if prompted to import

> If the Java extension prompts you to configure the JDK, point it to your installed JDK folder.

---

### Step 6 — Configure Database Password

Open this file in VS Code:

```
src/main/java/com/schooldb/db/DatabaseManager.java
```

Find this line near the top:

```java
private static final String DB_PASSWORD = "your_password_here";
```

Replace `your_password_here` with your actual PostgreSQL password:

```java
private static final String DB_PASSWORD = "mypassword123";
```

Save the file (`Ctrl + S`).

> **Note:** Make sure PostgreSQL is running before you build or run the app.

---

### Step 7 — Build the Project

**Using the VS Code Terminal:**

Open the integrated terminal in VS Code (`Ctrl + `` ` ``) and run:

```bash
mvn clean package
```

A successful build shows:
```
BUILD SUCCESS
```

The runnable JAR is created at:
```
target\school-db-app-1.0-SNAPSHOT-jar-with-dependencies.jar
```
---

### Step 8 — Run the Application

**Using the VS Code Terminal:**

```bash
java -jar target\school-db-app-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The **School Database Manager** window will open.

> **Note:** You only need to run `mvn clean package` once, or whenever you change the code. After that, you can use `java -jar ...`.

---

## Quick Reference Commands

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Create the database (inside psql)
CREATE DATABASE school_db;

# Build the project
mvn clean package

# Run the application
java -jar target\school-db-app-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Project Structure

```
school_db_app/
├── src/
│   └── main/
│       └── java/
│           └── com/schooldb/
│               ├── Main.java               ← Entry point
│               ├── db/
│               │   └── DatabaseManager.java ← All SQL / DB logic
│               └── ui/
│                   ├── MainFrame.java       ← App window & tabs
│                   ├── StudentPanel.java    ← Students tab
│                   ├── SubjectPanel.java    ← Subjects tab
│                   └── EnrollmentPanel.java ← Enrollments tab
├── pom.xml                                 ← Maven build config
└── README.md
```

---

## Troubleshooting

**`java` is not recognized as a command**
- Java is not installed or not added to PATH
- Re-install from https://adoptium.net and check **"Set JAVA_HOME"** during setup

**`mvn` is not recognized as a command**
- Maven is not installed or its `bin` folder is not in PATH
- Follow Step 2 again carefully, especially the PATH setup
- Restart VS Code after updating PATH

**VS Code doesn't recognize the Java project**
- Make sure the **Extension Pack for Java** is installed
- Go to **View → Command Palette** → type `Java: Clean Java Language Server Workspace` → restart

**Could not connect to the database**
- Make sure PostgreSQL is running (check Windows Services or pgAdmin)
- Double-check the password in `DatabaseManager.java`
- Make sure the `school_db` database was created in psql

**BUILD FAILURE**
- Read the `[ERROR]` lines in the terminal carefully
- The most common cause is a wrong password or a typo in `DatabaseManager.java`
- Run `mvn clean package -e` for detailed error output

---

*School Database Manager — Java Swing + PostgreSQL + Maven*
