package blitzEdit.core;

import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.paint.Color;
import tools.GraphicDesignContainer;
import tools.SelectionMode;

import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;

import javafx.scene.canvas.GraphicsContext;

import blitzEdit.core.Element;
import blitzEdit.core.Component;

/**
 * Specifies a point which is connecting Components with each other
 * by lines
 * 
 * @author David Schick
 * @author Christian G�rtner
 *
 */
public final class Connector extends Element
{
	@Override
	public void draw(GraphicsContext gc, double scale, SelectionMode mode) 
	{
		double x1 = _position.getX();
		double y1 = _position.getY();
		double x2 = getAnkerPoint().x;
		double y2 = getAnkerPoint().y;
		
		gc.save();
		//zeichnet die Linie des Connectors von seinem Ursprungspunkt zu seiner
		// momentanen Position
		gc.setStroke(GraphicDesignContainer.connector_line_color);
		gc.setLineWidth(GraphicDesignContainer.connector_line_width);
		gc.strokeLine(x2, y2, x1, y1);
		
		switch(mode)
		{
			case SELECTED:
				gc.setFill(GraphicDesignContainer.selected_connector_color);
				break;
			case HIGHLIGHTED:
				gc.setFill(GraphicDesignContainer.connector_highlight_color);
				break;
			default:
				break;
		}
		
		//zeichnet den Verbindungspunkt des Connectors
		gc.fillOval(getX()-getSizeX()/2, getY()-getSizeY()/2, getSizeX(), getSizeY());
		gc.restore();
	}
	
	@Override
	public boolean contains(int x, int y)
	{
		//Ellipse wird groesser erstellt als graphisch dargestellt, 
		//damit die benutzung einfacher wird
		Ellipse2D shape = new Ellipse2D.Double(_position.x - _sizeX, _position.y - _sizeY, _sizeX*2, _sizeY*2);
		return shape.contains(x, y);
	}
	
	/**
	 * Returns all connectors which are connected to this connector
	 * @return List of all connected connectors
	 */
	public ArrayList<Connector> getConnections()
	{
		return _connections;
	}
	
	/**
	 * Returns this connectors relative rotation to its owner
	 * @return relative rotation
	 */
	public short getRelativeRotation()
	{
		return _relRotation;
	}
	
	/**
	 * Moves the Connector in a straight line from its original location
	 * @param x requested x-location
	 * @param y requested y-location
	 */
	@Override
	public Connector move(int x, int y)
	{
		short rotation = (short)(_owner.getRotation() + _relRotation);		
		double dx = (Math.sin( -Math.toRadians(rotation) ));
		double dy = (Math.cos( -Math.toRadians(rotation) ));
				
		int length = (int)(((x-getX()) * dx + (y-getY()) * dy)/(dx * dx + dy * dy));
		
		//waere die Laenge negativ, wird der Connector an seinen Ausgangspunkt
		//verschobe
		if (_length + length < 0)
		{
			_position.setLocation(getAnkerPoint());
			_length = 0;
			return this;
		}
		_length += length;
		_position.translate((int)(dx * length), (int)(dy * length));
		
		return this;
	}
	
	/**
	 * Moves the Connector in a straight line from its original location
	 * @param x requested x-location
	 * @param y requested y-location
	 */
	@Override
	public Connector move(double x, double y) 
	{
		return move((int)x, (int)y);
	}
	
	/**
	 * returns the absolute rotation of this connector
	 * @return absolute rotation
	 */
	public short getRotation()
	{
		short r = (short)(((int)_owner.getRotation() + _relRotation) % 360);
		return (short)((r < 0) ?  360 + r : r);
	}
	
	/**
	 * returns the relative Position of this Connector
	 * @return int array containing relative Position ([0]:x-pos [1]:y-pos)
	 */
	public int [] getRelPos()
	{
		return _conRelPos.clone();
	}
	
	// not implemented
	@Override
	public Element clone()
	{
		return null;
	}
	
	/**
	 * Returns the anker point of this connector, aka the original position just after deployment
	 * @return ankerPoint of this Connector
	 */
	public Point getAnkerPoint()
	{
		AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(_owner.getRotation()), _owner.getX(), _owner.getY());
		Point originalPosition = new Point(_owner.getX() + _conRelPos[0], _owner.getY() + _conRelPos[1]);
		Point transformedPosition = new Point();
		at.transform(originalPosition, transformedPosition);
		
