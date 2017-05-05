package blitzEdit.application;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

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
			return scanString(fileString);
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
	private static String scanString(String text)
	{
		String result = null;
		
		String[] textList = text.split("<");
		for(String s : textList)
		{
			if(s.contains("rect") || s.contains("polygon"))
			{
				result += s;
			}
		}
		return result;
	}
}
