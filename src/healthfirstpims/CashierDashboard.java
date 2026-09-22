package healthfirstpims;

import javax.swing.*;
import java.awt.*;

public class CashierDashboard extends JFrame {

    private String username;
    private String fullName;

    public CashierDashboard(String username, String fullName) {

        this.username = username;
        this.fullName = fullName;

        setTitle("HealthFirst Pharmacy - Cashier Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(
                "HealthFirst Pharmacy - Cashier Dashboard",
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

        JLabel welcomeLabel = new JLabel(
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

        JButton posButton =
                new JButton("Point of Sale");

        JButton stockButton =
                new JButton("Check Stock");

        JButton logoutButton =
                new JButton("Logout");

        posButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        stockButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        logoutButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        JPanel buttonPanel =
                new JPanel(new GridLayout(3, 1, 15, 15));

        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        50, 200, 50, 200
                )
        );

        buttonPanel.add(posButton);
        buttonPanel.add(stockButton);
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

        posButton.addActionListener(e ->
                new PointOfSale(
                        username,
                        fullName
                ).setVisible(true)
        );

        stockButton.addActionListener(e ->
                new CheckStock().setVisible(true)
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