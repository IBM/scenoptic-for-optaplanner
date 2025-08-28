package com.ibm.hrl.scenoptic.domain;

/**
 * Marker interface for classes that have a special way to indicate whether they have changed.
 * <p>
 * An example is cells whose contents change but the object itself remains the same.
 */
public interface ISpecialChangeIndicator extends ICellWithProcess {
	boolean changedInLastCycle(Object oldValue);
}
