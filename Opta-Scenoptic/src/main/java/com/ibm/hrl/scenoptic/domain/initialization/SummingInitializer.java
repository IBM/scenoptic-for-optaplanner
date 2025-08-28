package com.ibm.hrl.scenoptic.domain.initialization;

import com.ibm.hrl.scenoptic.domain.ISummingCell;

public class SummingInitializer<S extends ISummingCell<?, ?>> implements IncrementalInitializer<S> {
	private S summingCell;

	public void setRef(S summingCell) {
		assert this.summingCell == null;
		this.summingCell = summingCell;
	}

	@Override
	public S addValue(S acc, S value) {
		summingCell.setEnabled(true);
		return summingCell;
	}

	@Override
	public S removeValue(S acc, S value) {
		summingCell.setEnabled(false);
		return summingCell;
	}
}
