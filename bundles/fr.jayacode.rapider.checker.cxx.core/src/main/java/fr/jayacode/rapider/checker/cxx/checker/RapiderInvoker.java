package fr.jayacode.rapider.checker.cxx.checker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.codan.core.cxx.externaltool.ArgsSeparator;
import org.eclipse.cdt.codan.core.cxx.externaltool.InvocationFailure;
import org.eclipse.cdt.codan.core.cxx.externaltool.InvocationParameters;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CommandLauncher;
import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.ICommandLauncher;
import org.eclipse.cdt.core.resources.IConsole;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.framework.Bundle;

import fr.jayacode.rapider.checker.cxx.Activator;
import fr.jayacode.rapider.checker.cxx.Messages;
import fr.jayacode.rapider.checker.cxx.prefs.PreferencePage;

public class RapiderInvoker {
	private static final String RAPIDER_EXE_RELATIVE_PATH = "/bin/clang-tidy"; //$NON-NLS-1$
	private static final String RAPIDER_LIB_RELATIVE_PATH = "/bin/lib"; //$NON-NLS-1$
	private static final String DEFAULT_CONTEXT_MENU_ID = "org.eclipse.cdt.ui.CDTBuildConsole"; //$NON-NLS-1$
	private static final NullProgressMonitor NULL_PROGRESS_MONITOR = new NullProgressMonitor();
	private File embeddedRapider = null;
	ErrorParser parser = new ErrorParser();

	/**
	 * Invokes Rapider as an external tool.
	 * 
	 * @param parameters
	 *            the parameters to pass to the external tool executable.
	 * @param settings
	 * @param errorParserManager
	 *            this manager is only provided to find a resource (file) from a
	 *            partial path. It is not used for its primary use.
	 * @throws InvocationFailure
	 *             if the external tool could not be invoked or if the external tool
	 *             itself reports that it cannot be executed (e.g. due to a
	 *             configuration error).
	 * @throws Throwable
	 *             if something else goes wrong.
	 */
	public void invoke(InvocationParameters parameters, ConfigurationSettings settings,
			ErrorParserManager errorParserManager) throws InvocationFailure, Throwable {
		boolean isEmbeddedRapiderUsed = Activator.getInstance().getPreferenceStore()
				.getBoolean(PreferencePage.USE_EXTERNAL_TOOL_PREF_KEY);
		File rapiderExe;
		if (isEmbeddedRapiderUsed) {
			rapiderExe = this.getEmbeddedRapider();
		} else {
			rapiderExe = new File(
					Activator.getInstance().getPreferenceStore().getString(PreferencePage.EXTERNAL_TOOL_PATH_PREF_KEY));
		}
		ArgsSeparator argsSeparator = new ArgsSeparator();

		// building the args (notably the export file)
		// TODO gérer le chemin vers le compile_commands.json
		// + si ce chemin n'est pas renseigné --> lever une exception
		final File outFile = createExportFixesFile(parameters.getActualFile());
		Command command = CommandBuilder.buildCommand(rapiderExe, parameters, settings, argsSeparator, outFile,
				buildEnvs());
		launchCommand(command, parameters, settings);
		this.parser.processReport(outFile, errorParserManager);
	}

	private static File createExportFixesFile(final IResource actualFile) throws IOException {
		String exportFileName = "RapiderTmp_export_fixes_" + actualFile.getName(); //$NON-NLS-1$
		final File tempFile = File.createTempFile(exportFileName, Long.toString(System.currentTimeMillis()));
		tempFile.deleteOnExit(); // the temporary file will be deleted as soon as Java quits
		return tempFile;
	}

