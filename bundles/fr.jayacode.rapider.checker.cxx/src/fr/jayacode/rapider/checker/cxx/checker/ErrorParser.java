/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.checker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.core.resources.IFile;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import fr.jayacode.rapider.checker.cxx.Activator;
import fr.jayacode.rapider.checker.cxx.model.Diagnostic;
import fr.jayacode.rapider.checker.cxx.model.RapiderReport;
import fr.jayacode.rapider.checker.cxx.model.Replacement;

/**
 * Class in charge of processing outstream from rapider into problem markers.
 * 
 * @author cconversin
 */
public class ErrorParser implements IErrorParser {

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

		String exportFilePath = null;
		for (final String substring : line.split(" ")) { //$NON-NLS-1$
			if (substring.startsWith(ExternalToolInvoker.EXPORT_FIXES_OPTION_KEYWORD)) {
				exportFilePath = substring.substring(ExternalToolInvoker.EXPORT_FIXES_OPTION_KEYWORD.length());
				break;
			}
		}
		if (exportFilePath == null) {
			return false;
		}

		try {
            try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// do nothing
			}
			RapiderReport report = parseFixesExportFile(exportFilePath);
			String reportedFileName = report.getMainSourceFile();

			for (Diagnostic diag : report.getDiagnostics()) {
				String ruleName = diag.getDiagnosticName();
				for (Replacement replacement : diag.getReplacements()) {
					String errorFilePath = replacement.getFilePath(); 
					int startChar = replacement.getOffset();
					int endChar = startChar + replacement.getLength();
					@SuppressWarnings("boxing")
					String description = String.format("TODO : avoir la description pour l'erreur [%s/%d/%d] de type %s" , reportedFileName, startChar, endChar, ruleName);
					IFile reportedFile = eoParser.findFileName(errorFilePath);
					if (reportedFile != null)
					{
						RapiderProblemMarkerInfo info = new RapiderProblemMarkerInfo(reportedFile, diag.getId(), startChar, endChar, IMarkerGenerator.SEVERITY_WARNING,
								ruleName, description, replacement.getReplacementText());
						eoParser.addProblemMarker(info);
					} else {
						int i = 0;
					}
				}
			}

		} catch (ParsingErrorException e) {
			Activator.logError(String.format("An error occured while parsing file : %s", exportFilePath), e);
		}

		// Matcher matcher = lineParsingPattern.matcher(line);
		// if (!matcher.matches()) {
		// // TODO log
		// return false;
		// }
		// String group1 = matcher.group(FILE_PATH_GROUP_INDEX);
		// IFile fileName = eoParser.findFileName(group1);
		// if (fileName != null) {
		// int startChar = Integer.parseInt(matcher.group(OFFSET_GROUP_INDEX));
		// int endChar = Integer.parseInt(matcher.group(LENGTH_GROUP_INDEX));
		// String ruleName = matcher.group(RULE_NAME_GROUP_INDEX);
		// String description = matcher.group(DESCRIPTION_GROUP_INDEX);
		// int severity = mapSeverity(matcher.group(SEVERITY_GROUP_INDEX));
		// String replacementText = matcher.group(REPLACEMENT_TEXT_GROUP_INDEX);
		// // TODO log error and return false if infos are missing
		// RapiderProblemMarkerInfo info = new RapiderProblemMarkerInfo(fileName,
		// startChar, endChar, severity,
		// ruleName, description, replacementText);
		// eoParser.addProblemMarker(info);
		// return true;
		// }

		return false;
	}

	/**
	 * NB: the method is declared puiblic only for unit tests
	 * 
	 * @param filePath
	 */
	public RapiderReport parseFixesExportFile(String filePath) throws ParsingErrorException {
		Yaml yaml = new Yaml(new Constructor(RapiderReport.class));
		File file = new File(filePath);
		RapiderReport report = null;
		try (InputStream str = new FileInputStream(file);) {
			report = (RapiderReport) yaml.load(str);
		} catch (FileNotFoundException e) {
			throw new ParsingErrorException(
					String.format("Error while reading export file %s : file not found", filePath), e);
		} catch (IOException e1) {
			Activator.logWarning(String.format("Error while closing file %s", filePath));
		}
		return report;
	}

	public class ParsingErrorException extends RapiderException {
		private static final long serialVersionUID = 1L;

		public ParsingErrorException(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}
	}
}
