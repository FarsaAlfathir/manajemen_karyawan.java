package manajemenkaryawan;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;

/**
 * MainFrameGUI - GUI Utama Sistem Manajemen Karyawan
 *
 */
public class MainFrameGUI extends JFrame {

    // ── Palet Warna ────────────────────────────────────────────────────────
    private static final Color C_PRIMARY      = new Color(37,  99,  235);
    private static final Color C_PRIMARY_DARK = new Color(29,  78,  216);
    private static final Color C_SIDEBAR      = new Color(15,  23,  42);
    private static final Color C_SIDEBAR_SEL  = new Color(30,  41,  59);
    private static final Color C_BG           = new Color(241, 245, 249);
    private static final Color C_CARD         = Color.WHITE;
    private static final Color C_MUTED        = new Color(100, 116, 139);
    private static final Color C_BORDER       = new Color(226, 232, 240);
    private static final Color C_SUCCESS      = new Color(22,  163, 74);
    private static final Color C_WARNING      = new Color(217, 119, 6);

    // ── Komponen UI ────────────────────────────────────────────────────────
    private JList<String>    listKaryawan;
    private JTextArea        txtOutput;
    private JComboBox<String> comboMenu;
    private JCheckBox        cbConsole, cbLog;
    private JLabel           lblWelcome, lblStatus, lblDBStatus;
    private JButton          btnJalankan;

    // ── State ─────────────────────────────────────────────────────────────
    private Karyawan karyawanAktif;
    private Karyawan karyawanLogin;

