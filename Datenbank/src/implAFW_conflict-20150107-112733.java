
import edu.whs.gdb.ApplicationException;
import edu.whs.gdb.DataAccessObject;
import static edu.whs.gdb.DataAccessObject.VISUALISIERUNG_ANMELDUNGEN_TESTATE;
import edu.whs.gdb.entity.Modul;
import edu.whs.gdb.entity.Praktikumsteilnahme;
import edu.whs.gdb.entity.Student;
import edu.whs.gdb.entity.Studienrichtung;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.jdbc.JDBCPieDataset;

/**
 *
 * @author Dmitry Rybinkin
 */
public class implAFW implements DataAccessObject {

//    String url = "jdbc:derby:C:\\Users\\Dimitrij\\Dropbox\\Studium\\GDB\\netbeans\\Datenbank\\libs\\javaDB";
//    String url = "jdbc:derby:C:\\Users\\home\\Dropbox\\Studium\\GDB\\netbeans\\Datenbank\\libs\\javaDB";
//    String url = "jdbc:derby:C:\\Users\\home\\Dropbox\\javaprojects\\Datenbank\\libs\\javaDB";
//    String url = "jdbc:derby:C:\\Users\\Dimitrij\\Dropbox\\Studium\\GDB\\netbeans\\Datenbank\\libs\\javaDB";
    String url = "jdbc:derby:D:\\ownCloud\\Studium\\GDB\\netbeans\\Datenbank\\libs\\studiDB";
    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    Statement stmt;
    Connection con = null;

