Property Listing Management System

Project Title and Description

Property Listing Management System is a JavaFX application for managing real estate listings. It allows sellers to create, read, update, and delete (CRUD) property listings and view registered buyers. The system uses a PostgreSQL database for data persistence and supports data export to CSV files.

Student: Azimbai Zhoodarov

Purpose

The application enables sellers to manage their property listings efficiently and view potential buyers, providing a user-friendly interface for real estate operations.

Objectives

Implement CRUD operations for property listings.
Provide a graphical user interface (GUI) using JavaFX.
Ensure data persistence with a PostgreSQL database.
Support data export to CSV for properties and buyers.
Validate user inputs to prevent errors.

How to Run

Prerequisites:
Java 17 or higher.
PostgreSQL installed with a database named property_db.
JDBC driver for PostgreSQL.


Setup:
Clone the repository: git clone https://github.com/yaxaxaw/PropertyListingManagement.git.

Configure the database in DatabaseConnection.java with your PostgreSQL credentials.

Run SQL script to create tables:

CREATE TABLE users (

    id SERIAL PRIMARY KEY,
    
    username VARCHAR(50),
    
    password VARCHAR(100),
    
    name VARCHAR(100),
    
    surname VARCHAR(100),
    
    role VARCHAR(10)
    
);


CREATE TABLE properties (

    id SERIAL PRIMARY KEY,
    
    type VARCHAR(50),
    
    square_feet INT,
    
    price DECIMAL(10,2),
    
    address TEXT,
    
    status VARCHAR(10),
    
    seller_id INT
    
);

INSERT INTO users (username, password, name, surname, role)

VALUES ('admin', 'qwerty12', 'Your Name', 'Your Surname', 'seller'); 




Run the Application:
Compile and run Main.java using an IDE 


Usage:
Log in as a seller.
Use the GUI to add, update, or delete properties.
Switch to the buyers tab to view registered buyers.
Export data to CSV using the export buttons.

Log in as a buyer to see available properties.



Features

CRUD Operations: Add, view, update, delete properties.
GUI: JavaFX interface with tables and input forms.
Database: PostgreSQL for storing users and properties.
Export: Save properties and buyers to CSV files.
Input Validation: Checks for empty fields and valid numbers.
Modular Design: Separate classes for UI, data models, and database logic.





Documentation:

Property Listing Management System

Overview

The Property Listing Management System is a JavaFX application for sellers to manage real estate listings and view buyers. It uses a PostgreSQL database for data storage and supports CSV export. This document covers the main algorithms, data structures, functions, test cases, and challenges faced.
Algorithms and Data Structures

Data Models:
Property: Stores property details (id, type, square feet, price, address, seller id, status). Uses private fields with getters/setters for encapsulation.
User: Stores buyer details (id, name, surname) for display in the buyers table.


Database Operations:
CRUD: SQL queries for creating (INSERT), reading (SELECT), updating (UPDATE), and deleting (DELETE) properties.
Connection: JDBC with PostgreSQL for persistent storage.


Collections:
ArrayList<Property>: Stores properties loaded from the database for display in propertyTable.
ArrayList<User>: Stores buyers for buyersTable.


UI Rendering:
JavaFX TableView for dynamic display of properties and buyers.
PropertyValueFactory maps object fields to table columns.



Main Functions:

loadProperties(): Queries the database for properties where seller_id matches the current user. Returns a list of Property objects.
loadBuyers(): Retrieves all users with role "buyer" from the database. Returns a list of User objects.

addProperty(): Validates input fields, inserts a new property into the database, and refreshes the table.

updateProperty(): Updates the selected property in the database with new input values.

deleteProperty(): Deletes the selected property after user confirmation.

exportPropertiesToCSV() / exportBuyersToCSV(): Writes table data to CSV files with proper formatting.

showAlert(): Displays error or success messages to the user.

Test Cases:

Add Property:
Input: Type="Apartment", Square Feet="1200", Price="250000", Address="123 Main St", Status="For Sale".

Expected Output: New row in propertyTable: 1 | Apartment | 1200 | 250000.0 | For Sale | 123 Main St.

Result: Property added to database and table.


Update Property:
Input: Select property ID=1, change Price to "260000".

Expected Output: Table updates to show 1 | Apartment | 1200 | 260000.0 | For Sale | 123 Main St.

Result: Database and UI updated.


Delete Property:
Input: Select property ID=1, confirm deletion.

Expected Output: Property removed from table and database.

Result: Table is empty or shows remaining properties.


Export to CSV:

Input: Click "Export Properties" button.

Expected Output: File properties.csv:

ID;Type;Square Feet;Price;Status;Address
1;Apartment;1200;250000.00;For Sale;123 Main St

Result: File created successfully.

Invalid Input:
Input: Empty typeField, click "Add Property".

Expected Output: Alert: "Please fill in all fields".

Result: No database changes, user prompted to correct input.



Challenges Faced:

Database Connection: Configuring JDBC with PostgreSQL required correct driver setup and error handling for connection failures.
Input Validation: Ensuring numeric fields (squareFeet, price) reject invalid inputs required exception handling.
CSV Export: Handling file paths and ensuring proper formatting (e.g., decimal places for price) took careful design.

Screenshots:

![image](https://github.com/user-attachments/assets/bb4ca747-b0f9-4a93-9c33-f537a1b303df)

![image](https://github.com/user-attachments/assets/661eb0fd-c640-4842-a1b8-cba831bc08cb)

When loged in as seller:

![image](https://github.com/user-attachments/assets/3f5cca37-0291-40a6-9985-082188fd32fd)

![image](https://github.com/user-attachments/assets/3a1209a6-6154-4ad9-a0e6-cb93c92beabd)

When loged in as buyer:

![image](https://github.com/user-attachments/assets/0f14b893-c90b-4694-88aa-ba8120469a3c)










