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
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.PropertyUtils;

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

		construct: try {
			RapiderReport report = parseFixesExportFile(exportFile);
			if (report == null) {
				break construct;
			}

			// report is not empty
			String reportedFileName = report.getMainSourceFile();

			if (report.getDiagnostics() == null) {
				break construct;
			}

			for (Diagnostic diag : report.getDiagnostics()) {
				String ruleName = diag.getDiagnosticName();

				if (diag.getReplacements() == null) {
					continue;
				}

				for (Replacement replacement : diag.getReplacements()) {
					String errorFilePath = replacement.getFilePath();
					int startChar = replacement.getOffset();
					int endChar = startChar + replacement.getLength();
					@SuppressWarnings("boxing")
					String description = String.format(
							"TODO : avoir la description pour l'erreur [%s/%d/%d] de type %s", reportedFileName,
							startChar, endChar, ruleName);
					IFile reportedFile = eoParser.findFileName(errorFilePath);
					boolean everythingIsInOrder = (reportedFile != null) && (errorFilePath != null);
					if (everythingIsInOrder) {
						RapiderProblemMarkerInfo info = new RapiderProblemMarkerInfo(reportedFile, diag.getId(),
								startChar, endChar, IMarkerGenerator.SEVERITY_WARNING, ruleName, description,
								replacement.getReplacementText());
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
					String.format("Error while reading export file %s : file not found", file.getAbsolutePath()), e);
		} catch (YAMLException e) {
			throw new ParsingErrorException(String.format("Error while parsing export file %s", file.getAbsolutePath()),
					e);
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
