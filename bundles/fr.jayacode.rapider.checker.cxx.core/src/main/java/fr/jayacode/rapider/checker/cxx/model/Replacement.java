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
		this.FilePath = filePath;
	}

	public int getOffset() {
		return this.Offset;
	}

	public void setOffset(int offset) {
		this.Offset = offset;
	}

	public int getLength() {
		return this.Length;
	}

	public void setLength(int length) {
		this.Length = length;
	}

	public String getReplacementText() {
		return this.ReplacementText;
	}

	public void setReplacementText(String replacementText) {
		this.ReplacementText = replacementText;
	}



}
