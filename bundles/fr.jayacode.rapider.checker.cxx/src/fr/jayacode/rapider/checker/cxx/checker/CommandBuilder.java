package fr.jayacode.rapider.checker.cxx.checker;

import java.io.File;

import org.eclipse.cdt.codan.core.cxx.externaltool.ArgsSeparator;
import org.eclipse.cdt.codan.core.cxx.externaltool.ConfigurationSettings;
import org.eclipse.cdt.codan.core.cxx.externaltool.InvocationParameters;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class CommandBuilder {

	public static Command buildCommand(InvocationParameters parameters, ConfigurationSettings settings,
			ArgsSeparator argsSeparator) {
		IPath executablePath = executablePath(settings);
		String[] args = argsToPass(parameters, settings, argsSeparator);
		return new Command(executablePath, args);
	}

	private static IPath executablePath(ConfigurationSettings configurationSettings) {
		File executablePath = configurationSettings.getPath().getValue();
		if (null == executablePath) {
			executablePath = configurationSettings.getPath().getDefaultValue();
		}
		return new Path(executablePath.toString());
	}

	private static String[] argsToPass(InvocationParameters parameters,
			ConfigurationSettings configurationSettings, ArgsSeparator argsSeparator) {
		String actualFilePath = parameters.getActualFilePath();
		String[] args = configuredArgs(configurationSettings, argsSeparator);
		return addFilePathToArgs(actualFilePath, args);
	}

	private static String[] configuredArgs(ConfigurationSettings settings, ArgsSeparator argsSeparator) {
		String args = settings.getArgs().getValue();
		return argsSeparator.splitArguments(args);
	}

	private static String[] addFilePathToArgs(String actualFilePath, String[] configuredArgs) {
		int argCount = configuredArgs.length;
		String[] allArgs = new String[argCount + 1];
		allArgs[0] = actualFilePath;
		// Copy arguments
		System.arraycopy(configuredArgs, 0, allArgs, 1, argCount);
		return allArgs;
	}

}
