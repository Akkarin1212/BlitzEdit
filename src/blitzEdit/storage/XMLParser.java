package blitzEdit.storage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import blitzEdit.core.Circuit;
import blitzEdit.core.Component;
import blitzEdit.core.Connector;
import blitzEdit.core.Element;

public class XMLParser implements IParser{
	private Circuit currentCircuit = null;
	private ArrayList<Element> elements = new ArrayList<Element>();
	
	public void saveCircuit (Circuit circuit, String destination) 
	{
		currentCircuit = circuit;
		
		String elementsString = new String();
		String connectionsString = new String();
		
		elements = currentCircuit.getElements();
		for(int i=0; i<elements.size();i++)
		{
			Element elem = elements.get(i);
			if(elem.getClass() == Component.class)
			{
				elementsString += saveComponent((Component)elem, i);
				connectionsString += saveComponentChilds((Component)elem);
			}
			else if(elem.getClass() == Connector.class)
			{
				Connector conn = (Connector) elem;
				elementsString += saveConnector(conn,i);
				if(conn.connected())
				{
					ArrayList<Connector> connections = conn.getConnections();
					for(Connector c : connections)
					{
						connectionsString += saveConnection(conn, c);
					}
				}
			}
		}
		
		// TODO: String in file speichern
		Path path = Paths.get(destination);

		String result = elementsString + connectionsString;

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
		
		String [] xmlElements = fileString.split("<");
		for(String s: xmlElements)
		{
			if(s.startsWith("component"))
			{
				elements.add(readComponent(s));
			}
			else if(s.startsWith("connector"))
			{
				elements.add(readConnector(s));
			}
			else if(s.startsWith("connection"))
			{
				createConnection(s);
			}
			else if(s.startsWith("child"))
			{
				addComponentChilds(s);
			}
		}
		
		//check for connectors without owner
		ArrayList<Element> elemsToRemove = new ArrayList<Element>();
		for(Element e : elements)
		{
			if(e.getClass() == Connector.class)
			{
				Connector c = (Connector)e;
				if(c.getOwner() == null)
				{
					System.err.println("Error in file: Connector " + elements.indexOf(e) + " has no owner.");
					elemsToRemove.add(e);
				}
			}
		}
		if(elemsToRemove.isEmpty() || elements.removeAll(elemsToRemove))
		{
			circuit.addElements(elements);
		}
	}
	
	// creates String from file
	private String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	private Element readComponent (String xml) 
	{
		double id=0;
		double x=0;
		double y=0;
		double height=0;
		double width=0;
		double rot=0;
		String type = null;
		String svgPath = null;
		String hash = null;
		
		String[] rectElements = xml.split(" ");
		for(String s : rectElements)
		{		
			s = s.trim();
			if(s.contains("id="))
			{
				String[] sElem = s.split("=");
				id = Double.parseDouble(sElem[1]);
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
			}
			
		}
		
		return new Component (x, y, width, height, rot, type, svgPath);
		
		// TODO: if(hash == gethast)
		
	}
	
	private Element readConnector (String xml)
	{
		double id=0;
		double x=0;
		double y=0;
		double rot=0;
		int relX=0;
		int relY=0;
		String hash = null;
		
		String[] rectElements = xml.split(" ");
		for(String s : rectElements)
		{		
			s = s.trim();
			if(s.contains("id="))
			{
				String[] sElem = s.split("=");
				id = Double.parseDouble(sElem[1]);
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
			}
		}
		int[] relPos = {relX,relY};
		return new Connector((int)x,(int)y,relPos,(short)rot);
		
	}
	
	private void createConnection(String xml)
	{
		double conn1=0;
		double conn2=0;
		String hash = null;
		
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
			}
		}
		Connector connector1 = (Connector) elements.get((int) conn1);
		Connector connector2 = (Connector) elements.get((int) conn2);
		
		connector1.connect(connector2);
	}
	
	private void addComponentChilds(String xml)
	{
		double comp=0;
		ArrayList<Integer> conn= new ArrayList<Integer>();
		String hash = null;
		
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
			}
		}
		if (!elements.isEmpty())
		{
			Component component = (Component) elements.get((int) comp);
			for (int i : conn)
			{
				component.addConnenctor((Connector) elements.get(i));
			}
		}
	}
	
	private String saveComponent(Component comp, int id)
	{
		String result = "component id=" + id + 
						" x=" + comp.getX() +
						" y=" + comp.getY() +
						" width=" + comp.getSizeX() +
						" height=" + comp.getSizeY() +
						" rot=" + comp.getRotation() +
						" type=" + comp.getType() +
						" svgPath=" + comp.getSvgFilePath();
		
		result += " hash=" + createHash(result);
		result = "<" +result + "/>\n";
		return result;
	}
	
	private String saveConnector(Connector conn, int id)
	{
		int[] relPos = conn.getRelPos();
		
		String result = "connector id=" + id +
						" x=" + (conn.getX()+5) +
						" y=" + (conn.getY()+5) + 
						" rot=" + conn.getRelativeRotation() +
						" relX=" + relPos[0] +
						" relY=" + relPos[1] ;
		
		result += " hash=" + createHash(result);
		result = "<" + result + "/>\n";
		return result;
	}
	
	private String saveConnection(Connector conn, Connector conn2)
	{
		String result = "connection conn=" + elements.indexOf(conn) +
						" conn=" + elements.indexOf(conn2);
			
		result += " hash=" + createHash(result);
		result = "<" + result + "/>\n";
		return result;
	}
	
	private String saveComponentChilds(Component comp)
	{
		String result;
		ArrayList<Connector> connectors = comp.getConnectors();
		
		result = "child comp=" + elements.indexOf(comp) + 
				 " conn=";
		
		for(Connector conn : connectors)
		{
			result += elements.indexOf(conn) + ";";
		}
		
		result += " hash=" + createHash(result);
		result = "<" + result + "/>\n";
		return result;
	}
	
	private boolean checkHash(String hash, String xml)
	{
		return false;
	}
	
	private String createHash(String xml)
	{
		return new String("nA ");
	}
	
	
}
