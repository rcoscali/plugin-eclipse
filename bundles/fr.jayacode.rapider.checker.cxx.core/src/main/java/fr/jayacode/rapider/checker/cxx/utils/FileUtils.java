/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.utils;

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
	 * Computes the line number corresponding to the offset-th char<br>
	 * Offset begins at 0 !
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

	/**
	 * Searches for a substring on a specific line in a specific file. The line is
	 * not represented by its number, but by the offset of the first char to search
	 * from.<br>
 	 * Offset begins at 0 !<br>
	 * Attention! The line can begin before the offset? Pay attention to the offset
	 * you give in parameter.
	 * 
	 * @param file The file to earsch in
	 * @param offset the offset of the first character to search from
	 * @param substring The substring to search
	 * @return True if substring is containd between the offset-th character and the end of the line.
	 * @throws IOException
	 * @throws CoreException
	 */
	public static boolean doesLineInFileContains(IFile file, int offset, String substring)
			throws IOException, CoreException {

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
