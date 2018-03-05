package fr.jayacode.rapider.checker.cxx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Parent class for all Unit tests of this bundle
 * 
 * @author cconversin
 *
 */
public abstract class AbstractTest {

	/**
	 * Insert the resrouce file in the Eclipse workspace in order to test methods
	 * that need Eclipse resource file (IFile)
	 * 
	 * @param path
	 * @return the file
	 * @throws CoreException
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("nls")
	protected static IFile getFileInWorspace(final String path) throws CoreException, FileNotFoundException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		if (!project.exists()) {
			project.create(null);
			project.open(null);

			IPath p = new Path(path);
			String incrementalPath = "";
			for (int i = 0; i < (p.segmentCount() - 1); i++) {
				incrementalPath += (i > 0 ? "/" : "") + p.segment(i);
				IFolder folder1 = project.getFolder(incrementalPath);
				folder1.create(true, true, null);
			}

		}

		IFile file = project.getFile(path);
		if (!file.exists()) {
			FileInputStream fileStream = new FileInputStream(path);
			file.create(fileStream, true, null);
		}
		return file;
	}

}
