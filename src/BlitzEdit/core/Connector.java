package blitzEdit.core;

import java.util.ArrayList;
import java.util.Collection;

import blitzEdit.core.Element;
import blitzEdit.core.Component;

public final class Connector extends Element
{
	public ArrayList<Connector> getConnections()
	{
		return _connections;
	}
	
	//TODO: move-methode implementieren
	@Override
	public Connector move(int x, int y)
	{
		return this;
	}
	
	//Nimmt conn in die Verbindungsliste auf
	public void connect(Connector conn)
	{
		if (this.connected())
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
		
		_connections.add(conn);
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
		if (this.connected())
		{
			for (Connector c : _connections)
			{
				if (c == conn)
					_connections.remove(conn);
			}
		}
		if (_connections.isEmpty())
			_connected = false;
	}
	
	// Disconnects all Elements of elems
	// that are connected
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
	
	// Disconnects all Connections to comp
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
	
	public Connector(Component owner)
	{
		super();
		if (owner != null)
		{
			_owner = owner;
		}
		_connected = false;
		_connections = new ArrayList<Connector>();
	}
	
	public Connector(int x, int y, Component owner)
	{
		super(x+3, y+3, 6, 6); //Standartwerte: breite 6, höhe 6
		if (owner != null)
		{
			_owner = owner;
		}
		_connected = false;
		_connections = new ArrayList<Connector>();
	}
	
	private ArrayList<Connector> _connections;
	private boolean _connected;
	private Component _owner;
}
