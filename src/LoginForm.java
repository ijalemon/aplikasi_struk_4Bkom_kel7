import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Login Admin");
        setSize(900, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridLayout(1, 2));
        setContentPane(backgroundPanel);

        // Panel kiri
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        JLabel welcomeLabel = new JLabel("Selamat Datang");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>Silakan login dengan akun admin Anda untuk mengakses sistem kasir. Setelah login, Anda dapat memproses transaksi dan menghasilkan laporan.</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        descLabel.setForeground(Color.LIGHT_GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(welcomeLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        leftPanel.add(descLabel);
        leftPanel.add(Box.createVerticalGlue());

        // Panel kanan
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100));

        JLabel loginTitle = new JLabel("Login");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        loginTitle.setForeground(Color.WHITE);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        decorateField(usernameField, "👤 Username");

        passwordField = new JPasswordField();
        decorateField(passwordField, "🔒 Password");

        JButton toggleButton = new JButton("👁️");
        toggleButton.setFocusable(false);
        toggleButton.setPreferredSize(new Dimension(60, 55));
        toggleButton.setBackground(new Color(81, 81, 81));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        toggleButton.setBorder(new LineBorder(Color.GRAY));
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(toggleButton, BorderLayout.EAST);

        toggleButton.addActionListener((ActionEvent e) -> {
            if (passwordField.getEchoChar() == (char) 0) {
                passwordField.setEchoChar('•');
                toggleButton.setText("👁️");
            } else {
                passwordField.setEchoChar((char) 0);
                toggleButton.setText("❌");
            }
        });

        JButton loginButton = new JButton("🔓 Login");
        JButton forgotButton = new JButton("❓ Lupa Password");
        JButton registerButton = new JButton("➕ Buat Akun");
        JButton cancelButton = new JButton("❌ Keluar");

        styleButton(loginButton, new Color(0x2196F3));
        styleButton(forgotButton, new Color(0xFFC107));
        styleButton(registerButton, new Color(0x4CAF50));
        styleButton(cancelButton, new Color(0xF44336));

        rightPanel.add(loginTitle);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        rightPanel.add(usernameField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        rightPanel.add(passwordPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        rightPanel.add(loginButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(forgotButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(registerButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(cancelButton);

        loginButton.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
        cancelButton.addActionListener(e -> System.exit(0));
        forgotButton.addActionListener(e -> new ResetPasswordForm().setVisible(true));
        registerButton.addActionListener(e -> new RegisterForm().setVisible(true));

        backgroundPanel.add(leftPanel);
        backgroundPanel.add(rightPanel);
    }



    private void login() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String checkSql = "SELECT * FROM admin WHERE username=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, user);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int attempts = rs.getInt("login_attempt");
                boolean isBlocked = rs.getBoolean("is_blocked");

                if (isBlocked) {
                    JOptionPane.showMessageDialog(this, "Akun ini telah diblokir karena 3 kali gagal login.", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String dbPass = rs.getString("password");
                if (pass.equals(dbPass)) {
                    // Login sukses → reset login_attempt
                    String resetSql = "UPDATE admin SET login_attempt=0 WHERE username=?";
                    PreparedStatement resetStmt = conn.prepareStatement(resetSql);
                    resetStmt.setString(1, user);
                    resetStmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Login Berhasil!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new Main().setVisible(true);

                } else {
                    attempts++;
                    if (attempts >= 3) {
                        String blockSql = "UPDATE admin SET login_attempt=?, is_blocked=1 WHERE username=?";
                        PreparedStatement blockStmt = conn.prepareStatement(blockSql);
                        blockStmt.setInt(1, attempts);
                        blockStmt.setString(2, user);
                        blockStmt.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Akun telah diblokir setelah 3 kali gagal login!", "Diblokir", JOptionPane.ERROR_MESSAGE);
                    } else {
                        String updateSql = "UPDATE admin SET login_attempt=? WHERE username=?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setInt(1, attempts);
                        updateStmt.setString(2, user);
                        updateStmt.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Password salah! Percobaan ke-" + attempts + " dari 3.", "Login Gagal", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username tidak ditemukan!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Koneksi ke database gagal!", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        button.setFocusPainted(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }

    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = new ImageIcon(getClass().getResource("/images/logo1.jpg")).getImage();
            } catch (Exception e) {
                System.err.println("Gagal memuat gambar latar: " + e.getMessage());
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
}
