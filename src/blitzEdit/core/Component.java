package blitzEdit.core;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.canvas.GraphicsContext;
import tools.SelectionMode;
import tools.SvgRenderer;

/**
 * Represents an electrical Component
 * @author David Schick
 *
 */
public class Component extends RotatableElement
{
	/**
	 * moves the Component to the designated Location
	 * @param x x-coordinate of new Location
	 * @param y y-coordinate of new Location
	 */
	@Override
	public Component setPosition(int x, int y)
	{
		for (Connector conn : _ports)
		{
			conn.setPosition((int)(x + (conn.getX() - _position.getX())), (int)(y + (conn.getY() - _position.getY())));
		}
		_position.setLocation(x, y);
		
		return this;
	}
	/**
	 * moves the Component to the designated Location
	 * @param x x-coordinate of new Location
	 * @param y y-coordinate of new Location
	 */
	@Override
	public Component setPosition(double x, double y)
	{
		return setPosition((int)x, (int)y);
	}
	
	/**
	 * moves the Component to the designated Location
	 * @param x x-coordinate of new Location
	 * @param y y-coordinate of new Location
	 */
	@Override
	public Component move(int x, int y)
	{
		for (Connector conn : _ports)
		{
			conn.setPosition((conn.getX() - getX()) + x, (conn.getY() - getY()) + y);
		}
		_position.setLocation(x, y);
		
		return this;
	}
	
	/**
	 * moves the Component to the designated Location
	 * @param x x-coordinate of new Location
	 * @param y y-coordinate of new Location
	 */
	public Component move(double x, double y)
	{
		return move((int)x, (int)y);
	}
	
	/**
	 * returns Path of Components svg-image
	 * @return path of svg image
	 */
	public String getSvgFilePath()
	{
		return new String(_svgFilePath);
	}
	
	public String getSvgFileString()
	{
		return new String(_svgFileString);
	}
	
	
	@Override
	public void draw(GraphicsContext gc, double scale, SelectionMode mode)
	{
		SvgRenderer.renderSvgString(getSvgFileString(), gc, getX(), getY(), scale, getRotation(), mode);
	}
	
	/**
	 * Returns deep copy of this Component
	 * @return deep copy of this Component
	 */
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
	
	/**
	 * Returns all {@link Connector Connectors} of this Component
	 * @return List of Connectors
	 */
	public ArrayList<Connector> getConnectors()
	{
		return _ports;
	}
	
	/**
	 * Checks, if one of the Components {@link Connector Connectors}
	 * is connected to the designated Connector
	 * @param conn Connector to be checked for Connection
	 * @return true if Component is Connected, else false
	 */
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
	
