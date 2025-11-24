package com.shop.inventory;

import java.util.Objects;

public class Product {
    private final String id;           // Unique Product ID
    private String name;               // Product name
    private double price;              // Unit price (>= 0)
    private int stockQuantity;         // Current stock (>= 0)
    private int minStockLevel;         // Alert threshold (>= 0)

    public Product(String id, String name, double price, int stockQuantity, int minStockLevel) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Product ID cannot be empty.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Product name cannot be empty.");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        if (stockQuantity < 0) throw new IllegalArgumentException("Stock quantity cannot be negative.");
        if (minStockLevel < 0) throw new IllegalArgumentException("Minimum stock level cannot be negative.");

        this.id = id.trim();
        this.name = name.trim();
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minStockLevel = minStockLevel;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public int getMinStockLevel() { return minStockLevel; }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }

    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        this.price = price;
    }

    public void setMinStockLevel(int minStockLevel) {
        if (minStockLevel < 0) throw new IllegalArgumentException("Minimum stock level cannot be negative.");
        this.minStockLevel = minStockLevel;
    }

    public void increaseStock(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Increase quantity must be > 0.");
        this.stockQuantity += qty;
    }

    public void decreaseStock(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Decrease quantity must be > 0.");
        if (qty > stockQuantity) throw new IllegalArgumentException("Cannot sell more than available stock.");
        this.stockQuantity -= qty;
    }

    public boolean isLowStock() {
        return stockQuantity <= minStockLevel;
    }

    public String toCsv() {
        // id,name,price,stockQuantity,minStockLevel
        return String.format("%s,%s,%.2f,%d,%d", escape(id), escape(name), price, stockQuantity, minStockLevel);
    }

    public static Product fromCsv(String line) {
        String[] parts = splitCsv(line, 5);
        String id = unescape(parts[0]);
        String name = unescape(parts[1]);
        double price = Double.parseDouble(parts[2]);
        int stock = Integer.parseInt(parts[3]);
        int min = Integer.parseInt(parts[4]);
        return new Product(id, name, price, stock, min);
    }

    private static String escape(String s) {
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    private static String unescape(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }

    private static String[] splitCsv(String line, int expected) {
        // Simple CSV split handling quoted fields
        String[] result = new String[expected];
        int idx = 0;
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result[idx++] = sb.toString();
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result[idx] = sb.toString();
        if (idx != expected - 1) {
            throw new IllegalArgumentException("CSV parse error: " + line);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", minStockLevel=" + minStockLevel +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
