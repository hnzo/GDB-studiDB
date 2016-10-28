
import edu.whs.gdb.entity.Modul;
import java.util.Objects;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry Rybinkin
 */
public class implModul implements Modul {

    private String kuerzel;
    private String name;
    private int vorlesung;
    private int uebung;
    private int praktikum;
    private int credits;
    
    public implModul(String kuerzel, String name, int vorlesung, int uebung, int praktikum, int credits) {
        this.kuerzel = kuerzel;
        this.name = name;
        this.vorlesung = vorlesung;
        this.uebung = uebung;
        this.praktikum = praktikum;
        this.credits = credits;
    }
    
    @Override
    public String getKuerzel() {
        return this.kuerzel;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getVorlesung() {
        return this.vorlesung;
    }

    @Override
    public int getUebung() {
        return this.uebung;
    }

    @Override
    public int getPraktikum() {
        return this.praktikum;
    }

    @Override
    public int getCredits() {
        return this.credits;
    }
    
    @Override
    public String toString() {
        return kuerzel + " - " + name;
    }
    
    @Override
    public boolean equals(Object obj) {
        
        boolean state = false;
        
        if(obj instanceof Modul) {
            if(this.name.equals(((Modul)obj).getName()))
                state = true;
        }
        return state;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
}
