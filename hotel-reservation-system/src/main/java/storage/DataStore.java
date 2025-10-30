package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Customer;
import model.Reservation;
import model.Room;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based persistence using JSON (Gson).
 * Each list is saved to a separate JSON file inside /data folder.
 */
public class DataStore {
    private static final String DATA_DIR = "data/";
    private static final String ROOMS_FILE = DATA_DIR + "rooms.json";
    private static final String CUSTOMERS_FILE = DATA_DIR + "customers.json";
    private static final String RESERVATIONS_FILE = DATA_DIR + "reservations.json";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs(); // Create data folder if missing
    }

    // ---------- Load ----------
    public static List<Room> loadRooms() {
        return readList(ROOMS_FILE, new TypeToken<List<Room>>() {}.getType());
    }

    public static List<Customer> loadCustomers() {
        return readList(CUSTOMERS_FILE, new TypeToken<List<Customer>>() {}.getType());
    }

    public static List<Reservation> loadReservations() {
        return readList(RESERVATIONS_FILE, new TypeToken<List<Reservation>>() {}.getType());
    }

    // ---------- Save ----------
    public static void saveRooms(List<Room> rooms) {
        writeList(ROOMS_FILE, rooms);
    }

    public static void saveCustomers(List<Customer> customers) {
        writeList(CUSTOMERS_FILE, customers);
    }

    public static void saveReservations(List<Reservation> reservations) {
        writeList(RESERVATIONS_FILE, reservations);
    }

    // ---------- Helper methods ----------
    private static <T> List<T> readList(String path, Type type) {
        try (Reader reader = new FileReader(path)) {
            List<T> list = gson.fromJson(reader, type);
            return list != null ? list : new ArrayList<>();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static <T> void writeList(String path, List<T> list) {
        try (Writer writer = new FileWriter(path)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
