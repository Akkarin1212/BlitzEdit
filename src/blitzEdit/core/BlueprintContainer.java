package blitzEdit.core;

import java.util.Collection;
import java.util.ArrayList;

public class BlueprintContainer 
{
	// put new Blueprint in container.
	// the old one will be overwritten, if already existing. 
	public void setBlueprint(ComponentBlueprint blueprint)
	{
		for (ComponentBlueprint cb : _blueprints)
		{
			if(cb.getType().matches(blueprint.getType()))
				_blueprints.remove(cb);
		}
		_blueprints.add(blueprint);
	}
	
	// Checks if blueprints with same type
	// are identical.
	public boolean checkBlueprint(ComponentBlueprint blueprint)
	{
		for (ComponentBlueprint cb : _blueprints)
		{
			if(cb.equals(blueprint))
				return true;
		}
		return false;
	}
	
	// compares all blueprints to those in the desginated Collection
	// returns all blueprints which have changed.
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
	
	public boolean hasBlueprint(String type)
	{
		for (ComponentBlueprint cb : _blueprints)
		{
			if(cb.getType().matches(type))
				return true;
		}
		return false;
	}
	
	// returns blueprint for the designated type
	public ComponentBlueprint getBlueprint(String type)
	{
		for (ComponentBlueprint cb : _blueprints)
		{
			if (cb.getType().matches(type))
				return cb;
		}
		return null;
	}
	
	public ArrayList<ComponentBlueprint> getBlueprints()
	{
		return new ArrayList<ComponentBlueprint>(_blueprints);
	}
	
	// returns instance (singleton)
	public static BlueprintContainer get()
	{
		if (_instance == null)
			_instance = new BlueprintContainer();
		return _instance;
	}
	
	private BlueprintContainer()
	{
		_blueprints = new ArrayList<ComponentBlueprint>();
	}
	
	private static BlueprintContainer _instance;
	private ArrayList<ComponentBlueprint> _blueprints;
}
