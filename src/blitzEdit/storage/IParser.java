package blitzEdit.storage;

import blitzEdit.core.Circuit;

public interface IParser {
	public void saveCircuit(Circuit circuit, String destination);
	public void loadCircuit(Circuit circuit, String filePath);
}
