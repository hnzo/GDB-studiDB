
import edu.whs.gdb.entity.Student;
import java.util.Objects;


/**
 *
 * @author Dmitry Rybinkin
 */
public class implStudent implements Student {

    private String matrikelNr;
    private String name;
    private String vorname;
    private String adresse;
    private String SRKuerzel;
    
    public implStudent(String matrikelNr, String name, String vorname, String adresse, String SRKuerzel) {
        this.matrikelNr = matrikelNr;
        this.name = name;
        this.vorname = vorname;
        this.adresse = adresse;
        this.SRKuerzel = SRKuerzel;
    }
    
    @Override
    public String getMatrikel() {
        return this.matrikelNr;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getVorname() {
        return this.vorname;
    }

    @Override
    public String getAdresse() {
        return this.adresse;
    }

    @Override
    public String getStudienrichtungKuerzel() {
        return this.SRKuerzel;
    }
    
    @Override
    public String toString() {
        
        return vorname + " " + name;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean state = false;
        if(obj instanceof Student) {
            if(this.name.equals(((Student)obj).getName()) && 
                    this.vorname.equals(((Student)obj).getVorname()) && 
                    this.matrikelNr.equals(((Student)obj).getMatrikel())) {
                state = true;
            }
        }
        return state;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.matrikelNr);
        hash = 61 * hash + Objects.hashCode(this.name);
        hash = 61 * hash + Objects.hashCode(this.vorname);
        return hash;
    }


    
}
