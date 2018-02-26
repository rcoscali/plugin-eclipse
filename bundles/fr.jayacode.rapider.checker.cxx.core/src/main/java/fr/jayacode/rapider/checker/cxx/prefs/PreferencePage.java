/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.prefs;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import fr.jayacode.rapider.checker.cxx.Activator;
import fr.jayacode.rapider.checker.cxx.Messages;

/**
 * @author cconversin
 *
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String USE_EXTERNAL_TOOL_PREF_KEY = "UseExternalRapider"; //$NON-NLS-1$
	public static final String EXTERNAL_TOOL_PATH_PREF_KEY = "ExternalRapiderPath"; //$NON-NLS-1$
	private FileFieldEditor pathField;
	private BooleanFieldEditor useEmbeddedField;
	
	// we store the file path editor in order to enable/disable it.
	// NB : can not use {@link getFieldEditorParent} so we have to store it in a class member
	// , even if it's ugly (maybe there is a better way but I did not find out)
	// don't know how I would do if I had tons of FieldEditor's to deal with
	private Composite pathFieldParent;
	

	/**
	 * 
	 */
	public PreferencePage() {
	}

	/**
	 * @param style
	 */
	public PreferencePage(int style) {
		super(style);
	}

	/**
	 * @param title
	 * @param style
	 */
	public PreferencePage(String title, int style) {
		super(title, style);
	}

	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public PreferencePage(String title, ImageDescriptor image, int style) {
		super(title, image, style);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID));
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		super.createControl(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		this.useEmbeddedField = new BooleanFieldEditor(USE_EXTERNAL_TOOL_PREF_KEY, Messages.PreferencePage_use_embedded_rapider_label, getFieldEditorParent());
		this.pathFieldParent = getFieldEditorParent();
		this.pathField = new FileFieldEditor(EXTERNAL_TOOL_PATH_PREF_KEY, Messages.PreferencePage_path_to_rapider_label, this.pathFieldParent); 
        addField(this.useEmbeddedField);
        addField(this.pathField);
        boolean isEmbeddedRapiderused = getPreferenceStore().getBoolean(USE_EXTERNAL_TOOL_PREF_KEY); 
        updatePathFieldEnablement(! isEmbeddedRapiderused);
    }
	
	/**
	 * Updates the fields according to entered values
	 */
	private void updatePathFieldEnablement(boolean enabled) {
		this.pathField.setEnabled(enabled, this.pathFieldParent);
	}

	@SuppressWarnings("boxing")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.VALUE) && event.getSource() == this.useEmbeddedField) {
			updatePathFieldEnablement(! (boolean) event.getNewValue());
		}
		super.propertyChange(event);
	}
	
	

}
