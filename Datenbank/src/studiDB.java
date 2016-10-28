import edu.whs.gdb.DataAccessObject;
import edu.whs.gdb.GUIFactory;
import java.sql.SQLException;




/**
 *
 * @author Dmitry Rybinkin
 */
public class studiDB {
   
    
    /**
     *
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args){
        
        DataAccessObject access = new implAFW();
        GUIFactory.createMainFrame("Verwaltung", access).setVisible(true);
//        System.out.println(""+access.getAllStudienrichtung().toString());
    }
}