    public implAFW() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load Jdbc-Odbc Bridge...:" 
                    + e.getMessage());
        }
        
        try {           
            con = DriverManager.getConnection(url);
            con.setAutoCommit(false);
            
            stmt = con.createStatement();
        } catch (SQLException ex) {
            System.out.println("SQL Exception occured: " + ex.getMessage());
        }
    }
    
    
    public void rollback() {
                
            try {
                con.rollback();
            } catch (SQLException exc) {
                System.err.println("SQL Exception occured: " + exc.getMessage());
            }
                           
    }

    

    /**
     * Die Methode erstellt einen Studienverlaufsplan, in dem für jedes Semester
     * und für jeden Studiengang alle Module und deren zugehörigen Übungen,
     * Praktika und Vorlesungen mit Stunden datiert sind.
     * @param s Die Studienrichtung für die der Plan erstellt werden soll.
     * @return Ausgabe des ganzen Studienverlaufsplans.
     * @throws ApplicationException Falls ein Fehler bei der Erstellung des 
     * Plans auftreten sollte, wird eine Exception geworfen.
     */
    @Override
    public List<List<String>> getStudienverlaufsplan(Studienrichtung s) throws ApplicationException {
        
        List<List<String>> katego = new ArrayList<>();
        ArrayList<String> semesterZahl = new ArrayList<>();

        int[] sws = new int[6];
        String headline = "Studienverlaufsplan\n " + s.getName() + " (" + s.getKuerzel() + ")";
        semesterZahl.add(headline);
        for (int i = 0; i < 6; i++) {
            semesterZahl.add(i + 1 + "." + " Semester");
        }
        semesterZahl.add(" ");

        katego.add(semesterZahl);
        ArrayList<String> abb = new ArrayList<>(); //abbreviation
        abb.add("Kategorie");
        for (int j = 0; j < 6; j++) {
            abb.add("Mod    V    Ü    P    Cr");
        }
        abb.add("Summe");

        katego.add(abb);

        ArrayList<String> kName = new ArrayList<>();
        ArrayList<String> kKuerzel = new ArrayList<>();
 
        try {
//            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT distinct K.LFDNR, K.NAME,K.KKUERZEL "
                    + "FROM APP.KATEGORIE as K, APP.MODUL as M, APP.VERLAUFSPLAN as V, APP.STUDIENRICHTUNG as S "
                    + "where K.KKuerzel = M.KKuerzel and M.MKUERZEL = V.MKUERZEL and V.SKUERZEL = S.SKUERZEL and S.SKUERZEL like " + "'" + s.getKuerzel() + "'");

            while (rs.next()) {
                kName.add("" + rs.getString(2));
                kKuerzel.add("" + rs.getString(3));
            }

            for (int a = 0; a < kName.size(); a++) {
                ArrayList<String> ersteModulZeile = new ArrayList<>();
                ersteModulZeile.add(kName.get(a));
                int stunden = 0;
                for (int b = 1; b <= 6; b++) {
                    //ausgeben der Moduldetails, abhaengig von dem Studiengang und dem Kategorienamen
                    rs = stmt.executeQuery("select distinct m.mkuerzel, m.vl,"
                            + " m.ub, m.pr, m.credits from modul as m, "
                            + "kategorie as k, verlaufsplan as v, "
                            + "studienrichtung as s where "
                            + "m.kkuerzel = k.kkuerzel and m.mkuerzel = "
                            + "v.mkuerzel and v.SKUERZEL = s.SKUERZEL and "
                            + "v.sem = " + b + " and k.kkuerzel = " + "'"
                            + kKuerzel.get(a) + "'" + " and s.skuerzel = "
                            + "'" + s.getKuerzel() + "'");


                    String Info = "";

                    while (rs.next()) {
                        //STUNDEN FÜR das SEMESTER B addieren
                        sws[b - 1] = sws[b - 1] + rs.getInt(2) + rs.getInt(3) + rs.getInt(4);
                        String modName = rs.getString(1);
                        int vl = rs.getInt(2);
                        int ub = rs.getInt(3);
                        int pr = rs.getInt(4);
                        int cr = rs.getInt(5);

                        stunden = stunden + vl + ub + pr;
                        Info = Info + modName + "    " + vl + "    "
                                + ub + "    " + pr + "    " + cr + "\n";
                    }
                    ersteModulZeile.add(Info);

                }
                ersteModulZeile.add("" + stunden);
                katego.add(ersteModulZeile);
            }

        } catch (SQLException ex) {
            System.err.println("SQL Exception occured: " + ex.getMessage());               
            rollback();
                           
        }
        
        //STUNDEN PRO SEMESTER AUSLESEN UND IN ZEILE SCHREIBEN
        ArrayList<String> swsZeile = new ArrayList<>();
        swsZeile.add("Summe SWS");

        int Summe = 0;
        for (int j = 0; j <= 5; j++) {
            swsZeile.add("" + sws[j]);
            Summe = Summe + sws[j];
        }

        swsZeile.add("" + Summe);
        katego.add(swsZeile);
        return katego;
    }

    /**
     * Die Methode fügt durch die GUI einen neuen Studenten in die
     * JDBC - Datenbank ein dabei wird auch auf schon vorhandene Einträge
     * geprüft. 
     * @param matrikel Die Matrikelnummer des neuen Studenten.
     * @param name Der Name des Studenten.
     * @param vorname Der Vorname des Studenten.
     * @param adresse Die Adresse des Studenten.
     * @param srkuerzel Die Studienrichtung als Kürzel, an die der Student 
     * angemeldet wird.
     * @throws ApplicationException Falls Studienrichtung nicht bekannt oder 
     * Matrikelnummer schon vorhanden, wird eine Exception geworfen.
     */
    @Override
    public void addStudent(String matrikel, String name, String vorname, String adresse, String srkuerzel) throws ApplicationException {

        implStudent studi = new implStudent(matrikel, name, vorname, adresse, srkuerzel);

        try {
           
            ResultSet rs = stmt.executeQuery("SELECT MATRIKEL FROM STUDENT WHERE MATRIKEL ='" + studi.getMatrikel() + "'");

            /*
             * Falls Einträge vorhanden sind, wird eine Exception geworfen.
             */
            if (rs.next()) {
                throw new ApplicationException("Die Matrikelnummer ist bereits vergeben!");
            }

            rs = stmt.executeQuery("SELECT * FROM STUDIENRICHTUNG WHERE SKUERZEL = '"
                    + studi.getStudienrichtungKuerzel() + "'");
            /*
             * Falls keine Einträge im ResultSet vorhanden sind, wird eine
             * Exception geoworfen.
             */
            if (!rs.next()) {
                throw new ApplicationException("Die Studienrichtung ist nicht bekannt.");
            }

            stmt.executeUpdate("INSERT INTO APP.STUDENT (MATRIKEL, NAME, VORNAME, "
                    + "ADRESSE, SKUERZEL ) VALUES ('" + studi.getMatrikel() + "', '"
                    + studi.getName() + "', '" + studi.getVorname() + "', '"
                    + studi.getAdresse() + "', '"
                    + studi.getStudienrichtungKuerzel() + "')");
        } catch (SQLException ex) {
            System.err.println("SQL Exception occured: " + ex.getMessage());
           rollback();
        }

    }

    /**
     * Die Methode erstellt eine Collection aus allen eingetragenen Studenten
     * in der Datenbank.
     * @return Ausgegeben wird eine Liste aller Studenten.
     */
    @Override
    public Collection<Student> getAllStudent() {
        Collection<Student> studenten = new ArrayList<>();

        

        try {
           
            ResultSet rs = stmt.executeQuery(
                    "select * from APP.STUDENT");

            while (rs.next()) {
                String matrikelNr = rs.getString(1);
                String name = rs.getString(2);
                String vorname = rs.getString(3);
                String adresse = rs.getString(4);
                String SRKuerzel = rs.getString(5);
                studenten.add(new implStudent(matrikelNr, name, vorname, adresse, SRKuerzel));
            }

        } catch (SQLException ex) {
            System.err.println("SQL Exception occured: " + ex.getMessage());
            rollback();
        }
        return studenten;
    }

    /**
     * Mit der Methode kann man die Testate für alle Praktikumsanmeldungen 
     * setzen und löschen.
     * @param clctn Liste in die die Testate abgespeichert werden.
     */
    @Override
    public void setTestate(Collection<Praktikumsteilnahme> clctn) {
        Iterator iter = clctn.iterator();
       
        try {
           con.setAutoCommit(false);

            /*
             * Über die Liste der Praktikumsteilnahmen wird iteriert und für jede Teilnahme
             * die Matrikelnummer, das Modulkürzel und das Semester.
             */
            while (iter.hasNext()) {
                Praktikumsteilnahme folgender = (Praktikumsteilnahme) iter.next();
                stmt.executeUpdate("UPDATE PRAKTIKUMSTEILNAHME SET TESTAT = 1 WHERE MATRIKEL = "
                        + "'" + folgender.getStudent().getMatrikel() + "'"
                        + " AND MKUERZEL = " + "'" + folgender.getModul().getKuerzel()
                        + "'" + " AND SEMESTER = " + "'" + folgender.getSemester() + "'");
            }
            con.commit();
        } catch (SQLException ex) {
            System.err.println("SQL Exception occured: " + ex.getMessage());
            rollback();
        } finally {
            try{
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("");
                
            }
            
        }
    }

    @Override
    public JPanel getChart(int i, Object o, Object o1) throws ApplicationException {
        JFreeChart chart = null;

        try {
            

            switch (i) {
                // <editor-fold defaultstate="collapsed" desc="VISUALISIERUNG_ANTEIL_TESTATABNAHMEN">
                case VISUALISIERUNG_ANTEIL_TESTATABNAHMEN: {
                    if (!(o instanceof String)) {
                        throw new ApplicationException("Erster Parameter ist kein String!");
                    } else if (o1 != null) {
                        throw new ApplicationException("Zweiter Parameter muss 'null' sein! ");
                    }
                    String semester = (String) o;
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                    for (Studienrichtung sr : getAllStudienrichtung()) {
                        
                        String abfrage0 = "select m.mkuerzel as modul"
                                + ",count(*) as anmeldungen "
                                + "from modul as m, praktikumsteilnahme as p, student as s "
                                + "where m.pr > 0 "
                                + "and m.mkuerzel = p.mkuerzel "
                                + "and s.matrikel = p.matrikel "
                                + "and s.skuerzel = " + "'" + sr.getKuerzel() + "'"
                                + "and p.semester = " + "'" + semester + "'"
                                + "group by m.mkuerzel";

                        ResultSet rs = stmt.executeQuery(abfrage0);
                        stmt.close();
                        
                        while (rs.next()) {
                            String mKuerzel = rs.getString("modul");
                            int anzahl = rs.getInt("anmeldungen");
                            String abfrage = "select count(m.mkuerzel) as bestanden "
                                    + "from modul as m, praktikumsteilnahme as p, student as s "
                                    + "where m.pr > 0 "
                                    + "and m.mkuerzel = p.mkuerzel "
                                    + "and s.matrikel = p.matrikel "
                                    + "and p.testat = 1 "
                                    + "and s.skuerzel = " + "'" + sr.getKuerzel() + "'"
                                    + "and m.mkuerzel = " + "'" + mKuerzel + "'"
                                    + "and p.semester = " + "'" + semester + "'";

                            
                            stmt = con.createStatement();
                            ResultSet rsTestate = stmt.executeQuery(abfrage);

                            rsTestate.next();

                            int bestanden = rsTestate.getInt("bestanden");

                            double gesamt = (bestanden == 0 ? 0 : ((double) bestanden / (double) anzahl) * 100.0);
                            dataset.addValue(gesamt, mKuerzel, sr.getKuerzel());
                        }

                    }
                    chart = ChartFactory.createBarChart(semester,
                            "Praktikumsmodule nach Studienrichtung",
                            "Erfolgreiche Teilnahme in %",
                            dataset,
                            PlotOrientation.VERTICAL,
                            true, true, false);

                }
                break;
                // </editor-fold>
                //<editor-fold defaultstate="collapsed" desc="VISUALISIERUNG_AUFTEILUNG_ANMELDUNGEN">    
                case VISUALISIERUNG_AUFTEILUNG_ANMELDUNGEN: {

                    if (!(o instanceof Studienrichtung)) {
                        throw new ApplicationException("Das Objekt ist nicht vom Typ Studienrichtung!");

                    } else if (!(o1 instanceof String)) {
                        throw new ApplicationException("Das Objekt ist nicht vom Typ String.");
                    }
                    Studienrichtung sr = (Studienrichtung) o;
                    String semester = (String) o1;

                    /* Vorbereitung der SQL Abfrage */
                    String sqlQuery = "select mkuerzel as modul, count(*) as anmeldungen "
                            + "from praktikumsteilnahme p, student s "
                            + "where p.matrikel = s.matrikel "
                            + "and s.skuerzel ='" + sr.getKuerzel() + "' "
                            + "and semester = '" + semester + "' "
                            + "group by p.mkuerzel";

                    PieDataset dataset = new JDBCPieDataset(this.con, sqlQuery);

                    /* Erstellen des Kuchendiagramms */
                    chart = ChartFactory.createPieChart(
                            sr.getName() + " (" + sr.getKuerzel() + ") " + semester,
                            dataset,
                            true,
                            true,
                            false);
                    ((PiePlot) chart.getPlot()).setLabelGenerator(
                            new StandardPieSectionLabelGenerator("{0}: {1} Anmeldungen"));

                }

                break;
                // </editor-fold>
                //<editor-fold defaultstate="collapsed" desc="VISUALISIERUNG_ENTWICKLUNG_ANMELDUNGEN">

                case VISUALISIERUNG_ENTWICKLUNG_ANMELDUNGEN: {
                    if (!(o instanceof Modul)) {

                        throw new ApplicationException("Das Objekt ist nicht vom Typ Modul.");
                    } else if (o1 != null) {
                        throw new ApplicationException("Das Objekt ist kein leeres Objekt.");
                    }
                    Modul modul = (Modul) o;
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                    
                    ResultSet rsTestate;

                    String testatVergaben
                            = "select semester, count(*) as anmeldungen"
                            + " from praktikumsteilnahme"
                            + " where mkuerzel = " + "'" + modul.getKuerzel() + "'"
                            + " group by semester";
                    rsTestate = stmt.executeQuery(testatVergaben);
//                    stmt.close();
                    
                    while (rsTestate.next()) {

                        dataset.addValue(
                                rsTestate.getInt("anmeldungen"),
                                "Anmeldungen",
                                rsTestate.getString("semester"));
                        dataset.addValue(
                                0,
                                "Testatvergaben",
                                rsTestate.getString("semester"));

                    }

                    stmt.close();

                    stmt = con.createStatement();

                    ResultSet rsTestateBestanden;

                    /* Vorbereitung der SQL Abfrage */
                    String testatVergabenBestanden
                            = "select semester, count(*) as vergaben "
                            + "from praktikumsteilnahme"
                            + " where mkuerzel = " + "'" + modul.getKuerzel() + "'"
                            + " and testat = 1"
                            + " group by semester";

                    rsTestateBestanden = stmt.executeQuery(testatVergabenBestanden);



                    while (rsTestateBestanden.next()) {

                        dataset.addValue(
                                rsTestateBestanden.getInt("vergaben"),
                                "Testatvergaben",
                                rsTestateBestanden.getString("semester"));

                    }

//                    rsTestateBestanden.close();
                    stmt.close();

                    /* Erstellen des Liniendiagramms */
                    chart = ChartFactory.createLineChart(modul.getName()
                            + " (" + modul.getKuerzel() + ")",
                            "Semester",
                            "Studierende",
                            dataset,
                            PlotOrientation.VERTICAL,
                            true,
                            true,
                            false);

                }

                break;
                // </editor-fold>
                //<editor-fold defaultstate="collapsed" desc="VISUALISIERUNG_ANMELDUNGEN_TESTATE">
                case VISUALISIERUNG_ANMELDUNGEN_TESTATE: {

                    /* Fehlerbehandlung für ungültige Parameter */
                    if (!(o instanceof Studienrichtung)) {
                        throw new ApplicationException("Das Objekt ist nicht vom Typ Studienrichtung.");
                    } else if (!(o1 instanceof String)) {
                        throw new ApplicationException("Das Objekt ist nicht vom Typ String.");

                    }

                    Studienrichtung sr = (Studienrichtung) o;
                    String sem = (String) o1;
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                    
                    /* Tabelle aller Anmeldungen zu Praktika */
                    ResultSet rsAnmeldungen;

                    /* Tabelle aller Anmeldungen zu Praktika mit Testatvergabe */
                    ResultSet rsTestatvergaben;

                    /* Anmeldungen zu Praktika */
                    /* Vorbereitung der SQL Abfrage */
                    String sqlQueryAnmeldungen = "select p.mkuerzel as modul ,count(p.mkuerzel) as anmeldungen"
                            + " from praktikumsteilnahme as p, student as s "
                            + " where p.semester = " + "'" + sem + "'"
                            + " and p.matrikel = s.matrikel"
                            + " and s.skuerzel = " + "'" + sr.getKuerzel() + "'"
                            + " group by p.mkuerzel";


                    /* Durchführung der SQL Abfrage */
                    rsAnmeldungen = stmt.executeQuery(sqlQueryAnmeldungen);

                    
                    /* Alle Praktikumsanmeldungen */
                    while (rsAnmeldungen.next()) {

                        /* Modulkürzel */
                        String mKuerzel = rsAnmeldungen.getString("modul");

                        /* Anzahl Praktikumsanmeldungen */
                        int anmeldungen = rsAnmeldungen.getInt("anmeldungen");

                        dataset.addValue(anmeldungen, "Anmeldungen", mKuerzel);

                        /* Vorbereitung der SQL Abfrage */
                        String sqlQueryTestatvergaben = "select count(p.mkuerzel) as anzahl"
                                + " from praktikumsteilnahme as p, student as S"
                                + " where p.semester = " + "'" + sem + "'"
                                + " and p.matrikel = s.matrikel"
                                + " and s.skuerzel = " + "'" + sr.getKuerzel() + "'"
                                + " and p.mkuerzel = " + "'" + mKuerzel + "'"
                                + " and p.testat = 1"
                                + " group by p.mkuerzel";

                        /* Durchführung der SQL Abfrage */
                        rsTestatvergaben = stmt.executeQuery(sqlQueryTestatvergaben);
                        stmt.close();
                        
                        /* Alle Testatvergaben */
                        while (rsTestatvergaben.next()) {
                            dataset.addValue(rsTestatvergaben.getInt("anzahl"),
                                    "Testatvergabe",
                                    mKuerzel);
                        }
                    }

                    /* Erstellen des Balkendiagramms */
                    chart = ChartFactory.createBarChart(sr.getName()
                            + " " + sr.getKuerzel() + " - " + sem,
                            "Module",
                            "Studierende",
                            dataset,
                            PlotOrientation.VERTICAL,
                            true,
                            true,
                            false);

                   
                }

                break;
                // </editor-fold>

                default: {

                    throw new ApplicationException("Visualisierungstyp nicht vorhanden");

                }
            }
        } catch (SQLException ex) {
            System.err.println("SQL Exception occured: " + ex.getMessage() + "");
            rollback();
        }

        return new ChartPanel(chart);
    }

    @Override
    public Collection<Studienrichtung> getAllStudienrichtung() {

        Collection<Studienrichtung> sr = new ArrayList<>();
        

        try {
            
            ResultSet rs = stmt.executeQuery(
                    "select * from APP.STUDIENRICHTUNG");
            

            while (rs.next()) {
                String kuerzel = rs.getString(1);
                String name = rs.getString(2);

                sr.add(new implStudienrichtung(kuerzel, name));
            }
        } catch (SQLException ex) {
            System.err.println("SQL Exception occured: " + ex.getMessage());
            rollback();
        }
        return sr;
    }

    @Override
    public Collection<Modul> getAllModul() {
        Collection<Modul> mod = new ArrayList<>();
        

        try {
            
            ResultSet rs = stmt.executeQuery(
                    "select * from APP.MODUL");
            

            while (rs.next()) {
                String modulkurz = rs.getString(1);
                String modulname = rs.getString(2);
                int vl = rs.getInt(3);
                int ub = rs.getInt(4);
                int pr = rs.getInt(5);
                int cr = rs.getInt(6);

                mod.add(new implModul(modulkurz, modulname, vl, ub, pr, cr));
            }

        } catch (SQLException ex) {
            System.err.println("SQL Exception occured: " + ex.getMessage());
            rollback();
        }
        return mod;
    }

    @Override
    public Collection<Praktikumsteilnahme> getAllPraktikumsteilnahme(Modul modul, String semester) {
        Collection<Praktikumsteilnahme> pt = new ArrayList<>();
        

        if (modul != null && !semester.isEmpty()) {
            try {
                
                ResultSet rs = stmt.executeQuery(
                        "select * from praktikumsteilnahme prakt, student s "
                        + "where prakt.MATRIKEL = s.MATRIKEL and\n"
                        + "prakt.MKUERZEL = " + "'" + modul.getKuerzel() + "'"
                        + " and prakt.SEMESTER = " + "'" + semester + "'");
                

                boolean testatKonvertierung;
                while (rs.next()) {

                    int testat = rs.getInt(4);
                    if (testat == 1) {
                        testatKonvertierung = true;
                    } else {
                        testatKonvertierung = false;
                    }

                    pt.add(new implPraktika(new implStudent(
                            rs.getString(1),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getString(8),
                            rs.getString(9)),
                            modul,
                            semester,
                            testatKonvertierung));
                }

            } catch (SQLException ex) {
                System.err.println("SQL Exception occured: " + ex.getMessage());
                rollback();
            }
        }
        return pt;
    }

    @Override
    public void close() throws ApplicationException {
        try {
            con.close();
        } catch (SQLException ex) {
            System.err.println("SQL Exception occured: " + ex.getMessage());
        }
    }

    @Override
    public boolean enroll(String matrikel, String name, String vorname, String adresse, String srkuerzel, Modul modul, String semester) throws ApplicationException {
        boolean checked = true;

        String studiCheck = "SELECT MATRIKEL FROM STUDENT WHERE MATRIKEL ='" + matrikel + "'";
        String srCheck = "SELECT * FROM STUDIENRICHTUNG WHERE SKUERZEL ='" + srkuerzel + "'";
        String vpCheck = "SELECT MKUERZEL FROM VERLAUFSPLAN WHERE SKUERZEL='" + srkuerzel + "' AND MKUERZEL='"
                + modul.getKuerzel() + "'";
        String mCheck = "SELECT PR FROM MODUL WHERE MKUERZEL ='" + modul.getKuerzel() + "' AND PR >0";
        String enrollCheck = "SELECT * FROM PRAKTIKUMSTEILNAHME WHERE " + "MATRIKEL ='" + matrikel + "' AND MKUERZEL ='" + modul.getKuerzel()
                + "' AND SEMESTER ='" + semester + "'";
        String enroll = "INSERT INTO PRAKTIKUMSTEILNAHME (MATRIKEL, MKUERZEL, SEMESTER, TESTAT)VALUES ( '"
                + matrikel + "','" + modul.getKuerzel() + "','" + semester + "',0" + ")";

        
        try {//##############################################
            con.setAutoCommit(false);
            try {
                addStudent(matrikel, name, vorname, adresse, srkuerzel);

            } catch (ApplicationException ae) {
                /* Student existiert bereits */
                checked = false;
            }

            
            
           
            ResultSet rs;

            rs = stmt.executeQuery(vpCheck);
            if (!rs.next()) {
                checked = false;
                throw new ApplicationException("Das Modul ist in dieser Studienrichtung nicht zulässig!");
            }

            rs = stmt.executeQuery(mCheck);
            if (!rs.next()) {
                checked = false;
                throw new ApplicationException("Das Modul sieht kein Praktikum vor!");
            }

            rs = stmt.executeQuery(enrollCheck);
            if (rs.next()) {
                checked = false;
                throw new ApplicationException("Es ist bereits eine Teilnahme für diesen Studenten eingetragen!");
            }

            rs = stmt.executeQuery(srCheck);
            if (!rs.next()) {
                checked = false;
                throw new ApplicationException("Die Studienrichtung ist nicht vorhanden.");
            }

            stmt.close();

            stmt = con.createStatement();
            if (checked) {
                stmt.executeUpdate(enroll);
            }

            con.commit();
        } catch (SQLException ex) {//#########################################
            System.err.println("SQL Exception occured: " + ex.getMessage());
            rollback();
            throw new ApplicationException(ex.getMessage());
        } finally {
            try{
                con.setAutoCommit(true);
            } catch(SQLException exc) {
                System.err.println("SQL Exception occured: " + exc.getMessage());
            }
        }
        return checked;
    }

    /**
     * PDF-Export nicht ausgebaut.
     *
     * @param out
     * @param string
     */
    @Override
    public void exportEnrollmentList(OutputStream out, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
