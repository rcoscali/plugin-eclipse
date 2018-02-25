package fr.jayacode.rapider.checker.cxx.checker;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import fr.jayacode.rapider.checker.cxx.checker.ErrorParser.ParsingErrorException;
import fr.jayacode.rapider.checker.cxx.model.RapiderReport;

public class ErrorParserTest {

	/**
	 * test avec un fichier qui est bien formé : il a tous les champs nécessaires et
	 * uniquement ceux-là
	 * @throws ParsingErrorException 
	 */
	@Test
	public void testCasDroit() throws ParsingErrorException {
		ErrorParser ep = new ErrorParser();
		File fixesFile = new File("resources/ErrorParserTest/export-fixes-normal-uc.log"); //$NON-NLS-1$
		RapiderReport report = null;
		report = ep.parseFixesExportFile(fixesFile);
		assertNotNull(report);
	}

	/**
	 * test avec un fichier qui est bien formé mais qui a des champs surnuméraires
	 * qui ne correspondent à rien --> il faut les ignorer
	 * @throws ParsingErrorException 
	 */
	@Test
	public void testCasChampsSurnumeraires() throws ParsingErrorException {
		ErrorParser ep = new ErrorParser();
		File fixesFile = new File("resources/ErrorParserTest/export-fixes-champs-surnumeraires.log"); //$NON-NLS-1$
		RapiderReport report = null;
		report = ep.parseFixesExportFile(fixesFile);
		assertNotNull(report);
	}

	/**
	 * test avec un fichier syntaxiquement malformé
	 * @throws ParsingErrorException 
	 */
	@Test(expected = ParsingErrorException.class)
	public void testCasErreurSyntaxe() throws ParsingErrorException {
		ErrorParser ep = new ErrorParser();
		File fixesFile = new File("resources/ErrorParserTest/export-fixes-erreur-syntaxe.log"); //$NON-NLS-1$
		RapiderReport report = null;
		report = ep.parseFixesExportFile(fixesFile);
		fail();
	}
	
	/**
	 * test avec un fichier syntaxiquement malformé
	 * @throws ParsingErrorException 
	 */
	@Test(expected = ParsingErrorException.class)
	public void testCasFicherNonExistant() throws ParsingErrorException {
		ErrorParser ep = new ErrorParser();
		File fixesFile = new File("resources/ErrorParserTest/ghost_file.log"); //$NON-NLS-1$
		RapiderReport report = null;
		report = ep.parseFixesExportFile(fixesFile);
		fail();
	}
	
	// test avec un fichier sans replacements
	
}
