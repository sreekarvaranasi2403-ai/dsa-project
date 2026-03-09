import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DeliveryRequest {
    private static int nextId = 1001;

    private final int requestId;
    private final Patient patient;
    private final String medicineName;
    private final int quantity;
    private final boolean urgent;
    private final LocalDateTime bookedAt;
    private final String bookedByUsername;

    public DeliveryRequest(Patient patient, String medicineName, int quantity, boolean urgent, String bookedByUsername) {
        this.requestId = nextId++;
        this.patient = patient;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.urgent = urgent;
        this.bookedAt = LocalDateTime.now();
        this.bookedByUsername = bookedByUsername;
    }

    public int getRequestId() {
        return requestId;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public String getBookedByUsername() {
        return bookedByUsername;
    }

    @Override
    public String toString() {
        return "ID: " + requestId
                + " | " + (urgent ? "URGENT" : "NORMAL")
                + " | Patient: " + patient.getName()
                + " | Medicine: " + medicineName
                + " | Qty: " + quantity
                + " | Phone: " + patient.getPhoneNumber()
                + " | Address: " + patient.getAddress()
                + " | Sent By User: " + bookedByUsername
                + " | Booked: " + bookedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
