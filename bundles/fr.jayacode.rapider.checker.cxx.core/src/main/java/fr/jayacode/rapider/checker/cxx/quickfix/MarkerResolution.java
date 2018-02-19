/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.quickfix;

import org.eclipse.cdt.codan.ui.AbstractCodanCMarkerResolution;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

/**
 * @author cconversin
 *
 */
public class MarkerResolution extends AbstractCodanCMarkerResolution implements IMarkerResolution2 {

	/**
	 * 
	 */
	public MarkerResolution() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLabel() {
		return "KikouCCNMarkerResolution"; //$NON-NLS-1$
	}

	@Override
	public boolean isApplicable(IMarker marker) {
		return true;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void apply(IMarker marker, IDocument document) {
		// TODO Encore en chantier
		// int startChar = MarkerUtilities.getCharStart(marker);
		// int endChar = MarkerUtilities.getCharEnd(marker);
		// String replacementString = marker.getAttribute("patch", "");

		String a = this.getProblemArgument(marker, 0);
		String b = this.getProblemArgument(marker, 1);
		System.out.println("KikouCCN"); //$NON-NLS-1$
	}

}
