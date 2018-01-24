package fr.jayacode.rapider.checker.cxx.checker;

import org.eclipse.core.runtime.IPath;

public class Command {
	private final IPath path;
	private final String[] args;
	private final String[] env;

	Command(IPath path, String[] args) {
		this(path, args, new String[] {});
	}

	Command(IPath path, String[] args, String[] env) {
		this.path = path;
		this.args = args;
		this.env = env;
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
}
