package blitzEdit.storage;

import blitzEdit.core.Circuit;

/**
 * Parser interface used for saving and loading circuits.
 * 
 * @author Chrisian Gartner
 */
public interface IParser {
	/**
	 * Used for saving the circuit on disk
	 * 
	 * @param circuit		Circuit to save
	 * @param destination	Destination on disk
	 * @param useHashes		If true uses hashes to ensure consistency
	 */
	public void saveCircuit(Circuit circuit, String destination, boolean useHashes);
	
	/**
	 * Used for loading a circuit from a filepath on the disk
	 * 
	 * @param circuit	Circuit used for loading
	 * @param filePath	Contains direction of circuit on disk
	 */
	public void loadCircuit(Circuit circuit, String filePath);
}
