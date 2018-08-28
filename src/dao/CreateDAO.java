package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import javax.swing.JOptionPane;
import pfad.Pfad;

/**
 * Hier erstellen wir die Datenbank mit entsprechenden Beispielen
 *
 * // create a java calendar instance
Calendar calendar = Calendar.getInstance();

// get a java date (java.util.Date) from the Calendar instance.
// this java date will represent the current date, or "now".
java.util.Date currentDate = calendar.getTime();

// now, create a java.sql.Date from the java.util.Date
java.sql.Date date = new java.sql.Date(currentDate.getTime());
*
 */
public class CreateDAO {

    private Connection myConn;
    private Statement myStmt;

    private static CreateDAO handler = null;
    private String dburl = Pfad.getUrl();
    private String user = Pfad.getUser();
    private String password = Pfad.getPassword();

    private String TABLE_NAME_BLOCK = "Block";
    private String TABLE_NAME_BETREUER = "Betreuer";

    public static CreateDAO getInstance() throws Exception {
        if (handler == null) {
            handler = new CreateDAO();
        }
        return handler;
    }

    public CreateDAO() throws Exception {
        myConn = DriverManager.getConnection(dburl, user, password);
        createConnection();

        setupBetreuerTable();
    }

    /**
     * with this method we close the Connection and statement overload
     * constructor
     *
     * @param myConn
     * @param myStmt
     * @param myRs
     * @throws SQLException
     */
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

    /**
     * with this method we close the Connection and statement overload
     * constructor
     */
    @SuppressWarnings("unused")
	private void close(Statement myStmt, ResultSet myRs) throws SQLException {
        close(null, myStmt, myRs);
    }

    /**
     * with this method we close the Connection and statement overload
     * constructor
     *
     * @param myStmt
     * @throws SQLException
     */
    @SuppressWarnings("unused")
	private void close(Statement myStmt) throws SQLException {
        close(null, myStmt, null);
    }

    /**
     * Mit dieser Mehtode stellen wir die Verbindung zur Datenbank her
     */
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

    /**
     * Mit dieser Methode erstellen wir die Tabelle Betreuer, wenn diese noch
     * nicht existiert
     */
    void setupBetreuerTable() {

        try {
            myStmt = myConn.createStatement();

            DatabaseMetaData dbm = myConn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME_BETREUER.toUpperCase(), null);

            if (tables.next()) {

                JOptionPane.showMessageDialog(null, "Tabelle "
                        + TABLE_NAME_BETREUER + "Existiert bereits",
                        "Datenbanktabelle existiert", JOptionPane.INFORMATION_MESSAGE);
            } else {
                myStmt.execute("CREATE TABLE " + TABLE_NAME_BETREUER
                        + "(" + " ID int primary key,\n"
                        + " PERSNR int,\n"
                        + " VORNAME varchar(100),\n"
                        + " NACHNAME varchar(100),\n"
                        + " GESCHLECHT varchar(1),\n"
                        + " STRASSE varchar(100),\n"
                        + " WOHNORT varchar(100),\n"
                        + " FUNKTION varchar(100),\n"
                        + " EINTRITT date,\n"
                        + " RD int,\n"
                        + " RDNAME varchar(100)" + " )");

                JOptionPane.showMessageDialog(null, "Tabelle "
                        + TABLE_NAME_BETREUER + " wurde erstellt",
                        "Datenbanktabelle erstellt", JOptionPane.INFORMATION_MESSAGE);

                insertIntoBetreuer();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Can't load database",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
        }
    }

    /**
     * Mit dieser Methode fuegen wir Datensaetze ein, wenn die Tabelle Block neu
     * erstellt wurde.
     *
     * @throws SQLException
     */
    void insertIntoBetreuer() throws SQLException {

        try {
            Connection verbindung = DriverManager.getConnection(dburl, user, password);

            verbindung.setAutoCommit(false);
            Statement einfuegeAnweisung = verbindung.createStatement();

            //Mehrere Daten einfuegen


einfuegeAnweisung.addBatch("INSERT INTO Betreuer (ID, PERSNR, VORNAME, NACHNAME, GESCHLECHT, STRASSE, WOHNORT, FUNKTION, EINTRITT, RD, RDNAME) VALUES (1, 83635, 'Esra', 'Wagner', 'M', 'Marktplatz 24', '14199  Berlin', 'Angestellter', '01.01.2013', 23961, 'Abteilung 140') ");
einfuegeAnweisung.addBatch("INSERT INTO Betreuer (ID, PERSNR, VORNAME, NACHNAME, GESCHLECHT, STRASSE, WOHNORT, FUNKTION, EINTRITT, RD, RDNAME) VALUES (140, 83635, 'Esra', 'Wagner', 'M', 'Marktplatz 24', '14199  Berlin', 'Angestellter', '01.04.2018', 23961, 'Abteilung 140') ");
            //Mehrere Anweisungen mit executeBatch() einzelne mit executeUpdate
            einfuegeAnweisung.executeBatch();
            einfuegeAnweisung.close();

            verbindung.commit();
            verbindung.setAutoCommit(true);

            JOptionPane.showMessageDialog(null, "Daten in Tabelle "
                    + TABLE_NAME_BLOCK + " eingefuegt",
                    "Daten eingefuegt", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            System.out.println(ex.getMessage() + " " + ex.getStackTrace());
        }
    }

    public static void main(String[] args) throws Exception {
        @SuppressWarnings("unused")
		CreateDAO dao = new CreateDAO();

    }
}
