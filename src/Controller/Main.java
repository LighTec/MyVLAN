package Controller;

import GUI.MainWindow;

public class Main{

    public static final String VERSION = "1.0";
    public static final String DATE = "2018/08/22";

    public static final String USERNAME = "kell";
    public static final String PASSWORD = "Summer2018!";
    public static final String DATABASEURL = "mysql://10.41.146.120/vlan?";
    public static final String DATABASE = "vlan";
    public static final String TABLE = "Kell";

    public static void main(String args[]){
        System.out.println("               __     ___        _    _   _");
        System.out.println(" _ __ ___  _   \\ \\   / / |      / \\  | \\ | |");
        System.out.println("| '_ ` _ \\| | | \\ \\ / /| |     / _ \\ |  \\| |");
        System.out.println("| | | | | | |_| |\\ V / | |___ / ___ \\| |\\  |");
        System.out.println("|_| |_| |_|\\__, | \\_/  |_____/_/   \\_\\_| \\_|");
        System.out.println("           |___/   ");
        System.out.println("Vlan Network Database Viewer version " + VERSION);
        System.out.println("\t\t\tBy Kell Larson");
        System.out.println("Last updated " + DATE + "\n\n");
        try{
            Thread.sleep(400);
            System.out.println("Starting GUI elements...");
            Thread.sleep(400);
            MainWindow.init(args);
        }catch (InterruptedException e) {
            System.err.println("Interrupt error in main loop!");
        }
    }
}





