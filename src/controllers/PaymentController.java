package controllers;

import models.Payment;
import utils.DBConnection;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class PaymentController {

    private LocalDate paymentDate = LocalDate.now();

    // Method to search for a user
    public Payment searchUser(String name) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                throw new Exception("Failed to establish database connection.");
            }

            String sql = "SELECT u.first_name, p.name AS package_name, p.duration, p.price " +
                    "FROM users u " +
                    "JOIN packages p ON u.package_id = p.id " +
                    "WHERE CONCAT(u.first_name) LIKE ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String packageName = rs.getString("package_name");
                int duration = rs.getInt("duration");
                double price = rs.getDouble("price");

                // Calculate valid date as payment date + package duration
                LocalDate validDate = paymentDate.plusDays(duration);

                return new Payment(firstName, null, packageName, validDate, price); // Set coach as null
            } else {
                return null; // No user found
            }
        } catch (Exception e) {
            throw new Exception("Error searching user: " + e.getMessage());
        }
    }

    // Confirm payment
    public void confirmPayment(Payment payment) {
        System.out.println("Payment confirmed for: " + payment.getFirstName());
    }

    // Method to print receipt
    public void printReceipt(Payment payment) {
        String receipt = createReceiptText(payment);
        sendToPrinter(receipt);
    }

    // Create receipt text in a structured format
    private String createReceiptText(Payment payment) {
        StringBuilder receiptBuilder = new StringBuilder();

        // Ensure lines do not exceed 32 characters for better alignment
        receiptBuilder.append("--------------------------------\n");
        receiptBuilder.append("        Turbo Fitness Studio\n");
        receiptBuilder.append("      No. 88/1, Mahahunupitiya,\n");
        receiptBuilder.append("           Negombo, SL\n");
        receiptBuilder.append("       Tel: +94-76-699-3040\n");
        receiptBuilder.append("--------------------------------\n");
        receiptBuilder.append("Date: ").append(LocalDate.now()).append("\n");
        receiptBuilder.append("Time: ").append(java.time.LocalTime.now()).append("\n");
        receiptBuilder.append("\n");
        receiptBuilder.append("Member Name: ").append(payment.getFirstName()).append("\n");
        receiptBuilder.append("Package: ").append(payment.getPackageName()).append("\n");
        receiptBuilder.append("Valid Date: ").append(payment.getValidDate()).append("\n");
        receiptBuilder.append("Price: Rs. ").append(String.format("%.2f", payment.getPrice())).append("\n");
        receiptBuilder.append("--------------------------------\n");
        receiptBuilder.append("  Thank you for your payment!\n");
        receiptBuilder.append("We look forward to seeing you soon!\n");
        receiptBuilder.append("--------------------------------\n");

        return receiptBuilder.toString();
    }




    // Method to send receipt to the printer and show the print dialog
    private void sendToPrinter(String receipt) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();

        // Define how to print the receipt content
        Printable printable = new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                // Cast Graphics to Graphics2D for better control
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Split the receipt text into lines and print each line
                String[] lines = receipt.split("\n");
                int y = 20; // Starting y position
                for (String line : lines) {
                    g2d.drawString(line, 10, y);
                    y += 15; // Move down for the next line
                }

                return PAGE_EXISTS;
            }
        };

        printerJob.setPrintable(printable);

        // Open the print dialog so the user can select the printer and print
        if (printerJob.printDialog()) {
            try {
                // Print the receipt
                printerJob.print();
                System.out.println("Receipt printed successfully.");
            } catch (PrinterException e) {
                e.printStackTrace();
                System.out.println("Error printing receipt: " + e.getMessage());
            }
        } else {
            System.out.println("Print job was cancelled.");
        }
    }
}
