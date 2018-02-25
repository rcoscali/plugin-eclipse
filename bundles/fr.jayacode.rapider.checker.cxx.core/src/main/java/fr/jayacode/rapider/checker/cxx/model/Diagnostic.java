/**
 * 
 */
package fr.jayacode.rapider.checker.cxx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cconversin
 *
 */
public class Diagnostic {

	private static AtomicInteger uidGenerator = new AtomicInteger(); 
	private int uid;
	public String Message;
	public String DiagnosticName;
	public List<Replacement> Replacements;

	public Diagnostic() {
		super();
		this.uid = uidGenerator.getAndIncrement();
		this.Replacements = new ArrayList<Replacement>();
	}

	public int getId() {
		return this.uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getMessage() {
		return this.Message;
	}

	public String getDiagnosticName() {
		return this.DiagnosticName;
	}

	public List<Replacement> getReplacements() {
		return this.Replacements;
	}

}
