/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.checker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import fr.jayacode.rapider.checker.cxx.Activator;
import fr.jayacode.rapider.checker.cxx.Messages;
import fr.jayacode.rapider.checker.cxx.model.Diagnostic;
import fr.jayacode.rapider.checker.cxx.model.RapiderReport;
import fr.jayacode.rapider.checker.cxx.model.Replacement;
import fr.jayacode.rapider.checker.cxx.utils.FileUtils;

/**
 * Class in charge of processing outstream from rapider into problem markers.
 * 
 * @author cconversin
 */
public class ErrorParser implements IErrorParser {

	public ErrorParser() {
	}

	public boolean processReport(File exportFile, ErrorParserManager eoParser) {

		Assert.isNotNull(exportFile);

		construct: try {
			RapiderReport report = parseFixesExportFile(exportFile);
			if (report == null) {
				break construct;
			}

			// report is not empty

			if (report.getDiagnostics() == null) {
				// no diagnostic in report -> get out of here
				break construct;
			}

			for (Diagnostic diag : report.getDiagnostics()) {
				String ruleName = StringUtils.defaultString(diag.getDiagnosticName(),
						Messages.ErrorParser_undefined_rule);
				String description = StringUtils.defaultString(diag.getMessage(), Messages.ErrorParser_undefined_error);
				String message = String.format("%s : %s", ruleName, description); //$NON-NLS-1$

				if (diag.getReplacements() == null) {
					// no replacements in diagnostic -> next !
					continue;
				}

				for (Replacement replacement : diag.getReplacements()) {
					String errorFilePath = replacement.getFilePath();
					IFile reportedFile = eoParser.findFileName(errorFilePath);
					if (! reportedFile.exists()) {
						Activator.logWarning(String.format(Messages.ErrorParser_inexistant_file, errorFilePath));
						continue;
					}

					int startChar = replacement.getOffset();
					int endChar = startChar + replacement.getLength();
					boolean everythingIsInOrder = (reportedFile != null) && (errorFilePath != null);
					if (everythingIsInOrder) {
						RapiderProblemMarkerInfo info = new RapiderProblemMarkerInfo(reportedFile, diag.getId(),
								startChar, endChar, IMarkerGenerator.SEVERITY_WARNING, ruleName, message,
								replacement.getReplacementText());
						eoParser.addProblemMarker(info);
					}
				}
			}

		} catch (ParsingErrorException e) {
			Activator.logError(String.format(Messages.ErrorParser_parsing_error_log, exportFile), e);
		}

		return false;
	}

	/**
	 * Parses the Rapider export-fixes file NB: the method is declared public only
	 * for unit tests
	 * 
	 * @param file
	 * @return the {@link RapiderReport} or null if the file did not contain errors
	 * @throws ParsingErrorException
	 */
	public RapiderReport parseFixesExportFile(File file) throws ParsingErrorException {

		Constructor constructor = new Constructor(RapiderReport.class);

		TypeDescription reportDescription = new TypeDescription(RapiderReport.class);
		reportDescription.addPropertyParameters("Diagnostics", Diagnostic.class); //$NON-NLS-1$
		constructor.addTypeDescription(reportDescription);

		TypeDescription diagnosticDescription = new TypeDescription(Diagnostic.class);
		diagnosticDescription.addPropertyParameters("Replacements", Replacement.class); //$NON-NLS-1$
		constructor.addTypeDescription(diagnosticDescription);

		PropertyUtils putils = new PropertyUtils();
		putils.setSkipMissingProperties(true);
		constructor.setPropertyUtils(putils);

		Yaml yaml = new Yaml(constructor);
		RapiderReport report = null;
		try (InputStream str = new FileInputStream(file);) {
			report = (RapiderReport) yaml.load(str);
		} catch (FileNotFoundException e) {
			throw new ParsingErrorException(
					String.format(Messages.ErrorParser_file_not_found_exception_message, file.getAbsolutePath()), e);
		} catch (YAMLException e) {
			throw new ParsingErrorException(
					String.format(Messages.ErrorParser_parsing_error_exception_message, file.getAbsolutePath()), e);
		} catch (IOException e1) {
			Activator.logWarning(
					String.format(Messages.ErrorParser_file_closing_exception_error_message, file.getAbsolutePath()));
		}
		return report;
	}

	public class ParsingErrorException extends RapiderException {
		private static final long serialVersionUID = 1L;

		public ParsingErrorException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	@Override
	public boolean processLine(String line, ErrorParserManager eoParser) {
		// do nothing. This method is just there to justify the fact that this class
		// must implements IErrorParser
		return false;
	}

}
