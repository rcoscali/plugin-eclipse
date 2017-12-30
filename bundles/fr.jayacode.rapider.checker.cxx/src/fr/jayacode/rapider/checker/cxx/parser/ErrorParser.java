/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.ProblemMarkerInfo;
import org.eclipse.core.resources.IFile;

/**
 * @author cconversin
 *
 */
public class ErrorParser implements IErrorParser {

	// sample line to parse:
	//
	// /path/to/project/src/HelloWorld.cpp|1|1|7|erreur type1
	// -----------------1----------------- 2 3 4 -----5------
	//
	private static Pattern lineParsingPattern = Pattern.compile("(.*)\\|(\\d+)\\|(\\d+)\\|(\\d+)\\|(.*)"); //$NON-NLS-1$

	/**
	 * 
	 */
	public ErrorParser() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.core.IErrorParser#processLine(java.lang.String,
	 * org.eclipse.cdt.core.ErrorParserManager)
	 */
	@Override
	public boolean processLine(String line, ErrorParserManager eoParser) {
		Matcher matcher = lineParsingPattern.matcher(line);
		if (!matcher.matches()) {
			return false;
		}
		String group1 = matcher.group(1);
		String group2 = matcher.group(2);
		String group3 = matcher.group(3);
		String group5 = matcher.group(5);
		String group4 = matcher.group(4);
		IFile fileName = eoParser.findFileName(group1);
		if (fileName != null) {
			int lineNumber = Integer.parseInt(matcher.group(2));
			String description = matcher.group(5);
			ProblemMarkerInfo info = new ProblemMarkerInfo(fileName, lineNumber, description, IMarkerGenerator.SEVERITY_ERROR_RESOURCE, null);
			eoParser.addProblemMarker(info);
			return true;
		}
		return false;
	}

}
