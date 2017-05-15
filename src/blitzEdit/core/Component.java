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
		SvgRenderer.renderSvgString(getSvgFileString(), gc, getX(), getY(), scale, selected);
		for (Connector conn : getConnectors())
			conn.draw(gc, scale, selected); // TODO event. check ob connector selektiert wurde
	}
	
	
	public Element clone()
	{

		int[][] connRelPos = new int[_ports.size()][];
		for(int i = 0; i < connRelPos.length; i++)
		{
			connRelPos[i] = _ports.get(i).getRelPos();
		}
		
		short[] connRelRot = new short[_ports.size()];
		for(int i = 0; i < connRelRot.length; i++)
		{
			connRelRot[i] = _ports.get(i).getRelativeRotation();
		}
		
		return new Component(getX(), getY(), _sizeX, _sizeY, _rotation, _type, connRelPos, connRelRot, _svgFilePath);
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
		initialize(x, y, rot, type, svg, connRelPos, connRelRot);
		super.setSize(SvgRenderer.getSvgWidth(_svgFileString), SvgRenderer.getSvgHeight(_svgFileString));
	}
	
	
	public Component(int x, int y, int sizeX, int sizeY, short rot, String type,
						int[][] connRelPos, short [] connRelRot ,String svg)
	{
		super(x, y , sizeX, sizeY, rot);
		initialize(x, y, rot, type, svg, connRelPos, connRelRot);
	}
	
	public Component(double x, double y, double sizeX, double sizeY, double rot, String type, int[][] connRelPos, short[] connRelRot,
			String svg) 
	{
		super((int)x, (int)y, (int)sizeX, (int)sizeY, (short)rot);
		initialize((int)x, (int)y, (short)rot, type, svg, connRelPos, connRelRot);
	}
	
	//Private method called by constructor
	private void initialize(int x, int y, short rot, String type, String svg, int[][] connRelPos, short[] connRelRot)
	{
		_type = new String(type);
		_svgFilePath = new String(svg);
		_svgFileString = SvgRenderer.getSvgFileString(_svgFilePath);
		_ports = new ArrayList<Connector>();
		for (int i = 0; i < connRelPos.length; i++) {
			_ports.add(new Connector(x + connRelPos[i][0], y + connRelPos[i][1], connRelPos[i], connRelRot[i], this));
		}
		rotate(rot);
	}
	
	// @Override TODO rotate in element/rotatableElement?
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
