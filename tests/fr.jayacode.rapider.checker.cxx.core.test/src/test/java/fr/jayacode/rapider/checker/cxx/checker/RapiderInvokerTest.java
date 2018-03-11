package fr.jayacode.rapider.checker.cxx.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.cdt.codan.core.cxx.externaltool.InvocationParameters;
import org.eclipse.cdt.codan.core.param.IProblemPreference;
import org.eclipse.cdt.codan.core.param.MapProblemPreference;
import org.eclipse.cdt.codan.core.param.RootProblemPreference;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import fr.jayacode.rapider.checker.cxx.AbstractTest;
import fr.jayacode.rapider.checker.cxx.Activator;
import fr.jayacode.rapider.checker.cxx.Messages;
import fr.jayacode.rapider.checker.cxx.checker.ErrorParser.ParsingErrorException;
import fr.jayacode.rapider.checker.cxx.prefs.PreferencePage;

@SuppressWarnings({ "nls" })
public class RapiderInvokerTest extends AbstractTest {

	private static void updateSettings(ConfigurationSettings settings, File userDefinedFile,
			String userDefinedExtraArgs) {
		final String RAPIDER_NAME = "RapiderTest";
		MapProblemPreference preferences = new RootProblemPreference();
		CompileCommandFileSettings compileCommandsFile = new CompileCommandFileSettings(RAPIDER_NAME, null);
		ArgsSettings argsSettings = new ArgsSettings(RAPIDER_NAME, "");
		preferences.addChildDescriptor((IProblemPreference) compileCommandsFile.getDescriptor());
		preferences.addChildDescriptor((IProblemPreference) argsSettings.getDescriptor());
		preferences.setChildValue(compileCommandsFile.getDescriptor().getKey(), userDefinedFile);
		preferences.setChildValue(argsSettings.getDescriptor().getKey(), userDefinedExtraArgs);
		settings.updateValuesFrom(preferences);

	}

	@Test
	public void testCasDroit() throws ParsingErrorException, CoreException, URISyntaxException, IOException {

		IPath workingDirectory = new Path("");
		IFile testFile = (IFile) getFileInWorspace("resources/RapiderInvokerTest/ex.c");

		InvocationParameters params = new InvocationParameters(testFile, testFile, testFile.getFullPath().toString(),
				workingDirectory);
		ConfigurationSettings settings = new ConfigurationSettings(Messages.Checker_RapiderToolName, null, "");
		updateSettings(settings, null, "");
		RapiderInvoker invoker = new RapiderInvoker();
		Command command = invoker.buildCommand(params, settings);

		String[] args = command.getArgs();
		assertEquals(5, args.length);

		assertTrue(args[0].contains("-export-fixes="));
		String exportFilePath = args[0].substring(14);
		File exportFile = new File(exportFilePath);
		assertTrue(exportFile.exists());

		assertEquals("-p", args[1]);

		String expectedPath = getTestProject().getLocation().toString() + "/compile_commands.json"; // TODO : OS
																									// dependant !
		assertEquals(expectedPath, args[2]);

		assertEquals("-checks=*", args[3]);

		assertEquals(testFile.getFullPath().toString(), args[4]);

		String[] envs = command.getEnv();

		assertEquals(2, envs.length);
		assertTrue(envs[0].matches("LD_LIBRARY_PATH=/.*/fr.jayacode.rapider.checker.cxx.llvm.*/bin/lib/"));
		assertEquals("LD_PRELOAD=/usr/lib64/libstdc++.so.6", envs[1]);

		File exe = command.getPath().toFile();
		assertTrue(exe.getAbsolutePath().matches("/.*/fr.jayacode.rapider.checker.cxx.llvm.*/bin/clang-tidy"));
		assertTrue(exe.exists());

	}

	/**
	 * Tests with compile_commands et additional args
	 * 
	 * @throws ParsingErrorException
	 * @throws CoreException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testArgsAdditionnels() throws ParsingErrorException, CoreException, URISyntaxException, IOException {
		IPath workingDirectory = new Path("");
		IFile testFile = (IFile) getFileInWorspace("resources/RapiderInvokerTest/ex.c");
		InvocationParameters params = new InvocationParameters(testFile, testFile, testFile.getFullPath().toString(),
				workingDirectory);
		ConfigurationSettings settings = new ConfigurationSettings(Messages.Checker_RapiderToolName, null, "");
		File tmpCompileCommandFile = File.createTempFile("RapiderInvokerTest_testArgsAdditionnels",
				Long.toString(System.currentTimeMillis()));
		updateSettings(settings, tmpCompileCommandFile, "-extraArg1 -extraArg2");
		RapiderInvoker invoker = new RapiderInvoker();
		Command command = invoker.buildCommand(params, settings);

		String[] args = command.getArgs();
		assertEquals(7, args.length);

		assertTrue(args[0].contains("-export-fixes="));
		String exportFilePath = args[0].substring(14);
		File exportFile = new File(exportFilePath);
		assertTrue(exportFile.exists());

		assertEquals("-p", args[1]);

		String expectedPath = tmpCompileCommandFile.getAbsolutePath(); // TODO : OS dependant !
		assertEquals(expectedPath, args[2]);

		assertEquals("-extraArg1", args[3]);
		assertEquals("-extraArg2", args[4]);
		assertEquals("-checks=*", args[5]);
		assertEquals(testFile.getFullPath().toString(), args[6]);

		String[] envs = command.getEnv();

		assertEquals(2, envs.length);
		assertTrue(envs[0].matches("LD_LIBRARY_PATH=/.*/fr.jayacode.rapider.checker.cxx.llvm.*/bin/lib/"));
		assertEquals("LD_PRELOAD=/usr/lib64/libstdc++.so.6", envs[1]);

