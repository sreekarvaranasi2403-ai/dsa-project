import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class CarePlusPharmacyMain {
    private final Scanner scanner = new Scanner(System.in);

    // Login/Signup store using HashMap (username -> account).
    private final Map<String, UserAccount> users = new HashMap<>();
    private UserAccount loggedInUser;
    private final Map<String, Double> medicinePriceMap = new HashMap<>();
    private final Random random = new Random();

    // Reminder storage using LinkedList + Deque + Circular Queue.
    private final LinkedList<Reminder> reminders = new LinkedList<>();
    private final Deque<Reminder> reminderDeque = new ArrayDeque<>();
    private final CircularReminderQueue recurringReminderQueue = new CircularReminderQueue(20);

    // Delivery storage using Queue + PriorityQueue.
    private final Queue<DeliveryRequest> normalDeliveryQueue = new LinkedList<>();
    private final PriorityQueue<DeliveryRequest> urgentDeliveryQueue = new PriorityQueue<>(
            Comparator.comparing(DeliveryRequest::getBookedAt)
    );

    // Payment history and emergency action logs.
    private final Stack<Payment> paymentHistory = new Stack<>();
    private final Stack<String> emergencyActionStack = new Stack<>();

    public static void main(String[] args) {
        CarePlusPharmacyMain app = new CarePlusPharmacyMain();
        app.seedDefaultUsers();
        app.start();
    }

    private void start() {
        while (true) {
            if (loggedInUser == null) {
                if (!authMenu()) {
                    System.out.println("Thank you for using CarePlus Pharmacy. Stay healthy.");
                    return;
                }
            } else {
                userMenu();
            }
        }
    }

    private boolean authMenu() {
        System.out.println("\n==================================================");
        System.out.println("CarePlus Pharmacy - Your Health, Our Priority");
        System.out.println("==================================================");
        System.out.println("1. Signup");
        System.out.println("2. User Login");
        System.out.println("3. Admin Login");
        System.out.println("4. Exit");

        int choice = readInt("Enter your choice: ");
        switch (choice) {
            case 1:
                signupUser();
                return true;
            case 2:
                loginUserOnly();
                return true;
            case 3:
                loginAdminOnly();
                return true;
            case 4:
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    private void signupUser() {
        String username = readLine("Choose username: ").trim();
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }
        String key = username.toLowerCase(Locale.ROOT);
        if (users.containsKey(key)) {
            System.out.println("Username already exists. Please login.");
            return;
        }

        String password = readLine("Choose password: ").trim();
        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        users.put(key, new UserAccount(username, password, "USER"));
        System.out.println("Signup successful. You can login now.");
    }

    private void loginUserOnly() {
        String username = readLine("Enter username: ").trim();
        String password = readLine("Enter password: ").trim();

        UserAccount account = users.get(username.toLowerCase(Locale.ROOT));
        if (account == null || !account.getPassword().equals(password)) {
            System.out.println("Invalid login credentials.");
            return;
        }
        if (account.isAdmin()) {
            System.out.println("This is an admin account. Please use Admin Login.");
            return;
        }

        loggedInUser = account;
        System.out.println("Login successful. Welcome, " + loggedInUser.getUsername()
                + " (" + loggedInUser.getRole() + ").");
    }

    private void loginAdminOnly() {
        String username = readLine("Enter admin username: ").trim();
        String password = readLine("Enter admin password: ").trim();

        UserAccount account = users.get(username.toLowerCase(Locale.ROOT));
        if (account == null || !account.getPassword().equals(password) || !account.isAdmin()) {
            System.out.println("Invalid admin credentials.");
            return;
        }

        loggedInUser = account;
        System.out.println("Admin login successful. Welcome to Admin Page, " + loggedInUser.getUsername() + ".");
    }

    private void logoutUser() {
        System.out.println("Logged out from user: " + loggedInUser.getUsername());
        loggedInUser = null;
    }

    private void userMenu() {
        if (loggedInUser.isAdmin()) {
            printAdminMenu();
            int choice = readInt("Enter your choice: ");
            handleAdminChoice(choice);
        } else {
            printUserMenu();
            int choice = readInt("Enter your choice: ");
            handleUserChoice(choice);
        }
    }

    private void printAdminMenu() {
        System.out.println("\n==================================================");
        System.out.println("CarePlus Pharmacy - Main Menu (Admin)");
        System.out.println("==================================================");
        System.out.println("1. Add Medication Reminder");
        System.out.println("2. View/Remove Reminders");
        System.out.println("3. Book Medicine Delivery + Make Payment");
        System.out.println("4. View Delivery Queue (Admin)");
        System.out.println("5. View/Undo Payment History");
        System.out.println("6. Emergency Help");
        System.out.println("7. Logout");
        System.out.println("8. Exit");
    }

    private void printUserMenu() {
        System.out.println("\n==================================================");
        System.out.println("CarePlus Pharmacy - Main Menu (User)");
        System.out.println("==================================================");
        System.out.println("1. Add Medication Reminder");
        System.out.println("2. View/Remove Reminders");
        System.out.println("3. Book Medicine Delivery + Make Payment");
        System.out.println("4. View/Undo Payment History");
        System.out.println("5. Emergency Help");
        System.out.println("6. Logout");
        System.out.println("7. Exit");
    }

    private void handleAdminChoice(int choice) {
        switch (choice) {
            case 1:
                addMedicationReminder();
                break;
            case 2:
                reminderOperationsMenu();
                break;
            case 3:
                deliveryBookingMenu();
                break;
            case 4:
                viewDeliveryQueue();
                break;
            case 5:
                paymentMenu();
                break;
            case 6:
                emergencyHelp();
                break;
            case 7:
                logoutUser();
                break;
            case 8:
                System.out.println("Thank you for using CarePlus Pharmacy. Stay healthy.");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void handleUserChoice(int choice) {
        switch (choice) {
            case 1:
                addMedicationReminder();
                break;
            case 2:
                reminderOperationsMenu();
                break;
            case 3:
                deliveryBookingMenu();
                break;
            case 4:
                paymentMenu();
                break;
            case 5:
                emergencyHelp();
                break;
            case 6:
                logoutUser();
                break;
            case 7:
                System.out.println("Thank you for using CarePlus Pharmacy. Stay healthy.");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // ------------------------ REMINDER MODULE ------------------------
    private void addMedicationReminder() {
        String medicineName = readLine("Enter medicine name: ");
        String time = readLine("Enter time (e.g., 08:00 AM): ");
        String dose = readLine("Enter dose (e.g., 1 tablet): ");
        String frequency = readLine("Enter frequency (Daily/Weekly/etc): ");

        Reminder reminder = new Reminder(medicineName, time, dose, frequency);
        reminders.add(reminder);
        reminderDeque.addLast(reminder);
        recurringReminderQueue.enqueue(reminder);

        System.out.println("Reminder added successfully.");
    }

    private void reminderOperationsMenu() {
        System.out.println("\nReminder Module");
        System.out.println("1. Display All Reminders");
        System.out.println("2. Remove Reminder");
        System.out.println("3. Mark Reminder as Completed");
        System.out.println("4. Show Recurring Reminders (Circular Queue)");
        int choice = readInt("Choose option: ");

        switch (choice) {
            case 1:
                displayReminders();
                break;
            case 2:
                removeReminder();
                break;
            case 3:
                markReminderCompleted();
                break;
            case 4:
                showRecurringReminders();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void displayReminders() {
        if (reminders.isEmpty()) {
            System.out.println("No reminders available.");
            return;
        }

        List<Reminder> sorted = new ArrayList<>(reminders);
        sorted.sort(Comparator.comparing(Reminder::getTime, String.CASE_INSENSITIVE_ORDER));
        System.out.println("\nAll Reminders:");
        for (int i = 0; i < sorted.size(); i++) {
            System.out.println((i + 1) + ". " + sorted.get(i));
        }
    }

    private void removeReminder() {
        if (reminders.isEmpty()) {
            System.out.println("No reminders to remove.");
            return;
        }
        displayReminders();
        int idx = readInt("Enter reminder number to remove: ") - 1;
        if (idx < 0 || idx >= reminders.size()) {
            System.out.println("Invalid reminder number.");
            return;
        }
        Reminder removed = reminders.remove(idx);
        reminderDeque.remove(removed);
        System.out.println("Reminder removed.");
    }

    private void markReminderCompleted() {
        if (reminders.isEmpty()) {
            System.out.println("No reminders available.");
            return;
        }
        displayReminders();
        int idx = readInt("Enter reminder number to mark complete: ") - 1;
        if (idx < 0 || idx >= reminders.size()) {
            System.out.println("Invalid reminder number.");
            return;
        }
        Reminder reminder = reminders.get(idx);
        reminder.markCompleted();
        System.out.println("Reminder marked as completed.");
    }

    private void showRecurringReminders() {
        List<Reminder> recurring = recurringReminderQueue.getAll();
        if (recurring.isEmpty()) {
            System.out.println("No recurring reminders in circular queue.");
            return;
        }
        System.out.println("Recurring reminder cycle:");
        for (int i = 0; i < recurring.size(); i++) {
            System.out.println((i + 1) + ". " + recurring.get(i));
        }
    }

    // ------------------------ DELIVERY MODULE ------------------------
    private void deliveryBookingMenu() {
        System.out.println("\nDelivery Module");
        System.out.println("1. Book Delivery");
        System.out.println("2. Cancel Delivery");
        System.out.println("3. Process Next Delivery");
        int choice = readInt("Choose option: ");

        switch (choice) {
            case 1:
                bookDelivery();
                break;
            case 2:
                cancelDelivery();
                break;
            case 3:
                processNextDelivery();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void bookDelivery() {
        String customerName = readLine("Enter patient/customer name: ");
        String medicineName = readLine("Enter medicine name: ");
        int quantity = readInt("Enter quantity: ");
        String address = readLine("Enter address: ");
        String phone = readLine("Enter phone number: ");
        String deliveryType = readLine("Delivery type (Normal/Urgent): ");

        Patient patient = new Patient(customerName, address, phone);
        boolean urgent = deliveryType.equalsIgnoreCase("Urgent");
        DeliveryRequest request = new DeliveryRequest(patient, medicineName, quantity, urgent, loggedInUser.getUsername());

        if (urgent) {
            urgentDeliveryQueue.offer(request);
        } else {
            normalDeliveryQueue.offer(request);
        }
        System.out.println("Delivery booked successfully. Request ID: " + request.getRequestId());

        // Integrated flow: payment right after booking.
        processBookingPayment(medicineName, quantity);
    }

    private void viewDeliveryQueue() {
        if (!loggedInUser.isAdmin()) {
            System.out.println("Access denied. Only admin can view delivery queue.");
            return;
        }

        System.out.println("\nUrgent Deliveries (PriorityQueue):");
        if (urgentDeliveryQueue.isEmpty()) {
            System.out.println("No urgent deliveries.");
        } else {
            List<DeliveryRequest> urgentList = new ArrayList<>(urgentDeliveryQueue);
            urgentList.sort(Comparator.comparing(DeliveryRequest::getBookedAt));
            for (DeliveryRequest req : urgentList) {
                System.out.println(req);
            }
        }

        System.out.println("\nNormal Deliveries (Queue):");
        if (normalDeliveryQueue.isEmpty()) {
            System.out.println("No normal deliveries.");
        } else {
            for (DeliveryRequest req : normalDeliveryQueue) {
                System.out.println(req);
            }
        }
    }

    private void cancelDelivery() {
        int id = readInt("Enter request ID to cancel: ");
        DeliveryRequest removedUrgent = removeByIdFromPriorityQueue(urgentDeliveryQueue, id);
        if (removedUrgent != null) {
            System.out.println("Urgent delivery cancelled.");
            return;
        }

        DeliveryRequest removedNormal = removeByIdFromQueue(normalDeliveryQueue, id);
        if (removedNormal != null) {
            System.out.println("Normal delivery cancelled.");
            return;
        }

        System.out.println("Delivery request not found.");
    }

    private DeliveryRequest removeByIdFromPriorityQueue(PriorityQueue<DeliveryRequest> pq, int id) {
        List<DeliveryRequest> temp = new ArrayList<>();
        DeliveryRequest removed = null;
        while (!pq.isEmpty()) {
            DeliveryRequest request = pq.poll();
            if (request.getRequestId() == id && removed == null) {
                removed = request;
            } else {
                temp.add(request);
            }
        }
        pq.addAll(temp);
        return removed;
    }

    private DeliveryRequest removeByIdFromQueue(Queue<DeliveryRequest> queue, int id) {
        int n = queue.size();
        DeliveryRequest removed = null;
        for (int i = 0; i < n; i++) {
            DeliveryRequest request = queue.poll();
            if (request.getRequestId() == id && removed == null) {
                removed = request;
            } else {
                queue.offer(request);
            }
        }
        return removed;
    }

    private void processNextDelivery() {
        DeliveryRequest next = null;
        if (!urgentDeliveryQueue.isEmpty()) {
            next = urgentDeliveryQueue.poll();
            System.out.println("Processed urgent delivery:");
        } else if (!normalDeliveryQueue.isEmpty()) {
            next = normalDeliveryQueue.poll();
            System.out.println("Processed normal delivery:");
        }

        if (next == null) {
            System.out.println("No delivery requests to process.");
        } else {
            System.out.println(next);
        }
    }

    // ------------------------ PAYMENT MODULE ------------------------
    private void paymentMenu() {
        System.out.println("\nPayment History");
        System.out.println("1. View Payment History");
        System.out.println("2. Undo Last Payment");
        int choice = readInt("Choose option: ");

        if (choice == 1) {
            viewPaymentHistory();
        } else if (choice == 2) {
            undoLastPayment();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void processBookingPayment(String medicineName, int quantity) {
        double unitPrice = getMedicinePrice(medicineName);
        double suggestedAmount = unitPrice * quantity;
        System.out.println("\nMake Payment for this booking");
        System.out.println("Payment details: " + medicineName + " - Rs " + String.format("%.2f", unitPrice)
                + " x " + quantity + " = Rs " + String.format("%.2f", suggestedAmount));
        double amount = readDouble("Enter payment amount (Suggested Rs " + String.format("%.2f", suggestedAmount) + "): ");
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero. Booking saved, payment pending.");
            return;
        }
        makePayment(amount);
    }

    private double getMedicinePrice(String medicineName) {
        String key = medicineName.toLowerCase(Locale.ROOT).trim();
        if (medicinePriceMap.containsKey(key)) {
            return medicinePriceMap.get(key);
        }
        // For unknown medicines, assign a sample price (random) and reuse it next time.
        double generatedPrice = 30 + random.nextInt(171); // Rs 30 to Rs 200
        medicinePriceMap.put(key, generatedPrice);
        return generatedPrice;
    }

    private void makePayment(double amount) {
        System.out.println("Choose payment method:");
        System.out.println("1. Cash on Delivery");
        System.out.println("2. UPI");
        System.out.println("3. Card");
        int methodChoice = readInt("Enter option: ");

        String method;
        switch (methodChoice) {
            case 1:
                method = "Cash on Delivery";
                break;
            case 2:
                method = "UPI";
                String upiId = readLine("Enter UPI ID (e.g., name@upi): ").trim();
                while (!isValidUpiId(upiId)) {
                    System.out.println("Invalid UPI ID format.");
                    upiId = readLine("Enter valid UPI ID: ").trim();
                }
                System.out.println("UPI ID accepted: " + upiId);
                break;
            case 3:
                method = "Card";
                String cardHolderName = readLine("Enter card holder name: ").trim();
                while (cardHolderName.isEmpty()) {
                    System.out.println("Card holder name cannot be empty.");
                    cardHolderName = readLine("Enter card holder name: ").trim();
                }

                String cardNumber = readLine("Enter card number (16 digits): ").replaceAll("\\s+", "");
                while (!cardNumber.matches("\\d{16}")) {
                    System.out.println("Invalid card number.");
                    cardNumber = readLine("Enter card number (16 digits): ").replaceAll("\\s+", "");
                }

                String expiry = readLine("Enter expiry (MM/YY): ").trim();
                while (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                    System.out.println("Invalid expiry format.");
                    expiry = readLine("Enter expiry (MM/YY): ").trim();
                }

                String cvv = readLine("Enter CVV (3 digits): ").trim();
                while (!cvv.matches("\\d{3}")) {
                    System.out.println("Invalid CVV.");
                    cvv = readLine("Enter CVV (3 digits): ").trim();
                }

                String maskedNumber = "************" + cardNumber.substring(12);
                System.out.println("Card accepted: " + cardHolderName + " | " + maskedNumber + " | Exp: " + expiry);
                break;
            default:
                System.out.println("Invalid payment method.");
                return;
        }

        Payment payment = new Payment(method, amount);
        paymentHistory.push(payment);

        System.out.println("Payment successful.");
        System.out.println("Confirmation: " + payment);
    }

    private boolean isValidUpiId(String upiId) {
        return upiId.matches("[a-zA-Z0-9._-]+@[a-zA-Z]{2,}");
    }

    private void viewPaymentHistory() {
        if (paymentHistory.isEmpty()) {
            System.out.println("No payment history available.");
            return;
        }
        System.out.println("Payment History (most recent first):");
        for (int i = paymentHistory.size() - 1; i >= 0; i--) {
            System.out.println(paymentHistory.get(i));
        }
    }

    private void undoLastPayment() {
        if (paymentHistory.isEmpty()) {
            System.out.println("No payment to undo.");
            return;
        }
        Payment removed = paymentHistory.pop();
        System.out.println("Last payment removed: " + removed);
    }

    // ------------------------ EMERGENCY MODULE ------------------------
    private void emergencyHelp() {
        System.out.println("\nEmergency Help");
        System.out.println("Pharmacy Contact Number: +91-1800-111-222");
        System.out.println("Working Hours: 08:00 AM - 10:00 PM");
        System.out.println("Emergency Message: For severe symptoms, contact nearest hospital immediately.");

        emergencyActionStack.push("Emergency Help Accessed");
    }

    private void seedDefaultUsers() {
        users.put("admin", new UserAccount("admin", "admin123", "ADMIN"));
        medicinePriceMap.put("aspirin", 45.0);
        medicinePriceMap.put("paracetamol", 35.0);
        medicinePriceMap.put("metformin", 120.0);
        medicinePriceMap.put("amoxicillin", 180.0);
        medicinePriceMap.put("atorvastatin", 210.0);
    }

    // ------------------------ INPUT HELPERS ------------------------
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
