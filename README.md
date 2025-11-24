#Project title
Inventory Management System (Small Shop)

#Overview of the project
A console-based Java application to manage products, record sales and purchases, maintain stock levels, and generate alerts when inventory falls below minimum thresholds. It uses OOP principles, file-based persistence (CSV), and a HashMap for efficient product access.

#Features
Product management: Add, update, and view products.

Sales and purchases: Record transactions with validation to prevent overselling.

Low Stock Report: Automatic report of products below minimum stock.

Data persistence: Load and save products and transactions to CSV files.

Search by Product ID: Fast lookup via HashMap<String, Product>.

Error handling: Validates IDs, prices, quantities, and transaction logic.

Technologies/tools used
Language: Java (Core Java, OOP, Collections, File I/O)

Data structures: HashMap<String, Product>

Build & run: javac and java CLI; works in VS Code or any Java IDE

Files: CSV storage for products and transactions

#Steps to install & run the project
Clone the repository

git clone <(https://github.com/Adhya739/Inventory-Management-System.git)

cd inventory-management-small-shop

Ensure Java is installed

Java 11+ recommended: java -version

Prepare data files

Ensure data/products.csv and data/transactions.csv exist (sample provided below).

Compile

javac -d out src/com/shop/inventory/*.java

Run

java -cp out com.shop.inventory.InventoryApp

Default data paths

Products: data/products.csv

Transactions: data/transactions.csv

#Instructions for testing
Add products: Use menu option to add products with ID, name, price, stock, and minimum stock.

Record purchase: Increases stock; verify in product listing and CSV files after exit.

Record sale: Decreases stock; app prevents selling more than available.

Low stock report: Generate and confirm flagged products match expectations.

Restart the app: Confirm data persistence across sessions.

Edge cases:

Invalid numeric input (e.g., price < 0, quantity <= 0) should be rejected.

Duplicate Product IDs cannot be added.

Selling when stock is insufficient should be blocked with a clear message.



Data files (CSV) contents
