package BlitzEdit.test;

import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import BlitzEdit.core.*;
import javafx.scene.shape.SVGPath;

public class CircuitSelectTest implements Runnable {

	@Override
	public void run() {
		System.out.println("Circuit Select Test: tests selection of Elements in a Circuit");
		
		ArrayList<Element> elements = new ArrayList<Element>();
		int [][] relPos = {{0, 10},{0, -10}};
		
		Component source = new Component(0, 0, (short)0, "Source", relPos, new SVGPath());
		Component resistor = new Component(100, 100, (short)0, "Resistor", relPos, new SVGPath());
		Component coil = new Component(200, 100, (short)0, "Coil", relPos, new SVGPath());
		
		elements.add(source);
		elements.add(coil);
		elements.add(resistor);
		
		Circuit circuit = new Circuit(elements, "Schaltplan 1");
		
		System.out.println("Select Elements: insert position (press q to quit)");
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		
		int x, y;
		String line;
		ArrayList<Element> elems;
		while (true)
		{
			try
			{
				System.out.println("posX:");
				if ((line = br.readLine()).matches("q"))
					break;
				x = Integer.parseInt(line);
				System.out.println("posY:");
				if ((line = br.readLine()).matches("q"))
					break;
				y = Integer.parseInt(line);
				if ((elems = circuit.getElementsByPosition(x, y)) == null)
				{
					System.out.println("Keine Elemente an Position.");
					continue;
				}
				int i = 0, j = 0;
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
				elems.clear();
			}
			catch (IOException e){}
		}//<-- while
	}//<-- void run()

}