package manajemenkaryawan;

public class Karyawan {
    private int id;
    private String nama;
    private String jabatan;
    private String noTelp;
    private double gaji;
    private String role;

    // Constructor BARU dengan role
    public Karyawan(int id, String nama, String jabatan, String noTelp, 
                    double gaji, String role) {
        this.id      = id;
        this.nama    = nama;
        this.jabatan = jabatan;
        this.noTelp  = noTelp;
        this.gaji    = gaji;
        this.role    = role;
    }

    // Getter
    public int getId() { return id; }
    public String getNama()    { return nama; }
    public String getJabatan() { return jabatan; }
    public String getNoTelp()  { return noTelp; }
    public double getGaji()    { return gaji; }
    public String getRole()    { return role; }

    public void tampilData() {
        System.out.println("Nama     : " + nama);
        System.out.println("Jabatan  : " + jabatan);
        System.out.println("No Telp  : " + noTelp);
        System.out.println("----------------------");
    }
}