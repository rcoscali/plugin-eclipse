/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import fr.jayacode.rapider.checker.cxx.checker.RapiderProblemMarkerInfo;
import org.eclipse.core.resources.IFile;

/**
 * Class in charge of processing outstream from rapider into problem markers.
 * 
 * @author cconversin
 */
public class ErrorParser implements IErrorParser {

	// sample line to parse:
	//
	// /path/to/project/src/file.cpp|charStart|charEnd|règle|sévérité|description de l'erreur|ReplacementText
	// --------------1-------------- ----2---- ---3--- --4-- ----5--- ----------6------------ -------7-------
	//
	//
	private static Pattern lineParsingPattern = Pattern.compile("(.*)\\|(\\d+)\\|(\\d+)\\|(.*)\\|(.*)\\|(.*)\\|(.*)"); //$NON-NLS-1$

	// Indexes of value in pattern
	private static final int FILE_PATH_GROUP_INDEX = 1;
	private static final int OFFSET_GROUP_INDEX = 2;
	private static final int LENGTH_GROUP_INDEX = 3;
	private static final int RULE_NAME_GROUP_INDEX = 4;
	private static final int SEVERITY_GROUP_INDEX = 5;
	private static final int DESCRIPTION_GROUP_INDEX = 6;
	private static final int REPLACEMENT_TEXT_GROUP_INDEX = 7;

	// Severity keywords in Rapider's world
	private static final String RAPIDER_INFO_VALUE = "info"; //$NON-NLS-1$
	private static final String RAPIDER_WARNING_VALUE = "warning"; //$NON-NLS-1$

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
			// TODO log
			return false;
		}
		String group1 = matcher.group(FILE_PATH_GROUP_INDEX);
		IFile fileName = eoParser.findFileName(group1);
		if (fileName != null) {
			int startChar = Integer.parseInt(matcher.group(OFFSET_GROUP_INDEX));
			int endChar = Integer.parseInt(matcher.group(LENGTH_GROUP_INDEX));
			String ruleName = matcher.group(RULE_NAME_GROUP_INDEX);
			String description = matcher.group(DESCRIPTION_GROUP_INDEX);
			int severity = mapSeverity(matcher.group(SEVERITY_GROUP_INDEX));
			String replacementText = matcher.group(REPLACEMENT_TEXT_GROUP_INDEX);
			// TODO log error and return false if infos are missing
			RapiderProblemMarkerInfo info = new RapiderProblemMarkerInfo(fileName, startChar, endChar, severity,
					ruleName, description, replacementText);
			eoParser.addProblemMarker(info);
			return true;
		}
		return false;
	}

	/** Maps rapider severity keywords with Eclipse internal severity values
	 * @param severityFromRapider
	 * @return
	 */
	private static int mapSeverity(final String severityFromRapider) {
		switch (severityFromRapider) {
		case RAPIDER_INFO_VALUE:
			return IMarkerGenerator.SEVERITY_INFO;
		case RAPIDER_WARNING_VALUE:
			return IMarkerGenerator.SEVERITY_WARNING;
		default:
			return IMarkerGenerator.SEVERITY_ERROR_RESOURCE;
		}
	}
}
