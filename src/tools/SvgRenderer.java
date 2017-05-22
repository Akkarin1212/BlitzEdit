package tools;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class SvgRenderer
{	
	private static Point rotationPoint = new Point();
	
	static public String getSvgFileString(String SvgFilePath)
	{
		String fileString = null;
		try
		{
			fileString = readFile(SvgFilePath, StandardCharsets.UTF_8);
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
				renderRect(s,gc,offsetX-svgWidthMedian,offsetY-svgHeightMedian, scale, 0);
			}
			else if(s.startsWith("polygon"))
			{
				//TODO polygin renderer
			}
		}
		
		// draws rect around element when selected
		if(drawSelectRect)
		{
			gc.save();
			gc.setStroke(GraphicDesignContainer.selected_element_color);
			gc.strokeRect(offsetX-svgWidthMedian - selectedRectPadding, offsetY-svgHeightMedian - selectedRectPadding, svgWidth + 2*selectedRectPadding, svgHeight + 2*selectedRectPadding);
			gc.restore();
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
		
		rotationPoint.x = (int) offsetX;
		rotationPoint.y = (int) offsetY;
		
		
		for(String s: svgElements)
		{
			if(s.startsWith("rect"))
			{
				renderRect(s,gc,offsetX-svgWidthMedian,offsetY-svgHeightMedian, scale, rot);
			}
			else if(s.startsWith("polygon"))
			{
				//TODO polygin renderer
			}
		}
		
		
		// draws rect around element when selected
		if(drawSelectRect)
		{
			double x = offsetX-svgWidthMedian - selectedRectPadding;
			double y = offsetY-svgHeightMedian - selectedRectPadding;
			double width = svgWidth + 2*selectedRectPadding;
			double height = svgHeight + 2*selectedRectPadding;
			
			gc.save();
			gc.setStroke(GraphicDesignContainer.selected_element_color);
			gc.setLineWidth(GraphicDesignContainer.selected_stroke_width);
			
			RotatableRectangle rect = new RotatableRectangle(x,y,width,height);			
			rect.rotateRect(rot);
			
			gc.strokePolygon(rect.getXCoordinates(),rect.getYCoordinates(), 4);
			gc.restore();
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
	
	private static void renderRect(String rectString, GraphicsContext gc, double offsetX, double offsetY, double scale, double rot)
	{
		gc.save();
		gc.setFill(GraphicDesignContainer.elements_color);
		gc.setStroke(GraphicDesignContainer.elements_color);
		
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

		if (rot == 0)
		{
			if (stroke_width != 0)
			{
				gc.save();
				gc.setLineWidth(stroke_width);
				gc.strokeRect(x + offsetX, y + offsetY, width, height);
				gc.restore();
			}
			else if (fill.contains("none"))
			{
				gc.rect(x + offsetX, y + offsetY, width, height);
			}
			else
			{
				gc.fillRect(x + offsetX, y + offsetY, width, height);
			}
		}
		else
		{
			x += offsetX;
			y += offsetY;

			RotatableRectangle rect = new RotatableRectangle(x,y,width,height, rotationPoint);
			
			rect.rotateRect(rot);

			if (stroke_width != 0)
			{
				gc.save();
				gc.setLineWidth(stroke_width);
				gc.strokePolygon(rect.getXCoordinates(), rect.getYCoordinates(), 4);
				gc.restore();
			}
			else if (fill.contains("none"))
			{
				gc.strokePolygon(rect.getXCoordinates(), rect.getYCoordinates(), 4);
			}
			else
			{
				gc.fillPolygon(rect.getXCoordinates(), rect.getYCoordinates(), 4);
			}
		}
		gc.restore();
	}
}






