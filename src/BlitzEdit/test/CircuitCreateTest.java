package BlitzEdit.test;

import java.util.ArrayList;

import BlitzEdit.core.Circuit;
import BlitzEdit.core.Component;
import BlitzEdit.core.Connector;
import BlitzEdit.core.Element;
import javafx.scene.shape.*;

public class CircuitCreateTest implements Runnable {

	@Override
	public void run() {
		System.out.println("Circuit Create test: tests the Circuit constructors...\n\n");
		
		
		System.out.println("Creating Circuit by Constructor Circuit()...");
		Circuit circuit = new Circuit();
		System.out.println("circuit.name = " + circuit.getName() + "\n");

		System.out.println("Creating Circuit by Constructor Circuit(String)...");
		Circuit circuit2 = new Circuit("Schaltplan1");
		System.out.println("circuit2.name = " + circuit2.getName() + "\n");
		
		int[][] relPos = {{0, 1}, {0, -1}};
		ArrayList elements = new ArrayList<Element>();
		
		//elemente, die eingefügt werden sollen
		elements.add(new Component(1, 3, (short)0, "Resistor", relPos, new SVGPath()));
		elements.add(new Component(5, 8, (short)0, "Source", relPos, new SVGPath()));
		
		System.out.println("Creating Circuit by Constructor Circuit(ArrayList<Element>, String)");
		Circuit circuit3 = new Circuit(elements, "Schaltplan2");
		System.out.println("circuit3.name = " + circuit3.getName() + "\n");
		System.out.println("circuit3 contents:");
		ArrayList<Element> elems = circuit3.getElements();
		int i = 0, j = 0;
		for (Element e : elems)
		{
			if (e instanceof Component)
			{
				System.out.println(i++ + ": " + ((Component)e).getType() 
									+ ": (" +  e.getX() + ", " + e.getY() + ")"
									);
				ArrayList<Connector> e_connectors = ((Component)e).getConnectors();
				for (Connector con : e_connectors)
				{
					System.out.println("\tConnector " + j++ 
										+ "(" + con.getX() + ", " + con.getY() + ")");
				}
					
			}
		}
		System.out.println("\nCircuit Create Test complete.\n");
	}

}
