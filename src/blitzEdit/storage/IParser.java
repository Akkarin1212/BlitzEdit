package blitzEdit.storage;

import blitzEdit.core.Circuit;

/**
 * Parser interface used for saving and loading circuits.
 * 
 * @author Chrisian Gartner
 */
public interface IParser {
	public void saveCircuit(Circuit circuit, String destination, boolean useHashes);
	public void loadCircuit(Circuit circuit, String filePath);
}
