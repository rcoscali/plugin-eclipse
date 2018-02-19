package fr.jayacode.rapider.checker.cxx.model;

public class Replacement {

	public String FilePath;
	public int Offset;
	public int Length;
	public String ReplacementText;

	public String getFilePath() {
		return this.FilePath;
	}

	public int getOffset() {
		return this.Offset;
	}

	public int getLength() {
		return this.Length;
	}

	public String getReplacementText() {
		return this.ReplacementText;
	}

}
