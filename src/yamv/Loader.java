package yamv;

/*Klasa loader služi za čitanje molekula iz cml fileova (readMolecule), te čitanje atributa zas pojedini kemijski element
 * iz baze(readProp). Pošto su cml fileovi slični xml fileovima, koristimo xml parser iz javinih biblioteka.
 * 
 * */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class Loader {
		 
		  public static void readMolecule(Molecule mol, String filename) {
		 //čitanje molekule i zapisivanje u molekulu mol
		    try {
		    	
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
		 
			doc.getDocumentElement().normalize();
		 
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		 
			NodeList aList = doc.getElementsByTagName("atom");
			mol.numberOfAtoms = aList.getLength();
			for (int temp = 0; temp < aList.getLength(); temp++) {
		 
				Node aNode = aList.item(temp);
		 
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
		 
				if (aNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) aNode;
		 
					String atomID = eElement.getAttribute("id");
					String elementType = eElement.getAttribute("elementType");
					double x = Double.parseDouble(eElement.getAttribute("x3"));
					double y = Double.parseDouble(eElement.getAttribute("y3"));
					double z = Double.parseDouble(eElement.getAttribute("z3"));
					
					mol.addAtom(atomID, elementType, x, y, z);
					
				}
			}
				
				NodeList bList = doc.getElementsByTagName("bond");
				mol.numberOfBonds = bList.getLength();
				for (int temp = 0; temp < bList.getLength(); temp++) {
			 
					Node bNode = bList.item(temp);
			 			 
					if (bNode.getNodeType() == Node.ELEMENT_NODE) {
			 
						Element eElement = (Element) bNode;
			 
						String id = eElement.getAttribute("atomRefs2");
						String[] ids = id.split("\\s+");
						
						int id1 = Integer.parseInt(ids[0].substring(1));
						int id2 = Integer.parseInt(ids[1].substring(1));

						
						mol.addBdond(id1, id2);
					}
				
				}
		    }
		     catch (Exception e) {
			e.printStackTrace();
		    }
		  }
		  
		  
		  
		  public static void readProp(Atom atom, String elementType){
			  	
			    try {
					 //čitanje iz baze
					File fXmlFile = new File("database.cml");
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(fXmlFile);
				 
					doc.getDocumentElement().normalize();
					//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

					NodeList nList = doc.getElementsByTagName("atom");
				 
					for (int temp = 0; temp < nList.getLength(); temp++) {
				 
						Node nNode = nList.item(temp);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				 
							Element eElement = (Element) nNode;

							if (eElement.getAttribute("type").equals(elementType)){
								double r = Double.parseDouble(eElement.getAttribute("r"));
								double g = Double.parseDouble(eElement.getAttribute("g"));
								double b = Double.parseDouble(eElement.getAttribute("b"));
								atom.setColor(r, g, b);
								
								atom.radius = Double.parseDouble(eElement.getAttribute("radius"));
								
							};

						}
					}
				    } catch (Exception e) {
					e.printStackTrace();
				    }
				  }
		  }
		