package blitzEdit.core;


import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import blitzEdit.application.SvgRenderer;
import javafx.scene.canvas.GraphicsContext;

public class Component extends RotatableElement
{
	public Component setPosition(int x, int y)
	{
		for (Connector conn : _ports)
		{
			conn.setPosition(x + (conn.getX() - _posX), y + (conn.getY() - _posY));
		}
		_posX = x;
		_posY = y;
		
		return this;
	}
	
	@Override
	public Component move(int x, int y)
	{
		for (Connector conn : _ports)
		{
			conn.setPosition(conn.getX() + x, conn.getY() + y);
		}
		_posX += x;
		_posY += y;
		
		return this;
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
	
	public boolean isConnected(Component comp)
	{
		
		for (Component c : getConnectedComponents())
		{
			if (c == comp)
				return true;
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
	
	public String getSvgFilePath()
	{
		return new String(_svgFilePath);
	}
	
	public String getSvgFileString()
	{
		return new String(_svgFileString);
	}
	
	@Override
	public void draw(GraphicsContext gc, double scale)
	{
		//TODO neue bool für selektierung statt false
		SvgRenderer.renderSvgString(getSvgFileString(), gc, getX(), getY(), scale, false);
	}
	
	@Override
	//Rotates Componenet to the specified angle
	public void setRotation(short rotation)
	{
		AffineTransform at = AffineTransform.getRotateInstance((Math.PI * 2)*((double)rotation / 360.0), _posX, _posY);

		//change connectors absolute position according to components rotation
		for (Connector con: getConnectors())
		{
			Point2D p = new Point(con.getX(), con.getY());
			Point2D p2 = new Point();
			at.transform(p, p2);
			con.setPosition((int)p2.getX(), (int)p2.getY());
		}
		_rotation = rotation;
	}
	
	//TODO Konstruktor mit double für width und height, circuit methoden funktionieren bisher nicht
	public Component(int x, int y, short rot, String type, int[][] connRelPos, String svgFilePath)
	{
		super(x, y, rot);
		_type = new String(type);
		_svgFilePath = svgFilePath;
		_svgFileString = SvgRenderer.getSvgFileString(_svgFilePath);
		//Erstellt die Connectoren für die Komponente und setzt sie an
		//die richtige Position
		_ports = new ArrayList<Connector>();
		for (int i = 0; i < connRelPos.length; i++)
		{
			_ports.add(new Connector(x + connRelPos[i][0], y + connRelPos[i][1], this));
		}
	}
	
	private ArrayList<Connector> _ports;
	private String _svgFilePath;
	private String _svgFileString;
	private String _type;
}
