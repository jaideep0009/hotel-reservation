package model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String type; // e.g., "Single", "Double", "Suite"
    private double pricePerNight;
    private boolean available;

    public Room(String type, double pricePerNight) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.available = true;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("%s | %s | ₹%.2f | %s", id.substring(0,8), type, pricePerNight, available ? "Available" : "Booked");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
