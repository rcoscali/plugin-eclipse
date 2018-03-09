package fr.jayacode.rapider.checker.cxx.checker;

import static org.eclipse.cdt.codan.core.param.IProblemPreferenceDescriptor.PreferenceType.TYPE_STRING;

import org.eclipse.cdt.codan.core.cxx.externaltool.SingleConfigurationSetting;
import org.eclipse.cdt.codan.core.param.BasicProblemPreference;
import org.eclipse.cdt.codan.core.param.IProblemPreferenceDescriptor;

/**
 * User-configurable setting that specifies the arguments to pass when invoking the external tool.
 * The arguments are stored in a single {@code String}.
 */
public class ArgsSettings extends SingleConfigurationSetting<String> {
	static final String KEY = "externalToolArgs"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * @param externalToolName the name of the external tool. The name of the external tool is
	 * used in the label of this setting's input field.
	 * @param defaultValue the default value of the setting.
	 */
	public ArgsSettings(String externalToolName, String defaultValue) {
		super(newPreferenceDescriptor(externalToolName), defaultValue, String.class);
	}

	private static IProblemPreferenceDescriptor newPreferenceDescriptor(String externalToolName) {
		String label = String.format("Additional arguments:", externalToolName);
		return new BasicProblemPreference(KEY, label, TYPE_STRING);
	}
}
