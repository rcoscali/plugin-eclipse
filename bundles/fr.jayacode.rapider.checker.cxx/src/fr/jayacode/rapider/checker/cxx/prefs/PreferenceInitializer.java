package fr.jayacode.rapider.checker.cxx.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import fr.jayacode.rapider.checker.cxx.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getInstance().getPreferenceStore();
        store.setDefault(PreferencePage.EXTERNAL_TOOL_PATH_PREF_KEY, ""); //$NON-NLS-1$
        store.setDefault(PreferencePage.USE_EXTERNAL_TOOL_PREF_KEY, true);
	}

}
