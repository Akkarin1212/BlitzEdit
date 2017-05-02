package BlitzEdit.core;


import java.util.ArrayList;
import java.awt.Rectangle;
import BlitzEdit.core.Component;

public class Circuit
{
	//Gibt alle Elemente des Schaltplans zurück, die sich an der Position (x, y) befinden.
	public ArrayList<Element> getElementsByPosition(int x, int y)
	{
		ArrayList<Element> resultList = new ArrayList<Element>();
		for (Element element : _elements)
		{
			int ex = element.getX();
			int ey = element.getY();
			int esx = element.getSizeX();
			int esy = element.getSizeY();
			Rectangle rect = new Rectangle(ex, ey, esx, esy);
			
			if (rect.contains(x, y)) //checkt, ob element angeklickt wurde
			{
				resultList.add(element);
			}
		}
		
		if (resultList.isEmpty()) //kein Element an position
			return null;
		return resultList;
	}
	
	//Gibt alle Elemente des Schaltplans zurück, die sich im Rechteck befinden, dass durch
	//die Punkte (x1, y1) und (x2, y2) aufgespannt wird.
	public ArrayList<Element> getElementsByPosition(int x1, int y1, int x2, int y2)
	{
		ArrayList<Element> resultList = new ArrayList<Element>();
		Rectangle rect = new Rectangle(x1, y1, x2, y2);
		for(Element elem : _elements)
		{
			if (rect.contains(elem.getX(), elem.getY())) 
				resultList.add(elem);
		}
		
		if (resultList.isEmpty())
			return null;
		return resultList;
	}
	
	//Gibt alle Components zurück, deren Typnamen type entsprechen.
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
	
	public ArrayList<Element> getElements()
	{
		return new ArrayList<Element>(_elements);
	}
	
	//Fügt das Element elem an den Schaltplan an.
	public void addElement(Element elem)
	{
		if (elem != null)
			_elements.add(elem);
	}
	
	public void addElements(ArrayList<Element> elements)
	{
		if (elements != null)
		{
			for (Element elem : elements)
				_elements.add(elem);
		}
	}
	
	//Entfernt das Element elem aus dem Schaltplan.
	public void removeElement(Element elem)
	{
		if (elem != null)
		{
			for (Element element : _elements)
			{
				if (element == elem)
					_elements.remove(elem);
			}
		}
	}
	
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
	
	public Circuit(ArrayList<Element> elems, String name) 
	{
		_elements = new ArrayList<Element>(elems);
		_name = new String(name);
	}
	
	private ArrayList<Element> _elements;
	private String _name;
	private String _path;
}
