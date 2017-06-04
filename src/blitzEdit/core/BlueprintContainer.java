package blitzEdit.core;

import java.util.Collection;

import blitzEdit.storage.XMLParser;
import tools.SvgRenderer;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Organizes {@link ComponentBlueprint blueprints} in a container
 * 
 * @author David Schick
 * @author Christian G�rtner
 */
public class BlueprintContainer 
{
	/**
	 * adds new {@link ComponentBlueprint Blueprint} to container.
	 *  The old one will be overwritten, if already existing. 
	 * @param blueprint {@link ComponentBlueprint} to be added
	 */
	public void setBlueprint(ComponentBlueprint blueprint)
	{
		for (ComponentBlueprint cb : _blueprints)
		{
			if(cb.getType().matches(blueprint.getType()))
				_blueprints.remove(cb);
		}
		_blueprints.add(blueprint);
	}
	
	/**
	 * Checks if blueprints with same type are identical.
	 * @param blueprint {@link ComponentBlueprint} to be compared
	 * @return true if blueprints are equal, else false
	 */
	public boolean checkBlueprint(ComponentBlueprint blueprint)
	{
		for (ComponentBlueprint cb : _blueprints)
		{
			if(cb.equals(blueprint))
				return true;
		}
		return false;
	}
	
	/**
	 * compares all blueprints to those in the designated Collection
	 * returns all blueprints which have changed.
	 * @param cbs {@link ComponentBlueprint blueprints} to be compared
	 * @return List of all Blueprints which did change
	 */
	public ArrayList<ComponentBlueprint> compareAll(Collection<ComponentBlueprint> cbs)
	{
		ArrayList<ComponentBlueprint> ret = new ArrayList<ComponentBlueprint>();
		
		for (ComponentBlueprint cb : cbs)
		{
			if(!checkBlueprint(cb))
				ret.add(cb);
		}
		if (ret.isEmpty())
			return null;
		return ret;
	}
	
	/**
	 * Checks, if {@link ComponentBlueprint} with specified name already exists in container
	 * @param type typename of blueprint
	 * @return true if exists, else false
	 */
	public boolean hasBlueprint(String type)
	{
		for (ComponentBlueprint cb : _blueprints)
		{
			if(cb.getType().matches(type))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns blueprint that matches typename type
	 * @param type
	 * @return matching {@link ComponentBlueprint} or null, if the blueprint is not 
	 * contained in this BlueprintContainer
	 */
	public ComponentBlueprint getBlueprint(String type)
	{
		for (ComponentBlueprint cb : _blueprints)
		{
			if (cb.getType().matches(type))
				return cb;
		}
		return null;
	}
	
	/**
	 * @return List of all {@link ComponentBlueprints} in this BlueprintContainer
	 */
	public ArrayList<ComponentBlueprint> getBlueprints()
	{
		return new ArrayList<ComponentBlueprint>(_blueprints);
	}
	
	/**
	 * adds {@link ComponentBlueprint} to this Container
	 * @param filepath path of xml-representation on the filesystem
	 */
	public void addBlueprint(File filepath)
	{
		/*String type = filepath.getName().replace(".svg", "");
		String svgFilePath = filepath.toString();
		String svgFileString = SvgRenderer.getSvgFileString(filepath.toString());
		// TODO
		int[][] relPos = { { 0, 100 }, { 0, -100 } };
		short[] relRot = { 0, 180 };
		int sizeX = (int) SvgRenderer.getSvgWidth(svgFileString);
		int sizeY = (int) SvgRenderer.getSvgHeight(svgFileString);
		
		ComponentBlueprint blueprint = new ComponentBlueprint(type, svgFilePath, relPos, 
					relRot, sizeX, sizeY, new ArrayList<ComponentProperty>());
		*/
		
		ComponentBlueprint blueprint = XMLParser.readBlueprint(filepath.toString());
		
		_blueprints.add(blueprint);
	}
	
	public static BlueprintContainer get()
	{
		if (_instance == null)
			_instance = new BlueprintContainer();
		return _instance;
	}
	
	/**
	 * Constructs new BlueprintContainer
	 */
	private BlueprintContainer()
	{
		_blueprints = new ArrayList<ComponentBlueprint>();
	}
	
	private static BlueprintContainer _instance;
	private ArrayList<ComponentBlueprint> _blueprints;
}
