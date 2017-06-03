package blitzEdit.core;

import java.io.IOException;

// class Component Property
// speichert eigenschaften der Bauteile
// Beispiel f�r Widerstand: name="resistance" value="24.0" unit=OHM type=DECIMAL 
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
		AMPERE,
		HENRY,
		WATT,
		COULOMB,
		HERTZ,
		WINDUNGEN,
		INVALID;
	}
	
	public enum PropType
	{
		DECIMAL,
		INTEGRAL,
		STRING,
		INVALID;
	}
	
	public static PropType toPropType(String propType)
	{
		switch (propType)
		{
		case "decimal":
			return PropType.DECIMAL;

		case "integral":
			return PropType.INTEGRAL;

		case "string":
			return PropType.STRING;

		default:
			return PropType.INVALID;
		}
	}
	
	public static Unit toUnit(String unit)
	{
		switch (unit)
		{
		case "ohm":
			return Unit.OHM;

		case "farrad":
			return Unit.FARRAD;

		case "volt":
			return Unit.VOLT;

		case "ampere":
			return Unit.AMPERE;
			
		case "henry":
			return Unit.HENRY;
			
		case "watt":
			return Unit.WATT;

		case "coulomb":
			return Unit.COULOMB;

		case "hertz":
			return Unit.HERTZ;
			
		case "windungen":
			return Unit.WINDUNGEN;

		default:
			return Unit.INVALID;
		}
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
		// Parser werden aufgerufen, nicht aus funktionalen Gr�nden
		// sondern lediglich um das Format zu checken und eine Exception zu
		// werfen,
		// falls das Format nicht passt.
		case DECIMAL:
		{
			if(value == "")
				value = "0";
			Double.parseDouble(value);
			_value = value;
		}
		case INTEGRAL:
		{
			if(value == "")
				value = "0";
			Integer.parseInt(value);
			_value = value;
		}
		case STRING:
		{
			_value = value;
		}
		case INVALID:
			break;
		default:
			break;
		}
	}
	
	public ComponentProperty(String name, String value, Unit unit, PropType type)
	{
		_name = name;
		_type = type;
		_unit = unit;
		try
		{
			setValue(value);
		}
		catch (IOException e)
		{}
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