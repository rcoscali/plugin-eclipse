package fr.jayacode.rapider.checker.cxx;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "fr.jayacode.rapider.checker.cxx.messages"; //$NON-NLS-1$
	public static String Checker_RapiderToolName;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
