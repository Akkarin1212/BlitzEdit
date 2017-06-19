package tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Contains static methods used for actions regarding files.
 * @author Christian Gärtner
 */
public class FileTools
{
	/**
	 * Reads the given file by reading all bytes of it and saving it into a string.
	 * 
	 * @param 	path			File path
	 * @param 	encoding		File encoding type
	 * @return	String			Contains file content
	 * @throws 	IOException		When reading from file failes
	 */
	public static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	/**
	 * Reads all files in the given directory. If the folder is a file and not a directory it returns the single file.
	 * Can be used to additionally get files in the subdirectories.
	 * 
	 * @param 	folder					Folder path
	 * @param 	checkSubdirectories		If true adds files from subdirectories to returned array
	 * @return	ArrayList&lt;File&gt;	Contains the files in the given folder
	 */
	public static ArrayList<File> getFilesInDirectory(final File folder, boolean checkSubdirectories)
	{
		ArrayList<File> files = new ArrayList<File>();
		if (folder.isDirectory())
		{
			for (final File fileEntry : folder.listFiles())
			{
				if (fileEntry.isDirectory() && checkSubdirectories)
				{
					files.addAll(getFilesInDirectory(fileEntry, checkSubdirectories));
				}
				else
				{
					files.add(fileEntry);
				}
			}
		}
		else
		{
			files.add(folder);
		}
		return files;
	}
}
