public class Reminder {
    private final String medicineName;
    private final String time;
    private final String dose;
    private final String frequency;
    private boolean completed;

    public Reminder(String medicineName, String time, String dose, String frequency) {
        this.medicineName = medicineName;
        this.time = time;
        this.dose = dose;
        this.frequency = frequency;
        this.completed = false;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getTime() {
        return time;
    }

    public String getDose() {
        return dose;
    }

    public String getFrequency() {
        return frequency;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted() {
        this.completed = true;
    }

    @Override
    public String toString() {
        return medicineName + " | Time: " + time + " | Dose: " + dose + " | Frequency: " + frequency
                + " | Status: " + (completed ? "Completed" : "Pending");
    }
}
