import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;

public class ResetPasswordForm extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JButton cancelButton;

    public ResetPasswordForm() {
        setTitle("Reset Password");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Background panel dengan gambar
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Panel kiri: informasi
        JPanel leftPanel = createLeftPanel();

        // Panel kanan: form reset password
        JPanel rightPanel = createRightPanel();

        // Split pane untuk kiri dan kanan
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0);
        splitPane.setDividerSize(0);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        backgroundPanel.add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 30, 60, 30));

        JLabel welcomeLabel = new JLabel("🔒 Reset Password");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descArea = new JTextArea(
                "Masukkan username, email, dan password baru Anda untuk mengatur ulang akun Anda."
        );
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        descArea.setForeground(Color.LIGHT_GRAY);
        descArea.setBackground(new Color(0, 0, 0, 0));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setFocusable(false);
        descArea.setHighlighter(null);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(500, 120));

        panel.add(Box.createVerticalGlue());
        panel.add(welcomeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        panel.add(descArea);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Username
        usernameField = new JTextField();
        decorateField(usernameField, "Username");
        JLabel usernameLabel = new JLabel("👤 Username");
        styleLabel(usernameLabel);

        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        gbc.gridy++;
        panel.add(usernameField, gbc);

        // Email
        emailField = new JTextField();
        decorateField(emailField, "Email");
        JLabel emailLabel = new JLabel("📧 Email");
        styleLabel(emailLabel);

        gbc.gridy++;
        panel.add(emailLabel, gbc);

        gbc.gridy++;
        panel.add(emailField, gbc);

        // Password
        newPasswordField = new JPasswordField();
        decorateField(newPasswordField, "New Password");
        JLabel passwordLabel = new JLabel("🔑 New Password");
        styleLabel(passwordLabel);

        // Show/hide password button
        JButton toggleButton = new JButton("Show");
        styleToggleButton(toggleButton);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        passwordPanel.add(newPasswordField, BorderLayout.CENTER);
        passwordPanel.add(toggleButton, BorderLayout.EAST);

        toggleButton.addActionListener(e -> {
            if (newPasswordField.getEchoChar() == (char) 0) {
                newPasswordField.setEchoChar('•');
                toggleButton.setText("Show");
            } else {
                newPasswordField.setEchoChar((char) 0);
                toggleButton.setText("Hide");
            }
        });

        gbc.gridy++;
        panel.add(passwordLabel, gbc);

        gbc.gridy++;
        panel.add(passwordPanel, gbc);

        // Buttons
        JButton resetButton = new JButton("Reset Password");
        cancelButton = new JButton("Cancel");

        styleButton(resetButton, new Color(0x4CAF50));
        styleButton(cancelButton, new Color(0xF44336));

        gbc.gridy++;
        gbc.insets = new Insets(25, 10, 10, 10);
        panel.add(resetButton, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 20, 10);
        panel.add(cancelButton, gbc);

        // Event handlers
        resetButton.addActionListener(e -> resetPassword());
        cancelButton.addActionListener(e -> dispose());

        return panel;
    }

    private void styleLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
    }

    private void styleToggleButton(JButton button) {
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(60, 55));
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void resetPassword() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword()).trim();

        // Validasi input
        if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.matches("^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            JOptionPane.showMessageDialog(this, "Format email tidak valid!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isPasswordStrong(newPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Password harus memiliki setidaknya 8 karakter dan mengandung angka.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Interaksi database
        try (Connection conn = DatabaseUtil.getConnection()) {
            String checkSql = "SELECT * FROM admin WHERE username=? AND email=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String updateSql = "UPDATE admin SET password=? WHERE username=? AND email=?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, username);
                updateStmt.setString(3, email);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Password berhasil direset!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau email tidak ditemukan!", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat reset.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8 && password.matches(".\\d.");
    }

    private void decorateField(JTextField field, String placeholder) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        field.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                placeholder,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI Emoji", Font.PLAIN, 16),
                Color.DARK_GRAY
        ));
    }

    private void styleButton(JButton button, Color bg) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setFocusPainted(false);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
            }
        });
    }

    // Background Panel with image
    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = new ImageIcon(getClass().getResource("/images/logo1.jpg")).getImage();
            } catch (Exception e) {
                System.err.println("Failed to load background image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ResetPasswordForm form = new ResetPasswordForm();
            form.setVisible(true);
        });
    }
}