package fr.jayacode.rapider.checker.cxx.checker;

import org.eclipse.cdt.core.ProblemMarkerInfo;
import org.eclipse.core.resources.IResource;

/**
 * Convenience class for acessing rapider specific values in the ProblemMarkerInfo.
 * @author cconversin
 *
 */
public class RapiderProblemMarkerInfo extends ProblemMarkerInfo {

	private static final String RAPIDER_RULENAME_KEY = "RAPIDER_RULENAME_KEY"; //$NON-NLS-1$
	private static final String RAPIDER_REPLACEMENT_TEXT_KEY = "RAPIDER_REPLACEMENT_TEXT_KEY"; //$NON-NLS-1$

	public RapiderProblemMarkerInfo(IResource file, int startChar, int endChar, int severity, String ruleName, String description, String replacementText) {
		super(file, -1, startChar, endChar, description, severity, null);
		this.setRuleName(ruleName);
		this.setReplacementText(replacementText);
	}

	public String getRuleName() {
		return this.getAttribute(RAPIDER_RULENAME_KEY);
	}

	public String getReplacementText() {
		return this.getAttribute(RAPIDER_REPLACEMENT_TEXT_KEY);
	}

	public void setRuleName(final String ruleName) {
		this.setAttribute(RAPIDER_RULENAME_KEY, ruleName);
	}

	public void setReplacementText(final String replacementText) {
		this.setAttribute(RAPIDER_REPLACEMENT_TEXT_KEY, replacementText);
	}
}
