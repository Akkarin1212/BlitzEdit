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
			conn.setPosition((int)(x + (conn.getX() - _position.getX())), (int)(y + (conn.getY() - _position.getY())));
		}
		_position.setLocation(x, y);
		
		return this;
	}
	
	public Component setPosition(double x, double y)
	{
		return setPosition((int)x, (int)y);
	}
	
	@Override
	public Component move(int x, int y)
	{
		for (Connector conn : _ports)
		{
			conn.setPosition((conn.getX() - getX()) + x, (conn.getY() - getY()) + y);
		}
		_position.move(x, y);
		
		return this;
	}
	
	public Component move(double x, double y)
	{
		return move((int)x, (int)y);
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
	//Draws Component on GraphicalContext
	public void draw(GraphicsContext gc, double scale, boolean selected)
	{
		SvgRenderer.renderSvgString(getSvgFileString(), gc, getX(), getY(), scale);
		gc.setStroke(Color.GRAY);
		if (selected) {
			gc.strokeRect(_position.getX()-((double)_sizeX/2+2), 
					(double)_position.getY()-((double)_sizeY/2+2), 
					(double)_sizeX+4, (double)_sizeY+4);
		}
		for (Connector conn : getConnectors())
			conn.draw(gc, scale);
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
	
	public String getSVG()
	{
		return new String(_svgFilePath);
	}
	
	//Returns the connector that is connected to conn
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
	
	//Returns the connector that is connected to comp
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
	
	public Component(int x, int y, short rot, String type, int[][] connRelPos, short [] connRelRot ,String svg)
	{
		super(x, y, rot);
		_type = new String(type);
		_svgFilePath = new String(svg);
		
		//Erstellt die Connectoren für die Komponente und setzt sie an
		//die richtige Position
		_ports = new ArrayList<Connector>();
		for (int i = 0; i < connRelPos.length; i++)
		{
			_ports.add(new Connector(x + connRelPos[i][0], y + connRelPos[i][1], connRelPos[i], connRelRot[i], this));
		}
		rotate(rot);
	}
	
	public Component(int x, int y, int sizeX, int sizeY, short rot, String type,
						int[][] connRelPos, short [] connRelRot ,String svg)
	{
		super(x, y , sizeX, sizeY, rot);
		_type = new String(type);
		_svgFilePath = new String(svg);
		
		//Erstellt die Connectoren für die Komponente und setzt sie an
		//die richtige Position
		_ports = new ArrayList<Connector>();
		for (int i = 0; i < connRelPos.length; i++)
		{
			_ports.add(new Connector(x + connRelPos[i][0], y + connRelPos[i][1], connRelPos[i], connRelRot[i], this));
		}
		rotate(rot);
	}
	
	@Override
	//Rotates Componenet to the specified angle
	public void rotate(short rotation)
	{
		// Erstellt AffineTransform Objekt, um die connectoren um den schwerpunkt der component zu drehen
		// die rotation ist negativ, damit nach rechts gedreht wird
		AffineTransform at = AffineTransform.getRotateInstance((-((double)rotation / 360.0) * (Math.PI * 2)) 
				, _position.getX(), _position.getY());						

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
	
	private ArrayList<Connector> _ports;
	private String _svgFilePath;
	private String _svgFileString;
	private String _type;
}
