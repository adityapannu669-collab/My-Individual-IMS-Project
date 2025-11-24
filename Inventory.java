package com.shop.inventory;

import java.io.*;
import java.util.*;

public class Inventory {
    private final Map<String, Product> products = new HashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    private final File productsFile;
    private final File transactionsFile;

    public Inventory(String productsPath, String transactionsPath) {
        this.productsFile = new File(productsPath);
        this.transactionsFile = new File(transactionsPath);
        ensureDataFiles();
        loadProducts();
        loadTransactions();
    }

    public boolean addProduct(Product p) {
        String id = p.getId();
        if (products.containsKey(id)) return false; // duplicate ID not allowed
        products.put(id, p);
        return true;
    }

    public Product getProduct(String id) {
        if (id == null) return null;
        return products.get(id.trim());
    }

    public Collection<Product> listProducts() {
        return new ArrayList<>(products.values());
    }

    public List<Product> lowStockReport() {
        List<Product> low = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.isLowStock()) low.add(p);
        }
        low.sort(Comparator.comparing(Product::getId));
        return low;
    }

    public boolean recordPurchase(Map<String, Integer> items) {
        Transaction t = new Transaction(TransactionType.PURCHASE);
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            String id = e.getKey();
            int qty = e.getValue();
            if (qty <= 0) throw new IllegalArgumentException("Quantity must be > 0 for purchases.");
            Product p = getProduct(id);
            if (p == null) throw new IllegalArgumentException("Product not found: " + id);
            p.increaseStock(qty);
            t.addItem(id, qty);
        }
        transactions.add(t);
        return true;
    }

    public boolean recordSale(Map<String, Integer> items) {
        Transaction t = new Transaction(TransactionType.SALE);
        // First pass: validate
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            String id = e.getKey();
            int qty = e.getValue();
            if (qty <= 0) throw new IllegalArgumentException("Quantity must be > 0 for sales.");
            Product p = getProduct(id);
            if (p == null) throw new IllegalArgumentException("Product not found: " + id);
            if (qty > p.getStockQuantity())
                throw new IllegalArgumentException("Insufficient stock for product " + id + ". Available: " + p.getStockQuantity());
        }
        // Second pass: apply
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            Product p = getProduct(e.getKey());
            p.decreaseStock(e.getValue());
            t.addItem(e.getKey(), e.getValue());
        }
        transactions.add(t);
        return true;
    }

    public void saveAll() {
        saveProducts();
        saveTransactions();
    }

    private void ensureDataFiles() {
        try {
            File dir = productsFile.getParentFile();
            if (dir != null && !dir.exists()) dir.mkdirs();
            dir = transactionsFile.getParentFile();
            if (dir != null && !dir.exists()) dir.mkdirs();

            if (!productsFile.exists()) productsFile.createNewFile();
            if (!transactionsFile.exists()) transactionsFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to prepare data files: " + e.getMessage(), e);
        }
    }

    private void loadProducts() {
        try (BufferedReader br = new BufferedReader(new FileReader(productsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                Product p = Product.fromCsv(line);
                products.put(p.getId(), p);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load products: " + e.getMessage(), e);
        }
    }

    private void loadTransactions() {
        try (BufferedReader br = new BufferedReader(new FileReader(transactionsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                Transaction t = Transaction.fromCsv(line);
                transactions.add(t);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load transactions: " + e.getMessage(), e);
        }
    }

    private void saveProducts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(productsFile, false))) {
            for (Product p : products.values()) {
                pw.println(p.toCsv());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save products: " + e.getMessage(), e);
        }
    }

    private void saveTransactions() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(transactionsFile, false))) {
            for (Transaction t : transactions) {
                pw.println(t.toCsv());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save transactions: " + e.getMessage(), e);
        }
    }
}
