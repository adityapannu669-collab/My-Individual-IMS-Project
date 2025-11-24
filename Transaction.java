package com.shop.inventory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Transaction {
    private final TransactionType type;                // SALE or PURCHASE
    private final LocalDateTime timestamp;             // when recorded
    private final Map<String, Integer> productQuantities; // ProductID -> quantity (positive integers)

    public Transaction(TransactionType type) {
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.productQuantities = new LinkedHashMap<>();
    }

    public Transaction(TransactionType type, LocalDateTime timestamp, Map<String, Integer> productQuantities) {
        this.type = type;
        this.timestamp = timestamp;
        this.productQuantities = new LinkedHashMap<>(productQuantities);
    }

    public TransactionType getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<String, Integer> getProductQuantities() { return productQuantities; }

    public void addItem(String productId, int quantity) {
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product ID cannot be empty.");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0.");
        productQuantities.merge(productId.trim(), quantity, Integer::sum);
    }

    public String toCsv() {
        // Format: type,timestamp,productId1:qty1|productId2:qty2|...
        StringBuilder items = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Integer> e : productQuantities.entrySet()) {
            if (!first) items.append("|");
            items.append(e.getKey()).append(":").append(e.getValue());
            first = false;
        }
        return String.format("%s,%s,%s",
                type.name(),
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                items.toString());
    }

    public static Transaction fromCsv(String line) {
        String[] parts = line.split(",", 3);
        TransactionType type = TransactionType.valueOf(parts[0]);
        LocalDateTime ts = LocalDateTime.parse(parts[1]);
        Map<String, Integer> map = new LinkedHashMap<>();
        if (parts.length == 3 && !parts[2].isBlank()) {
            String[] items = parts[2].split("\\|");
            for (String item : items) {
                String[] kv = item.split(":");
                String pid = kv[0];
                int qty = Integer.parseInt(kv[1]);
                map.put(pid, qty);
            }
        }
        return new Transaction(type, ts, map);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "type=" + type +
                ", timestamp=" + timestamp +
                ", productQuantities=" + productQuantities +
                '}';
    }
}
