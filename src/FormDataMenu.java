import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class FormDataMenu extends JDialog {
    private DefaultTableModel modelMakanan;
    private DefaultTableModel modelMinuman;

    public FormDataMenu(JFrame parent,
                        String[] kodeMakanan, String[] makanan, double[] hargaMakanan, int[] stokMakanan,
                        String[] kodeMinuman, String[] minuman, double[] hargaMinuman, int[] stokMinuman) {

        super(parent, "Data Menu", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Table Makanan
        modelMakanan = new DefaultTableModel(new Object[]{"Kode", "Nama Makanan", "Harga (Rp)", "Stok"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (int i = 0; i < makanan.length; i++) {
            modelMakanan.addRow(new Object[]{
                    kodeMakanan[i],
                    makanan[i],
                    (int) hargaMakanan[i],
                    stokMakanan[i]
            });
        }
        JTable tableMakanan = new JTable(modelMakanan);

        // Table Minuman
        modelMinuman = new DefaultTableModel(new Object[]{"Kode", "Nama Minuman", "Harga (Rp)", "Stok"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (int i = 0; i < minuman.length; i++) {
            modelMinuman.addRow(new Object[]{
                    kodeMinuman[i],
                    minuman[i],
                    (int) hargaMinuman[i],
                    stokMinuman[i]
            });
        }
        JTable tableMinuman = new JTable(modelMinuman);

        // Tab Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Makanan", new JScrollPane(tableMakanan));
        tabbedPane.addTab("Minuman", new JScrollPane(tableMinuman));
        add(tabbedPane, BorderLayout.CENTER);

        // Tombol Panel
        JPanel panelBtn = new JPanel();

        JButton btnTambahMakanan = new JButton("Tambah Makanan");
        btnTambahMakanan.addActionListener(e -> tambahData(modelMakanan, "Makanan"));
        JButton btnTambahMinuman = new JButton("Tambah Minuman");
        btnTambahMinuman.addActionListener(e -> tambahData(modelMinuman, "Minuman"));

        JButton btnHapus = new JButton("Hapus Baris");
        btnHapus.addActionListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();
            JTable selectedTable = selectedTab == 0 ? tableMakanan : tableMinuman;
            DefaultTableModel selectedModel = selectedTab == 0 ? modelMakanan : modelMinuman;
            int selectedRow = selectedTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.");
            }
        });

        JButton btnEditStok = new JButton("Edit Stok");
        btnEditStok.addActionListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();
            JTable selectedTable = selectedTab == 0 ? tableMakanan : tableMinuman;
            DefaultTableModel selectedModel = selectedTab == 0 ? modelMakanan : modelMinuman;
            int selectedRow = selectedTable.getSelectedRow();

            if (selectedRow >= 0) {
                String namaItem = selectedModel.getValueAt(selectedRow, 1).toString();
                String stokLama = selectedModel.getValueAt(selectedRow, 3).toString();
                String inputStok = JOptionPane.showInputDialog(this,
                        "Ubah stok untuk " + namaItem + ":", stokLama);

                if (inputStok != null) {
                    try {
                        int stokBaru = Integer.parseInt(inputStok);
                        if (stokBaru >= 0) {
                            selectedModel.setValueAt(stokBaru, selectedRow, 3);
                        } else {
                            JOptionPane.showMessageDialog(this, "Stok tidak boleh negatif.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Input harus berupa angka.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih item yang ingin diubah stoknya.");
            }
        });

        JButton btnSimpan = new JButton("Simpan ke File");
        btnSimpan.addActionListener(e -> {
            simpanKeCSV(modelMakanan, "data_makanan.csv");
            simpanKeCSV(modelMinuman, "data_minuman.csv");
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan ke file CSV.");
        });

        JButton btnClose = new JButton("Tutup");
        btnClose.addActionListener(e -> dispose());

        panelBtn.add(btnTambahMakanan);
        panelBtn.add(btnTambahMinuman);
        panelBtn.add(btnHapus);
        panelBtn.add(btnEditStok);
        panelBtn.add(btnSimpan);
        panelBtn.add(btnClose);
        add(panelBtn, BorderLayout.SOUTH);
    }

    private String generateKodeMakanan() {
        int nextNum = modelMakanan.getRowCount() + 1;
        return String.format("M%03d", nextNum);
    }

    private String generateKodeMinuman() {
        int nextNum = modelMinuman.getRowCount() + 1;
        return String.format("D%03d", nextNum);
    }

    private void tambahData(DefaultTableModel model, String tipe) {
        String generatedCode = tipe.equals("Makanan") ? generateKodeMakanan() : generateKodeMinuman();
        JTextField tfNama = new JTextField();
        JTextField tfHarga = new JTextField();
        JTextField tfStok = new JTextField("1");

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Kode:")); panel.add(new JLabel(generatedCode));
        panel.add(new JLabel("Nama " + tipe + ":")); panel.add(tfNama);
        panel.add(new JLabel("Harga:")); panel.add(tfHarga);
        panel.add(new JLabel("Stok:")); panel.add(tfStok);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Tambah " + tipe + " Baru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String nama = tfNama.getText().trim();
                int harga = Integer.parseInt(tfHarga.getText().trim());
                int stok = Integer.parseInt(tfStok.getText().trim());

                if (!nama.isEmpty()) {
                    model.addRow(new Object[]{generatedCode, nama, harga, stok});
                } else {
                    JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka.");
            }
        }
    }

    private void simpanKeCSV(DefaultTableModel model, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                writer.print(model.getColumnName(col));
                if (col < model.getColumnCount() - 1) writer.print(",");
            }
            writer.println();

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    writer.print(model.getValueAt(row, col));
                    if (col < model.getColumnCount() - 1) writer.print(",");
                }
                writer.println();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + filename);
        }
    }

    // === GETTER DATA MENU (dipanggil dari Main) ===
    public String[] getKodeMakanan() {
        String[] result = new String[modelMakanan.getRowCount()];
        for (int i = 0; i < result.length; i++)
            result[i] = modelMakanan.getValueAt(i, 0).toString();
        return result;
    }

    public String[] getMakanan() {
        String[] result = new String[modelMakanan.getRowCount()];
        for (int i = 0; i < result.length; i++)
            result[i] = modelMakanan.getValueAt(i, 1).toString();
        return result;
    }

    public double[] getHargaMakanan() {
        double[] result = new double[modelMakanan.getRowCount()];
        for (int i = 0; i < result.length; i++)
            result[i] = Double.parseDouble(modelMakanan.getValueAt(i, 2).toString());
        return result;
    }

    public int[] getStokMakanan() {
        int[] result = new int[modelMakanan.getRowCount()];
        for (int i = 0; i < result.length; i++)
            result[i] = Integer.parseInt(modelMakanan.getValueAt(i, 3).toString());
        return result;
    }

    public String[] getKodeMinuman() {
        String[] result = new String[modelMinuman.getRowCount()];
        for (int i = 0; i < result.length; i++)
            result[i] = modelMinuman.getValueAt(i, 0).toString();
        return result;
    }

    public String[] getMinuman() {
        String[] result = new String[modelMinuman.getRowCount()];
        for (int i = 0; i < result.length; i++)
            result[i] = modelMinuman.getValueAt(i, 1).toString();
        return result;
    }

    public double[] getHargaMinuman() {
        double[] result = new double[modelMinuman.getRowCount()];
        for (int i = 0; i < result.length; i++)
            result[i] = Double.parseDouble(modelMinuman.getValueAt(i, 2).toString());
        return result;
    }

    public int[] getStokMinuman() {
        int[] result = new int[modelMinuman.getRowCount()];
        for (int i = 0; i < result.length; i++)
            result[i] = Integer.parseInt(modelMinuman.getValueAt(i, 3).toString());
        return result;
    }
}
