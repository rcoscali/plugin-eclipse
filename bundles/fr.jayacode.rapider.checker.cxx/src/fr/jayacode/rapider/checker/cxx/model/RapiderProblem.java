package fr.jayacode.rapider.checker.cxx.model;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import fr.jayacode.rapider.checker.cxx.checker.RapiderProblemMarkerInfo;

/**
 * Instance of a problem declared during a rapider analysis. NB : this is not an
 * Eclipse marker, but a representation of a problem, that will lead to a
 * problem marker
 * 
 * @author cconversin
 *
 */
public class RapiderProblem {

	public static final String RAPIDER_PROBLEMMARKER_ID = "fr.jayacode.rapider.problemmarker"; //$NON-NLS-1$

	/**
	 * This prefix will be concatenated with replacement text to store quickfix
	 * patches in Codan markers. Cf.
	 * {@link fr.jayacode.rapider.checker.cxx.quickfix.MarkerResolution}.
	 */
//	private static final String REPLACEMENT_TEXT_PREFIX = "rapiderreplacementtext:"; //$NON-NLS-1$

	public class RapiderProblemLocation {

		private IResource file;
		private int offset;
		private int length;

		public RapiderProblemLocation(IResource file2, int offset, int length) {
			super();
			this.file = file2;
			this.offset = offset;
			this.length = length;
		}

		public IResource getFile() {
			return this.file;
		}

		public void setFile(IFile file) {
			this.file = file;
		}

		public int getOffset() {
			return this.offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}

		public int getLength() {
			return this.length;
		}

		public void setLength(int length) {
			this.length = length;
		}

	}

	private int severity;
	private String description;
	private String ruleName;
	private String replacementText;
	private RapiderProblemLocation location;

	public RapiderProblem(RapiderProblemMarkerInfo info) {
		this.location = new RapiderProblemLocation(info.file, info.startChar, info.endChar - info.startChar);
		this.description = info.description;
		this.severity = info.severity;
		this.ruleName = info.getRuleName();
		this.replacementText = info.getReplacementText();
	}

	public RapiderProblemLocation getLocation() {
		return this.location;
	}

	public void setLocation(RapiderProblemLocation location) {
		this.location = location;
	}

	public int getSeverity() {
		return this.severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRuleName() {
		return this.ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getReplacementText() {
		return this.replacementText;
	}

	public void setReplacementText(String replacementText) {
		this.replacementText = replacementText;
	}

	/**
	 * @return the newly created marker
	 * @throws CoreException 
	 */
	public IMarker createMarker() throws CoreException {
		IResource file = this.location.getFile();
		IMarker marker = file.createMarker(RAPIDER_PROBLEMMARKER_ID);
		int startChar = this.getLocation().getOffset();
		int endChar = startChar + this.getLocation().getLength();
		
		marker.setAttribute(IMarker.MESSAGE, this.getDescription());
		marker.setAttribute(IMarker.SEVERITY, this.getSeverity());
		marker.setAttribute(IMarker.CHAR_START, startChar);
		marker.setAttribute(IMarker.CHAR_END, endChar);
		return marker;
	}
}
