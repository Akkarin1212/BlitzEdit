package blitzEdit.core;

import java.io.IOException;

// class Component Property
// speichert eigenschaften der Bauteile
// Beispiel für Widerstand: name="resistance" value="24.0" unit=OHM type=DECIMAL 
public class ComponentProperty 
{
	private String _name;
	private String _value;
	private PropType _type;
	private Unit _unit;

	public enum Unit
	{
		OHM,
		FARRAD,
		VOLT,
		AMPERE;
	}
	
	public enum PropType
	{
		DECIMAL,
		INTEGRAL,
		STRING;
	}
	
	public String getValue()
	{
		return new String(_value);
	}
	
	public String getName()
	{
		return new String(_name);
	}
	
	public Unit getUnit()
	{
		return _unit;
	}
	
	public PropType getType()
	{
		return _type;
	}
	
	public void setValue(String value) throws IOException
	{
		switch (_type)
		{
			//Parser werden aufgerufen, nicht aus funktionalen Gründen
			// sondern lediglich um das Format zu checken und eine Exception zu werfen,
			// falls das Format nicht passt.
			case DECIMAL:
			{
				Double.parseDouble(value);
				_value = value;
			}
			case INTEGRAL:
			{
				Integer.parseInt(value);
				_value = value;
			}
			case STRING:
			{
				_value = value;
			}
		}
	}
	
	public ComponentProperty(String name, String value, Unit unit, PropType type) throws IOException
	{
		_name = name;
		_type = type;
		_unit = unit;
		setValue(value);
	}
	
	public ComponentProperty(ComponentProperty prop)
	{
		_name = prop.getName();
		_type = prop.getType();
		_unit = prop.getUnit();
		try
		{
			setValue(prop.getValue());
		}
		catch (IOException e)
		{}
	}
}