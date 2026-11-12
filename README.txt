
HEALTHFIRST PHARMACY INVENTORY MANAGEMENT SYSTEM

A project by NxG Technologies
Main logo: src/healthfirstpims/nxg-logo.png (shown on the login screen)

Programming 732 Project
Richfield Graduate Institute of Technology

1. SYSTEM DESCRIPTION

HealthFirst Pharmacy Inventory Management System (PIMS) is a Java desktop application developed to manage pharmacy medicines, suppliers, users, stock and sales.

The system uses Java Swing for the graphical user interface and MySQL for database management.

2. SYSTEM REQUIREMENTS

The following software is required:

* Java JDK
* NetBeans IDE
* MySQL Server
* MySQL Workbench
* MySQL Connector/J

3. DATABASE SETUP

4. Open MySQL Workbench.

5. Open the file named database.sql.

6. Execute the SQL script.

7. The script creates the healthfirst_pims database.

8. The script creates all required tables and inserts the initial users, suppliers and medicines.

9. DATABASE CONNECTION

The application connects to MySQL using the following database:

Database name:
healthfirst_pims

Database server:
localhost

Database port:
3306

Database username:
root

The MySQL password must be configured in DatabaseConnection.java according to the MySQL installation being used.

5. HOW TO RUN THE APPLICATION

6. Open the HealthFirstPIMS project in NetBeans.

7. Make sure MySQL Server is running.

8. Make sure the MySQL Connector/J library is added to the project.

9. Check the database connection settings in DatabaseConnection.java.

10. Run the project.

11. The HealthFirst Pharmacy login screen will appear.

12. DEFAULT LOGIN DETAILS

ADMIN

Username:
admin

Password:
admin123

Role:
Admin

CASHIER

Username:
cashier

Password:
cash123

Role:
Cashier

7. ADMIN FEATURES

The Admin can:

* Manage medicines
* Add, update and delete medicines
* Manage suppliers
* Add, update and delete suppliers
* Manage system users
* Add, update and delete users
* View sales reports
* View item-wise sales reports
* View low stock reports
* View expiry reports
* Log out of the system

8. CASHIER FEATURES

The Cashier can:

* Search medicines
* Check available stock
* Add medicines to the shopping cart
* Remove items from the cart
* Process sales
* Update stock after a sale
* Generate receipts
* Save receipts
* Print receipts
* Log out of the system

9. DATABASE TABLES

The system contains the following tables:

* users
* suppliers
* medicines
* sales
* sale_items

10. PROJECT STRUCTURE

The main Java classes include:

HealthFirstPIMS.java
DatabaseConnection.java
LoginFrame.java
AdminDashboard.java
CashierDashboard.java
ManageMedicines.java
ManageSuppliers.java
ManageUsers.java
PointOfSale.java
CheckStock.java
SalesReport.java
ItemWiseReport.java
LowStockReport.java
ExpiryReport.java

11. NOTES

The application is designed as a desktop-based pharmacy inventory management system.

User access is controlled according to the assigned role.

The database must be running before starting the application.

The MySQL password in DatabaseConnection.java should not be shared publicly.
