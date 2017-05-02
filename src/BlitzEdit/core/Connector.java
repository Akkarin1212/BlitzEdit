package BlitzEdit.core;

import java.util.ArrayList;

import BlitzEdit.core.Element;
import BlitzEdit.core.Component;

public class Connector extends Element
{
	public ArrayList<Connector> getConnections()
	{
		return _connections;
	}
	
	//TODO: move-methode implementieren
	@Override
	public void move(int x, int y)
	{
		
	}
	
	public void connect(Connector conn)
	{
		if (this.connected())
		{
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
		if (_connections.size() == 0)
			_connected = false;
	}
	
	public boolean connected()
	{
		if(_connected)
			return true;
		else return false;
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
		super(x, y);
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