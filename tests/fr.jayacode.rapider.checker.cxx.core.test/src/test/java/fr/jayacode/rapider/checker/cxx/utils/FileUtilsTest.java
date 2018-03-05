package fr.jayacode.rapider.checker.cxx.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import fr.jayacode.rapider.checker.cxx.checker.ErrorParser.ParsingErrorException;

@SuppressWarnings("nls")
public class FileUtilsTest {

	@Test
	public void testLineNumber_casDroit() throws ParsingErrorException, IOException, CoreException {

		IFile testFile = (IFile) getFileInWorspace("resources/FileUtilsTest/file-utils-test-resource.txt");
		assertEquals(1, FileUtils.getLineNumberFromOffset(testFile, 0));
		assertEquals(1, FileUtils.getLineNumberFromOffset(testFile, 1));
		assertEquals(1, FileUtils.getLineNumberFromOffset(testFile, 26));
		assertEquals(2, FileUtils.getLineNumberFromOffset(testFile, 27));
		assertEquals(2, FileUtils.getLineNumberFromOffset(testFile, 28));
	}

	@Test
	public void testLineNumber_outOfBound() throws ParsingErrorException, CoreException, IOException {
		IFile testFile = (IFile) getFileInWorspace("resources/FileUtilsTest/file-utils-test-resource.txt");
		assertEquals(3, FileUtils.getLineNumberFromOffset(testFile, 1000));
	}

	@Test
	public void testLineContains_casDroit() throws CoreException, IOException {

		IFile testFile = (IFile) getFileInWorspace("resources/FileUtilsTest/file-utils-test-resource.txt");
		assertFalse(FileUtils.doesLineInFileContains(testFile, 0, "toto"));
		assertFalse(FileUtils.doesLineInFileContains(testFile, 20, "toto"));
		assertTrue(FileUtils.doesLineInFileContains(testFile, 27, "toto"));
		assertTrue(FileUtils.doesLineInFileContains(testFile, 60, "toto"));
		assertFalse(FileUtils.doesLineInFileContains(testFile, 61, "toto"));
		assertFalse(FileUtils.doesLineInFileContains(testFile, 65, "toto"));
		assertTrue(FileUtils.doesLineInFileContains(testFile, 97, "toto"));
		assertTrue(FileUtils.doesLineInFileContains(testFile, 130, "toto"));
		assertFalse(FileUtils.doesLineInFileContains(testFile, 131, "toto"));
	}

	private static IResource getFileInWorspace(final String path) throws CoreException, FileNotFoundException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		if (!project.exists()) {
			project.create(null);
			project.open(null);

			IFolder folder1 = project.getFolder("resources");
			folder1.create(true, true, null);

			IFolder folder2 = project.getFolder("resources/FileUtilsTest");
			folder2.create(true, true, null);
		}

		IFile file = project.getFile(path);
		if (!file.exists()) {
			FileInputStream fileStream = new FileInputStream(path);
			file.create(fileStream, true, null);
		}
		return file;
	}

}
