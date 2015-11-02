 
package yamv;
import vtk.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.ArrayList;
import java.util.List;

public class Core extends JPanel implements ActionListener{
	
 
  private vtkPanel renWin;
  private vtkActor tubeActor;
  private List<vtkActor> sphereActor;
  private JPanel buttons;
  private JToggleButton bondsButton;
  private JButton openButton;
  private JButton exitButton;
  private JFileChooser fc;
  private File file;
  FileFilter filter = new FileNameExtensionFilter("cml file", new String[] {"cml"});
  Molecule mol;

  

    public Core() {
    	super(new BorderLayout());
    	//postavljanje početnih vrijednosti
    	fc = new JFileChooser();
    	mol = new Molecule();
    	renWin = new vtkPanel();
    	
    	tubeActor = new vtkActor();
    	renWin.GetRenderer().AddActor(tubeActor);

    	renWin.GetRenderer().SetBackground(0.1, 0.2, 0.4);    
    	renWin.GetRenderer().GetActiveCamera().Dolly(0.033); 	

        add(renWin, BorderLayout.CENTER);


        //***BUTTONS***
        buttons  = new JPanel();
    	buttons.setLayout(new GridLayout(1,0));
    	
    	openButton = new JButton("Open file");
        openButton.addActionListener(this);
    	bondsButton = new JToggleButton("Toggle bonds",false);
        bondsButton.addActionListener(this);
    	exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        
        buttons.add(openButton);
        buttons.add(bondsButton);
        buttons.add(exitButton);
        
        add(buttons, BorderLayout.NORTH);	
    	
    
    }
    
    public void loadEverything(){
    
    	//čitanje molekule iz datoteke
    Loader.readMolecule(mol, file.getAbsolutePath());


    List<Atom>uniqueAtoms = new ArrayList<>(mol.getUniqueAtoms());
	
   List<vtkAppendPolyData> apf = new ArrayList<vtkAppendPolyData>();
	
   //prvi dio VTK pipelinea, dodavanje sfera za svaki atom u molekuli
    for (int j = 0; j<uniqueAtoms.size();j++){
    	vtkAppendPolyData element = new vtkAppendPolyData();
    	apf.add(j,element);
    	for(int i = 0; i < mol.getAtoms().size(); i++){
    		
			if (uniqueAtoms.get(j).elementType.equals(mol.getAtoms().get(i).elementType)){
				
				vtkSphereSource tmp = new vtkSphereSource();
				tmp.SetRadius(mol.getAtoms().get(i).radius/140);
				tmp.SetPhiResolution(25);
				tmp.SetThetaResolution(25);
				
				tmp.SetCenter(mol.getAtoms().get(i).getCoords());
	
				apf.get(j).AddInput(tmp.GetOutput());
			
			}
    	}
    }
    /*Svaki sljedeći korak moramo raditi onoliko puta koliko ima jedinstvenih atoma u molekuli.
     *Razlog tome je potreba da prikažemo sfere u različitim bojama i različitih veličina, te nam onda za svaki tip sfere
     *treba poseban mapper i actor te renderer. Za veze se koristimo koordinatama atoma te vučemo takozvane Tube između njih.
     *Za sve veze je dovoljan jedan mapper, jedan actor i jedan renderer. */
	
    vtkAppendPolyData apfTube = new vtkAppendPolyData();
	
    vtkPolyData pd = new vtkPolyData();    
    vtkTubeFilter tubes = new vtkTubeFilter();  
    vtkCellArray polys = new vtkCellArray();   
    vtkPoints points = new vtkPoints();
    
    points.Allocate(10, 10);
    polys.Allocate(10, 10);
    pd.Allocate(10, 10);
	points.InsertNextPoint(0,0,0);

    for (int i = 0; i < mol.getAtoms().size();i++){
    	points.InsertNextPoint(mol.getAtoms().get(i).getCoords());
    }
    
    for (int i = 0; i < mol.getBonds().size();i++){
    	polys.InsertNextCell(2);
        polys.InsertCellPoint(mol.getBonds().get(i).id1);
        polys.InsertCellPoint(mol.getBonds().get(i).id2);
    }
    
     
    pd.SetLines(polys);
    pd.SetPoints(points);
    
    tubes.AddInput(pd);
    tubes.SetRadius(1 );
    tubes.SetNumberOfSides(9);
 
    apfTube.AddInput(tubes.GetOutput());    
    
    vtkPolyDataMapper tubeMapper = new vtkPolyDataMapper();
    tubeMapper.SetInput(pd);

    List<vtkPolyDataMapper> sphereMapper = new ArrayList<vtkPolyDataMapper>();
    
    for (int i = 0; i < uniqueAtoms.size(); i++){
    	vtkPolyDataMapper tmpSphereMapper = new vtkPolyDataMapper();
    	
    	tmpSphereMapper.SetInput(apf.get(i).GetOutput());
    	sphereMapper.add(tmpSphereMapper);
    }
    
     tubeActor = new vtkActor();
    tubeActor.SetMapper(tubeMapper);
    
    sphereActor = new ArrayList<vtkActor>();
    
    for (int i = 0; i < uniqueAtoms.size(); i++){
    	vtkActor tmpSphereActor = new vtkActor();
    	tmpSphereActor.SetMapper(sphereMapper.get(i));
    	tmpSphereActor.GetProperty().SetColor(uniqueAtoms.get(i).colorRGB);
    	//tmpSphereActor.GetProperty().EdgeVisibilityOn();
    	//tmpSphereActor.GetProperty().SetEdgeColor(0.7,0.7,0.7);
    	tmpSphereActor.GetProperty().SetInterpolationToFlat();
    	sphereActor.add(tmpSphereActor);
    }
 
	renWin.GetRenderer().AddActor(tubeActor);
    for (int i = 0; i < uniqueAtoms.size(); i++){
    	renWin.GetRenderer().AddActor(sphereActor.get(i));
    }
		
    add(renWin, BorderLayout.CENTER);
    
 
    }
    public static void main(String s[]) 
    {
        SwingUtilities.invokeLater(new Runnable() 
	{
            @Override
            public void run() 
	    {		JLabel label1 = new JLabel("   Rotate with left click, zoom with right click, move with middle click!");
                JFrame frame = new JFrame("Yet Another Molecule Viewer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(new Core(), BorderLayout.CENTER);
                frame.getContentPane().add(label1, BorderLayout.SOUTH);
                
                frame.setSize(800, 700);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    
//nadjačana metoda za klik gumba
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(openButton))
		{	fc.setFileFilter(filter); //učitavamo filter tako da prikazuje samo cml fileove te pozivamo file chooser
			 int returnVal = fc.showOpenDialog(Core.this);
			 
	            if (returnVal == JFileChooser.APPROVE_OPTION) {	            	
	                file = fc.getSelectedFile();
	                loadEverything();
	    			renWin.Render();

	            }
		}
		else if (e.getSource().equals(bondsButton))
		{
			if (bondsButton.isSelected())
				renWin.GetRenderer().RemoveActor(tubeActor);

			else
			renWin.GetRenderer().AddActor(tubeActor);

			renWin.Render();
		}
		/*exit button, end application */
		else if (e.getSource().equals(exitButton)) 
		{
	            System.exit(0);
	        }
		
	}
    
    
}