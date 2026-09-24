package manajemenkaryawan;

public class Gaji extends Karyawan {
    private int gaji;

    public Gaji(String nama, String jabatan, String noTelp, int gaji){
        super(0, nama,jabatan,noTelp,gaji,"karyawan");
        this.gaji = gaji;
    }

    @Override
    public void tampilData(){
        super.tampilData();
        System.out.println("Gaji : " + gaji);
    }
}