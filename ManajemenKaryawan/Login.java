package manajemenkaryawan;

public class Login {
    private String user1;
    private String user2;
    private String user3;
    private String user4;
    private String user5;

    public Login() {
        user1 = "Sony Ghanim";
        user2 = "Ahmad Izzudin Al Qasam";
        user3 = "M Firza Al Rasikh";
        user4 = "Rifki Raditya";
        user5 = "M Farsa Alfathir";
    }

    public boolean cekLogin(String nama) {
        return nama.equalsIgnoreCase(user1) ||
               nama.equalsIgnoreCase(user2) ||
               nama.equalsIgnoreCase(user3) ||
               nama.equalsIgnoreCase(user4) ||
               nama.equalsIgnoreCase(user5);
    }
}