    // ════════════════════════════════════════════════════════════════════════
    //  KONSTRUKTOR
    // ════════════════════════════════════════════════════════════════════════
    public MainFrameGUI(Karyawan karyawanLogin) {
        this.karyawanLogin = karyawanLogin;
        buildUI();
        checkDB();
        loadKaryawan();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  INISIALISASI UI
    // ════════════════════════════════════════════════════════════════════════
    private void buildUI() {
        setTitle("Sistem Manajemen Karyawan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 600);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildHeader(),  BorderLayout.NORTH);
        getContentPane().add(buildSidebar(), BorderLayout.WEST);
        getContentPane().add(buildMain(),    BorderLayout.CENTER);
        getContentPane().add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_PRIMARY);
        p.setPreferredSize(new Dimension(0, 64));
        p.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel title = styledLabel("  Sistem Manajemen Karyawan",
                new Font("Segoe UI", Font.BOLD, 18), Color.WHITE);

        lblWelcome = styledLabel("Login sebagai : " + karyawanLogin.getNama() +
                "(" + karyawanLogin.getRole() + ")",
                new Font("Segoe UI", Font.PLAIN, 12), new Color(191, 219, 254));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        left.add(title);
        left.add(lblWelcome);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_SIDEBAR);
        p.setPreferredSize(new Dimension(210, 0));

        // Judul sidebar
        JLabel lbl = styledLabel("  Daftar Karyawan",
                new Font("Segoe UI", Font.BOLD, 12), new Color(148, 163, 184));
        lbl.setPreferredSize(new Dimension(0, 42));
        lbl.setBorder(new EmptyBorder(0, 15, 0, 0));
        p.add(lbl, BorderLayout.NORTH);

        // JList karyawan
        listKaryawan = new JList<>();
        listKaryawan.setBackground(C_SIDEBAR);
        listKaryawan.setForeground(Color.WHITE);
        listKaryawan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listKaryawan.setSelectionBackground(C_PRIMARY);
        listKaryawan.setSelectionForeground(Color.WHITE);
        listKaryawan.setFixedCellHeight(44);
        listKaryawan.setCellRenderer(new SidebarCellRenderer());
        listKaryawan.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listKaryawan.getSelectedValue() != null) {
                lblWelcome.setText("  Dipilih: " + listKaryawan.getSelectedValue());
                setStatus("Karyawan dipilih: " + listKaryawan.getSelectedValue());
            }
        });

        JScrollPane scroll = new JScrollPane(listKaryawan);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(C_SIDEBAR);
        scroll.getVerticalScrollBar().setBackground(C_SIDEBAR);
        p.add(scroll, BorderLayout.CENTER);

        // Tombol refresh di bawah sidebar
        JButton btnRefresh = new JButton("Refresh Daftar");
        styleButton(btnRefresh, C_SIDEBAR_SEL, Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.addActionListener(e -> loadKaryawan());
        
        // Tombol Kelola Karyawan (BARU)
        JButton btnKelola = new JButton("Kelola Karyawan");
        styleButton(btnKelola, new Color(22, 163, 74), Color.WHITE);
        btnKelola.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnKelola.addActionListener(e -> {
            new CRUDKaryawanFrame(karyawanLogin).setVisible(true);
            dispose();
        });

        // Bottom panel diubah jadi GridLayout supaya 2 tombol muat
        JPanel bottom = new JPanel(new java.awt.GridLayout(2, 1, 0, 5));
        bottom.setBackground(C_SIDEBAR);
        bottom.setBorder(new EmptyBorder(8, 10, 10, 10));
        bottom.add(btnRefresh);  // tombol lama
        if (karyawanLogin.getRole().equalsIgnoreCase("admin")) {
        bottom.add(btnKelola);}
        p.add(bottom, BorderLayout.SOUTH);

        return p;
           
       
    }

    // ── Konten Utama ──────────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        // ─ Toolbar atas ─────────────────────────────────────────────────
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
        toolbar.setBackground(C_CARD);
        toolbar.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblMenu = styledLabel("Menu : ", new Font("Segoe UI", Font.BOLD, 13), Color.DARK_GRAY);

        comboMenu = new JComboBox<>(new String[]{
            "Absen Masuk", "Absen Keluar", "Ajukan Cuti",
            "Lihat Data", "Lihat Gaji", "Lihat Shift","Riwayat Absensi"
        });
        comboMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboMenu.setMaximumSize(new Dimension(185, 34));

        btnJalankan = new JButton("  Jalankan  ");
        styleButton(btnJalankan, C_PRIMARY, Color.WHITE);
        btnJalankan.addActionListener(e -> jalankanMenu());

        cbConsole = new JCheckBox("Tampilkan Console");
        cbLog     = new JCheckBox("Simpan LOG");
        cbConsole.setOpaque(false);
        cbLog.setOpaque(false);
        cbConsole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbLog.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        toolbar.add(lblMenu);
        toolbar.add(Box.createHorizontalStrut(6));
        toolbar.add(comboMenu);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(btnJalankan);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        toolbar.add(Box.createHorizontalStrut(15));
        toolbar.add(cbConsole);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(cbLog);
        toolbar.add(Box.createHorizontalGlue());

        // ─ Kartu Output ─────────────────────────────────────────────────
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(C_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblOut = styledLabel("Output", new Font("Segoe UI", Font.BOLD, 13), Color.DARK_GRAY);
        card.add(lblOut, BorderLayout.NORTH);

        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        txtOutput.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtOutput.setBackground(new Color(248, 250, 252));
        txtOutput.setForeground(new Color(30, 41, 59));
        txtOutput.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);

        JScrollPane scrollOut = new JScrollPane(txtOutput);
        scrollOut.setBorder(new LineBorder(C_BORDER, 1, true));
        card.add(scrollOut, BorderLayout.CENTER);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(card,    BorderLayout.CENTER);
        return p;
    }

    // ── Status Bar ────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_BORDER);
        p.setPreferredSize(new Dimension(0, 26));
        p.setBorder(new EmptyBorder(3, 14, 3, 14));

        lblStatus   = styledLabel("Siap", new Font("Segoe UI", Font.PLAIN, 11), C_MUTED);
        lblDBStatus = styledLabel("● Mengecek koneksi...",
                new Font("Segoe UI", Font.PLAIN, 11), C_MUTED);

        p.add(lblStatus,   BorderLayout.WEST);
        p.add(lblDBStatus, BorderLayout.EAST);
        return p;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LOGIKA UTAMA
    // ════════════════════════════════════════════════════════════════════════

    /** Cek koneksi database saat aplikasi dibuka. */
    private void checkDB() {
        if (KoneksiDB.testConnection()) {
            lblDBStatus.setText("● Database Terhubung");
            lblDBStatus.setForeground(C_SUCCESS);
        } else {
            lblDBStatus.setText("● Database Terputus");
            lblDBStatus.setForeground(new Color(220, 38, 38));
            JOptionPane.showMessageDialog(this,
                "Gagal terhubung ke database!\n" +
                "Pastikan MySQL sudah berjalan dan jalankan db_setup.sql terlebih dahulu.",
                "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Muat daftar karyawan dari database ke JList. */
    private void loadKaryawan() {
        DefaultListModel<String> model = new DefaultListModel<>();

        // Cek role yang login
        if (karyawanLogin.getRole().equalsIgnoreCase("admin")) {
            // Admin → tampilkan SEMUA karyawan
            List<String> namaList = KaryawanDAO.getAllNama();
            namaList.forEach(model::addElement);
            setStatus("Data karyawan berhasil dimuat (" + namaList.size() + " karyawan).");
        } else {
            // Karyawan biasa → hanya tampilkan diri sendiri!
            model.addElement(karyawanLogin.getNama());
            setStatus("Login sebagai: " + karyawanLogin.getNama());
        }

        listKaryawan.setModel(model);
    }

    /** Dijalankan saat tombol "Jalankan" ditekan. */
    private void jalankanMenu() {
        if (listKaryawan.getSelectedValue() == null) {
            showOutput("⚠  Pilih karyawan terlebih dahulu dari daftar sebelah kiri.");
            return;
        }

        // Ambil data karyawan dari DB
        karyawanAktif = KaryawanDAO.getByNama(listKaryawan.getSelectedValue());
        if (karyawanAktif == null) {
            showOutput("❌  Data karyawan tidak ditemukan di database.");
            return;
        }

        String menu  = comboMenu.getSelectedItem().toString();
        String hasil = prosesMenu(menu);

        showOutput(hasil);
        setStatus("Menu '" + menu + "' dijalankan untuk " + karyawanAktif.getNama());

        if (cbConsole.isSelected()) System.out.println(hasil);
        if (cbLog.isSelected()) {
            simpanLog(hasil);
            txtOutput.append("\n\n📁 LOG disimpan ke file log_karyawan.txt");
        }
    }

    /** Switch menu → kembalikan teks hasil. */
    private String prosesMenu(String menu) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String waktu = LocalDateTime.now().format(fmt);

        switch (menu) {

            case "Absen Masuk": {
                boolean ok = KaryawanDAO.simpanAbsensi(karyawanAktif.getNama(), "masuk");
                return ok
                    ? "✅  ABSEN MASUK BERHASIL\n\n"
                      + "Nama   : " + karyawanAktif.getNama() + "\n"
                      + "Waktu  : " + waktu + "\n"
                      + "Status : Tercatat di database"
                    : "⚠  Absen masuk gagal disimpan ke database.";
            }

            case "Absen Keluar": {
                boolean ok = KaryawanDAO.simpanAbsensi(karyawanAktif.getNama(), "keluar");
                return ok
                    ? "✅  ABSEN KELUAR BERHASIL\n\n"
                      + "Nama   : " + karyawanAktif.getNama() + "\n"
                      + "Waktu  : " + waktu + "\n"
                      + "Status : Tercatat di database"
                    : "⚠  Absen keluar gagal disimpan ke database.";
            }

            case "Ajukan Cuti": {
                String tanggal = JOptionPane.showInputDialog(
                    this, "Masukkan tanggal cuti (YYYY-MM-DD):",
                    "Ajukan Cuti", JOptionPane.QUESTION_MESSAGE);
                if (tanggal == null || tanggal.isBlank()) return "❌  Pengajuan cuti dibatalkan.";
                boolean ok = KaryawanDAO.ajukanCuti(karyawanAktif.getNama(), tanggal.trim());
                return ok
                    ? "✅  CUTI BERHASIL DIAJUKAN\n\n"
                      + "Nama    : " + karyawanAktif.getNama() + "\n"
                      + "Tanggal : " + tanggal + "\n"
                      + "Status  : Menunggu Persetujuan"
                    : "⚠  Cuti gagal disimpan ke database.";
            }

            case "Lihat Data":
                return "═══════════════════════════════\n"
                     + "         DATA KARYAWAN         \n"
                     + "═══════════════════════════════\n\n"
                     + "Nama    : " + karyawanAktif.getNama()    + "\n"
                     + "Jabatan : " + karyawanAktif.getJabatan() + "\n"
                     + "No HP   : " + karyawanAktif.getNoTelp()  + "\n"
                     + "Gaji    : " + formatRupiah((int) karyawanAktif.getGaji());

            case "Lihat Gaji": {
                int bulanan  = (int) karyawanAktif.getGaji();
                int mingguan = bulanan / 4;
                int harian   = mingguan / 5;
                return "═══════════════════════════════\n"
                     + "         RINCIAN GAJI          \n"
                     + "═══════════════════════════════\n\n"
                     + "Nama          : " + karyawanAktif.getNama()   + "\n\n"
                     + "Gaji Bulanan  : " + formatRupiah(bulanan)  + "\n"
                     + "Gaji Mingguan : " + formatRupiah(mingguan) + "\n"
                     + "Gaji Harian   : " + formatRupiah(harian);
            }

            case "Lihat Shift": {
                String shift = karyawanAktif.getJabatan().equalsIgnoreCase("satpam")
                    ? "24 JAM (Sistem Rotasi)"
                    : "PAGI — 08:00 s.d. 17:00";
                return "═══════════════════════════════\n"
                     + "         JADWAL SHIFT          \n"
                     + "═══════════════════════════════\n\n"
                     + "Nama    : " + karyawanAktif.getNama()    + "\n"
                     + "Jabatan : " + karyawanAktif.getJabatan() + "\n"
                     + "Shift   : " + shift;
            }
            case "Riwayat Absensi": {
                List<String[]> riwayat = KaryawanDAO.getRiwayatAbsensi(karyawanAktif.getNama());
                
                if (riwayat.isEmpty()) {
                    return "═══════════════════════════════\n"
                         + "       RIWAYAT ABSENSI         \n"
                         + "═══════════════════════════════\n\n"
                         + "Nama   : " + karyawanAktif.getNama() + "\n\n"
                         + "⚠ Belum ada data absensi!";
                }
                
                StringBuilder sb = new StringBuilder();
                sb.append("═══════════════════════════════════════════\n");
                sb.append("            RIWAYAT ABSENSI                \n");
                sb.append("═══════════════════════════════════════════\n\n");
                sb.append("Nama   : ").append(karyawanAktif.getNama()).append("\n");
                sb.append("Total  : ").append(riwayat.size()).append(" data\n\n");
                sb.append("───────────────────────────────────────────\n");
                sb.append(String.format("%-10s %-25s%n", "TIPE", "WAKTU"));
                sb.append("───────────────────────────────────────────\n");
                
                for (String[] row : riwayat) {
                    String tipe  = row[0].toUpperCase();
                    String waktu2 = row[1];
                    sb.append(String.format("%-10s %-25s%n", tipe, waktu2));
                }
                
                sb.append("───────────────────────────────────────────");
                return sb.toString();
            }
            default:
                return "Menu tidak dikenali.";
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UTILITAS
    // ════════════════════════════════════════════════════════════════════════

    private void showOutput(String teks) {
        txtOutput.setText(teks);
    }

    private void setStatus(String teks) {
        lblStatus.setText(teks);
    }

    public void simpanLog(String teks) {
        try (FileWriter fw = new FileWriter("log_karyawan.txt", true)) {
            fw.write(LocalDateTime.now() + " | " + teks.replace("\n", " | ") + "\n");
        } catch (IOException e) {
            txtOutput.append("\n❌ Gagal menyimpan log: " + e.getMessage());
        }
    }

    public String formatRupiah(int angka) {
        return NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(angka);
    }

    // ── Helper UI ─────────────────────────────────────────────────────────

    private static JLabel styledLabel(String text, Font font, Color fg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(fg);
        return lbl;
    }

    private static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
    }

    // ── Cell Renderer Sidebar ─────────────────────────────────────────────

    /** Renderer kustom untuk item JList di sidebar. */
    private static class SidebarCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            lbl.setText("  \uD83D\uDC64  " + value);   // 👤
            lbl.setBorder(new EmptyBorder(0, 8, 0, 8));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            if (isSelected) {
                lbl.setBackground(new Color(37, 99, 235));
                lbl.setForeground(Color.WHITE);
            } else {
                lbl.setBackground(index % 2 == 0
                    ? new Color(15, 23, 42)
                    : new Color(20, 30, 50));
                lbl.setForeground(Color.WHITE);
            }
            return lbl;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Coba gunakan tampilan native OS atau Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
