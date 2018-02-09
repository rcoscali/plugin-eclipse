/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.model;

import java.util.List;

/**
 * @author cconversin
 *
 */
public class Diagnostic {

	public String DiagnosticName;
	public List<Replacement> Replacements;

	public String getDiagnosticName() {
		return this.DiagnosticName;
	}

	public List<Replacement> getReplacements() {
		return this.Replacements;
	}

}
