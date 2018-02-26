package fr.jayacode.rapider.checker.cxx.checker;

import static org.eclipse.cdt.codan.core.param.IProblemPreferenceDescriptor.PreferenceType.TYPE_FILE;

import java.io.File;

import org.eclipse.cdt.codan.core.cxx.externaltool.SingleConfigurationSetting;
import org.eclipse.cdt.codan.core.param.BasicProblemPreference;
import org.eclipse.cdt.codan.core.param.IProblemPreferenceDescriptor;

import fr.jayacode.rapider.checker.cxx.Messages;

public class CompileCommandFileSettings extends SingleConfigurationSetting<File> {

	static final String KEY = "RapiderCompileCommandPath"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * @param externalToolName the name of the external tool. The name of the external tool is
	 * used in the label of this setting's input field.
	 * @param defaultValue the default value of the setting.
	 */
	public CompileCommandFileSettings(String externalToolName, File defaultValue) {
		super(newPreferenceDescriptor(externalToolName), defaultValue, File.class);
	}

	private static IProblemPreferenceDescriptor newPreferenceDescriptor(String externalToolName) {
		String label = String.format(Messages.CompileCommandFileSettings_label, externalToolName);
		return new BasicProblemPreference(KEY, label, TYPE_FILE);
	}
}