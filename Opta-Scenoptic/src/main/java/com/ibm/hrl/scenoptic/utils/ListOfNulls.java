package com.ibm.hrl.scenoptic.utils;

public class ListOfNulls<E> extends DefaultArrayList<E> {
	private static final long serialVersionUID = -9216000640196599694L;

	private final int size;

	public ListOfNulls(int size) {
		super();
		this.size = size;
	}

	@Override
	public E get(int index) {
		return null;
	}

	@Override
	public int size() {
		return size;
	}
}
