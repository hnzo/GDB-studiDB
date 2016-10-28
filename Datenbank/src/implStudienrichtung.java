
import edu.whs.gdb.entity.Studienrichtung;
import java.util.Objects;


/**
 *
 * @author Dmitry Rybinkin
 */
public class implStudienrichtung implements Studienrichtung{

    private String name;
    private String kuerzel;
    
    public implStudienrichtung(String kuerzel, String name) {
        this.name = name;
        this.kuerzel = kuerzel;
    }
    @Override
    public String getKuerzel() {
       return kuerzel;
    }

    @Override
    public String getName() {
       return name;
    }
    
    @Override 
    public String toString() {
     
        return name + " " + kuerzel;
    }
    

    @Override
    public boolean equals(Object obj) {
        boolean state = false;
        if(obj instanceof Studienrichtung) {
            if(this.name.equals(((Studienrichtung)obj).getName()) && 
                    (this.kuerzel.equals(((Studienrichtung) obj).getKuerzel())))
                state = true;
        }
            return state;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.kuerzel);
        return hash;
    }
    
}
