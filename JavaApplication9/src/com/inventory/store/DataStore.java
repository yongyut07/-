package com.inventory.store;

import com.inventory.model.Product;
import com.inventory.model.TransactionRecord;
import com.inventory.model.TransactionType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataStore {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path PRODUCT_FILE = DATA_DIR.resolve("products.dat");
    private static final Path TRANSACTION_FILE = DATA_DIR.resolve("transactions.dat");

    private List<Product> products = new ArrayList<>();
    private List<TransactionRecord> transactions = new ArrayList<>();
    private int nextProductId = 1;
    private int nextTransactionId = 1;

    public DataStore() {
        load();
        if (products.isEmpty()) {
            seedSampleData();
        }
    }

    public synchronized List<Product> getProducts() {
        return products.stream()
                .sorted(Comparator.comparing(Product::getId))
                .map(this::copyProduct)
                .collect(Collectors.toList());
    }

    public synchronized List<TransactionRecord> getTransactions() {
        return transactions.stream()
                .sorted(Comparator.comparing(TransactionRecord::getDateTime).reversed())
                .map(this::copyTransaction)
                .collect(Collectors.toList());
    }

    public synchronized void addProduct(Product product) {
        product.setId(nextProductId++);
        products.add(copyProduct(product));
        save();
    }

    public synchronized boolean updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, copyProduct(product));
                save();
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteProduct(int id) {
        boolean removed = products.removeIf(p -> p.getId() == id);
        if (removed) {
            save();
        }
        return removed;
    }

    public synchronized boolean purchaseStock(int productId, int quantity, double unitPrice) {
        Optional<Product> optionalProduct = products.stream().filter(p -> p.getId() == productId).findFirst();
        if (optionalProduct.isEmpty() || quantity <= 0) {
            return false;
        }

        Product product = optionalProduct.get();
        product.setQuantity(product.getQuantity() + quantity);
        transactions.add(new TransactionRecord(
                nextTransactionId++,
                product.getId(),
                product.getName(),
                TransactionType.PURCHASE,
                quantity,
                unitPrice,
                unitPrice * quantity,
                LocalDateTime.now()
        ));
        save();
        return true;
    }

    public synchronized boolean sellStock(int productId, int quantity) {
    Optional<Product> optionalProduct = products.stream().filter(p -> p.getId() == productId).findFirst();
    if (optionalProduct.isEmpty() || quantity <= 0) {
        return false;
    }

    Product product = optionalProduct.get();

    
    if ((product.getQuantity() - quantity) < product.getMinQuantity()) {
        return false;
    }

    product.setQuantity(product.getQuantity() - quantity);
    transactions.add(new TransactionRecord(
            nextTransactionId++,
            product.getId(),
            product.getName(),
            TransactionType.SALE,
            quantity,
            product.getSellPrice(),
            product.getSellPrice() * quantity,
            LocalDateTime.now()
    ));
    save();
    return true;
}

    public synchronized List<Product> getLowStockProducts() {
        return products.stream()
                .filter(Product::isLowStock)
                .map(this::copyProduct)
                .collect(Collectors.toList());
    }

    public synchronized int getTotalProducts() {
        return products.size();
    }

    public synchronized int getTotalStockUnits() {
        return products.stream().mapToInt(Product::getQuantity).sum();
    }

    public synchronized double getTodaySales() {
        LocalDate today = LocalDate.now();
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.SALE)
                .filter(t -> t.getDateTime().toLocalDate().equals(today))
                .mapToDouble(TransactionRecord::getTotalPrice)
                .sum();
    }

    public synchronized double getMonthSales() {
        LocalDate today = LocalDate.now();
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.SALE)
                .filter(t -> t.getDateTime().getYear() == today.getYear() && t.getDateTime().getMonth() == today.getMonth())
                .mapToDouble(TransactionRecord::getTotalPrice)
                .sum();
    }

    public synchronized List<TransactionRecord> getTransactionsByDate(LocalDate date) {
        return transactions.stream()
                .filter(t -> t.getDateTime().toLocalDate().equals(date))
                .sorted(Comparator.comparing(TransactionRecord::getDateTime).reversed())
                .map(this::copyTransaction)
                .collect(Collectors.toList());
    }

    private void load() {
        try {
            Files.createDirectories(DATA_DIR);

            if (Files.exists(PRODUCT_FILE)) {
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(PRODUCT_FILE.toFile()))) {
                    products = (List<Product>) in.readObject();
                }
            }

            if (Files.exists(TRANSACTION_FILE)) {
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(TRANSACTION_FILE.toFile()))) {
                    transactions = (List<TransactionRecord>) in.readObject();
                }
            }

            nextProductId = products.stream().map(Product::getId).max(Integer::compareTo).orElse(0) + 1;
            nextTransactionId = transactions.stream().map(TransactionRecord::getId).max(Integer::compareTo).orElse(0) + 1;
        } catch (Exception e) {
            products = new ArrayList<>();
            transactions = new ArrayList<>();
            nextProductId = 1;
            nextTransactionId = 1;
        }
    }

    private void save() {
        try {
            Files.createDirectories(DATA_DIR);
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PRODUCT_FILE.toFile()))) {
                out.writeObject(products);
            }
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(TRANSACTION_FILE.toFile()))) {
                out.writeObject(transactions);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot save data", e);
        }
    }

    private void seedSampleData() {
        addProduct(new Product(0, "น้ำดื่ม", "เครื่องดื่ม", 8.0, 12.0, 50, 10));
        addProduct(new Product(0, "บะหมี่กึ่งสำเร็จรูป", "อาหาร", 5.5, 7.0, 30, 8));
        addProduct(new Product(0, "นม", "ของใช้", 18.0, 25.0, 15, 5));
        purchaseStock(1, 10, 8.0);
        sellStock(1, 5);
        sellStock(2, 3);
    }

    private Product copyProduct(Product p) {
        return new Product(p.getId(), p.getName(), p.getCategory(), p.getCostPrice(), p.getSellPrice(), p.getQuantity(), p.getMinQuantity());
    }

    private TransactionRecord copyTransaction(TransactionRecord t) {
        return new TransactionRecord(t.getId(), t.getProductId(), t.getProductName(), t.getType(), t.getQuantity(), t.getUnitPrice(), t.getTotalPrice(), t.getDateTime());
    }
}
