أكيد ✅
ده الكود النهائي لملف **`README.md`** بعد التنسيق والضبط الكامل — جاهز تنسخه وتحطه في الـ GitHub repo مباشرة:

---

````markdown
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

✅ **Make sure both scripts run successfully before starting the app.**

---

### 🔗 2️⃣ Add MySQL Connector (JDBC)

To enable database connectivity between Java and MySQL, you need the **MySQL Connector/J** library.

* **Download it from the official MySQL website:**
  👉 [MySQL Connector/J Download Page](https://dev.mysql.com/downloads/connector/j/)

#### Steps:

1. Extract the ZIP file.
2. Locate the `.jar` file (e.g., `mysql-connector-j-8.4.0.jar`).
3. In **NetBeans / IntelliJ / Eclipse**, go to:
   `Project → Properties → Libraries → Add JAR/Folder` → select the `.jar` file.

---

### 📊 3️⃣ Add JFreeChart Library (for Report Generator)

To enable the **Report Generator** and financial chart features, add the **JFreeChart** library.

* **Download from SourceForge:**
  👉 [JFreeChart Download Page](https://sourceforge.net/projects/jfreechart/files/)

#### Steps:

1. Extract the ZIP file.
2. Locate these main JARs:

   * `jfreechart-x.x.x.jar`
   * `jcommon-x.x.x.jar`
3. Add them both to your project libraries (same way as MySQL connector).

---

### ▶️ 4️⃣ Run the Application

Once libraries and the database are ready:

1. Open the project in **NetBeans** or **IntelliJ**.
2. Ensure your folder structure looks like this:

```
pharmacy-management-system/
│
├── src/
│   ├── application/
│   │   └── Main.java                
│   │
│   ├── model/                       
│   │   ├── Products/                
│   │   │   ├── Product.java
│   │   │   ├── Medicine.java
│   │   │   ├── Cosmetic.java
│   │   │   ├── DosageForm.java
│   │   │   └── Inventory.java
│   │   │
│   │   ├── people/                   
│   │   │   ├── Person.java
│   │   │   ├── Customer.java
│   │   │   ├── Employee.java
│   │   │   └── UserAccount.java
│   │   │
│   │   ├── invoices/                
│   │   │   ├── Invoice.java
│   │   │   ├── PurchaseInvoice.java
│   │   │   ├── SaleInvoice.java
│   │   │   ├── InvoiceItem.java
│   │   │   ├── Supplier.java
│   │   │   └── Batch.java
│   │   │
│   │   ├── returns/                 
│   │   │   ├── ReturnItem.java
│   │   │   ├── SaleReturn.java
│   │   │   └── PurchaseReturn.java
│   │   │
│   │   ├── finance/                 
│   │   │   ├── Treasury.java
│   │   │   ├── Transaction.java
│   │   │   ├── ReportGenerator.java
│   │   │   └── AlertSystem.java
│   │   │
│   │   └── branch/                   
│   │       └── Branch.java
│   │
│   ├── view/                         
│   │   ├── fxml/                    
│   │   │   ├── login.fxml
│   │   │   ├── inventory.fxml
│   │   │   ├── sales.fxml
│   │   │   ├── reports.fxml
│   │   │   └── main.fxml
│   │   │
│   │   └── controllers/         
│   │       ├── LoginViewController.java
│   │       ├── InventoryViewController.java
│   │       ├── SaleViewController.java
│   │       └── ReportsViewController.java
│   │
│   ├── enums/                        
│   │   ├── Category.java
│   │   └── Role.java
│   │
│   │   
│   └── dao/                  
│       ├── ProductDAO.java
│       ├── InvoiceDAO.java
│       └── EmployeeDAO.java
│
├── resources/
│   ├── css/                     
│   └── images/                       
│
│
└── SQL/                              
```


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