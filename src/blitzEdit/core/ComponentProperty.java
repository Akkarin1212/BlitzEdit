package blitzEdit.core;

import java.io.IOException;

// class Component Property
// speichert eigenschaften der Bauteile
// Beispiel für Widerstand: name="resistance" value="24" unit=OHM 
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
	
	public void setValue(String value)
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
}