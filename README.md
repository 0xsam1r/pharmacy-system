# 💊 Pharmacy Management System  
Project for **COMP301** at Faculty of Science, Ain Shams University.  
A complete Java-based pharmacy management system with **database integration**, **financial utilities**, **reports**, and **alert management**.

---

## ⚙️ Setup Instructions  

### 🗄️ 1️⃣ Database Setup (MySQL)

Before running the system, you must set up the database.

#### ➤ Create the Database
1. Open your MySQL client (e.g., **MySQL Workbench**).
2. Run the **Database Creation Script** (`creation.sql`) to create all tables and relationships:

```sql
USE PMS;
-- Paste the creation script content here or run the file directly
````

#### ➤ Insert Initial Data

1. Run the **Insertion Script** (`insertion.sql`) to populate tables with default sample data:

```sql
USE PMS;
-- Paste or execute the insertion script here
```

#### ➤ Add user for DataBase Connection

1. Run the **(`setUser.sql`) script** to add the user used to connect to DB:

```sql
USE PMS;
-- Paste or execute the insertion script here
```
---

## 📦 Project Libraries (Required)

This project uses external libraries that are **not included in the repository**.
Download them here:

👉 **[https://drive.proton.me/urls/DDDWG3WQSW#qvJnpDTn61TO](https://drive.proton.me/urls/DDDWG3WQSW#qvJnpDTn61TO)**

You will get a ZIP file.

---

## 📁 How to Use

1. **Download the ZIP file** from the link above.
2. **Extract it** — you will get a folder named `lib`.
3. **Place the `lib` folder in the project root** (same folder as `src`).

Your project should look like:

```
project/
 ├── src/
 ├── lib/
 └── ...
```

4. Open the project in your IDE and make sure the libraries are added to the project:

* **IntelliJ:** Right-click `lib` → *Add as Library*
* **NetBeans/Eclipse:** Add JARs from the `lib` folder to the project’s build path

---

That's it — the project will now compile and run correctly.

---

### ⚠️ Notes

* Ensure the **MySQL server** is running before launching the program.
* If you see encoding issues in the console (like `?` instead of emojis),
  you can safely ignore them or replace emojis with `[INFO]`, `[WARN]`, etc.
* Reports and charts will be generated in the **`reports/`** folder inside the project directory.

---

## 👨‍💻 Contributors

| Name            | Email                                                                 |
| --------------- | --------------------------------------------------------------------- |
| Samir Ahmed     | [0xsam1r@proton.me](mailto:0xsam1r@proton.me)                         |
| MM Bayoumi Taha | [mmbayoumitaha@gmail.com](mailto:mmbayoumitaha@gmail.com)             |
| Ziad Ahmed      | [ziad166197@gmail.com](mailto:ziad166197@gmail.com)                   |
| Mahmoud Elsayed | [mahmoudelasyedahmed@gmail.com](mailto:mahmoudelasyedahmed@gmail.com) |


---

🧠 *Developed with Java SE, MySQL, and JFreeChart for academic and practical learning.*
