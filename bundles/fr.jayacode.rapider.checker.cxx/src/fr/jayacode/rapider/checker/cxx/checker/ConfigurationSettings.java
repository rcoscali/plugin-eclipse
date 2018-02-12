package fr.jayacode.rapider.checker.cxx.checker;

import java.io.File;

import org.eclipse.cdt.codan.core.cxx.externaltool.SingleConfigurationSetting;
import org.eclipse.cdt.codan.core.cxx.internal.externaltool.ArgsSetting;
import org.eclipse.cdt.codan.core.param.MapProblemPreference;

public final class ConfigurationSettings {
	private final ArgsSetting args;
	private final CompileCommandFileSettings compileCommandsFile;
	private final String externalToolName;

	/**
	 * Constructor.
	 * 
	 * @param defaultArgs
	 */
	/**
	 * Constructor.
	 * 
	 * @param externalToolName
	 *            Rapider's name ("Rapider", pardi !)
	 * @param defaultCompileCommandsFile
	 *            the default compile_commands.json file to pass when invoking the external tool.
	 * @param defaultArgs
	 *            the default arguments to pass when invoking the external tool.
	 */
	public ConfigurationSettings(String externalToolName, File defaultCompileCommandsFile, String defaultArgs) {
		this.compileCommandsFile = new CompileCommandFileSettings(externalToolName, defaultCompileCommandsFile);
		this.args = new ArgsSetting(externalToolName, defaultArgs);
		this.externalToolName = externalToolName;
	}

	public String getExternalToolName() {
		return this.externalToolName;
	}

	/**
	 * @return the setting that specifies the "compile_commands.json" file to pass
	 * when invoking Rapider
	 */
	public SingleConfigurationSetting<File> getCompileCommandsFile() {
		return this.compileCommandsFile;
	}

	/**
	 * @return the setting that specifies the arguments to pass when invoking
	 * Rapider.
	 */
	public SingleConfigurationSetting<String> getArgs() {
		return this.args;
	}

	/**
	 * Updates the values of the configuration settings value with the ones stored
	 * in the given preference map.
	 * 
	 * @param preferences
	 *            the given preference map that may contain the values to set.
	 * @throws ClassCastException
	 *             if any of the values to set is not of the same type as the one
	 *             supported by a setting.
	 */
	public void updateValuesFrom(MapProblemPreference preferences) {
		this.args.updateValue(preferences);
		this.compileCommandsFile.updateValue(preferences);
	}

}
