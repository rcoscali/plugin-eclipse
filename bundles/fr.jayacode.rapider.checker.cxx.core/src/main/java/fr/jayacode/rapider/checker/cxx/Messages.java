package fr.jayacode.rapider.checker.cxx;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "fr.jayacode.rapider.checker.cxx.messages"; //$NON-NLS-1$
	public static String Checker_RapiderToolName;
	public static String CompileCommandFileSettings_label;
	public static String ErrorParser_file_closing_exception_error_message;
	public static String ErrorParser_file_not_found_exception_message;
	public static String ErrorParser_inexistant_file;
	public static String ErrorParser_parsing_error_exception_message;
	public static String ErrorParser_parsing_error_log;
	public static String ErrorParser_undefined_error;
	public static String ErrorParser_undefined_rule;
	public static String PreferencePage_path_to_libs_label;
	public static String PreferencePage_path_to_rapider_label;
	public static String PreferencePage_use_embedded_rapider_label;
	public static String RapiderInvoker_dependencies_not_found_exception_message;
	public static String RapiderInvoker_file_not_found_exception_message;
	public static String RapiderInvoker_monitor_launching_message;
	public static String RapiderProblemMarkerInfo_line_number_processing_error;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
