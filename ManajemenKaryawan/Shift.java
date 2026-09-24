package manajemenkaryawan;

public class Shift {
    public void jadwal(Karyawan k) {
        if(k.getJabatan().equalsIgnoreCase("satpam")) {
            System.out.println(k.getNama() + " Shift : 24 JAM");
        } else {
            System.out.println(k.getNama() + " Shift : PAGI");
        }
    }
}