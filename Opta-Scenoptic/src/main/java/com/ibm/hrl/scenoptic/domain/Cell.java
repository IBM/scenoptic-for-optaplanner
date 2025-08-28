package com.ibm.hrl.scenoptic.domain;

abstract public class Cell<K, T> implements ICell<K, T> {
	@Override
	public String toString() {
		T value = getValue();
		return "<" + this.getClass().getSimpleName() + " " + getKey() + (value == null ? "" : " = " + value) + ">";
	}
}
