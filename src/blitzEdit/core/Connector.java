package blitzEdit.core;

import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.paint.Color;
import tools.GraphicDesignContainer;

import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;

import javafx.scene.canvas.GraphicsContext;

import blitzEdit.core.Element;
import blitzEdit.core.Component;

public final class Connector extends Element
{
	@Override
	public void draw(GraphicsContext gc, double scale, boolean selected) 
	{
		short rotation = (short)(_owner.getRotation() + _relRotation);		
		double px = (Math.sin( -Math.toRadians(rotation) ));
		double py = (Math.cos( -Math.toRadians(rotation) ));
		
		//monentaner Schwerpunkt des Connectors
		double x = _position.getX();// - getSizeX()/2;
		double y = _position.getY();// - getSizeY()/2;
		
		//zeichnet die Linie des Connectors von seinem Ursprungspunkt zu seiner
		// momentanen Position
		gc.save();
		gc.setStroke(GraphicDesignContainer.connector_line_color);
		gc.setLineWidth(GraphicDesignContainer.connector_line_width);
		
		Point ankerPoint = getAnkerPoint();
		//gc.strokeLine(x - px * _length, y - py * _length, x, y);
		gc.strokeLine(ankerPoint.x, ankerPoint.y, x, y);
		
		// wechsel die Farbe wenn ausgewählt
		if (selected)
		{
			gc.setFill(GraphicDesignContainer.selected_connector_color);
		}
		gc.fillOval(getX()-getSizeX()/2, getY()-getSizeY()/2, getSizeX(), getSizeY());
		gc.restore();
	}
	
	@Override
	public boolean contains(int x, int y)
	{
		Ellipse2D shape = new Ellipse2D.Double(_position.x - _sizeX/2, _position.y - _sizeY/2, _sizeX, _sizeY);
		return shape.contains(x, y);
	}
	
	public void highlight(GraphicsContext gc)
	{
		gc.save();
		gc.setFill(GraphicDesignContainer.connector_highlight_color);
		gc.fillOval(getX()-_sizeX/2, getY()-_sizeY/2, getSizeX(), getSizeY());
		gc.restore();
	}
	
	public ArrayList<Connector> getConnections()
	{
		return _connections;
	}
	
	public short getRelativeRotation()
	{
		return _relRotation;
	}
	
	// Verschiebt den Connector auf den linear auf
	// die Connectorachse projezierten Punkt (x, y)
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
	
	@Override
	public Connector move(double x, double y) 
	{
		return move((int)x, (int)y);
	}
	
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
	
	public Point getAnkerPoint()
	{
		AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(_owner.getRotation()), _owner.getX(), _owner.getY());
		Point originalPosition = new Point(_owner.getX() + _conRelPos[0], _owner.getY() + _conRelPos[1]);
		Point transformedPosition = new Point();
		at.transform(originalPosition, transformedPosition);
		
		return transformedPosition;
	}
	
	//Nimmt conn in die Verbindungsliste auf
	public void connect(Connector conn)
	{
		if (conn == this)
			return;
		if (_connected)
		{
			//Überprüft, ob Verbindung schon vorhanden ist
			for (Connector c : _connections)
			{
				if (c == conn)
					return;
			}
		}
		else
			_connected = true;
		
		ArrayList<Connector> newConnections = new ArrayList<Connector>(_connections);
		newConnections.add(conn);
		_connections = newConnections;
		conn.connect(this);
	}
	
	//Nimmt alle Connectoren aus conns in die Verbindungsliste auf
	//(wenn nicht schon vorhanden)
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
	
	//Löst die Verbindung zum übergebenen Connecor
	public void disconnect(Connector conn)
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
		newConnections.remove(conn);
		_connections = newConnections;
		if (_connections.isEmpty())
			_connected = false;
	}
	
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
	
	public boolean connected()
	{
		if(_connected)
			return true;
		else return false;
	}
	
	public boolean isConnectedTo(Connector con)
	{
		return _connections.contains(con);
	}
	
	public boolean isConnectedTo(Component comp)
	{
		for (Connector conn : _connections)
		{
			if (conn.getOwner() == comp)
				return true;
		}
		return false;
	}
	
	public Component getOwner()
	{
		return _owner;
	}
	
	public void setOwner(Component owner)
	{
		_owner = owner;
	}
	
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
	
	public Connector(int x, int y, int[] conRelPos, short relRotation)
	{
		super(x, y, 12, 12); //Standartwerte: breite 6, höhe 6
	
		_conRelPos = conRelPos.clone();
		_relRotation = relRotation;
		_connected = false;
		_connections = new ArrayList<Connector>();
		_length = 0;
	}
	
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
