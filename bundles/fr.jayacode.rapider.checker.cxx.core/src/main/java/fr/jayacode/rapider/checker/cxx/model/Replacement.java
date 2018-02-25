package fr.jayacode.rapider.checker.cxx.model;

public class Replacement {

	public String FilePath = "";; //$NON-NLS-1$
	public int Offset = 0;
	public int Length = 0;
	public String ReplacementText;

	public Replacement() {
		super();
	}

	public String getFilePath() {
		return this.FilePath;
	}

	public void setFilePath(String filePath) {
		FilePath = filePath;
	}

	public int getOffset() {
		return Offset;
	}

	public void setOffset(int offset) {
		Offset = offset;
	}

	public int getLength() {
		return Length;
	}

	public void setLength(int length) {
		Length = length;
	}

	public String getReplacementText() {
		return ReplacementText;
	}

	public void setReplacementText(String replacementText) {
		ReplacementText = replacementText;
	}



}
