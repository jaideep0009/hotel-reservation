package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String roomId;
    private String customerId;
    private LocalDate checkIn;
    private LocalDate checkOut;

    public Reservation(String roomId, String customerId, LocalDate checkIn, LocalDate checkOut) {
        this.id = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.customerId = customerId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getId() { return id; }
    public String getRoomId() { return roomId; }
    public String getCustomerId() { return customerId; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }

    @Override
    public String toString() {
        return String.format("%s | Room:%s | Cust:%s | %s -> %s",
            id.substring(0,8), roomId.substring(0,8), customerId.substring(0,8),
            checkIn, checkOut);
    }
}
