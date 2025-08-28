package com.ibm.hrl.scenoptic.keys;

import java.util.stream.Stream;

public abstract class AbstractCellKey implements IKey, Comparable<AbstractCellKey> {
	protected String sheet;
	static protected final boolean isAdHoc = false;

	public AbstractCellKey(String sheet) {
		super();
		this.sheet = sheet;
	}

	abstract public Stream<AbstractCellKey> getElements();

	public String getSheet() {
		return sheet;
	}

	public boolean isAdHoc() {
		return isAdHoc;
	}

	abstract public AbstractCellKey down(int n);

	abstract public AbstractCellKey right(int n);
	
	abstract public AbstractCellKey move(int down, int right);

	public static String indexToColumn(int index) {
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String result = "";
		while (index != 0) {
			int mod = (index - 1) % 26;
			result = letters.charAt(mod) + result;
			index = (index - mod) / 26;
		}
		return result;
	}

	public static String cellDesignator(int row, int col) {
		return indexToColumn(col) + row;
	}
}