		File exe = command.getPath().toFile();
		assertTrue(exe.getAbsolutePath().matches("/.*/fr.jayacode.rapider.checker.cxx.llvm.*/bin/clang-tidy"));
		assertTrue(exe.exists());

	}

	/**
	 * Tests external clang-tidy
	 * 
	 * @throws ParsingErrorException
	 * @throws CoreException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testExternalClangTidy() throws ParsingErrorException, CoreException, URISyntaxException, IOException {
		final String PATH_TO_SOME_LIB = "/path/to/some/lib";
		final String PATH_TO_SOME_EXE_FILE = "/path/to/some/exe/file.exe";

		Activator.getInstance().getPreferenceStore().setValue(PreferencePage.USE_EMBEDDED_TOOL_PREF_KEY, false);
		Activator.getInstance().getPreferenceStore().setValue(PreferencePage.EXTERNAL_TOOL_PATH_PREF_KEY,
				PATH_TO_SOME_EXE_FILE);
		Activator.getInstance().getPreferenceStore().setValue(PreferencePage.EXTERNAL_LIB_PATH_PREF_KEY,
				PATH_TO_SOME_LIB);
		IPath workingDirectory = new Path("");
		IFile testFile = (IFile) getFileInWorspace("resources/RapiderInvokerTest/ex.c");
		InvocationParameters params = new InvocationParameters(testFile, testFile, testFile.getFullPath().toString(),
				workingDirectory);
		ConfigurationSettings settings = new ConfigurationSettings(Messages.Checker_RapiderToolName, null, "");
		updateSettings(settings, null, "");
		RapiderInvoker invoker = new RapiderInvoker();
		Command command = invoker.buildCommand(params, settings);

		String[] args = command.getArgs();
		assertEquals(5, args.length);

		String[] envs = command.getEnv();

		assertEquals(1, envs.length);
		assertEquals("LD_LIBRARY_PATH=" + PATH_TO_SOME_LIB, envs[0]);

		File exe = command.getPath().toFile();
		assertEquals(PATH_TO_SOME_EXE_FILE, exe.getAbsolutePath());

	}

	/**
	 * Tests user-defined checks
	 * 
	 * @throws ParsingErrorException
	 * @throws CoreException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testUserDefinedChecks() throws ParsingErrorException, CoreException, URISyntaxException, IOException {
		final String USER_DEFINED_RULES = "-checks=rule1,rule2";
		IPath workingDirectory = new Path("");
		IFile testFile = (IFile) getFileInWorspace("resources/RapiderInvokerTest/ex.c");
		InvocationParameters params = new InvocationParameters(testFile, testFile, testFile.getFullPath().toString(),
				workingDirectory);
		ConfigurationSettings settings = new ConfigurationSettings(Messages.Checker_RapiderToolName, null, "");
		updateSettings(settings, null, USER_DEFINED_RULES);
		RapiderInvoker invoker = new RapiderInvoker();
		Command command = invoker.buildCommand(params, settings);

		String[] args = command.getArgs();
		assertEquals(5, args.length);

		assertFalse(args[0].contains("-checks"));
		assertFalse(args[1].contains("-checks"));
		assertFalse(args[2].contains("-checks"));
		assertEquals(USER_DEFINED_RULES, args[3]);
		assertFalse(args[4].contains("-checks"));
	}

	/**
	 * Tests that an empty (" ") compile_command file is not taken into account
	 * 
	 * @throws ParsingErrorException
	 * @throws CoreException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testNoEmptyCompileCommandPath()
			throws ParsingErrorException, CoreException, URISyntaxException, IOException {
		IPath workingDirectory = new Path("");
		IFile testFile = (IFile) getFileInWorspace("resources/RapiderInvokerTest/ex.c");
		InvocationParameters params = new InvocationParameters(testFile, testFile, testFile.getFullPath().toString(),
				workingDirectory);
		ConfigurationSettings settings = new ConfigurationSettings(Messages.Checker_RapiderToolName, null, "");
		updateSettings(settings, new File("   "), "");
		RapiderInvoker invoker = new RapiderInvoker();
		Command command = invoker.buildCommand(params, settings);

		String[] args = command.getArgs();
		assertEquals(5, args.length);

		assertEquals("-p", args[1]);

		String expectedPath = getTestProject().getLocation().toString() + "/compile_commands.json"; // TODO : OS
																									// dependant !
		assertEquals(expectedPath, args[2]);
	}

}
