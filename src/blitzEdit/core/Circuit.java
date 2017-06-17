package blitzEdit.core;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.event.AncestorEvent;

/**
 * Represents an electric Circuit
 * 
 * @author David Schick
 * 
 */
public class Circuit
{
	/**
	 * Returns all Elements at the designated Location
	 * 
	 * @param x x-Component of Location
	 * @param y y-Component of Location
	 * @return List of Elements at Location
	 */
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
	
	/**
	 * checks, if an {@link Element} is already existing in the circuit
	 * @param elem {@link Element}
	 * @return true if element is contained, else false
	 */
	public boolean containsElement(Element elem)
	{
		return _elements.contains(elem);
	}
	
	/**
	 * Returns all Elements at the designated Location
	 * 
	 * @param x x-Component of Location
	 * @param y y-Component of Location
	 * @return List of Elements at Location
	 */
	public ArrayList<Element> getElementsByPosition(double x, double y)
	{
		return getElementsByPosition((int)x, (int)y);
	}
	
	
	/**
	 * Returns overall width of Circuit
	 * @return overall width of Circuit
	 */
	public int getWidth()
	{
		int width = 0, elemWidth = 0;
		for (Element elem : _elements)
		{
			elemWidth = elem.getX() + elem.getSizeX()/2;
			if (elemWidth > width)
				width = elemWidth;
		}
		return width;
	}
	
	/**
	 * Returns overall height of Circuit
	 * @return overall height of Circuit
	 */
	public int getHeight()
	{
		int height = 0, elemHeight = 0;
		for (Element elem : _elements)
		{
			elemHeight = elem.getY() + elem.getSizeY()/2;
			if (elemHeight > height)
				height = elemHeight;
		}
		return height;
	}
	
	/**
	 * Returns all Elements in the designated Rectangle
	 *  
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return List of all Elements in rectangle
	 */
	public ArrayList<Element> getElementsByPosition(int x, int y, int width, int height)
	{
		ArrayList<Element> resultList = new ArrayList<Element>();
		Rectangle rect = new Rectangle(x, y, width, height);
		for(Element elem : _elements)
		{
			if (elem.intersects(rect)) 
				resultList.add(elem);
		}
		
		if (resultList.isEmpty())
			return null;
		return resultList;
	}
	
	/**
	 * Returns all Elements in the designated Rectangle
	 *  
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return List of all Elements in rectangle
	 */
	public ArrayList<Element> getElementsByPosition(double x, double y, double width, double height)
	{
		return getElementsByPosition((int)x, (int)y, (int)width, (int)height);
	}
	
	/**
	 * Returns all {@link Element Elements} with the designated typename
	 * 
	 * @param type typename
	 * @return List of all Elements with typename
	 */
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
	
	/**
	 * Returns all {@link Element Elements} of Circuit
	 * @return List of all Elements
	 */
	public ArrayList<Element> getElements()
	{
		return _elements;
	}
	
	/**
	 * Adds an {@link Element} to Circuit
	 * @param elem {@link Element} to be added
	 */
	public void addElement(Element elem)
	{
		if (elem == null || _elements.contains(elem))
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
	
	/**
	 * Adds multiple {@link Element Elements} to Circuit
	 * @param elements new Elements for Circuit
	 */
	public void addElements(Collection<Element> elements)
	{
		if (elements != null)
		{
			for (Element elem : elements)
			{
				if (_elements.contains(elem))
					continue;
				/*
				if (elem instanceof Component)
				{
					for (Connector con : ((Component)elem).getConnectors())
					{
						_elements.add(con);
					}
				}
				*/
				
				_elements.add(elem);
			}
		}
	}
	
	/**
	 * Removes Element from Circuit
	 * @param elem {@link Element} to be removed from Circuit
	 */
	public void removeElement(Element elem)
	{
		if (elem != null)
		{
			if (elem instanceof Connector)
				removeElement(((Connector)elem).getOwner());
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

				for (Connector c1 : comp.getConnectors()) {
					for (Connector c2 : c1.getConnections())
					{
						c2.disconnect(c1);
					}
					newElements.remove(c1);
				}

			}
			
			_elements = newElements;
		}
	}
	
	/**
	 * Removes all {@link Element Elements} in Circuit
	 */
	public void clearElements()
	{
		_elements.clear();
	}
	
	/**
	 * Returns the filesystem path of Circuit
	 * @return path of Circuit
	 */
	public String getPath()
	{
		return new String(_path);
	}
	
	/**
	 * Returns the name of Circuit
	 * @return name of circuit
	 */
	public String getName()
	{
		return new String(_name);
	}
	
	/**
	 * Returns all Lines in Circuit
	 * @return {@link Line Lines} in Circuit
	 */
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
						Connector conn1 = c1;
						Connector conn2 = c2;
						
						lines.add(new Line(conn1, conn2));
					}
				}
			}
		}
		
		ArrayList<Line> linesToRemove = new ArrayList<Line>();
		// Verschachtelte Schleife, die die vorhnadenen duplette aus der Liste
		// filtert.
		ArrayList<Line> toRemove = new ArrayList<Line>(); //prevent ConcurrentComodificationException
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
						if (!linesToRemove.contains(l1))
							linesToRemove.add(l2); //wird sie aus der Liste entfernt
				}
			}
		}
		lines.removeAll(linesToRemove);
		return lines;
	}
	
	/**
	 * Defaultcontructor
	 */
	public Circuit() 
	{
		_elements = new ArrayList<Element>();
		_name = new String("New Circuit");
	}
	
	/**
	 * Constructs new Circuit
	 * @param name name of Circuit
	 */
	public Circuit(String name) 
	{
		_elements = new ArrayList<Element>();
		_name = new String(name);
	}
	
	/**
	 * Constructs new Circuit
	 * 
	 * @param elems {@link Element Elements} to be added
	 * @param name name of circuit
	 */
	public Circuit(Collection<Element> elems, String name) 
	{
		_elements = new ArrayList<Element>();
		addElements(elems);
		_name = new String(name);
	}
	
	/**
	 * Updates blueprint list in Circuit
	 * 
	 * @param  blueprints {@link ComponentBlueprint blueprints} to be added
	 */
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
