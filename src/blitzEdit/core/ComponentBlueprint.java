package blitzEdit.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines an archetype for a Component.
 * Used by the {@link Component} constructor to make sure components of the same type
 * are constructed equally
 * @author David Schick
 */
public class ComponentBlueprint 
{
	/**
	 * @return typename of component
	 */
	public String getType()
	{
		return new String(_type);
	}
	
	/**
	 * @return path to svg-image assigned to this blueprint
	 */
	public String getSvgFilePath()
	{
		return new String(_svgFilePath);
	}
	/**
	 * @return array holding the relative positions of the components connectors
	 */
	public int [][] getRelPos() 
	{
		return _conRelPos.clone();
	}
	
	/**
	 * @return array holding the relative rotation of this components connectors
	 */
	public short [] getConRelRot()
	{
		return _conRelRot.clone();
	}
	
	/**
	 * @return x-size of component
	 */
	public int getSizeX()
	{
		return _sizeX;
	}
	
	/**
	 * @return y-size of component
	 */
	public int getSizeY()
	{
		return _sizeY;
	}
	
	/**
	 * Constructor
	 * 
	 * @param 	posX	Contains x position
	 * @param 	posY	Contains y position
	 * @param 	rot		Contains direction
	 * @return	Element	Created component
	 */
	public Element createComponentWithoutConnectors(int posX, int posY, short rot)
	{
		return new Component(posX, posY, rot, this.getType(), this.getSvgFilePath());
	}
	
	/**
	 * Constructor
	 * 
	 * @param 	posX	Contains x position
	 * @param 	posY	Contains y position
	 * @param 	rot		Contains direction
	 * @return	Element	Created component
	 */
	public Element createComponent(int posX, int posY, short rot)
	{
		return new Component(posX, posY, rot, this.getType(), this.getRelPos(), this.getConRelRot(), this.getSvgFilePath());
	}
	
	/**
	 * Constructor
	 * 
	 * @param 	posX		Contains x position
	 * @param 	posY		Contains y position
	 * @param 	rot			Contains direction
	 * @param 	conRelPos	Contains relative location of connectors
	 * @param	conRelRot	Contains relative rotation of connectors
	 * @return	Element		Created component
	 */
	public Element createComponent(int posX, int posY, short rot, int[][]conRelPos, short[]conRelRot)
	{
		return new Component(posX, posY, rot, this.getType(), conRelPos, conRelRot, this.getSvgFilePath());
	}
	
	/**
	 * Constructs new ComponentBlueprint
	 * @param type typename for {@link Component component}
	 * @param svgFilePath path to svg-image assigned to this {@link Component component}
	 * @param relPos array, holding the relative positions of the {@link Connector connectors} of this component
	 * @param conRelRot  array, holding the relative rotations of the {@link Connector connectors} of this component
	 * @param sizeX x-size of {@link Component component}
	 * @param sizeY y-size of {@link Component component} 
	 * @param properties list of all {@link ComponentProperty properties}
	 */
	public ComponentBlueprint(String type, String svgFilePath, int [][] relPos, 
							short [] conRelRot, int sizeX, int sizeY, List<ComponentProperty> properties)
	{
		_type = new String(type);
		_svgFilePath = new String(svgFilePath);
		_conRelPos = relPos.clone();
		_conRelRot = conRelRot.clone();
		_sizeX = sizeX;
		_sizeY = sizeY;
		_properties = new ArrayList<ComponentProperty>(properties);
	}
	
	
	private String _type;
	private String _svgFilePath;
	private int [][] _conRelPos;
	private short [] _conRelRot;
	private int _sizeX;
	private int _sizeY;
	private ArrayList<ComponentProperty> _properties;
}
