package fr.jayacode.rapider.checker.cxx.checker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.codan.core.cxx.externaltool.ArgsSeparator;
import org.eclipse.cdt.codan.core.cxx.externaltool.InvocationParameters;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class CommandBuilder {

	private static final String EXPORT_FIXES_OPTION_PREFIX = "-export-fixes="; //$NON-NLS-1$
	private static final String DEFAULT_EXPORT_FIXES_FILE_NAME = "compile_commands.json"; //$NON-NLS-1$
	private static final String COMPILE_COMMANDS_FILE_OPTION_PREFIX = "-p"; //$NON-NLS-1$
	private static final String RULES_TO_APPLY_OPTION_PREFIX = "-checks="; //$NON-NLS-1$
	private static final String APPLY_ALL_RULES_OPTION_VALUE = "*"; //$NON-NLS-1$

	public static Command buildCommand(final File rapiderExe, final InvocationParameters parameters,
			final ConfigurationSettings settings, final ArgsSeparator argsSeparator, final File exportFixesFile) {
		List<String> args = new ArrayList<String>();

		// add the export-fixes file to the args
		args.add(computeExportFixesFileArg(exportFixesFile));

		// add the "compile_commands.json" file to the args
		args.add(COMPILE_COMMANDS_FILE_OPTION_PREFIX);
		args.add(computeCompileCommandsArg(parameters, settings));

		// add user configured args
		args.addAll(computeUserAdditionalArgs(settings, argsSeparator));

		// if the user did not provide rules to check, then add them all
		args.addAll(computeRulesToCheck(settings));

		// last, the file to process
		args.add(parameters.getActualFilePath());

		return new Command(new Path(rapiderExe.getAbsolutePath()), args.toArray(new String[args.size()]));
	}

	/**
	 * Builds the option argument representing the compile commands file. If the
	 * user did not provide a compile_commands file, then use the default one,
	 * located at the project root and called "compile_commands.json"
	 * 
	 * @param parameters
	 * @param settings
	 * @return option representing the compile commands file to add to the command
	 *         line
	 */
	private static String computeCompileCommandsArg(final InvocationParameters parameters,
			final ConfigurationSettings settings) {
		if (settings.getCompileCommandsFile().getValue() != null) {
			return settings.getCompileCommandsFile().getValue().getAbsolutePath();
		}

		// if no compile_commands file is not provided by the user
		// use the default one with is a file called "compile_commands.json" located at
		// the root of the project
		IProject project = parameters.getActualFile().getProject();
		Assert.isNotNull(project);

		IPath projectPath = project.getLocation();
		Assert.isNotNull(projectPath);

		return projectPath.append(DEFAULT_EXPORT_FIXES_FILE_NAME).toString();
	}

	/**
	 * Builds the option argument representing the export fixes file
	 * 
	 * @param exportFixesFile
	 * 
	 * @return option representing the export fixes file to add to the command line
	 */
	private static String computeExportFixesFileArg(File exportFixesFile) {
		return EXPORT_FIXES_OPTION_PREFIX.concat(exportFixesFile.getAbsolutePath());
	}

	/**
	 * Builds the arguments representing user defined args
	 * 
	 * @param settings
	 * @param argsSeparator
	 * @return a list of arguments to add to the command line
	 */
	private static List<String> computeUserAdditionalArgs(ConfigurationSettings settings, ArgsSeparator argsSeparator) {
		String args = settings.getArgs().getValue();
		return Arrays.asList(argsSeparator.splitArguments(args));
	}

	/**
	 * Builds the option about the rules to apply. If the user has already provided
	 * rules to chack, then no checks are added.
	 * 
	 * @param settings
	 * @return option representing rules to apply. It is empty if the user already provided ones.
	 */
	private static List<String> computeRulesToCheck(ConfigurationSettings settings) {
		List<String> res = new ArrayList<String>();
		String args = settings.getArgs().getValue();
		if (!args.contains(RULES_TO_APPLY_OPTION_PREFIX)) {
			res.add(RULES_TO_APPLY_OPTION_PREFIX + APPLY_ALL_RULES_OPTION_VALUE);
		}
		return res;
	}

}
