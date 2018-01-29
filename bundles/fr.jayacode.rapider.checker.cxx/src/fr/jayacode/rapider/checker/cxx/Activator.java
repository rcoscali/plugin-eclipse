package fr.jayacode.rapider.checker.cxx;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Plug-in main Class. Should not instanciated. Will be instanciated by the 
 * @author cconversin
 *
 */
public class Activator extends AbstractUIPlugin {

	public static String PLUGIN_ID = "fr.jayacode.rapider.checker.cxx"; //$NON-NLS-1$
	private static Activator fgCPlugin;

	/**
	 * @noreference This constructor is not intended to be referenced by
	 *              clients.
	 */
	public Activator() {
		super();
		fgCPlugin = this;
	}

	public static Activator getInstance() {
		return fgCPlugin;
	}

	public static void logInfo(final String message) {
		log(createInfoStatus(message));
	}

	public static void logWarning(final String message) {
		log(createWarningStatus(message));
	}

	public static void logError(final String message, final Throwable e) {
		log(createErrorStatus(message, e));
	}

	public static void logError(final String message) {
		logError(message, null);
	}

	public static void logError(final Throwable e) {
		String msg = e.getMessage();
		if (msg == null) {
			log(createErrorStatus("Error", e)); //$NON-NLS-1$
		} else {
			log(createErrorStatus("Error: " + msg, e)); //$NON-NLS-1$
		}
	}

	private static IStatus createInfoStatus(final String msg) {
		return new Status(IStatus.INFO, PLUGIN_ID, msg);
	}

	private static IStatus createWarningStatus(final String msg) {
		return new Status(IStatus.WARNING, PLUGIN_ID, msg);
	}

	private static IStatus createErrorStatus(final String msg, final Throwable e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	private static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

}
