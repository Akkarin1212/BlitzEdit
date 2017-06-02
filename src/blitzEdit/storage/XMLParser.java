package blitzEdit.storage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import blitzEdit.core.Circuit;
import blitzEdit.core.Component;
import blitzEdit.core.Connector;
import blitzEdit.core.Element;

public class XMLParser implements IParser{
	private Circuit currentCircuit = null;
	private ArrayList<Element> elements = new ArrayList<Element>();
	private boolean useHashes;
	private boolean ignoreHashes;
	
	private final String xmlTag = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	public void saveCircuit (Circuit circuit, String destination, boolean useHashes) 
	{
		currentCircuit = circuit;
		this.useHashes = useHashes;
		
		String circuitString = new String();
		
		// save elements in String
		elements = currentCircuit.getElements();
		for(int i=0; i<elements.size();i++)
		{
			Element elem = elements.get(i);
			if(elem.getClass() == Component.class)
			{
				Component comp = (Component) elem;
				
				circuitString += "\t" + saveComponent((Component)elem, i);
				circuitString += "\t\t" + saveComponentChilds((Component)elem);
				
				ArrayList<Connector> connectors = comp.getConnectors();
				for(Connector connector : connectors)
				{
					circuitString += "\t\t" + saveConnector(connector, elements.indexOf(connector));
					if (connector.connected())
					{
						ArrayList<Connector> connections = connector.getConnections();
						for (Connector connectedConn : connections)
						{
							circuitString += "\t\t\t" + saveConnection(connector, connectedConn);
						}
					}
					circuitString += "\t\t" + "</connector>" + "\n";
				}
				circuitString += "\t" + "</component>" + "\n";
			}
		}
		
		String result = circuitString;
		if(useHashes)
		{
			result = "\t" + createCircuitHash(result) + result;
		}
		result = xmlTag + "\n" + "<Circuit>\n" + result + "</Circuit>";
			
		
		Path path = Paths.get(destination);
		// safe string in file
		try
		{
			FileOutputStream fos = new FileOutputStream(path.toString());
			fos.write(result.getBytes());
			fos.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public void loadCircuit (Circuit circuit, String filepath) 
	{
		currentCircuit = circuit;
	
		String fileString = null;
		try
		{
			fileString = readFile(filepath, StandardCharsets.UTF_8);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String originalFileString = fileString; // used for hash check
		// remove unnecessary tokens
		fileString = fileString.replace(xmlTag, "");
		fileString = fileString.replace("</component>", "");
		fileString = fileString.replace("</connector>", "");
		fileString = fileString.replace("/>", "");
		fileString = fileString.replace(">", "");
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\t", "");
		
		String [] xmlElements = fileString.split("<");
		
		ArrayList<String> components = new ArrayList<String>();
		ArrayList<String> connectors = new ArrayList<String>();
		ArrayList<String> connections = new ArrayList<String>();
		ArrayList<String> childs = new ArrayList<String>();
		
		for(String xml : xmlElements)
		{
			xml = xml.replace("\"", "");
			if(xml.startsWith("component"))
			{
				components.add(xml);
			}
			else if(xml.startsWith("connector"))
			{
				connectors.add(xml);
			}
			else if(xml.startsWith("connection"))
			{
				connections.add(xml);
			}
			else if(xml.startsWith("child"))
			{
				childs.add(xml);
			}
			else if(xml.startsWith("circuithash"))
			{
				String hash = xml.replace("circuithash hash=", "");
				if (!checkCircuitHash(hash, originalFileString))
				{
					int accepted = JOptionPane.showConfirmDialog(null,
							"Modifications have been made in this file. Do you still want to load it?", "Loading Error",
							JOptionPane.YES_NO_OPTION);
					if (accepted != 0)
					{
						System.err.println("blub");
						return;
					}
					else
					{
						ignoreHashes = true;
					}
				}
			}
		}
		
		// make sure elements array is large enough
		for (int i = 0; i < components.size() + connectors.size(); i++)
		{
			elements.add(null);
		}
		
		for(String xml : components)
		{
			if(!readComponent(xml))
			{
				System.err.println("Stopped loading process.");
				return;
			}
		}
		
		for(String xml : connectors)
		{
			if(!readConnector(xml))
			{
				System.err.println("Stopped loading process.");
				return;
			}
		}
		
		for(String xml : childs)
		{
			if(!addComponentChilds(xml))
			{
				System.err.println("Stopped loading process.");
				return;
			}
		}
		
		for(String xml : connections)
		{
			if(!createConnection(xml))
			{
				System.err.println("Stopped loading process.");
				return;
			}
		}

		// check for connectors without owner
		ArrayList<Element> elemsToRemove = new ArrayList<Element>();
		for (Element e : elements)
		{
			if (e.getClass() == Connector.class)
			{
				Connector c = (Connector) e;
				if (c.getOwner() == null)
				{
					System.err.println("Error in file: Connector " + elements.indexOf(e) + " has no owner.");
					elemsToRemove.add(e);
				}
			}
		}
		
		if(elemsToRemove.isEmpty() || elements.removeAll(elemsToRemove))
		{
			circuit.clearElements();
			circuit.addElements(elements);
		}
	}
	
	// creates String from file
	private String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	private boolean readComponent (String xml) 
	{
		int id=0;
		double x=0;
		double y=0;
		double height=0;
		double width=0;
		double rot=0;
		String type = null;
		String svgPath = null;
		String hash = null;
		boolean hasHash = false;
		
		String[] rectElements = xml.split(" ");
		for(String s : rectElements)
		{		
			s = s.trim();
			if(s.contains("id="))
			{
				String[] sElem = s.split("=");
				id = Integer.parseInt(sElem[1]);
			}
			if(s.contains("x="))
			{
				String[] sElem = s.split("=");
				x = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("y="))
			{
				String[] sElem = s.split("=");
				y = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("width="))
			{
				String[] sElem = s.split("=");
				width = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("height="))
			{
				String[] sElem = s.split("=");
				height = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("rot="))
			{
				String[] sElem = s.split("=");
				rot = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("type="))
			{
				String[] sElem = s.split("=");
				type = sElem[1];
			}
			else if(s.contains("svgPath="))
			{
				String[] sElem = s.split("=");
				svgPath = sElem[1];
			}
			else if(s.contains("hash="))
			{
				String[] sElem = s.split("=");
				hash = sElem[1];
				hasHash = true;
			}
			
		}
		
		if (!hasHash || (hasHash && checkHash(hash, xml)))
		{
			elements.set((int)id, new Component(x, y, width, height, rot, type, svgPath));
			return true;
		}
		else
		{
			System.err.println("Changes have been made in the component: " + xml + ". The component wasn't created.");
		}
		return false;
		
	}
	
	private boolean readConnector (String xml)
	{
		int id=0;
		double x=0;
		double y=0;
		double rot=0;
		int relX=0;
		int relY=0;
		String hash = null;
		boolean hasHash = false;
		
		String[] rectElements = xml.split(" ");
		for(String s : rectElements)
		{		
			s = s.trim();
			if(s.contains("id="))
			{
				String[] sElem = s.split("=");
				id = Integer.parseInt(sElem[1]);
			}
			else if(s.contains("x="))
			{
				String[] sElem = s.split("=");
				x = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("y="))
			{
				String[] sElem = s.split("=");
				y = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("rot="))
			{
				String[] sElem = s.split("=");
				rot = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("relX="))
			{
				String[] sElem = s.split("=");
				relX = Integer.parseInt(sElem[1]);
			}
			else if(s.contains("relY="))
			{
				String[] sElem = s.split("=");
				relY = Integer.parseInt(sElem[1]);
			}
			else if(s.contains("hash="))
			{
				String[] sElem = s.split("=");
				hash = sElem[1];
				hasHash = true;
			}
		}
		int[] relPos = { relX, relY };

		if (!hasHash || (hasHash && checkHash(hash, xml)))
		{
			elements.set((int)id, new Connector((int) x, (int) y, relPos, (short) rot));
			return true;
		}
		else
		{
			System.err.println("Changes have been made in the connector: " + xml + ". The connector wasn't created.");
		}
		return false;
	}
	
	private boolean createConnection(String xml)
	{
		double conn1=0;
		double conn2=0;
		String hash = null;
		boolean hasHash = false;
		
		String[] rectElements = xml.split(" ");
		for(String s : rectElements)
		{		
			s = s.trim();
			if(s.contains("conn1="))
			{
				String[] sElem = s.split("=");
				conn1 = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("conn2="))
			{
				String[] sElem = s.split("=");
				conn2 = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("hash="))
			{
				String[] sElem = s.split("=");
				hash = sElem[1];
				hasHash = true;
			}
		}

		if (!hasHash || (hasHash && checkHash(hash, xml)))
		{
			try
			{
				Connector connector1 = (Connector) elements.get((int) conn1);
				Connector connector2 = (Connector) elements.get((int) conn2);

				connector1.connect(connector2);
				return true;
			}
			catch (IndexOutOfBoundsException e)
			{
				System.err.println(
						"Error in generating connections between connectors: Tried to access " + e.getMessage());
			}
		}
		else
		{
			System.err.println("Changes have been made in the connection: " + xml + ". The connection wasn't created.");
		}
		return false;
	}
	
	private boolean addComponentChilds(String xml)
	{
		double comp=0;
		ArrayList<Integer> conn= new ArrayList<Integer>();
		String hash = null;
		boolean hasHash = false;
		
		String[] rectElements = xml.split(" ");
		for(String s : rectElements)
		{		
			s = s.trim();
			if(s.contains("comp="))
			{
				String[] sElem = s.split("=");
				comp = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("conn="))
			{
				String[] sElem = s.split("=");
				String[] sElem2 = sElem[1].split(";");
				for(String connectorId : sElem2)
				{
					conn.add(Integer.parseInt(connectorId));
				}
			}
			else if(s.contains("hash="))
			{
				String[] sElem = s.split("=");
				hash = sElem[1];
				hasHash = true;
			}
		}
		
		if (!hasHash || (hasHash && checkHash(hash, xml)))
		{
			if (!elements.isEmpty())
			{
				Component component = (Component) elements.get((int) comp);
				for (int i : conn)
				{
					component.addConnenctor((Connector) elements.get(i));
				}
				return true;
			}
		}
		else
		{
			System.err.println(
					"Changes have been made in the child property: " + xml + ". The connectors weren't added.");
		}
		return false;
	}
	
	private String saveComponent(Component comp, int id)
	{
		String result = "component id=\"" + id + 
						"\" x=\"" + comp.getX() +
						"\" y=\"" + comp.getY() +
						"\" width=\"" + comp.getSizeX() +
						"\" height=\"" + comp.getSizeY() +
						"\" rot=\"" + comp.getRotation() +
						"\" type=\"" + comp.getType() +
						"\" svgPath=\"" + comp.getSvgFilePath();
		
		if(useHashes)
		{
			result += "\" hash=\"" + createHash(result);
		}
		result = "<" +result + "\">\n";
		return result;
	}
	
	private String saveConnector(Connector conn, int id)
	{
		int[] relPos = conn.getRelPos();
		
		String result = "connector id=\"" + id +
						"\" x=\"" + conn.getX() +
						"\" y=\"" + conn.getY() + 
						"\" rot=\"" + conn.getRelativeRotation() +
						"\" relX=\"" + relPos[0] +
						"\" relY=\"" + relPos[1] ;
		if(useHashes)
		{
			result += "\" hash=\"" + createHash(result);
		}
		result = "<" + result + "\">\n";
		return result;
	}
	
	private String saveConnection(Connector conn, Connector conn2)
	{
		String result = "connection conn1=\"" + elements.indexOf(conn) +
						"\" conn2=\"" + elements.indexOf(conn2);
		
		if(useHashes)
		{
			result += "\" hash=\"" + createHash(result);
		}
		result = "<" + result + "\"/>\n";
		return result;
	}
	
	private String saveComponentChilds(Component comp)
	{
		String result;
		ArrayList<Connector> connectors = comp.getConnectors();
		
		result = "child comp=\"" + elements.indexOf(comp) + 
				 "\" conn=\"";
		
		for(Connector conn : connectors)
		{
			result += elements.indexOf(conn) + ";";
		}
		
		if(useHashes)
		{
			result += "\" hash=\"" + createHash(result);
		}
		result = "<" + result + "\"/>\n";
		return result;
	}
	
	private boolean checkHash(String hash, String xml)
	{
		if (!ignoreHashes)
		{
			xml = xml.replace("<", "");
			xml = xml.replace("/>", "");
			xml = xml.trim();
			xml = xml.replace("hash=", "");
			xml = xml.replace(hash, "");
			xml = xml.trim();

			try
			{
				int hashInt = Integer.parseInt(hash);
				int test = xml.hashCode();
				return (xml.hashCode() == hashInt);
			}
			catch (NumberFormatException e)
			{
				System.err.println("Changes to the hash code have been made. " + e.getMessage());
				return false;
			}
		}
		else
		{
			return true;
		}
	}
	
	private String createHash(String xml)
	{
		xml = xml.replace("\"", "");
		return new String(Integer.toString(xml.hashCode()));
	}
	
	private String createCircuitHash(String xml)
	{
		xml = xml.replace("<", "");
		xml = xml.replace("/>", "");
		xml = xml.replace("\n", "");
		xml = xml.replace("\t", "");
		
		return new String("<circuithash hash=\"" + xml.hashCode() + "\"/>\n");
	}
	
	private boolean checkCircuitHash(String hash, String xml)
	{
		xml = xml.replace(xmlTag, "");
		xml = xml.replace("</Circuit>", "");
		xml = xml.replace("<Circuit>", "");
		xml = xml.replace("<", "");
		xml = xml.replace("/>", "");
		xml = xml.replace("circuithash hash=\"" + hash + "\"", "");
		xml = xml.replace("\n", "");
		xml = xml.replace("\t", "");
		
		try{
			int hashInt = Integer.parseInt(hash);
			return (xml.hashCode() == hashInt);
		}catch (NumberFormatException e)
		{
			System.err.println("Changes to the hash code have been made. " + e.getMessage());
			return false;
		}
	}
}
