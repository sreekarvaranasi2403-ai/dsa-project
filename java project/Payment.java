import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Payment {
    private final String paymentId;
    private final String method;
    private final double amount;
    private final LocalDateTime paidAt;

    public Payment(String method, double amount) {
        this.paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.method = method;
        this.amount = amount;
        this.paidAt = LocalDateTime.now();
    }

    public String getMethod() {
        return method;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return paymentId + " | Method: " + method + " | Amount: Rs " + String.format("%.2f", amount)
                + " | Time: " + paidAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
