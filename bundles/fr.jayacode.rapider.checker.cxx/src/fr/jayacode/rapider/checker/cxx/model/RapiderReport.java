package fr.jayacode.rapider.checker.cxx.model;

import java.util.List;

public class RapiderReport {

	public String MainSourceFile;
	public List<Diagnostic> Diagnostics;

	public String getMainSourceFile() {
		return this.MainSourceFile;
	}

	public List<Diagnostic> getDiagnostics() {
		return this.Diagnostics;
	}

}
