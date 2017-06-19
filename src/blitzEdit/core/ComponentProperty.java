package blitzEdit.core;

import java.io.IOException;

/**
 * Saves a Property of a Component
 * 
 * @author David Schick
 * @author Chrisian Gärtner
 */
public class ComponentProperty 
{
	private String _name;
	private String _value;
	private PropType _type;
	private Unit _unit;

	/**
	 * enumeration, representing various SI-units
	 */
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
	
	/**
	 * specifies the accepted format of a {@link ComponentProperty}
	 */
	public enum PropType
	{
		DECIMAL,
		INTEGRAL,
		STRING,
		INVALID;
	}
	
	/**
	 * Returns the {@link PropType} the String is representing
	 * @param propType String representation of PropType
	 * @return {@link PropType} represented by String
	 */
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
	
	/**
	 * Returns the {@link Unit} the String is representing
	 * @param unit String representation of {@link Unit}
	 * @return {@link Unit} represented by String
	 */
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
	
	/**
	 * @return value of this property
	 */
	public String getValue()
	{
		return new String(_value);
	}
	
	/**
	 * @return name of this property
	 */
	public String getName()
	{
		return new String(_name);
	}
	
	/**
	 * @return {@link Unit} of this property
	 */
	public Unit getUnit()
	{
		return _unit;
	}
	
	/** 
	 * @return {@link PropType} of this property
	 */
	public PropType getType()
	{
		return _type;
	}
	
	/**
	 * sets the value of this property
	 * @param value new value
	 * @throws IOException thrown, if format does not match the properties {@link PropType}
	 */
	public void setValue(String value) throws IOException
	{
		switch (_type)
		{
		// Parser werden aufgerufen, nicht aus funktionalen Grï¿½nden
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
	
	/**
	 * Constructs a new ComponentProperty object
	 * @param name name of new property
	 * @param value value of new property
	 * @param unit {@link Unit} of this property
	 * @param type {@link PropType} of this property
	 */
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
	
	/**
	 * Copy-Constructor
	 * @param prop property to be copied
	 */
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