
import edu.whs.gdb.entity.Modul;
import edu.whs.gdb.entity.Praktikumsteilnahme;
import edu.whs.gdb.entity.Student;
import java.util.Objects;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry Rybinkin
 */
public class implPraktika implements Praktikumsteilnahme {

    private Student student;
    private Modul modul;
    private String semester;
    private boolean testat;
 
    
    public implPraktika(Student student, Modul modul, String semester, boolean testat) {
        this.student = student;
        this.modul = modul;
        this.semester = semester;
        this.testat = testat;
        
    }
    @Override
    public Student getStudent() {
        return this.student;
    }

    @Override
    public Modul getModul() {
        return this.modul;
    }

    @Override
    public String getSemester() {
        return this.semester;
    }

    @Override
    public boolean isTestat() {
        return this.testat;
    }

    @Override
    public void setTestat(boolean testat) {
        this.testat = testat;
    }
    
    @Override
    public String toString() {
        return "" + student + " " + modul + " " + semester + " " + testat; 
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean state = false;
        if(obj instanceof Praktikumsteilnahme){
            if(this.student.equals(((Praktikumsteilnahme)obj).getStudent()) && 
                    this.modul.equals(((Praktikumsteilnahme)obj).getModul()) && 
                    this.semester.equals(((Praktikumsteilnahme)obj).getSemester())) {
                state = true;
            }
        }
        return state;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.student);
        hash = 47 * hash + Objects.hashCode(this.modul);
        hash = 47 * hash + Objects.hashCode(this.semester);
        return hash;
    }
    
}
