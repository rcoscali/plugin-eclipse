/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.codan.core.cxx.externaltool.AbstractExternalToolBasedChecker;
import org.eclipse.cdt.codan.core.cxx.externaltool.ConfigurationSettings;
import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.ProblemMarkerInfo;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import fr.jayacode.rapider.checker.cxx.Messages;

/**
 * @author cconversin
 *
 */
@SuppressWarnings("boxing")
public class Checker extends AbstractExternalToolBasedChecker implements IChecker {

	private static final String RAPIDER_TOOL_NAME = Messages.Checker_RapiderToolName;
	static private Collection<String> PROCESSED_EXTENSIONS = Arrays.asList("cpp", "CPP", "c", "C"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$

	public Checker() {
		super(new ConfigurationSettings(RAPIDER_TOOL_NAME,
				new File("/home/cconversin/workspace/FakeRapider/Release/FakeRapider"), "")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public boolean processResource(IResource resource) {
		if (PROCESSED_EXTENSIONS.contains(resource.getFileExtension())) {
			return super.processResource(resource);
		}
		return false;
	}

	@Override
	public void initPreferences(IProblemWorkingCopy problem) {
		// TODO : ne faire ça qu'une seule fois
		super.initPreferences(problem);
	}

	@Override
	public void addMarker(ProblemMarkerInfo info) {
		super.addMarker(info);
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
		return new String[] { "fr.jayacode.rapider.checker.cxx.parser" }; //$NON-NLS-1$
	}

	private static final String ERROR_PROBLEM_ID = "fr.jayacode.rapider.checker.cxx.problem1"; //$NON-NLS-1$
	private static final Map<Integer, String> PROBLEM_IDS = new HashMap<Integer, String>();

	static {
		PROBLEM_IDS.put(IMarkerGenerator.SEVERITY_WARNING, ERROR_PROBLEM_ID);
		PROBLEM_IDS.put(IMarkerGenerator.SEVERITY_INFO, "fr.jayacode.rapider.checker.cxx.problem2"); //$NON-NLS-1$
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

	/* (non-Javadoc)
	 * Filtre pour les commentaires de suppression du problème : si la ligne contient le commentaire, alors on ne remonte pas le problème
	 * comme étant remonté à l'utilisateur
	 * @see org.eclipse.cdt.codan.core.model.AbstractCheckerWithProblemPreferences#shouldProduceProblem(org.eclipse.cdt.codan.core.model.IProblem, org.eclipse.cdt.codan.core.model.IProblemLocation, java.lang.Object[])
	 */
	@Override
	protected boolean shouldProduceProblem(IProblem problem, IProblemLocation loc, Object... args) {
		String suppressionComment = (String) getSuppressionCommentPreference(problem).getValue();
		if (suppressionComment.isEmpty()) {
			return super.shouldProduceProblem(problem, loc, args);
		}
		IResource resource = loc.getFile();
		if (!(resource instanceof IFile)) {
			return false;
		}
		IFile file = (IFile) resource;
		try {
			InputStream content = file.getContents();
			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			String line = new String();
			int n = loc.getLineNumber();
			for (int i = 0; i < loc.getLineNumber(); i++) {
				line = reader.readLine();
			}
			return !line.contains(suppressionComment);
		} catch (CoreException | IOException e) {
			// TODO : print stack ?
		}
		return super.shouldProduceProblem(problem, loc, args);
	}

}
