package yamv;
/*Klasa koja predstavlja molekulu. Sadrži liste atoma i njihovih veza, te atribute popout numberOfAtoms i numberOfBonds.
 * Metode za dodavanje atoma/veza u liste te metode za vraćanje svih/ svih jedinstvenih atoma u molekuli.
 * */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Molecule {
	public int numberOfAtoms;
	public int numberOfBonds;
	public int numberOfUniqueAtoms;
	
	List<Atom> atoms = new ArrayList<Atom>();
	List<Bond> bonds = new ArrayList<Bond>();

	public Set getUniqueAtoms(){
		
		Set uniqueAtoms = new HashSet(atoms);
		return uniqueAtoms;
	};
	public void addAtom(String atomID, String elementType, double x, double y, double z){
		
		Atom tmp = new Atom(elementType);
		tmp.atomID = atomID;
		tmp.setCoords(x,y,z);
		atoms.add(tmp);
		}
	public List<Atom> getAtoms(){
		return atoms;
	};
	
	public void addBdond(int id1, int id2){
		Bond tmpB = new Bond();
		tmpB.id1 = id1;
		tmpB.id2 = id2;
		bonds.add(tmpB);
	
	}
	public List<Bond> getBonds(){
		return bonds;
	};
	
	public void printData(){
		for(int i = 0; i < atoms.size(); i++)
		System.out.println(atoms.get(i).elementType);
		
		for(int i = 0; i < bonds.size(); i++){
			System.out.println(bonds.get(i).id1);
		System.out.println(bonds.get(i).id2);
		}
		
		
	}
	
	
}