	private static void launchCommand(Command command, InvocationParameters parameters, ConfigurationSettings settings)
			throws InvocationFailure, CoreException {
		IProject project = parameters.getActualFile().getProject();
		final String toolName = settings.getExternalToolName();
		final IPath workingDirectory = parameters.getWorkingDirectory();
		final IPath commandPath = command.getPath();
		final String[] commandArgs = command.getArgs();
		final String[] commandEnv = command.getEnv();
		NULL_PROGRESS_MONITOR.beginTask(Messages.RapiderInvoker_monitor_launching_message + toolName, 100);
		IConsole c = CCorePlugin.getDefault().getConsole(null, DEFAULT_CONTEXT_MENU_ID, toolName, null);

		// Start Build Console so we can get the OutputStream and ErrorStream properly.
		c.start(project);

		try {
			ICommandLauncher launcher = new CommandLauncher();
			launcher.showCommand(true);
			launcher.setProject(project);
			Process p = launcher.execute(commandPath, commandArgs, commandEnv, workingDirectory,
					SubMonitor.convert(NULL_PROGRESS_MONITOR, 50));
			if (p == null) {
				String format = "Unable to launch external tool '%s': %s"; //$NON-NLS-1$
				throw new InvocationFailure(String.format(format, commandPath, launcher.getErrorMessage()));
			}
			try {
				// this is process input stream which we don't need
				p.getOutputStream().close();
			} catch (Throwable ignored) {
				// ignore
			}
			try {
				launcher.waitAndRead(c.getOutputStream(), c.getErrorStream(),
						SubMonitor.convert(NULL_PROGRESS_MONITOR, 50));
			} finally {
				p.destroy();
			}
		} finally {
			// closing sniffer's streams will shut down the parsers as well
			NULL_PROGRESS_MONITOR.done();
		}
	}

	/**
	 * Returns the file containing the embedded rapider executable. Since this
	 * executable is contained in the bundle's jar, it is extracted in a temporary
	 * file that will be deleted once Eclipse returns.
	 * 
	 * @return
	 * @throws IOException
	 */
	private File getEmbeddedRapider() throws IOException {
		if (this.embeddedRapider != null) {
			return this.embeddedRapider;
		}

		// executable file is not know yet -> we have to find it
		final URL url = getRapiderURL();
		if (url == null) {
			throw new FileNotFoundException(Messages.RapiderInvoker_file_not_found_exception_message);
		}

		try {
			this.embeddedRapider = new File(FileLocator.resolve(url).toURI());
		} catch (URISyntaxException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return this.embeddedRapider;
	}

	/**
	 * @return the URL of the Rapider executable, null if not found
	 */
	private static URL getRapiderURL() {
		return findUrlInBundle(RAPIDER_EXE_RELATIVE_PATH);
	}

	/**
	 * Builds the list of environment variables in variable=value format
	 * 
	 * @return the list of environment variables
	 * @throws IOException 
	 */
	private static List<String> buildEnvs() throws IOException {
		
		final String LD_LIBRARY_PATH_PREFIX = "LD_LIBRARY_PATH="; //$NON-NLS-1$
		final String LD_PRELOAD_VARENV = "LD_PRELOAD=/usr/lib64/libstdc++.so.6"; //$NON-NLS-1$
		List<String> envs = new ArrayList<String>();

		URL fileURL = findUrlInBundle(RAPIDER_LIB_RELATIVE_PATH);

		if (fileURL == null) {
			throw new FileNotFoundException(Messages.RapiderInvoker_dependencies_not_found_exception_message);
		}

		fileURL = FileLocator.toFileURL(fileURL);

		String libDirPath = fileURL.getPath();
		envs.add(LD_LIBRARY_PATH_PREFIX + libDirPath);
		envs.add(LD_PRELOAD_VARENV); //$NON-NLS-1$
		return envs;
	}

	/**
	 * Finds a file or a dir in the bundle. It explores the fragments too.
	 * The path should be relative to the root of the bundle.
	 * @param relativePath
	 * @return the url corresponding to the path, null of it does not exist.
	 */
	private static URL findUrlInBundle(final String relativePath) {
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		return FileLocator.find(bundle, new Path(relativePath), null);
	}
}
