import java.util.ArrayList;
import java.util.List;

public class CircularReminderQueue {
    private final Reminder[] data;
    private int front;
    private int rear;
    private int size;

    public CircularReminderQueue(int capacity) {
        data = new Reminder[capacity];
        front = 0;
        rear = -1;
        size = 0;
    }

    public boolean enqueue(Reminder reminder) {
        if (isFull()) {
            return false;
        }
        rear = (rear + 1) % data.length;
        data[rear] = reminder;
        size++;
        return true;
    }

    public Reminder dequeue() {
        if (isEmpty()) {
            return null;
        }
        Reminder reminder = data[front];
        data[front] = null;
        front = (front + 1) % data.length;
        size--;
        return reminder;
    }

    public List<Reminder> getAll() {
        List<Reminder> all = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int idx = (front + i) % data.length;
            all.add(data[idx]);
        }
        return all;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == data.length;
    }
}
