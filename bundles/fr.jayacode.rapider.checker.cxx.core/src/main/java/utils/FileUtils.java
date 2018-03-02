/**
 * 
 */
package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Util class for file
 * 
 * @author cconversin
 *
 */
public class FileUtils {

	/**
	 * @param file
	 * @param offset
	 * @return
	 * @throws CoreException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static int getLineNumberFromOffset(IFile file, int offset) throws IOException, CoreException {
		try (LineNumberReader r = new LineNumberReader(new InputStreamReader(file.getContents()))) {
			r.skip(offset);
			return r.getLineNumber() + 1;
		}
	}

	public static boolean doesLineContains(IFile file, int offset, String substring) throws IOException, CoreException {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents()))) {
			reader.skip(offset);
			String line = reader.readLine();
			if (line != null) {
				return line.contains(substring);
			}
			return false;
		}
	}

}
