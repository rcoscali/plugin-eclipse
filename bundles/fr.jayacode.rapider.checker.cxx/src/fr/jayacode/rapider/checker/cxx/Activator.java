package fr.jayacode.rapider.checker.cxx;

/**
 * Plug-in main Class. Should not instanciated. Will be instanciated by the 
 * @author cconversin
 *
 */
public class Activator extends org.eclipse.core.runtime.Plugin {

	private static Activator fgCPlugin;

	/**
	 * @noreference This constructor is not intended to be referenced by
	 *              clients.
	 */
	public Activator() {
		super();
		fgCPlugin = this;
	}

	public static Activator getInstance() {
		return fgCPlugin;
	}
}
