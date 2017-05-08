package blitzEdit.test;

import blitzEdit.core.*;
import java.util.ArrayList;


public class CircuitAddElementTest implements Runnable
{

	@Override
	public void run() {
		int [][] relPos = {{0, 1},{0, -1}};
		
		System.out.println("Add Element Test:\n Creates a Circuit, adds Elements to it and renames it.");
		System.out.println("Create new Circuit...");
		Circuit circuit = new Circuit();
		
		System.out.println("Create new Elements...");
		System.out.println("Source (0, 0)");
		Component source = new Component(0, 0, (short)0, "Source", relPos, new String());
		System.out.println("Resistor (10, 10)");
		Component resistor = new Component(10, 10, (short)0, "Resistor", relPos, new String());
		System.out.println("Coil (20, 10)");
		Component coil = new Component(20, 10, (short)0, "Coil", relPos, new String());
		
		System.out.println("Adding new Elements separatly\n");
		circuit.addElement(source);
		circuit.addElement(resistor);
		circuit.addElement(coil);
		
		System.out.println("Content of circuit: ");
		
		int i = 0, j = 0;
		ArrayList<Element> elems = circuit.getElements(); 
		for (Element e : elems)
		{
			if (e instanceof Component)
			{
				System.out.println(i++ + ": " + ((Component)e).getType() 
									+ ": (" +  e.getX() + ", " + e.getY() + ")"
									);
				ArrayList<Connector> e_connectors = ((Component)e).getConnectors();
				j = 0;
				for (Connector con : e_connectors)
				{
					System.out.println("\tConnector " + j++ 
										+ "(" + con.getX() + ", " + con.getY() + ")");
				}
					
			}
		}
		
		circuit.clearElements();
		
		System.out.println("\nAdding new Elements as list:\n");
		ArrayList<Element> components = new ArrayList<Element>();
		components.add(source);
		components.add(resistor);
		components.add(coil);
		
		circuit.addElements(components);
		
		System.out.println("Content of circuit: ");

		i = 0; j = 0;
		elems = circuit.getElements(); 
		for (Element e : elems)
		{
			if (e instanceof Component)
			{
				System.out.println(i++ + ": " + ((Component)e).getType() 
									+ ": (" +  e.getX() + ", " + e.getY() + ")"
									);
				ArrayList<Connector> e_connectors = ((Component)e).getConnectors();
				j = 0;
				for (Connector con : e_connectors)
				{
					System.out.println("\tConnector " + j++ 
										+ "(" + con.getX() + ", " + con.getY() + ")");
				}
					
			}
		}
		System.out.println("\n\nCircuit Add Element Test complete.\n");
	} // <-- public void run()
	
} // <-- class CircuitAddElementTest
