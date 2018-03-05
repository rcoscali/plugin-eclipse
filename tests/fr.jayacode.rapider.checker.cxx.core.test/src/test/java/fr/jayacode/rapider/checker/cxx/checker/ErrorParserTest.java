package fr.jayacode.rapider.checker.cxx.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.jayacode.rapider.checker.cxx.checker.ErrorParser.ParsingErrorException;
import fr.jayacode.rapider.checker.cxx.model.Diagnostic;
import fr.jayacode.rapider.checker.cxx.model.RapiderReport;
import fr.jayacode.rapider.checker.cxx.model.Replacement;

/**
 * Tests for the ErrorParser
 * 
 * @author cconversin
 *
 */
@SuppressWarnings("nls")
public class ErrorParserTest {

	/**
	 * Tests with a well-formed YAML file: it's got all the compulsory fileds, and
	 * only these ones.
	 * 
	 * @throws ParsingErrorException
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testCasDroit() throws ParsingErrorException {
		ErrorParser ep = new ErrorParser();
		File fixesFile = new File("resources/ErrorParserTest/export-fixes-normal-uc.log"); //$NON-NLS-1$
		RapiderReport report = null;
		report = ep.parseFixesExportFile(fixesFile);

		assertNotNull(report);

		assertEquals("/home/user/workspace/Tidy/ex.c", report.getMainSourceFile());
		assertEquals(17, report.getDiagnostics().size());
		List<Integer> ids = new ArrayList<Integer>();
		// vérifier que tous les ids sont uniques
		for (Diagnostic diag : report.getDiagnostics()) {
			assertFalse(ids.contains(diag.getId()));
			ids.add(diag.getId());
		}

		Diagnostic diag0 = report.getDiagnostics().get(0);
		assertEquals("Cannot parse invalid groups file 'request_groups.json'!", diag0.getMessage());
		assertEquals("clang-diagnostic-error", diag0.getDiagnosticName());
		assertNull(diag0.getReplacements());

		Diagnostic diag2 = report.getDiagnostics().get(2);
		assertEquals("#includes are not sorted properly", diag2.getMessage());
		assertEquals("llvm-include-order", diag2.getDiagnosticName());
		assertEquals(4, diag2.getReplacements().size());

		Replacement rep2_0 = diag2.getReplacements().get(0);
		assertEquals("ex.c", rep2_0.getFilePath());
		assertEquals(9, rep2_0.getOffset());
		assertEquals(9, rep2_0.getLength());
		assertEquals("<alloca.h>", rep2_0.getReplacementText());

		Replacement rep2_1 = diag2.getReplacements().get(1);
		assertEquals("ex.c", rep2_1.getFilePath());
		assertEquals(28, rep2_1.getOffset());
		assertEquals(10, rep2_1.getLength());
		assertEquals("<stdio.h>", rep2_1.getReplacementText());

		Replacement rep2_2 = diag2.getReplacements().get(2);
		assertEquals("ex.c", rep2_2.getFilePath());
		assertEquals(48, rep2_2.getOffset());
		assertEquals(10, rep2_2.getLength());
		assertEquals("<stdlib.h>", rep2_2.getReplacementText());

		Replacement rep2_3 = diag2.getReplacements().get(3);
		assertEquals("ex.c", rep2_3.getFilePath());
		assertEquals(68, rep2_3.getOffset());
		assertEquals(10, rep2_3.getLength());
		assertEquals("<string.h>", rep2_3.getReplacementText());

	}

	/**
	 * Tests with a well-formed YAML file but that got an entry with no replacements
	 * 
	 * @throws ParsingErrorException
	 */
	@Test
	public void testZeroReplacements() throws ParsingErrorException {
		ErrorParser ep = new ErrorParser();
		File fixesFile = new File("resources/ErrorParserTest/export-fixes-normal-uc.log"); //$NON-NLS-1$
		RapiderReport report = null;
		report = ep.parseFixesExportFile(fixesFile);

		assertNotNull(report);

		assertTrue(report.getDiagnostics().size() >= 1);

		Diagnostic diag0 = report.getDiagnostics().get(0);
		assertEquals("Cannot parse invalid groups file 'request_groups.json'!", diag0.getMessage());
		assertEquals("clang-diagnostic-error", diag0.getDiagnosticName());
		assertNull(diag0.getReplacements());
	}

	/**
	 * Tests with a well-formed YAML file, but that contains extra fiels -> they
	 * should be ignored
	 * 
	 * @throws ParsingErrorException
	 */
	@Test
	public void testCasChampsSurnumeraires() throws ParsingErrorException {
		ErrorParser ep = new ErrorParser();
		File fixesFile = new File("resources/ErrorParserTest/export-fixes-champs-surnumeraires.log"); //$NON-NLS-1$
		RapiderReport report = null;
		report = ep.parseFixesExportFile(fixesFile);

		assertNotNull(report);

		assertEquals("/home/user/workspace/Tidy/ex.c", report.getMainSourceFile());
		assertEquals(2, report.getDiagnostics().size());

		Diagnostic diag0 = report.getDiagnostics().get(0);
		assertEquals("Cannot parse invalid groups file 'request_groups.json'!", diag0.getMessage());
		assertEquals("clang-diagnostic-error", diag0.getDiagnosticName());
		assertNull(diag0.getReplacements());

		Diagnostic diag1 = report.getDiagnostics().get(1);
		assertEquals("#includes are not sorted properly", diag1.getMessage());
		assertEquals("llvm-include-order", diag1.getDiagnosticName());
		assertEquals(2, diag1.getReplacements().size());

		Replacement rep1_0 = diag1.getReplacements().get(0);
		assertEquals("ex.c", rep1_0.getFilePath());
		assertEquals(9, rep1_0.getOffset());
		assertEquals(9, rep1_0.getLength());
		assertEquals("<alloca.h>", rep1_0.getReplacementText());

		Replacement rep1_1 = diag1.getReplacements().get(1);
		assertEquals("ex.c", rep1_1.getFilePath());
		assertEquals(28, rep1_1.getOffset());
		assertEquals(10, rep1_1.getLength());
		assertEquals("<stdio.h>", rep1_1.getReplacementText());

	}

	/**
	 * Tests with a file that has syntax errors in it.
	 * 
	 * @throws ParsingErrorException
	 */
	@Test(expected = ParsingErrorException.class)
	public void testCasErreurSyntaxe() throws ParsingErrorException {
		ErrorParser ep = new ErrorParser();
		File fixesFile = new File("resources/ErrorParserTest/export-fixes-erreur-syntaxe.log"); //$NON-NLS-1$
		@SuppressWarnings("unused")
		RapiderReport report = null;
		report = ep.parseFixesExportFile(fixesFile);
		fail();
	}

	/**
	 * tets with a non-exoistant file
	 * 
	 * @throws ParsingErrorException
	 */
	@Test(expected = ParsingErrorException.class)
	public void testCasFicherNonExistant() throws ParsingErrorException {
		ErrorParser ep = new ErrorParser();
		File fixesFile = new File("resources/ErrorParserTest/ghost_file.log"); //$NON-NLS-1$
		@SuppressWarnings("unused")
		RapiderReport report = null;
		report = ep.parseFixesExportFile(fixesFile);
		fail();
	}

}
