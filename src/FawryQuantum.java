import java.util.*;

public class FawryQuantum {

    public static void main(String[] args) {
        Customer customer = new Customer("Ahmed", 500);

        Item cheese = new Cheese("Cheese", 100, 200, false);
        Item biscuits = new Biscuits("Biscuits", 150, 700, false);
        Item scratchCard = new MobileScratchCard("Scratch Card", 50, 0);

        Cart cart = new Cart();
        cart.add(cheese, 2);
        cart.add(biscuits, 1);
        cart.add(scratchCard, 1);

        checkout(customer, cart);
    }

    interface Shippable {
        String getName();
        double getWeight(); // بالكيلو
    }

    abstract static class Item {
        protected String name;
        protected double price;
        protected double weightGrams;

        public Item(String name, double price, double weightGrams) {
            this.name = name;
            this.price = price;
            this.weightGrams = weightGrams;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
        public double getWeightGrams() { return weightGrams; }
        public abstract boolean isExpired();
        public abstract boolean needsShipping();
    }

    static class Cheese extends Item implements Shippable {
        private boolean expired;

        public Cheese(String name, double price, double weightGrams, boolean expired) {
            super(name, price, weightGrams);
            this.expired = expired;
        }

        public boolean isExpired() { return expired; }
        public boolean needsShipping() { return true; }
        public double getWeight() { return weightGrams / 1000.0; }
    }

    static class Biscuits extends Item implements Shippable {
        private boolean expired;

        public Biscuits(String name, double price, double weightGrams, boolean expired) {
            super(name, price, weightGrams);
            this.expired = expired;
        }

        public boolean isExpired() { return expired; }
        public boolean needsShipping() { return true; }
        public double getWeight() { return weightGrams / 1000.0; }
    }

    static class MobileScratchCard extends Item {
        public MobileScratchCard(String name, double price, double weightGrams) {
            super(name, price, weightGrams);
        }

        public boolean isExpired() { return false; }
        public boolean needsShipping() { return false; }
    }

    static class Customer {
        private String name;
        private double balance;

        public Customer(String name, double balance) {
            this.name = name;
            this.balance = balance;
        }

        public boolean pay(double amount) {
            if (balance >= amount) {
                balance -= amount;
                return true;
            }
            return false;
        }

        public double getBalance() { return balance; }
    }

    static class Cart {
        private Map<Item, Integer> items = new LinkedHashMap<>();

        public void add(Item item, int quantity) {
            if (quantity <= 0) {
                System.out.println("الكمية لازم تكون أكبر من صفر!");
                return;
            }
            items.put(item, items.getOrDefault(item, 0) + quantity);
        }

        public boolean isEmpty() {
            return items.isEmpty();
        }

        public double getSubtotal() {
            double total = 0;
            for (Map.Entry<Item, Integer> entry : items.entrySet()) {
                total += entry.getKey().getPrice() * entry.getValue();
            }
            return total;
        }

        public double getTotalWeightKg() {
            double totalWeightGrams = 0;
            for (Map.Entry<Item, Integer> entry : items.entrySet()) {
                totalWeightGrams += entry.getKey().getWeightGrams() * entry.getValue();
            }
            return totalWeightGrams / 1000.0;
        }

        public Map<Item, Integer> getItems() {
            return items;
        }
    }

    public static void checkout(Customer customer, Cart cart) {
        if (cart.isEmpty()) {
            System.out.println("العربية فاضية! مفيش حاجة تتدفع.");
            return;
        }

        // Check expiration
        for (Map.Entry<Item, Integer> entry : cart.getItems().entrySet()) {
            if (entry.getKey().isExpired()) {
                System.out.println("Error: المنتج " + entry.getKey().getName() + " منتهي الصلاحية.");
                return;
            }
        }

        double totalWeightKg = cart.getTotalWeightKg();
        double shippingCost = totalWeightKg > 0 ? 30 : 0;
        double subtotal = cart.getSubtotal();
        double total = subtotal + shippingCost;

        if (!customer.pay(total)) {
            System.out.println("Error: الرصيد غير كافي.");
            return;
        }

        // Shipment notice
        System.out.println("** Shipment notice **");
        for (Map.Entry<Item, Integer> entry : cart.getItems().entrySet()) {
            Item item = entry.getKey();
            int qty = entry.getValue();
            if (item.needsShipping()) {
                double totalWeight = item.getWeightGrams() * qty;
                System.out.println(qty + "x " + item.getName() + " " + (int)totalWeight + "g");
            }
        }
        System.out.printf("Total package weight %.1fkg\n", totalWeightKg);

        // Receipt
        System.out.println("** Checkout receipt **");
        for (Map.Entry<Item, Integer> entry : cart.getItems().entrySet()) {
            Item item = entry.getKey();
            int qty = entry.getValue();
            System.out.println(qty + "x " + item.getName() + " " + (int)(item.getPrice() * qty));
        }
        System.out.println("----------------------");
        System.out.println("Subtotal " + (int)subtotal);
        System.out.println("Shipping " + (int)shippingCost);
        System.out.println("Amount " + (int)total);
        System.out.println("Balance left " + (int)customer.getBalance());
    }
}
