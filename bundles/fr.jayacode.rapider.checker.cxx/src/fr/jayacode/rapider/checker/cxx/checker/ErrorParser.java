/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.checker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.cdt.codan.core.cxx.externaltool.InvocationParameters;
import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
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
public class ErrorParser {

	
	public ErrorParser() {
	}

	public boolean processReport(File exportFile, ErrorParserManager eoParser) {

		Assert.isNotNull(exportFile);

		try {
			RapiderReport report = parseFixesExportFile(exportFile);
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
					}
				}
			}

		} catch (ParsingErrorException e) {
			Activator.logError(String.format("An error occured while parsing file : %s", exportFile), e);
		}

		return false;
	}

	/**
	 * NB: the method is declared public only for unit tests
	 * 
	 * @param filePath
	 */
	public RapiderReport parseFixesExportFile(File file) throws ParsingErrorException {
		Yaml yaml = new Yaml(new Constructor(RapiderReport.class));
		RapiderReport report = null;
		try (InputStream str = new FileInputStream(file);) {
			report = (RapiderReport) yaml.load(str);
		} catch (FileNotFoundException e) {
			throw new ParsingErrorException(
					String.format("Error while reading export file %s : file not found", file.getAbsolutePath()), e);
		} catch (IOException e1) {
			Activator.logWarning(String.format("Error while closing file %s", file.getAbsolutePath()));
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
