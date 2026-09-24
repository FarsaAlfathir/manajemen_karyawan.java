package manajemenkaryawan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * KaryawanDAO - Semua operasi database dikumpulkan di satu tempat.
 * GUI hanya memanggil method di kelas ini, tidak langsung tulis SQL.
 */
public class KaryawanDAO {

    // ─── Karyawan ────────────────────────────────────────────────────────────
    /** Ambil semua nama karyawan untuk ditampilkan di JList. */
    public static List<String> getAllNama() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT nama FROM karyawan ORDER BY nama";
        try (Connection con = KoneksiDB.getConnection();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                String nama = rs.getString("nama");
                System.out.println("Ditemukan: " + nama); // ← tambah ini
                list.add(nama);
            }
            System.out.println("Total: " + list.size());  // ← dan ini
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage()); // ← ubah ini
            e.printStackTrace();
        }
        return list;
    }

    /** Ambil satu objek Karyawan berdasarkan nama. */
    public static Karyawan getByNama(String nama) {
        String sql = "SELECT * FROM karyawan WHERE nama = ?";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nama);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Karyawan(
                    rs.getInt("Id"),
                    rs.getString("nama"),
                    rs.getString("jabatan"),
                    rs.getString("no_telp"),
                    rs.getDouble("gaji"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ─── Absensi ─────────────────────────────────────────────────────────────
    public static boolean simpanAbsensi(String namaKaryawan, String tipe) {
        int nextId = getNextIdAbsensi();
        String sql = "INSERT INTO absensi (id, id_karyawan, tipe, waktu) "
                   + "SELECT ?, id, ?, NOW() FROM karyawan WHERE nama = ?";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nextId);
            ps.setString(2, tipe);
            ps.setString(3, namaKaryawan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── Cuti ────────────────────────────────────────────────────────────────
    public static boolean ajukanCuti(String namaKaryawan, String tanggal) {
        int nextId = getNextIdCuti();
        String sql = "INSERT INTO cuti (id, id_karyawan, tanggal, status) "
                   + "SELECT ?, id, ?, 'Menunggu' FROM karyawan WHERE nama = ?";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,nextId);
            ps.setString(2, tanggal);
            ps.setString(3, namaKaryawan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ─── Login ───────────────────────────────────────────────────────────────
    /*** Cek login berdasarkan nama dan password dari database*/
    public static Karyawan cekLogin(String nama, String password) {
        String sql = "SELECT * FROM karyawan WHERE nama = ? AND password = ?";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Karyawan(
                    rs.getInt("Id"),
                    rs.getString("nama"),
                    rs.getString("jabatan"),
                    rs.getString("no_telp"),
                    rs.getDouble("gaji"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // ─── CRUD ────────────────────────────────────────────────────────────────
    /** Ambil semua karyawan untuk ditampilkan di JTable */
    public static List<Karyawan> getAllKaryawan() {
        List<Karyawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan ORDER BY nama";
        try (Connection con = KoneksiDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Karyawan(
                    rs.getInt("Id"),
                    rs.getString("nama"),
                    rs.getString("jabatan"),
                    rs.getString("no_telp"),
                    rs.getDouble("gaji"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Tambah karyawan baru ke database */
    public static boolean tambahKaryawan(int id, String nama, String jabatan,
                                          String noTelp, double gaji,
                                          String password) {
        String sql = "INSERT INTO karyawan (id, nama, jabatan, no_telp, gaji, password, role) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, nama);
            ps.setString(3, jabatan);
            ps.setString(4, noTelp);
            ps.setDouble(5, gaji);
            ps.setString(6, password);
            ps.setString(7, "karyawan");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Update data karyawan berdasarkan nama lama */
    public static boolean updateKaryawan(String namaLama, String namaBaru,
                                          String jabatan, String noTelp,
                                          double gaji, String password) {
        String sql = "UPDATE karyawan SET nama=?, jabatan=?, no_telp=?, "
                   + "gaji=?, password=? WHERE nama=?";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, namaBaru);
            ps.setString(2, jabatan);
            ps.setString(3, noTelp);
            ps.setDouble(4, gaji);
            ps.setString(5, password);
            ps.setString(6, namaLama);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Hapus karyawan berdasarkan nama */
    public static boolean hapusKaryawan(String nama) {
        String sql = "DELETE FROM karyawan WHERE nama = ?";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nama);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /** Ambil ID berikutnya untuk tabel absensi */
    public static int getNextIdAbsensi() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM absensi";
        try (Connection con = KoneksiDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /** Ambil ID berikutnya untuk tabel cuti */
    public static int getNextIdCuti() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM cuti";
        try (Connection con = KoneksiDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    // ─── Riwayat Absensi ─────────────────────────────────────────────────────
    public static List<String[]> getRiwayatAbsensi(String namaKaryawan) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT a.tipe, a.waktu " +
                     "FROM absensi a " +
                     "JOIN karyawan k ON a.id_karyawan = k.id " +
                     "WHERE k.nama = ? " +
                     "ORDER BY a.waktu DESC";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, namaKaryawan);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("tipe"),
                    rs.getString("waktu")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
