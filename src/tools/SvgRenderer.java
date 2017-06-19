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

/**
 * Used for drawing svg images on graphical context.
 * 
 * @author Chrisian Gärtner
 */
public class SvgRenderer
{	
	private static Point rotationPoint = new Point();
	
	/**
	 * Used to get the xml string of a svg file.
	 * 
	 * @param	SvgFilePath		Location of the svg file on the operating system
	 * @return	String			Contains the content of the svg file as string
	 */
	static public String getSvgFileString(String SvgFilePath)
	{
		String fileString = null;
		try
		{
			fileString = FileTools.readFile(SvgFilePath, StandardCharsets.UTF_8);
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
	
	/**
	 * Scans a String for xml rectangle and polygon tags.
	 * 
	 * @param	fileString	String containing the xml
	 * @return	String		Contains the truncated content of the original string
	 */
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
	
	/**
	 * Used for drawing a svg image of an {@link blitzEdit.core.Element} on a graphical context.
	 * Considers SelectionMode and draws an image according to GraphicDesignContainer constants if an element is selected or unselected
	 * 
	 * @param	svgString	String containing the xml
	 * @param	gc			Graphical context of the canvas
	 * @param	offsetX		X position of the element (center)
	 * @param	offsetY		X position of the element (center)
	 * @param	scale		Scale of the image
	 * @param 	mode		Selection mode of the component
	 */
	static public void renderSvgString(String svgString, GraphicsContext gc, double offsetX, double offsetY, double scale, SelectionMode mode)
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
		if(mode.equals(SelectionMode.SELECTED))
		{
			gc.save();
			gc.setStroke(GraphicDesignContainer.selected_element_color);
			gc.strokeRect(offsetX-svgWidthMedian - selectedRectPadding, offsetY-svgHeightMedian - selectedRectPadding, svgWidth + 2*selectedRectPadding, svgHeight + 2*selectedRectPadding);
			gc.restore();
		}
	}
	
	/**
	 * Used for drawing a rotated svg image of an {@link blitzEdit.core.Element} on a graphical context.
	 * Considers SelectionMode and draws an image according to GraphicDesignContainer constants if an element is selected or unselected
	 * 
	 * @param	svgString	String containing the xml
	 * @param	gc			Graphical context of the canvas
	 * @param	offsetX		X position of the element (center)
	 * @param	offsetY		X position of the element (center)
	 * @param	scale		Scale of the image
	 * @param	rot			Rotation of the image
	 * @param 	mode		Selection mode of the component
	 */
	static public void renderSvgString(String svgString, GraphicsContext gc, double offsetX, double offsetY, double scale, double rot, SelectionMode mode)
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
		if(mode.equals(SelectionMode.SELECTED))
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
	
	/**
	 * Searches the svgString for svg tag and extracts the height property.
	 * 
	 * @param	svgString	String containing the xml
	 * @return	double		Height of the svg, -1 if height property wasn't found
	 */
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
	
	/**
	 * Searches the svgString for svg tag and extracts the width property.
	 * 
	 * @param	svgString	String containing the xml
	 * @return	double		Width of the svg, -1 if width property wasn't found
	 */
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
	
	/**
	 * Used for drawing a rotated rectangle on a graphical context.
	 * 
	 * @param	rectString	String containing the xml of the rect
	 * @param	gc			Graphical context of the canvas
	 * @param	offsetX		X position of the element
	 * @param	offsetY		X position of the element
	 * @param	scale		Scale of the image
	 * @param	rot			Rotation of the image
	 */
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






