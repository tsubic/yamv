package yamv;
/*Klasa koja predstavlja jedan atom u molekuli.
 * Sadrži parametre poput ID-a atoma, tipa kemijskog elementa te njegovog radijusa i boje(RGB).
 * Također sadrži i 3D koordinate atoma.
 * Metode poput getColor() i getCoords(), gdje nazivi metoda govore za sebe.
 * Konstruktor se poziva s kraticom kemijskog elementa kojeg je atom, te onda on iz baze vuče podatke o tom kemijskom elementu
 * kap što su boje i radijus.
 * */
public class Atom{
	//naziv elementa
	public String atomID;
	public String elementType;
	
	public double radius;
	//boja 
	public String color;
	//XYZ koordinate
	public double[] coords = new double[3];
	public double[] colorRGB = new double[3];
	
	public double[] getColor(){return colorRGB;};
	public void setColor(double r, double g, double b){
		colorRGB[0] = r;
		colorRGB[1] = g;
		colorRGB[2] = b;
	}
	
	public double[] getCoords(){return coords;};
	public void setCoords(double x, double y, double z){
		coords[0] = x;
		coords[1] = y;
		coords[2] = z;
	};
	
	public void setColor(){	Loader.readProp(this, elementType);};
	
	public Atom(String elementType){
		
		this.elementType = elementType;
		Loader.readProp(this, elementType);
				
	};
	
}