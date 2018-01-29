package fr.jayacode.rapider.checker.cxx.checker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.eclipse.cdt.codan.core.cxx.externaltool.ArgsSeparator;
import org.eclipse.cdt.codan.core.cxx.externaltool.ConfigurationSettings;
import org.eclipse.cdt.codan.core.cxx.externaltool.InvocationFailure;
import org.eclipse.cdt.codan.core.cxx.externaltool.InvocationParameters;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CommandLauncher;
import org.eclipse.cdt.core.ICommandLauncher;
import org.eclipse.cdt.core.IConsoleParser;
import org.eclipse.cdt.core.resources.IConsole;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.framework.Bundle;

import fr.jayacode.rapider.checker.cxx.Activator;
import fr.jayacode.rapider.checker.cxx.prefs.PreferencePage;

public class ExternalToolInvoker {
	private static final String DEFAULT_CONTEXT_MENU_ID = "org.eclipse.cdt.ui.CDTBuildConsole"; //$NON-NLS-1$
	private static final NullProgressMonitor NULL_PROGRESS_MONITOR = new NullProgressMonitor();
	private File embeddedRapider = null;

	/**
	 * Invokes an external tool.
	 * 
	 * @param parameters
	 *            the parameters to pass to the external tool executable.
	 * @param settings
	 *            user-configurable settings.
	 * @param argsSeparator
	 *            separates the arguments to pass to the external tool executable.
	 *            These arguments are stored in a single {@code String}.
	 * @param parsers
	 *            parse the output of the external tool.
	 * @throws InvocationFailure
	 *             if the external tool could not be invoked or if the external tool
	 *             itself reports that it cannot be executed (e.g. due to a
	 *             configuration error).
	 * @throws Throwable
	 *             if something else goes wrong.
	 */
	public void invoke(InvocationParameters parameters, IConsoleParser[] parsers) throws InvocationFailure, Throwable {
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
		ConfigurationSettings settings = new ConfigurationSettings("Rapider", rapiderExe, "");
		Command command = CommandBuilder.buildCommand(parameters, settings, argsSeparator);
		launchCommand(command, parsers, parameters, settings);
	}

	private static void launchCommand(Command command, IConsoleParser[] parsers, InvocationParameters parameters,
			ConfigurationSettings settings) throws InvocationFailure, CoreException {
		IProject project = parameters.getActualFile().getProject();
		final String toolName = settings.getExternalToolName();
		final IPath workingDirectory = parameters.getWorkingDirectory();
		final IPath commandPath = command.getPath();
		final String[] commandArgs = command.getArgs();
		final String[] commandEnv = command.getEnv();
		launchOnBuildConsole(project, parsers, toolName, commandPath, commandArgs, commandEnv, workingDirectory,
				NULL_PROGRESS_MONITOR);
	}

	private static void launchOnBuildConsole(IProject project, IConsoleParser[] parsers, final String toolName,
			final IPath commandPath, final String[] commandArgs, final String[] commandEnv,
			final IPath workingDirectory, final IProgressMonitor monitor) throws CoreException, InvocationFailure {
		monitor.beginTask("Launching " + toolName, 100);
		// TODO : ajouter une icône pour la console
		IConsole c = CCorePlugin.getDefault().getConsole(null, DEFAULT_CONTEXT_MENU_ID, toolName, null);

		// Start Build Console so we can get the OutputStream and ErrorStream properly.
		c.start(project);

		ConsoleOutputSniffer sniffer = new ConsoleOutputSniffer(c.getOutputStream(), c.getErrorStream(), parsers);
		try (final OutputStream out = sniffer.getOutputStream(); final OutputStream err = sniffer.getErrorStream();) {
			ICommandLauncher launcher = new CommandLauncher();
			launcher.showCommand(true);
			launcher.setProject(project);
			Process p = launcher.execute(commandPath, commandArgs, commandEnv, workingDirectory,
					SubMonitor.convert(monitor, 50));
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
				launcher.waitAndRead(out, err, SubMonitor.convert(monitor, 50));
			} finally {
				p.destroy();
			}
		} catch (IOException e) {
			// ignore
		} finally {
			// out and err streams will be closed automaticaly
			// closing sniffer's streams will shut down the parsers as well
			monitor.done();
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

		// executable file is not know yet -> we have to find it and extract it if
		// necessary
		final URL url = getRapiderURL();

		final File tempFile = File.createTempFile("RapiderTmp", Long.toString(System.currentTimeMillis())); //$NON-NLS-1$
		tempFile.deleteOnExit(); // the temporary file will be deleted as soon as Java quits

		try (InputStream inputStream = url.openConnection().getInputStream();
				OutputStream fileStream = new FileOutputStream(tempFile);) {

			final byte[] buf = new byte[1024];
			int i = 0;

			// copy the content of the embedded Rapider to the temporary one.
			while ((i = inputStream.read(buf)) != -1) {
				fileStream.write(buf, 0, i);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		tempFile.setExecutable(true, true);
		this.embeddedRapider = tempFile;
		return this.embeddedRapider;
	}

	private static URL getRapiderURL() {
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL fileURL = bundle.getEntry("./binres/FakeRapider"); //$NON-NLS-1$
		return fileURL;
	}

}
