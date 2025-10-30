package ui;

import model.*;
import storage.DataStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {
    private List<Room> rooms;
    private List<Customer> customers;
    private List<Reservation> reservations;

    private DefaultListModel<Room> roomListModel = new DefaultListModel<>();
    private JList<Room> roomJList = new JList<>(roomListModel);
    private DefaultListModel<Reservation> reservationListModel = new DefaultListModel<>();
    private JList<Reservation> reservationJList = new JList<>(reservationListModel);

    public MainFrame() {
        loadAll();
        setTitle("Hotel Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900,600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Rooms", createRoomsPanel());
        tabs.add("Book Room", createBookingPanel());
        tabs.add("Reservations", createReservationsPanel());
        tabs.add("Admin", createAdminPanel());

        add(tabs);
    }

    private void loadAll(){
        rooms = DataStore.loadRooms();
        customers = DataStore.loadCustomers();
        reservations = DataStore.loadReservations();

        // If first run and no rooms, add sample rooms
        if (rooms.isEmpty()) {
            rooms.add(new Room("Single", 1500));
            rooms.add(new Room("Double", 2500));
            rooms.add(new Room("Suite", 5000));
            DataStore.saveRooms(rooms);
        }
        refreshModels();
    }

    private void refreshModels() {
        roomListModel.clear();
        for (Room r : rooms) roomListModel.addElement(r);
        reservationListModel.clear();
        for (Reservation res : reservations) reservationListModel.addElement(res);
    }

    private JPanel createRoomsPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBorder(new EmptyBorder(10,10,10,10));
        roomJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(roomJList);
        p.add(sp, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(5,5));
        JLabel info = new JLabel("<html><b>Available Rooms</b><br>Select a room to view details or book via the Book tab.</html>");
        right.add(info, BorderLayout.NORTH);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshModels());
        right.add(refresh, BorderLayout.SOUTH);

        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel createBookingPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;

        p.add(new JLabel("Select Room:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JComboBox<Room> cbRooms = new JComboBox<>();
        rooms.forEach(cbRooms::addItem);
        p.add(cbRooms, gbc);

        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField tfName = new JTextField(20);
        p.add(tfName, gbc);

        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        JTextField tfPhone = new JTextField(15);
        p.add(tfPhone, gbc);

        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField tfEmail = new JTextField(20);
        p.add(tfEmail, gbc);

        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("Check-in (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1;
        JTextField tfIn = new JTextField(12);
        p.add(tfIn, gbc);

        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("Check-out (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1;
        JTextField tfOut = new JTextField(12);
        p.add(tfOut, gbc);

        gbc.gridx = 1; gbc.gridy++;
        JButton btnBook = new JButton("Book Room");
        p.add(btnBook, gbc);

        btnBook.addActionListener(e -> {
            Room selected = (Room) cbRooms.getSelectedItem();
            if (selected == null) { JOptionPane.showMessageDialog(this, "No room selected."); return; }
            if (!selected.isAvailable()) { JOptionPane.showMessageDialog(this, "Selected room is not available."); return; }

            String name = tfName.getText().trim();
            String phone = tfPhone.getText().trim();
            String email = tfEmail.getText().trim();
            String inStr = tfIn.getText().trim();
            String outStr = tfOut.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || inStr.isEmpty() || outStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill required fields.");
                return;
            }

            try {
                LocalDate checkIn = LocalDate.parse(inStr);
                LocalDate checkOut = LocalDate.parse(outStr);
                if (!checkOut.isAfter(checkIn)) {
                    JOptionPane.showMessageDialog(this, "Check-out must be after check-in.");
                    return;
                }

                Customer c = new Customer(name, phone, email);
                customers.add(c);
                DataStore.saveCustomers(customers);

                Reservation res = new Reservation(selected.getId(), c.getId(), checkIn, checkOut);
                reservations.add(res);
                DataStore.saveReservations(reservations);

                selected.setAvailable(false);
                DataStore.saveRooms(rooms);

                refreshModels();
                // update combo box
                cbRooms.removeAllItems();
                rooms.forEach(cbRooms::addItem);

                JOptionPane.showMessageDialog(this, "Booking successful!\nReservation ID: " + res.getId().substring(0,8));
                // clear
                tfName.setText(""); tfPhone.setText(""); tfEmail.setText("");
                tfIn.setText(""); tfOut.setText("");
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Date format incorrect. Use yyyy-mm-dd.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error booking room: " + ex.getMessage());
            }
        });

        return p;
    }

    private JPanel createReservationsPanel() {
        JPanel p = new JPanel(new BorderLayout(6,6));
        p.setBorder(new EmptyBorder(10,10,10,10));
        reservationJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(reservationJList);
        p.add(sp, BorderLayout.CENTER);

        JPanel right = new JPanel(new GridLayout(4,1,6,6));
        JButton btnCancel = new JButton("Cancel Selected Reservation");
        btnCancel.addActionListener(e -> {
            Reservation sel = reservationJList.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a reservation to cancel."); return; }
            // mark room available
            rooms.stream().filter(r -> r.getId().equals(sel.getRoomId())).findFirst().ifPresent(r -> r.setAvailable(true));
            reservations.remove(sel);
            DataStore.saveReservations(reservations);
            DataStore.saveRooms(rooms);
            refreshModels();
            JOptionPane.showMessageDialog(this, "Reservation cancelled.");
        });

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshModels());

        right.add(btnCancel);
        right.add(btnRefresh);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel createAdminPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.gridx = 0; gbc.gridy = 0;

        p.add(new JLabel("Room Type:"), gbc);
        gbc.gridx = 1;
        JTextField tfType = new JTextField(15);
        p.add(tfType, gbc);

        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("Price per night (INR):"), gbc);
        gbc.gridx = 1;
        JTextField tfPrice = new JTextField(10);
        p.add(tfPrice, gbc);

        gbc.gridx = 1; gbc.gridy++;
        JButton btnAdd = new JButton("Add Room");
        p.add(btnAdd, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JButton btnRemove = new JButton("Remove Selected Room");
        p.add(btnRemove, gbc);

        // List of rooms for admin remove
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JList<Room> adminRoomList = new JList<>(roomListModel);
        adminRoomList.setVisibleRowCount(6);
        JScrollPane sp = new JScrollPane(adminRoomList);
        sp.setPreferredSize(new Dimension(400,150));
        p.add(sp, gbc);

        btnAdd.addActionListener(e -> {
            String type = tfType.getText().trim();
            String priceStr = tfPrice.getText().trim();
            if (type.isEmpty() || priceStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Fill fields"); return; }
            try {
                double price = Double.parseDouble(priceStr);
                Room r = new Room(type, price);
                rooms.add(r);
                DataStore.saveRooms(rooms);
                refreshModels();
                tfType.setText(""); tfPrice.setText("");
                JOptionPane.showMessageDialog(this, "Room added.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price must be a number.");
            }
        });

        btnRemove.addActionListener(e -> {
            Room sel = adminRoomList.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a room to remove."); return; }
            // prevent removing if booked
            if (!sel.isAvailable()) { JOptionPane.showMessageDialog(this, "Cannot remove a booked room."); return; }
            rooms.remove(sel);
            DataStore.saveRooms(rooms);
            refreshModels();
            JOptionPane.showMessageDialog(this, "Room removed.");
        });

        return p;
    }
}
