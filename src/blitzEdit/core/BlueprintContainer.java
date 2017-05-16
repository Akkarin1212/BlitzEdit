package blitzEdit.core;

import java.util.TreeMap;
import java.util.Collection;
import java.util.ArrayList;

public class BlueprintContainer 
{
	// put new Blueprint in container.
	// the old one will be overwritten, if already existing. 
	public void setBlueprint(ComponentBlueprint blueprint)
	{
		_blueprints.put(blueprint.getType(), blueprint);
	}
	
	// Checks if blueprints with same type
	// are identical.
	public boolean checkBlueprint(ComponentBlueprint blueprint)
	{
		if (_blueprints.get(blueprint.getType()).equals(blueprint))
			return true;
		return false;
	}
	
	// compares all blueprints to those in the designated Collection
	// returns all blueprints which have changed.
	public ArrayList<ComponentBlueprint> compareAll(Collection<ComponentBlueprint> cbs)
	{
		ArrayList<ComponentBlueprint> ret = new ArrayList<ComponentBlueprint>();
		
		for (ComponentBlueprint cb : cbs)
		{
			if (!_blueprints.containsKey(cb.getType())
				|| _blueprints.get(cb.getType()).equals(cb))
				ret.add(cb);
		}
		if (ret.isEmpty())
			return null;
		return ret;
	}
	
	// returns blueprint for the designated type
	public ComponentBlueprint getBlueprint(String type)
	{
		return _blueprints.get(type);
	}
	
	public TreeMap<String, ComponentBlueprint> getBlueprints()
	{
		return new TreeMap<String, ComponentBlueprint>(_blueprints);
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
		_blueprints = new TreeMap<String, ComponentBlueprint>();
	}
	
	private static BlueprintContainer _instance;
	private TreeMap<String, ComponentBlueprint> _blueprints;
}
