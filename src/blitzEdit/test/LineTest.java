package blitzEdit.test;

import java.util.ArrayList;

import blitzEdit.core.*;

public class LineTest implements Runnable {

	@Override
	public void run() 
	{

		ArrayList<Element> elements = new ArrayList<Element>();
		int [][] relPos = {{0, 10},{0, -10}};
		short [] relRot = {0, 0};
		
		Component source = new Component(0, 0, (short)0, "Source", relPos, relRot, new String());
		Component resistor = new Component(100, 100, (short)0, "Resistor", relPos, relRot, new String());
		Component coil = new Component(200, 100, (short)0, "Coil", relPos, relRot, new String());
		
		elements.add(source);
		elements.add(coil);
		elements.add(resistor);
		
		Circuit circuit = new Circuit(elements, "Schaltplan 1");
		
		source.getConnectors().get(0).connect(resistor.getConnectors().get(0));
		source.getConnectors().get(1).connect(coil.getConnectors().get(0));
		coil.getConnectors().get(1).connect(resistor.getConnectors().get(1));
		
		for ( Line l : circuit.getLines())
		{
			System.out.println(l.getC1().getPosition() + " ---> " + l.getC2().getPosition());
		}
		
	}
	
	public LineTest()
	{}
}
