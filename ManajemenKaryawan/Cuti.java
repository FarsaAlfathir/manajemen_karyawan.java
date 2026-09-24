package manajemenkaryawan;
import java.util.Scanner;

public class Cuti {
    private String tanggal;

    public void ajukanCuti() {
        Scanner input = new Scanner(System.in);
        System.out.print("Masukkan tanggal cuti : ");
        tanggal = input.nextLine();
        System.out.println("Cuti pada tanggal " + tanggal + " berhasil diajukan ");
    }
}