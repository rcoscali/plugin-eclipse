package fr.jayacode.rapider.checker.cxx.checker;

import java.io.File;

import org.eclipse.core.runtime.IPath;

public class Command {
	private final IPath path;
	private final String[] args;
	private final String[] env;
	private final File exportFile;


	Command(IPath path, String[] args, String[] env, File exportFile) {
		this.path = path;
		this.args = args;
		this.env = env;
		this.exportFile = exportFile;
	}

	IPath getPath() {
		return this.path;
	}

	String[] getArgs() {
		return this.args;
	}

	String[] getEnv() {
		return this.env;
	}

	public File getExportFile() {
		return this.exportFile;
	}
}
