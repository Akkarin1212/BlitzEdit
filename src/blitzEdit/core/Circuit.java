package blitzEdit.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.awt.Rectangle;
import blitzEdit.core.Component;

// Circuit Klasse:
// Speichert Bauelemente und deren Verbindungen
// und regelt den Zugriff auf diese
public class Circuit
{
	//Gibt alle Elemente des Schaltplans zurÃ¼ck, die sich an der Position (x, y) befinden.
	public ArrayList<Element> getElementsByPosition(int x, int y)
	{
		ArrayList<Element> resultList = new ArrayList<Element>();
		for (Element element : _elements)
		{
			if (element.contains(x, y)) //checkt, ob element angeklickt wurde
			{
				resultList.add(element);
			}
		}
		
		if (resultList.isEmpty()) //kein Element an position
			return null;
		return resultList;
	}
	
	public ArrayList<Element> getElementsByPosition(double x, double y)
	{
		return getElementsByPosition((int)x, (int)y);
	}
	
	//Gibt alle Elemente des Schaltplans zurÃ¼ck, die sich im Rechteck befinden, dass durch
	//die Punkte (x1, y1) und (x2, y2) aufgespannt wird.
	public ArrayList<Element> getElementsByPosition(int x1, int y1, int x2, int y2)
	{
		ArrayList<Element> resultList = new ArrayList<Element>();
		Rectangle rect = new Rectangle(x1, y1, x2, y2);
		for(Element elem : _elements)
		{
			if (elem.intersects(rect)) 
				resultList.add(elem);
		}
		
		if (resultList.isEmpty())
			return null;
		return resultList;
	}
	
	public ArrayList<Element> getElementsByPosition(double x1, double y1, double x2, double y2)
	{
		return getElementsByPosition((int)x1, (int)y1, (int)x2, (int)y2);
	}
	
	//Gibt alle Components zurÃ¼ck, deren Typnamen type entsprechen.
	public ArrayList<Element> getElementsByPosition(String type)
	{
		ArrayList<Element> resultList = new ArrayList<Element>();
		for(Element elem : _elements)
		{
			if (!(elem instanceof Component)) 
				continue;
			if (((Component)elem).getType().matches(type))
				resultList.add(elem);
		}
		
		if (resultList.isEmpty())
			return null;
		return resultList;
	}
	
	//Gibt Liste aller Elemente zurück (keine Kopie!)
	public ArrayList<Element> getElements()
	{
		return _elements;
	}
	
	//FÃ¼gt das Element elem an den Schaltplan an.
	public void addElement(Element elem)
	{
		if (elem == null)
			return;
		if (elem instanceof Component)
		{
			for (Connector con : ((Component)elem).getConnectors())
			{
				_elements.add(con);
			}
		}
		_elements.add(elem);
	}
	
	//Fügt alle Elemente der übergebenene Collection in den Schaltplan ein
	//(sofern diese noch nicht im Schlatplan vorhanden sind)
	public void addElements(Collection<Element> elements)
	{
		if (elements != null)
		{
			for (Element elem : elements)
			{
				if (elem instanceof Component)
				{
					for (Connector con : ((Component)elem).getConnectors())
					{
						_elements.add(con);
					}
				}
				_elements.add(elem);
			}
		}
	}
	
	//Entfernt das Element elem aus dem Schaltplan.
	public void removeElement(Element elem)
	{
		if (elem != null)
		{
			ArrayList<Element> newElements = new ArrayList<Element>();
			for (Element element : _elements)
			{
				if (element != elem)
				{
					newElements.add(element);
				}
			}
			// delete connections too
			if (elem.getClass() == Component.class) {
				Component comp = (Component) elem;

				for (Connector c : comp.getConnectors()) {
					newElements.remove(c);
				}

			}
			
			_elements = newElements;
		}
	}
	
	//Entfernt alle Elemente aus dem Schaltplan
	public void clearElements()
	{
		_elements.clear();
	}
	
	public String getPath()
	{
		return new String(_path);
	}
	
	public String getName()
	{
		return new String(_name);
	}
	
	public ArrayList<Line> getLines()
	{
		ArrayList<Line> lines = new ArrayList<Line>();
		for (Element e: _elements)
		{
			if (e instanceof Component)
			{
				//geht alle Verbindungen des Bauteils durch
				for (Connector c1 : ((Component)e).getConnectors())
				{
					// geht alle verbundenen Connectoren durch
					for (Connector c2 : c1.getConnections())
					{
						// f�gt eine neue Linie vom Start zum Endpunkt in die
						// R�ckgabeliste ein
						lines.add(new Line(c1.getPosition(), c2.getPosition()));
					}
				}
			}
		}
		
		ArrayList<Line> linesToRemove = new ArrayList<Line>();
		// Verschachtelte Schleife, die die vorhnadenen duplette aus der Liste
		// filtert.
		for (Line l1 : lines)
		{
			for (Line l2 : lines)
			{
				// Referenzvergleich: soll verhindern, dass eine Leitung mit sich
				// selbst verglichen wird
				if (l1 != l2)
				{
					//wenn die Leitungen die selben Punkte hat
					if (l1.equals(l2))
						linesToRemove.remove(l2); //wird sie aus der Liste entfernt
				}
			}
		}
		lines.removeAll(linesToRemove);
		return lines;
	}
	
	public Circuit() 
	{
		_elements = new ArrayList<Element>();
		_name = new String("New Circuit");
	}
	
	public Circuit(String name) 
	{
		_elements = new ArrayList<Element>();
		_name = new String(name);
	}
	
	public Circuit(Collection<Element> elems, String name) 
	{
		_elements = new ArrayList<Element>();
		addElements(elems);
		_name = new String(name);
	}
	
	public void updateBlueprints(Collection<ComponentBlueprint> blueprints)
	{
		_blueprints.clear();
		_blueprints.addAll(blueprints);
	}
	
	private ArrayList<ComponentBlueprint> _blueprints;
	
	private ArrayList<Element> _elements;
	private String _name;
	private String _path;
}
