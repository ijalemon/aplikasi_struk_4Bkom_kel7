import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.*;



public class Main extends JFrame {

    private String[] kodeMakanan = {"M001", "M002", "M003", "M004", "M005", "M006", "M007", "M008", "M009", "M010", "M011", "M012", "M013"};
    private String[] makanan = {
            "Chicken Paha Bawah", "Chicken Sayap", "Chicken Paha Atas", "Chicken Dada", "Ayam Geprek Paha Bawah", "Ayam Geprek Sayap",
            "Ayam Geprek Paha Atas", "Ayam Geprek Dada", "Chicken Sayap + Nasi", "Chicken Dada + Nasi",
            "Geprek Sayap + Nasi", "Geprek Dada + Nasi", "Naget Ayam"
    };
    private double[] hargaMakanan = {8000, 8000, 10000, 10000, 10000, 10000, 12000, 12000, 12000, 14000, 14000, 16000, 7000};
    private int[] stokMakanan = new int[makanan.length];

    private String[] kodeMinuman = {"D001", "D002", "D003", "D004", "D005", "D006", "D007", "D008", "D009", "D010", "D011"};
    private String[] minuman = {
            "Jasmine Tea", "Teh Kampul", "Lychee Tea", "Strawberry Tea", "Es Teh Creamer",
            "Es Teh Susu", "Choco Tea", "Redvelvet Tea", "Chocolate Macchiato",
            "Redvelvet Macchiato", "Strawberry Macchiato"
    };
    private double[] hargaMinuman = {5000, 6000, 8000, 8000, 8000, 6000, 8000, 8000, 10000, 10000, 10000};
    private int[] stokMinuman = new int[minuman.length];

    private JComboBox<String> comboMakanan;
    private JComboBox<String> comboMinuman;
    private JTextField textJumlahMakanan, textJumlahMinuman, textDiskon, textTunai;
    private JButton btnCetakStruk, btnDataMenu, btnLogout;

    private final String FILE_STOK_MAKANAN = "stok_makanan.txt";
    private final String FILE_STOK_MINUMAN = "stok_minuman.txt";

    public Main() {
        super("Aplikasi Kasir");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 380);
        setLocationRelativeTo(null);

        bacaStokDariFile();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboMakanan = new JComboBox<>(makanan);
        comboMinuman = new JComboBox<>(minuman);
        textJumlahMakanan = new JTextField("0");
        textJumlahMinuman = new JTextField("0");
        textDiskon = new JTextField("0");
        textTunai = new JTextField();

        btnCetakStruk = new JButton("Cetak Struk");
        btnDataMenu = new JButton("Lihat Data Menu");
        btnLogout = new JButton("Logout");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        add(new JLabel("Pilih Makanan:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(comboMakanan, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        add(new JLabel("Jumlah Makanan:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(textJumlahMakanan, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        add(new JLabel("Pilih Minuman:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(comboMinuman, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        add(new JLabel("Jumlah Minuman:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(textJumlahMinuman, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        add(new JLabel("Diskon (%):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(textDiskon, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        add(new JLabel("Tunai:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(textTunai, gbc);

        gbc.gridy = 6;
        gbc.weightx = 0.33;

        gbc.gridx = 0;
        add(btnCetakStruk, gbc);

        gbc.gridx = 1;
        add(btnDataMenu, gbc);

        gbc.gridx = 2;
        add(btnLogout, gbc);


        btnCetakStruk.addActionListener(e -> cetakStruk());
        btnDataMenu.addActionListener(e -> bukaFormDataMenu());
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    simpanStokKeFile(); // ⬅ Tambahkan ini agar stok tersimpan
                    dispose(); // tutup jendela saat ini
                    new LoginForm().setVisible(true); // buka kembali menu login
                }
            }
        });


        // Simpan stok saat jendela ditutup
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                simpanStokKeFile();
            }
        });

        setVisible(true);
    }

