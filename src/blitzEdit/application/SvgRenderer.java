package blitzEdit.application;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;

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
			// add '<' that got removed by split()
			s = '<' + s;
			
			// only use xml statements important for rendering
			if(s.contains("<rect") || s.contains("<polygon") || s.contains("<svg version"))
			{
				result += s;
			}
		}
		return result;
	}
	
	static public void renderSvgString(String svgString, GraphicsContext gc, double offsetX, double offsetY, double scale, boolean drawSelectRect)
	{
		String[] svgElements = svgString.split("<");
		
		double svgWidth = scale*getSvgWidth(svgString);
		double svgHeight =scale*getSvgHeight(svgString);
		double svgWidthMedian = svgWidth*0.5;
		double svgHeightMedian = svgHeight*0.5;
		
		double selectedRectPadding = 10;
		
		
		for(String s: svgElements)
		{
			if(s.startsWith("rect"))
			{
				renderRect(s,gc,offsetX-svgWidthMedian,offsetY-svgHeightMedian, scale);
			}
			else if(s.startsWith("polygon"))
			{
				//TODO polygin renderer
			}
		}
		
		// draws rect around element when selected
		if(drawSelectRect)
		{
			gc.setStroke(Color.DARKGRAY);
			gc.strokeRect(offsetX-svgWidthMedian - selectedRectPadding, offsetY-svgHeightMedian - selectedRectPadding, svgWidth + 2*selectedRectPadding, svgHeight + 2*selectedRectPadding);
			gc.setStroke(Color.BLACK);
		}
	}
	
	static public void renderSvgString(String svgString, GraphicsContext gc, double offsetX, double offsetY, double scale, double rot, boolean drawSelectRect)
	{
		String[] svgElements = svgString.split("<");
		
		double svgWidth = scale*getSvgWidth(svgString);
		double svgHeight =scale*getSvgHeight(svgString);
		double svgWidthMedian = svgWidth*0.5;
		double svgHeightMedian = svgHeight*0.5;
		
		double selectedRectPadding = 10;
		
		rot = 90;
		
		for(String s: svgElements)
		{
			if(s.startsWith("rect"))
			{
				renderRect(s,gc,offsetX-svgWidthMedian,offsetY-svgHeightMedian, scale);
			}
			else if(s.startsWith("polygon"))
			{
				//TODO polygin renderer
			}
		}
		
		
		// draws rect around element when selected
		if(drawSelectRect)
		{
			gc.setStroke(Color.DARKGRAY);
			gc.strokeRect(offsetX-svgWidthMedian - selectedRectPadding, offsetY-svgHeightMedian - selectedRectPadding, svgWidth + 2*selectedRectPadding, svgHeight + 2*selectedRectPadding);
			gc.setStroke(Color.BLACK);
		}
	}
	
	public static double getSvgHeight(String svgString)
	{
		String[] svgElements = svgString.split("<");
		
		for(String s: svgElements)
		{
			if(s.startsWith("svg"))
			{
				String[] svgProperties = s.split(" ");
				for(String property : svgProperties)
				{
					property = property.replace('"', ' ');
					property = property.replace("px"," ");
					property = property.trim();
					if(property.contains("height="))
					{
						String[] propertyValues = property.split(" ");
						return Double.parseDouble(propertyValues[1]);
					}
				}
			}
		}
		return -1;
	}
	
	public static double getSvgWidth(String svgString)
	{
		String[] svgElements = svgString.split("<");
		
		for(String s: svgElements)
		{
			if(s.startsWith("svg"))
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
						return Double.parseDouble(propertyValues[1]);
					}
				}
			}
		}
		return -1;
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
