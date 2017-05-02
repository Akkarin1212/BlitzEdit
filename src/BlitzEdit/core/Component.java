package BlitzEdit.core;

import java.util.ArrayList;
import javafx.scene.shape.*;

public class Component extends RotatableElement
{
	public void setPosition(int x, int y)
	{
		for (Connector conn : _ports)
		{
			conn.setPosition(x + (conn.getX() - _posX), y + (conn.getY() - _posY));
		}
		_posX = x;
		_posY = y;
	}
	
	@Override
	public void move(int x, int y)
	{
		_posX += x;
		_posY += y;
		for (Connector conn : _ports)
		{
			conn.move(x, y);
		}
	}
	
	public ArrayList<Connector> getConnectors()
	{
		return _ports;
	}
	
	public boolean isConnected(Connector conn)
	{
		for (Connector c : _ports)
		{
			for (Connector co : c.getConnections())
			{
				if (co == conn)
					return true;
			}
		}
		return false;
	}
	
	public Connector getConnectorOfConnection(Connector conn)
	{
		for (Connector c : _ports)
		{
			for (Connector co : c.getConnections())
			{
				if (co == conn)
					return c;
			}
		}
		return null;
	}
	
	
	public Connector getConnectorOfConnection(Component comp)
	{
		for (Connector c : _ports)
		{
			for (Connector co : c.getConnections())
			{
				if (co.getOwner() == comp)
					return c;
			}
		}
		return null;
	}
	
	public ArrayList<Component> getConnectedComponents()
	{
		ArrayList<Component> resultList = new ArrayList<Component>();
		for (Connector c : _ports)
		{
			for(Connector co : c.getConnections())
			{
				if(!resultList.contains(co.getOwner()))
				{
					resultList.add(co.getOwner());
				}
			}
		}
		if (resultList.isEmpty())
			return null;
		return resultList;
	}
	
	public String getType()
	{
		return new String(_type);
	}
	
	public Component(int x, int y, short rot, String type, int[][] connRelPos, SVGPath svg)
	{
		super(x, y, rot);
		_type = new String(type);
		_svg = new SVGPath();
		_svg.setContent(svg.getContent());
		//Erstellt die Connectoren für die Komponente und setzt sie an
		//die richtige Position
		_ports = new ArrayList<Connector>();
		for (int i = 0; i < connRelPos.length; i++)
		{
			_ports.add(new Connector(x + connRelPos[i][0], y + connRelPos[i][1], this));
		}
	}
	
	private ArrayList<Connector> _ports;
	private SVGPath _svg;
	private String _type;
}