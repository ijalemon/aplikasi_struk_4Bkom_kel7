import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.regex.Pattern;

public class RegisterForm extends JFrame {
    private final JTextField usernameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JButton togglePasswordButton;

    public RegisterForm() {
        setTitle("Buat Akun Admin");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel fullscreen
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        // Panel transparan modern di tengah
        JPanel formPanel = new JPanel();
        formPanel.setPreferredSize(new Dimension(500, 600));
        formPanel.setBackground(new Color(255, 255, 255, 150)); // transparan putih
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        formPanel.setOpaque(false);

        // Rounded background (custom panel)
        formPanel = new RoundedPanel(30);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(30, 30, 30, 190));

        JLabel titleLabel = new JLabel(" Buat Akun Admin");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Field setup
        usernameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();
        decorateField(usernameField, "👤 Username");
        decorateField(emailField, "📧 Email");
        decorateField(passwordField, "🔑 Password");

        // Show/hide password button
        togglePasswordButton = new JButton("👁️");
        togglePasswordButton.setPreferredSize(new Dimension(50, 50));
        togglePasswordButton.setBackground(Color.DARK_GRAY);
        togglePasswordButton.setForeground(Color.WHITE);
        togglePasswordButton.setFocusPainted(false);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);

        togglePasswordButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == 0) {
                passwordField.setEchoChar('•');
                togglePasswordButton.setText("👁️");
            } else {
                passwordField.setEchoChar((char) 0);
                togglePasswordButton.setText("🙈");
            }
        });

        JButton registerButton = new JButton("✅ Daftar");
        JButton cancelButton = new JButton("❌ Batal");
        styleButton(registerButton, new Color(0x4CAF50));
        styleButton(cancelButton, new Color(0xF44336));

        formPanel.add(titleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(emailField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        formPanel.add(registerButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(cancelButton);

        registerButton.addActionListener(e -> registerUser());
        cancelButton.addActionListener(e -> dispose());

        backgroundPanel.add(formPanel, new GridBagConstraints());
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

    private void registerUser() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Format email tidak valid!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password minimal 8 karakter!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO admin (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Akun berhasil dibuat!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Username atau Email sudah digunakan!", "Gagal", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat registrasi.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(regex).matcher(email).matches();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegisterForm form = new RegisterForm();
            form.setVisible(true);
        });
    }
}

class RoundedPanel extends JPanel {
    private int cornerRadius;

    public RoundedPanel(int radius) {
        super();
        this.cornerRadius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcs.width, arcs.height);
    }
}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel() {
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/images/logo1.jpg")).getImage();
        } catch (Exception e) {
            System.err.println("Background gagal dimuat: " + e.getMessage());
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