	/**
	 * Checks, if one of the Components {@link Connector Connectors}
	 * is connected to the designated Component
	 * @param conn Component to be checked for connection
	 * @return true if Component is connected, else false
	 */
	public boolean isConnected(Component comp)
	{
		for (Component c : getConnectedComponents())
		{
			if (c == comp)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the {@link Connector} which is connected to conn
	 * @param conn {@link Connector} to be checked for connection
	 * @return {@link Connector} of connection
	 */
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
	
	/**
	 * Returns the {@link Connector} which is connected to Component comp
	 * @param comp Component to be checked for Connection
	 * @return {@link Connector} of connection
	 */
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
	
	/**
	 * Returns List of all connected Components
	 * @return list of connected Components
	 */
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
	
	/**
	 * Returns a String containing the typename of this Component
	 * @return typename of Component
	 */
	public String getType()
	{
		return new String(_type);
	}
	
	/**
	 * Adds a {@link Connector} to this Component
	 * @param conn {@link Connector} to be added
	 */
	public void addConnenctor(Connector conn)
	{
		conn.setOwner(this);
		_ports.add(conn);
	}
	
	@Override
	public void setRotation(short rotation)
	{
		short rot = (short)(rotation - _rotation);
		rotate(rot);
	}
	
	@Override
	public void rotate(short rotation)
	{
		// Erstellt AffineTransform Objekt, um die connectoren um den schwerpunkt der component zu drehen
		// die rotation ist negativ, damit nach rechts gedreht wird
		
		AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(rotation),
																_position.getX(), _position.getY());						

		//change connectors absolute position according to components rotation
		if (getConnectors() != null)
		{
			for (Connector con : getConnectors())
			{
				Point2D p = new Point(con.getX(), con.getY());
				Point2D p2 = new Point();
				at.transform(p, p2);
				con.setPosition((int) p2.getX(), (int) p2.getY());
			}
		}
		super.rotate(rotation);
	}
	
	/**
	 * Returns List Containing all {@link ComponentProperty Properties} of this Component
	 * @return
	 */
	public ArrayList<ComponentProperty> getProperties()
	{
		return new ArrayList<ComponentProperty>(_properties);
	}
	
	/**
	 * Adds a {@link ComponentProperty} to this Component
	 * @param prop {@link ComponentProperty} to be added
	 */
	public void addProperty(ComponentProperty prop)
	{
		_properties.add(prop);
	}
	
	/**
	 * Returns the {@link ComponentProperty} that matches name
	 * @param name name of the searched {@link ComponentProperty}
	 * @return {@link ComponentProperty} with the name of parameter name
	 */
	public ComponentProperty getProperty(String name)
	{
		for (ComponentProperty prop : _properties)
		{
			if (prop.getName().matches(name))
				return new ComponentProperty(prop);
		}
		return null;
	}
	
	/**
	 * Set the value of a {@link ComponentProperty}
	 * 
	 * @param name name of the property to be update
	 * @param value new value for property
	 */
	public void setProperty(String name, String value)
	{
		for (ComponentProperty prop : _properties)
		{
			if (prop.getName().matches(name))
			{
				try
				{
					prop.setValue(value);
				}
				catch (IOException e)
				{
					return;
				}
			}
		}
	}
	
	/**
	 * Redefines all Properties of this Component
	 * 
	 * @param properties new Properties
	 */
	public void setProperties(Collection<ComponentProperty> properties)
	{
		_properties = new ArrayList<ComponentProperty>(properties);
	}
	
	/**
	 * Private method, called by constructor to initialize the fields of
	 * the new Component Object
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param rot rotation
	 * @param type typename
	 * @param svg path of svg-image
	 * @param connRelPos Array, containing the relative positions of this Components {@link Connector Connectors}
	 * @param connRelRot Array, containing the relative rotations of this Components {@link Connector Connectors}
	 */
	private void initialize(int x, int y, short rot, String type, String svg, int[][] connRelPos, short[] connRelRot) 
	{
		_type = new String(type);
		_svgFilePath = new String(svg);
		_svgFileString = SvgRenderer.getSvgFileString(_svgFilePath);
		_ports = new ArrayList<Connector>();
		_properties = new ArrayList<ComponentProperty>();
		for (int i = 0; i < connRelPos.length; i++) 
		{
			_ports.add(new Connector(x + connRelPos[i][0], y + connRelPos[i][1], connRelPos[i], connRelRot[i], this));
		}
		setRotation(rot);
	}

	/**
	 * Private method, called by constructor to initialize the fields of
	 * the new Component Object
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param rot rotation
	 * @param type typename
	 * @param svg path of svg-image
	 */
	private void initialize(int x, int y, short rot, String type, String svg) 
	{
		_type = new String(type);
		_svgFilePath = new String(svg);
		_svgFileString = SvgRenderer.getSvgFileString(_svgFilePath);
		_ports = new ArrayList<Connector>();
		_properties = new ArrayList<ComponentProperty>();
	}
	
	/**
	 * Constructs new Component
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param rot rotation
	 * @param cb {@link ComponentBlueprint} specifying the archetype of the new Component
	 */
	public Component(int x, int y, short rot, ComponentBlueprint cb)
	{
		super (x, y, rot);
		initialize(x, y, rot, cb.getType(), cb.getSvgFilePath(), cb.getRelPos(), cb.getConRelRot());
	}
	
	/**
	 * Constructs new Component
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param rot rotation
	 * @param type typename
	 * @param connRelPos Array, containing the relative positions of this Components {@link Connector Connectors}
	 * @param connRelRot Array, containing the relative rotations of this Components {@link Connector Connectors}
	 * @param svg path of svg-image
	 */
	public Component(int x, int y, short rot, String type, int[][] connRelPos, short [] connRelRot, String svg)
	{
		super(x, y, rot);
		initialize(x, y, rot, type, svg, connRelPos, connRelRot);
		super.setSize(SvgRenderer.getSvgWidth(_svgFileString), SvgRenderer.getSvgHeight(_svgFileString));
	}
	
	/**
	 * Constructs new Component
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param rot rotation
	 * @param type typename
	 * @param svg path of svg-image
	 */
	public Component(int x, int y, short rot, String type, String svg)
	{
		super(x, y, rot);
		initialize(x, y, rot, type, svg);
		super.setSize(SvgRenderer.getSvgWidth(_svgFileString), SvgRenderer.getSvgHeight(_svgFileString));
	}
	
	/**
	 * Constructs new Component
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param sizeX size in x-direction
	 * @param sizeY size in y-direction
	 * @param rot rotation
	 * @param type typename
	 * @param connRelPos Array, containing the relative positions of this Components {@link Connector Connectors}
	 * @param connRelRot Array, containing the relative rotations of this Components {@link Connector Connectors}
	 * @param svg path of svg-image
	 */
	public Component(int x, int y, int sizeX, int sizeY, short rot, String type,
						int[][] connRelPos, short [] connRelRot ,String svg)
	{
		super(x, y , sizeX, sizeY, rot);
		initialize(x, y, rot, type, svg, connRelPos, connRelRot);
	}
	
	/**
	 * Constructs new Component
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param sizeX size in x-direction
	 * @param sizeY size in y-direction
	 * @param rot rotation
	 * @param type typename
	 * @param connRelPos Array, containing the relative positions of this Components {@link Connector Connectors}
	 * @param connRelRot Array, containing the relative rotations of this Components {@link Connector Connectors}
	 * @param svg path of svg-image
	 */
	public Component(double x, double y, double sizeX, double sizeY, double rot, String type
			, int[][] connRelPos, short[] connRelRot, String svg) 
	{
		super((int)x, (int)y, (int)sizeX, (int)sizeY, (short)rot);
		initialize((int)x, (int)y, (short)rot, type, svg, connRelPos, connRelRot);
	}
	
	/**
	 * Constructs new Component
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param sizeX size in x-direction
	 * @param sizeY size in y-direction
	 * @param rot rotation
	 * @param type typename
	 * @param svg path of svg-image
	 */
	public Component(double x, double y, double sizeX, double sizeY, double rot, String type,
			String svg) 
	{
		super((int)x, (int)y, (int)sizeX, (int)sizeY, (short)rot);
		initialize((int)x, (int)y, (short)rot, type, svg);
	}	

	private ArrayList<ComponentProperty>_properties;
	private ArrayList<Connector> _ports;
	private String _svgFilePath;
	private String _svgFileString;
	private String _type;
}
