/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.checker;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.codan.core.cxx.externaltool.AbstractExternalToolBasedChecker;
import org.eclipse.cdt.codan.core.cxx.externaltool.ArgsSeparator;
import org.eclipse.cdt.codan.core.cxx.externaltool.ConfigurationSettings;
import org.eclipse.cdt.codan.core.cxx.externaltool.IInvocationParametersProvider;
import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.ProblemMarkerInfo;
import org.eclipse.core.resources.IResource;

/**
 * @author cconversin
 *
 */
public class Checker extends AbstractExternalToolBasedChecker implements IChecker {

	static private Collection<String> PROCESSED_EXTENSIONS = Arrays.asList("cpp", "CPP", "c", "C");

	@Override
	public boolean processResource(IResource resource) {
		if (PROCESSED_EXTENSIONS.contains(resource.getFileExtension())) {
			return super.processResource(resource);
		}
		return false;
	}

	@Override
	public void addMarker(ProblemMarkerInfo info) {
		super.addMarker(info);
	}

	public Checker() {
		super(new ConfigurationSettings("Rapider",
				new File("/home/cconversin/workspace/FakeRapider/Release/FakeRapider"), ""));
	}

	/**
	 * @param settings
	 */
	public Checker(ConfigurationSettings settings) {
		super(settings);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parametersProvider
	 * @param argsSeparator
	 * @param settings
	 */
	public Checker(IInvocationParametersProvider parametersProvider, ArgsSeparator argsSeparator,
			ConfigurationSettings settings) {
		super(parametersProvider, argsSeparator, settings);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.codan.core.cxx.externaltool.AbstractExternalToolBasedChecker#
	 * getParserIDs()
	 */
	@Override
	protected String[] getParserIDs() {
		return new String[] { "fr.jayacode.rapider.checker.cxx.parser" };
	}

	private static final String ERROR_PROBLEM_ID = "fr.jayacode.rapider.checker.cxx.problem1";
	private static final Map<Integer, String> PROBLEM_IDS = new HashMap<Integer, String>();

	static {
		PROBLEM_IDS.put(IMarkerGenerator.SEVERITY_WARNING, ERROR_PROBLEM_ID);
		PROBLEM_IDS.put(IMarkerGenerator.SEVERITY_INFO, "fr.jayacode.rapider.checker.cxx.problem2");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.codan.core.cxx.externaltool.AbstractExternalToolBasedChecker#
	 * getReferenceProblemId()
	 */
	@Override
	protected String getReferenceProblemId() {
		return ERROR_PROBLEM_ID;
	}

}
