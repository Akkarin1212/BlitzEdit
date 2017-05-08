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
			
			if(s.contains("<rect") || s.contains("<polygon") || s.contains("<svg version"))
			{
				result += s;
			}
		}
		return result;
	}
	
	static public void renderSvgString(String svgString, GraphicsContext gc, double offsetX, double offsetY, double scale)
	{
		String[] svgElements = svgString.split("<");
		double svgWidthMedian = 0.0;
		double svgHeightMedian = 0.0;
		
		for(String s: svgElements)
		{
			if(s.startsWith("rect"))
			{
				renderRect(s,gc,offsetX-svgWidthMedian,offsetY-svgHeightMedian, scale);
			}
			else if(s.startsWith("polygon"))
			{
				
			}
			else if(s.startsWith("svg"))
			{
				String[] svgProperties = s.split(" ");
				for(String property : svgProperties)
				{
					property = property.replace('"', ' ');
					property = property.replace("px"," ");
					property = property.trim();
					if(property.contains("width="))
					{
						String[] propertyValues = property.split(" ");
						svgWidthMedian = 0.5*Double.parseDouble(propertyValues[1]);
					}
					else if(property.contains("height="))
					{
						String[] propertyValues = property.split(" ");
						svgHeightMedian = 0.5*Double.parseDouble(propertyValues[1]);
					}
				}
			}
		}
		
		
		
	}
	
	private static void renderRect(String rectString, GraphicsContext gc, double offsetX, double offsetY, double scale)
	{
		double x=0;
		double y=0;
		double height=0;
		double width=0;
		int stroke_width=0;
		String fill="";
		String stroke="";
		
		String[] rectElements = rectString.split(" ");
		for(String s : rectElements)
		{
			System.out.println(s);
			
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
			else if(s.contains("stroke-width="))
			{
				String[] sElem = s.split(" ");
				stroke_width = Integer.parseInt(sElem[1]);
			}
			else if(s.contains("stroke="))
			{
				String[] sElem = s.split(" ");
				stroke = sElem[1];
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
		}	
		//System.out.println(rectString);
		System.out.println(x + " " + y + " " + width + " " + height + " " + fill + " " + stroke + " " + stroke_width);
		
		x *= scale;
		y *= scale;
		
		width *= scale;
		height *= scale;
		stroke_width *= scale;
		
		if(stroke_width != 0)
		{
			gc.setLineWidth(stroke_width);
			gc.strokeRect(x+offsetX, y+offsetY, width, height);
		}
		else if(fill.contains("none"))
		{
			gc.rect(x+offsetX, y+offsetY, width, height);
		}
		else
		{
			gc.fillRect(x+offsetX, y+offsetY, width, height);
		}
	}
}
