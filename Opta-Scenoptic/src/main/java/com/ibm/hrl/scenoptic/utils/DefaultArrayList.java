package com.ibm.hrl.scenoptic.utils;

import java.util.ArrayList;

public class DefaultArrayList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 8819227003836545570L;

	public E getOrDefault(int index, E defaultValue) {
		E result = get(index);
		if (result == null)
			return defaultValue;
		return result;
	}
}