    private void cetakStruk() {
        try {
            if (textJumlahMakanan.getText().trim().isEmpty() ||
                    textJumlahMinuman.getText().trim().isEmpty() ||
                    textDiskon.getText().trim().isEmpty() ||
                    textTunai.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harap isi semua kolom jumlah, diskon, dan tunai!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int jumlahMakanan = Integer.parseInt(textJumlahMakanan.getText().trim());
            int jumlahMinuman = Integer.parseInt(textJumlahMinuman.getText().trim());
            double diskon = Double.parseDouble(textDiskon.getText().trim());
            double tunai = Double.parseDouble(textTunai.getText().trim());

            if (jumlahMakanan < 0 || jumlahMinuman < 0) {
                JOptionPane.showMessageDialog(this, "Jumlah makanan dan minuman tidak boleh negatif!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (jumlahMakanan == 0 && jumlahMinuman == 0) {
                JOptionPane.showMessageDialog(this, "Harap masukkan jumlah makanan atau minuman minimal 1!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (diskon < 0 || diskon > 100) {
                JOptionPane.showMessageDialog(this, "Diskon harus antara 0 hingga 100%!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int indexMakanan = comboMakanan.getSelectedIndex();
            int indexMinuman = comboMinuman.getSelectedIndex();

            if (jumlahMakanan > stokMakanan[indexMakanan]) {
                JOptionPane.showMessageDialog(this, "Stok makanan tidak mencukupi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (jumlahMinuman > stokMinuman[indexMinuman]) {
                JOptionPane.showMessageDialog(this, "Stok minuman tidak mencukupi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double subtotalMakanan = hargaMakanan[indexMakanan] * jumlahMakanan;
            double subtotalMinuman = hargaMinuman[indexMinuman] * jumlahMinuman;
            double subtotal = subtotalMakanan + subtotalMinuman;
            double potongan = subtotal * (diskon / 100);
            double total = subtotal - potongan;

            if (tunai < total) {
                JOptionPane.showMessageDialog(this, "Tunai tidak cukup untuk membayar!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double kembalian = tunai - total;

            // Update stok
            stokMakanan[indexMakanan] -= jumlahMakanan;
            stokMinuman[indexMinuman] -= jumlahMinuman;

// Periksa apakah stok tersisa ≤ 3
            StringBuilder stokWarning = new StringBuilder();
            if (stokMakanan[indexMakanan] <= 3) {
                stokWarning.append("⚠ Stok makanan \"")
                        .append(makanan[indexMakanan])
                        .append("\" tersisa ")
                        .append(stokMakanan[indexMakanan])
                        .append("\n");
            }
            if (stokMinuman[indexMinuman] <= 3) {
                stokWarning.append("⚠ Stok minuman \"")
                        .append(minuman[indexMinuman])
                        .append("\" tersisa ")
                        .append(stokMinuman[indexMinuman])
                        .append("\n");
            }
// Tampilkan QR Code setelah struk
            tampilkanQRPembayaran(total);

// Tampilkan notifikasi jika ada
            if (stokWarning.length() > 0) {
                JOptionPane.showMessageDialog(this, stokWarning.toString(), "Peringatan Stok Hampir Habis", JOptionPane.WARNING_MESSAGE);
            }


            // Format struk untuk kertas 58mm
            StringBuilder struk = new StringBuilder();
            struk.append("      STRUK PEMBAYARAN\n");
            struk.append("-------------------------\n");
            struk.append(("Makanan : \n"));
            struk.append(String.format("%s\n", makanan[indexMakanan]));
            struk.append(String.format("         x%-2d Rp%,9.0f\n", jumlahMakanan, subtotalMakanan));
            struk.append(String.format("Minuman : %s\n", minuman[indexMinuman]));
            struk.append(String.format("         x%-2d Rp%,9.0f\n", jumlahMinuman, subtotalMinuman));
            struk.append("-------------------------\n");
            struk.append(String.format("Subtotal   : Rp%,9.0f\n", subtotal));
            struk.append(String.format("Diskon  %.0f%%:-Rp%,9.0f\n", diskon, potongan));
            struk.append(String.format("Total      : Rp%,9.0f\n", total));
            struk.append(String.format("Tunai      : Rp%,9.0f\n", tunai));
            struk.append(String.format("Kembali    : Rp%,9.0f\n", kembalian));
            struk.append("========================\n");
            struk.append("      Terima Kasih!\n");

            JOptionPane.showMessageDialog(this, struk.toString(), "Struk Pembayaran", JOptionPane.INFORMATION_MESSAGE);

// Cetak ke printer thermal
            printStruk(struk.toString());


            // Cetak ke printer thermal (jika ada printer)
            printStruk(struk.toString());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input jumlah, diskon, dan tunai harus angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printStruk(String text) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            PageFormat format = job.defaultPage();
            Paper paper = new Paper();

            double width = 58 * 2.8346;  // 58 mm ke point (sekitar 164.4 pt)
            double height = 1000;        // Bisa lebih panjang dari 297mm
            double margin = 5;

            paper.setSize(width, height);
            paper.setImageableArea(margin, margin, width - 2 * margin, height - 2 * margin);
            format.setPaper(paper);

            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));

                int y = 15;
                for (String line : text.split("\n")) {
                    g2d.drawString(line, 0, y);
                    y += 12; // Jarak antar baris
                }

                return Printable.PAGE_EXISTS;
            }, format);

            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak struk: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void bukaFormDataMenu() {
        FormDataMenu formDataMenu = new FormDataMenu(this,
                kodeMakanan, makanan, hargaMakanan, stokMakanan,
                kodeMinuman, minuman, hargaMinuman, stokMinuman);
        formDataMenu.setVisible(true);
    }

    private void simpanStokKeFile() {
        try {
            PrintWriter writerMakanan = new PrintWriter(FILE_STOK_MAKANAN);
            for (int stok : stokMakanan) writerMakanan.println(stok);
            writerMakanan.close();

            PrintWriter writerMinuman = new PrintWriter(FILE_STOK_MINUMAN);
            for (int stok : stokMinuman) writerMinuman.println(stok);
            writerMinuman.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan stok: " + e.getMessage());
        }
    }

    private void tampilkanQRPembayaran(double subtotal) {
        try {
            // QRIS statis dari DANA (ganti sesuai milikmu)
            String dataQR = "00020101021126570011ID.DANA.WWW011893600915371685038702097168503870303UMI51440014ID.CO.QRIS.WWW0215ID10243357036990303UMI5204581253033605802ID5914CHICKEN YASAKA6014Kab. Tangerang6105156106304BC12";

            BitMatrix matrix = new MultiFormatWriter().encode(dataQR, BarcodeFormat.QR_CODE, 200, 200);
            ImageIcon iconQR = new ImageIcon(MatrixToImageWriter.toBufferedImage(matrix));

            // Perhitungan diskon dan total bayar
            double diskonPersen = 0;
            double nominalDiskon = subtotal * (diskonPersen / 100);
            double totalBayar = subtotal - nominalDiskon;

            // Label-label informasi pembayaran
            JLabel labelSubtotal = new JLabel("Subtotal: Rp " + String.format("%,.0f", subtotal), JLabel.CENTER);
            JLabel labelDiskon = new JLabel("Diskon (" + diskonPersen + "%): -Rp " + String.format("%,.0f", nominalDiskon), JLabel.CENTER);
            JLabel labelTotalBayar = new JLabel("Total yang harus dibayar: Rp " + String.format("%,.0f", totalBayar), JLabel.CENTER);

            labelSubtotal.setFont(new Font("Arial", Font.PLAIN, 14));
            labelDiskon.setFont(new Font("Arial", Font.PLAIN, 14));
            labelTotalBayar.setFont(new Font("Arial", Font.BOLD, 16));

            JLabel labelQR = new JLabel("Scan dengan DANA / QRIS", JLabel.CENTER);
            labelQR.setFont(new Font("Arial", Font.BOLD, 14));

            // Tampilkan dialog
            JOptionPane.showMessageDialog(this, new Object[]{
                    labelTotalBayar,
                    labelQR,
                    new JLabel(iconQR)
            }, "QR Code Pembayaran", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal membuat QR: " + e.getMessage());
        }
    }



    private void bacaStokDariFile() {
        try {
            File fileMakanan = new File(FILE_STOK_MAKANAN);
            File fileMinuman = new File(FILE_STOK_MINUMAN);

            if (fileMakanan.exists() && fileMinuman.exists()) {
                BufferedReader readerMakanan = new BufferedReader(new FileReader(fileMakanan));
                BufferedReader readerMinuman = new BufferedReader(new FileReader(fileMinuman));

                for (int i = 0; i < stokMakanan.length; i++) {
                    stokMakanan[i] = Integer.parseInt(readerMakanan.readLine());
                }
                for (int i = 0; i < stokMinuman.length; i++) {
                    stokMinuman[i] = Integer.parseInt(readerMinuman.readLine());
                }

                readerMakanan.close();
                readerMinuman.close();
            } else {
                // Inisialisasi stok default jika file tidak ada
                for (int i = 0; i < stokMakanan.length; i++) stokMakanan[i] = 10;
                for (int i = 0; i < stokMinuman.length; i++) stokMinuman[i] = 20;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal membaca stok: " + e.getMessage());
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}










