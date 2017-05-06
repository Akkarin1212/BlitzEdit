package blitzEdit.application;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Pattern;

import javafx.scene.canvas.*;

public class SvgRenderer
{	
	static public String getSvgFileString(String SvgPath)
	{
		String fileString = null;
		try
		{
			fileString = readFile(SvgPath, StandardCharsets.UTF_8);
			System.out.println(fileString);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(fileString != null)
		{
			return scanFileString(fileString);
		}
		return null;
	}
	
	// creates String from file
	private static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	// scans String for rectangles and polygons
	private static String scanFileString(String fileString)
	{
		String result = new String();
		
		String[] textList = fileString.split("<");
		for(String s : textList)
		{
			// add '<'
			s = '<' + s;
			
			if(s.contains("<rect") || s.contains("<polygon"))
			{
				result += s;
			}
		}
		return result;
	}
	
	static void renderSvgString(String svgString, GraphicsContext gc, double offsetX, double offsetY)
	{
		String[] svgElements = svgString.split("<");
		for(String s: svgElements)
		{
			if(s.startsWith("rect"))
			{
				renderRect(s,gc,offsetX,offsetY);
			}
			else if(s.startsWith("polygon"))
			{
				
			}
		}
		
		
		
	}
	
	private static void renderRect(String rectString, GraphicsContext gc, double offsetX, double offsetY)
	{
		double x=0;
		double y=0;
		double height=0;
		double width=0;
		double stroke_width=0;
		String fill="";
		String stroke="";
		
		String[] rectElements = rectString.split(" ");
		for(String s : rectElements)
		{
			s = s.replace('"', ' ');
			s = s.trim();
			if(s.contains("x="))
			{
				String[] sElem = s.split(" ");
				x = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("y="))
			{
				String[] sElem = s.split(" ");
				y = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("width="))
			{
				String[] sElem = s.split(" ");
				width = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("height="))
			{
				String[] sElem = s.split(" ");
				height = Double.parseDouble(sElem[1]);
			}
			else if(s.contains("fill="))
			{
				String[] sElem = s.split(" ");
				fill = sElem[1];
			}
			else if(s.contains("stroke="))
			{
				String[] sElem = s.split(" ");
				stroke = sElem[1];
			}
			else if(s.contains("stroke_width="))
			{
				String[] sElem = s.split(" ");
				stroke_width = Double.parseDouble(sElem[1]);
			}
		}	
		System.out.println(rectString);
		System.out.println(x + " " + y + " " + width + " " + height + " " + fill + " " + stroke + " " + stroke_width);
		gc.fillRect(x+offsetX, y+offsetY, width, height);
	}
}
