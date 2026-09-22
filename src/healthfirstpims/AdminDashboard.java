package healthfirstpims;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private String username;
    private String fullName;

    public AdminDashboard(String username, String fullName) {

        this.username = username;
        this.fullName = fullName;

        setTitle("Sthe's HealthFirst Pharmacy - Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        JLabel titleLabel =
                new JLabel(
                        "Sthe's HealthFirst Pharmacy - Admin Dashboard",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 10, 15, 10
                )
        );

        JLabel welcomeLabel =
                new JLabel(
                        "Welcome, " + fullName,
                        SwingConstants.CENTER
                );

        welcomeLabel.setFont(
                new Font("Arial", Font.PLAIN, 18)
        );

        JPanel headingPanel =
                new JPanel(new GridLayout(2, 1));

        headingPanel.add(titleLabel);
        headingPanel.add(welcomeLabel);

        JButton medicinesButton =
                new JButton("Manage Medicines");

        JButton suppliersButton =
                new JButton("Manage Suppliers");

        JButton usersButton =
                new JButton("Manage Users");

        JButton salesButton =
                new JButton("Sales Report");

        JButton itemWiseButton =
                new JButton("Item-Wise Report");

        JButton lowStockButton =
                new JButton("Low Stock Report");

        JButton expiryButton =
                new JButton("Expiry Report");

        JButton logoutButton =
                new JButton("Logout");

        JButton[] buttons = {
            medicinesButton,
            suppliersButton,
            usersButton,
            salesButton,
            itemWiseButton,
            lowStockButton,
            expiryButton,
            logoutButton
        };

        for (JButton button : buttons) {

            button.setFont(
                    new Font("Arial", Font.BOLD, 15)
            );
        }

        JPanel buttonPanel =
                new JPanel(new GridLayout(4, 2, 15, 15));

        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 60, 30, 60
                )
        );

        buttonPanel.add(medicinesButton);
        buttonPanel.add(suppliersButton);
        buttonPanel.add(usersButton);
        buttonPanel.add(salesButton);
        buttonPanel.add(itemWiseButton);
        buttonPanel.add(lowStockButton);
        buttonPanel.add(expiryButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(
                headingPanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                buttonPanel,
                BorderLayout.CENTER
        );

        add(mainPanel);

        medicinesButton.addActionListener(e ->
                new ManageMedicines().setVisible(true)
        );

        suppliersButton.addActionListener(e ->
                new ManageSuppliers().setVisible(true)
        );

        usersButton.addActionListener(e ->
                new ManageUsers().setVisible(true)
        );

        salesButton.addActionListener(e ->
                new SalesReport().setVisible(true)
        );

        itemWiseButton.addActionListener(e ->
                new ItemWiseReport().setVisible(true)
        );

        lowStockButton.addActionListener(e ->
                new LowStockReport().setVisible(true)
        );

        expiryButton.addActionListener(e ->
                new ExpiryReport().setVisible(true)
        );

        logoutButton.addActionListener(e ->
                logout()
        );
    }

    private void logout() {

        dispose();

        new LoginFrame().setVisible(true);
    }
}