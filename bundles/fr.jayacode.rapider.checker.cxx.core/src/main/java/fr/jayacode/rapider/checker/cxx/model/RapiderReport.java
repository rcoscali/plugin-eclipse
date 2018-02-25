package fr.jayacode.rapider.checker.cxx.model;

import java.util.ArrayList;
import java.util.List;

public class RapiderReport {

	public String MainSourceFile;
	public List<Diagnostic> Diagnostics;

	public RapiderReport() {
		super();
		this.Diagnostics = new ArrayList<Diagnostic>();
	}

	public String getMainSourceFile() {
		return this.MainSourceFile;
	}

	public void setMainSourceFile(String mainSourceFile) {
		this.MainSourceFile = mainSourceFile;
	}

	public void setDiagnostics(List<Diagnostic> diagnostics) {
		this.Diagnostics = diagnostics;
	}

	public List<Diagnostic> getDiagnostics() {
		return this.Diagnostics;
	}

}