		return transformedPosition;
	}
	
	/**
	 * Connects connector to another connector
	 * @param conn connector to connect to
	 * @return true on success, false on failure (i.e. conn == this)
	 */
	public boolean connect(Connector conn)
	{
		if (conn == this)
			return false;
		if (_connected)
		{
			//Überprüft, ob Verbindung schon vorhanden ist
			for (Connector c : _connections)
			{
				if (c == conn)
					return false;
			}
		}
		else
			_connected = true;
		
		ArrayList<Connector> newConnections = new ArrayList<Connector>(_connections);
		newConnections.add(conn);
		_connections = newConnections;
		conn.connect(this);
		return true;
	}
	
	/**
	 * coonnects this connector to every connector in the specified list
	 * @param conns connectors to connect to
	 */
	public void connect(Collection<Connector> conns)
	{
		if (this.connected())
		{
			//Überprüft, ob Verbindungen schon vorhanden sind
			for (Connector c : conns)
			{
				if (c == this) 
					continue;
				if (_connections.contains(c))
					continue;
				else
					_connections.add(c);
			}
		}
		else
			_connected = true;
		
		_connections.addAll(conns);
	}
	
	/**
	 * disconnects from specified connector
	 * @param conn connector to disconnect
	 * @return true on success, false on failure (i.e. connection didnt exist in the first place)
	 */
	public boolean disconnect(Connector conn)
	{
		ArrayList<Connector> newConnections = new ArrayList<Connector>(_connections);
		/*
		if (this.connected())
		{	
			for (Connector c : _connections)
			{
				if (c == conn)
					_connections.remove(conn);
			}
		}*/
		if(newConnections.remove(conn))
		{
			_connections = newConnections;
			if (_connections.isEmpty())
				_connected = false;
			
			conn.disconnect(this);
			return true;
		}
		return false;
	}
	
	/**
	 * disconnects this connector from all connectors in the specified list
	 * @param elems connectors to disconnect
	 */
	public void disconnect(Collection<Element> elems)
	{
		if (this.connected())
		{
			for (Element e : elems)
			{
				if (e instanceof Connector)
					disconnect((Connector)e);
				else if (e instanceof Component)
					disconnect((Component)e);
			}
		}
		if (_connections.isEmpty())
			_connected = false;
	}
	
	/**
	 * Disconnects all connectors which belong to the specified {@link Component}
	 * @param comp
	 */
	public void disconnect(Component comp)
	{
		if (this.connected())
		{
			for (Connector c : comp.getConnectors())
			{
				if (_connections.contains(c))
					_connections.remove(c);
			}
		}
		if (_connections.isEmpty())
			_connected = false;
	}
	
	/**
	 * checks whether this connector has a connection to another connector
	 * @return true if connections exist, else false
	 */
	public boolean connected()
	{
		if(_connected)
			return true;
		else return false;
	}
	
	/**
	 * checks whether this connector is connected to the specified connector
	 * @param conn Connector to be checked
	 * @return true if connection exists, else false
	 */
	public boolean isConnectedTo(Connector conn)
	{
		return _connections.contains(conn);
	}
	
	/**
	 * checks whether this connector is connected to the specified component
	 * @param comp {@link Component} to be checked
	 * @return true if connection exists, else false
	 */
	public boolean isConnectedTo(Component comp)
	{
		for (Connector conn : _connections)
		{
			if (conn.getOwner() == comp)
				return true;
		}
		return false;
	}
	
	/**
	 * returns the owner of this connector
	 * @return owner of connector
	 */
	public Component getOwner()
	{
		return _owner;
	}
	
	/**
	 * assigns an owner to this connector
	 * @param owner new owner for this connector
	 */
	public void setOwner(Component owner)
	{
		_owner = owner;
	}
	
	/**
	 * Constructs new Connector
	 * @param owner owner of the new connector
	 */
	public Connector(Component owner)
	{
		super();
		if (owner != null)
		{
			_owner = owner;
		}
		_connected = false;
		_connections = new ArrayList<Connector>();
		_length = 0;
	}
	
	/**
	 * Constructs new Connector
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param conRelPos location relative to its owner
	 * @param relRotation rotation relative to its owner
	 */
	public Connector(int x, int y, int[] conRelPos, short relRotation)
	{
		super(x, y, 12, 12); //Standartwerte: breite 6, höhe 6
	
		_conRelPos = conRelPos.clone();
		_relRotation = relRotation;
		_connected = false;
		_connections = new ArrayList<Connector>();
		_length = 0;
	}
	/**
	 * Constructs new Connector
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param conRelPos location relative to its owner
	 * @param relRotation rotation relative to its owner
	 * @param owner owner of this connector
	 */
	public Connector(int x, int y, int[] conRelPos, short relRotation, Component owner)
	{
		super(x, y, 12, 12); //Standartwerte: breite 6, höhe 6
		if (owner != null)
		{
			_owner = owner;
		}
		_conRelPos = conRelPos.clone();
		_relRotation = relRotation;
		_connected = false;
		_connections = new ArrayList<Connector>();
		_length = 0;
	}
	
	private int [] _conRelPos;
	private short _relRotation;
	private int _length;
	private ArrayList<Connector> _connections;
	private boolean _connected;
	private Component _owner;
}
