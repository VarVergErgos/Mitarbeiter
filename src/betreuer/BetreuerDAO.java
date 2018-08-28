package betreuer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import betreuer.Betreuer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.JOptionPane;
import pfad.Pfad;

/**
 * Mit dieser Klasse stellen wir die Verbindung zur
 * Datenbank her
 */
public class BetreuerDAO {

    private Connection myConn;
    private static BetreuerDAO handler = null;
    private String dburl = Pfad.getUrl();
    private String user = Pfad.getUser();
    private String password = Pfad.getPassword();

    public static BetreuerDAO getInstance() throws Exception {
        if (handler == null) {
            handler = new BetreuerDAO();
        }
        return handler;
    }

    public BetreuerDAO() throws Exception {
        myConn = DriverManager.getConnection(dburl, user, password);
        //nur beim ersten Mal bei einrichten nutzen createConnection()
        //    createConnection();
    }


    private static void close(Connection myConn, Statement myStmt,
            ResultSet myRs) throws SQLException {

        if (myRs != null) {
            myRs.close();
        }
        if (myStmt != null) {
        }
        if (myConn != null) {
            myConn.close();
        }
    }


    private void close(Statement myStmt, ResultSet myRs) throws SQLException {
        close(null, myStmt, myRs);
    }


    @SuppressWarnings("unused")
	private void close(Statement myStmt) throws SQLException {
        close(null, myStmt, null);
    }


    private Betreuer convertRowToDatabase(ResultSet myRs) throws SQLException {

        int id = myRs.getInt("ID");
        int persnr = myRs.getInt("PERSNR");
        String vorname = myRs.getString("VORNAME");
        String nachname = myRs.getString("NACHNAME");
        String strasse = myRs.getString("STRASSE");
        String wohnort = myRs.getString("WOHNORT");
        String funktion = myRs.getString("FUNKTION");
        int rd = myRs.getInt("RD");
        String rdname = myRs.getString("RDNAME");
        String geschlecht = myRs.getString("GESCHLECHT");
        String eintritt = myRs.getString("Eintritt");
        Double gehalt = myRs.getDouble("GEHALT");


        //In ACCESS MDB
        //Kommt aus slq so an = 1999-03-01 00:00:00.000000
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00.000000");

        //DERBY
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //Unterschiede je Datenbank oracle, mysql, sqLite, derby
        formatter = formatter.withLocale(Locale.GERMAN);
        LocalDate eintrittsdatum = LocalDate.parse(eintritt, formatter);


        Betreuer tempDaten = new Betreuer(id, persnr, vorname, nachname,
                geschlecht, strasse, wohnort, funktion, rd, rdname, eintrittsdatum, gehalt);

        /*
        System.out.println(id + ", " + persnr + ", " + vorname
                + ", " + nachname + ", " + strasse + ", " + wohnort
                + ", " + funktion + ", " + rd + ", " + rdname
                + ", " + geschlecht + ", " + eintritt+ ", " + gehalt);
		*/
        return tempDaten;

    }


    public List<Betreuer> getAllDaten() throws Exception {
        List<Betreuer> list = new ArrayList<>();

        PreparedStatement myStmt = null;
        ResultSet myRs = null;

        try {
            //partner += "%";

            String sql = "SELECT * FROM BETREUER";

            myStmt = myConn.prepareStatement(sql);

            //myStmt.setInt(1, persNr);
            myRs = myStmt.executeQuery();

            while (myRs.next()) {
                Betreuer tempDaten = convertRowToDatabase(myRs);
                list.add(tempDaten);
            }
            Thread.sleep(2);
            return list;
        } finally {
            close(myStmt, myRs);
        }

    }

    public int getAnzahlDatensaetze()throws Exception{
        int anzahl = 0;

          PreparedStatement myStmt = null;
        ResultSet myRs = null;

        try {
            //partner += "%";

            String sql = "SELECT Count(Betreuer.PERSNR) AS Anzahl FROM Betreuer";

            myStmt = myConn.prepareStatement(sql);

            //myStmt.setInt(1, persNr);
            myRs = myStmt.executeQuery();

            while (myRs.next()) {
                int Stueck = myRs.getInt("Anzahl");
                anzahl += Stueck;

            }
            Thread.sleep(2);
            return anzahl;
        } finally {
            close(myStmt, myRs);
        }

    }

    public List<Betreuer> getPersNr(int persNr) throws Exception {
        List<Betreuer> list = new ArrayList<>();

        PreparedStatement myStmt = null;
        ResultSet myRs = null;

        try {
            //partner += "%";

            String sql = "SELECT * FROM BETREUER WHERE PERSNR = ?";

            myStmt = myConn.prepareStatement(sql);

            myStmt.setInt(1, persNr);
            myRs = myStmt.executeQuery();

            while (myRs.next()) {
                Betreuer tempDaten = convertRowToDatabase(myRs);
                list.add(tempDaten);
            }
            Thread.sleep(2);
            return list;
        } finally {
            close(myStmt, myRs);
        }
    }


    public List<Betreuer> getPersonalName(String personalName) throws Exception {
        List<Betreuer> list = new ArrayList<>();

        PreparedStatement myStmt = null;
        ResultSet myRs = null;

        try {

            String sql = "SELECT * from Betreuer where NACHNAME like ?";

            myStmt = myConn.prepareStatement(sql);
            myStmt.setString(1, "%" + personalName + "%");
            myRs = myStmt.executeQuery();

            while (myRs.next()) {
                Betreuer tempDaten = convertRowToDatabase(myRs);
                list.add(tempDaten);
            }
            Thread.sleep(1);
            return list;
        } finally {
            close(myStmt, myRs);
        }

    }

    void createConnection() {
        try {
            myConn = DriverManager.getConnection(dburl, user, password);
            JOptionPane.showMessageDialog(null, "Datenbankverbindung ok",
                    "Datenbank verbunden", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cant load database",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) throws Exception {
        BetreuerDAO dao = new BetreuerDAO();
        System.out.println(dao.getAllDaten());

    }
}
