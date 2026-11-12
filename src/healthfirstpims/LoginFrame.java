package healthfirstpims;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("HealthFirst Pharmacy - Login");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Sthe's HealthFirst Pharmacy", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel subtitleLabel = new JLabel(
                "Pharmacy Inventory Management System",
                SwingConstants.CENTER
        );
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel titlePanel = new JPanel(new GridLayout(3, 1));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 21));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        JLabel brandCreditLabel = new JLabel("A project by NxG Technologies", SwingConstants.CENTER);
        brandCreditLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        titlePanel.add(brandCreditLabel);

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setToolTipText("NxG Technologies");
        logoLabel.getAccessibleContext().setAccessibleName("NxG Technologies logo");
        java.net.URL logoUrl = LoginFrame.class.getResource("nxg-logo.png");
        if (logoUrl != null) {
            ImageIcon logoSource = new ImageIcon(logoUrl);
            Image scaledLogo = logoSource.getImage().getScaledInstance(76, 76, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogo));
        }

        JPanel headingPanel = new JPanel(new BorderLayout(14, 0));
        headingPanel.add(logoLabel, BorderLayout.WEST);
        headingPanel.add(titlePanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        mainPanel.add(headingPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loginButton.addActionListener(e -> login());
        exitButton.addActionListener(e -> System.exit(0));

        getRootPane().setDefaultButton(loginButton);
    }

    private void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your username and password.",
                    "Login",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql = "SELECT role, full_name FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                String role = result.getString("role");
                String fullName = result.getString("full_name");

                JOptionPane.showMessageDialog(
                        this,
                        "Welcome, " + fullName + "!",
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dispose();

                if (role.equals("Admin")) {
                    new AdminDashboard(username, fullName).setVisible(true);
                } else {
                    new CashierDashboard(username, fullName).setVisible(true);
                }

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );

                passwordField.setText("");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to connect to the database:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}