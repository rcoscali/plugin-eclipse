package fr.jayacode.rapider.checker.cxx.checker;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import fr.jayacode.rapider.checker.cxx.AbstractTest;
import fr.jayacode.rapider.checker.cxx.checker.ErrorParser.ParsingErrorException;

@SuppressWarnings("nls")
public class RapiderProblemMarkerInfoTest extends AbstractTest {

	@Test
	public void testLineNumber_casDroit() throws ParsingErrorException, IOException, CoreException {

		IFile testFile = (IFile) getFileInWorspace("resources/FileUtilsTest/file-utils-test-resource.txt");
		RapiderProblemMarkerInfo info1 = new RapiderProblemMarkerInfo(testFile, 1, 10, 15, IMarkerGenerator.SEVERITY_WARNING, "fakeRuneName", "FakeDescription", "FakeReplacementText");
		RapiderProblemMarkerInfo info2 = new RapiderProblemMarkerInfo(testFile, 2, 100, 15, IMarkerGenerator.SEVERITY_WARNING, "fakeRuneName", "FakeDescription", "FakeReplacementText");

		assertEquals(1, info1.lineNumber);
		assertEquals(2, info2.lineNumber);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testLineNumber_outOfBound() throws ParsingErrorException, IOException, CoreException {

		IFile testFile = (IFile) getFileInWorspace("resources/FileUtilsTest/file-utils-test-resource.txt");
		RapiderProblemMarkerInfo info = new RapiderProblemMarkerInfo(testFile, 1, 1000, 15, IMarkerGenerator.SEVERITY_WARNING, "fakeRuneName", "FakeDescription", "FakeReplacementText");

		assertEquals(-1, info.lineNumber);
	}
}
