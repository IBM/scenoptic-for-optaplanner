package com.ibm.hrl.scenoptic.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.ibm.hrl.scenoptic.debug.DebugSpec;
import com.ibm.hrl.scenoptic.debug.DebugUtils;
import com.ibm.hrl.scenoptic.domain.PlanningCell;
import com.ibm.hrl.scenoptic.domain.SpreadsheetProblem;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

public class CellListener<K extends Comparable<K>> extends CellPropagator<K>
		implements VariableListener<SpreadsheetProblem<K>, PlanningCell<K, ?>> {
	protected SpreadsheetProblem<K> problem;
	protected Map<K, Optional<Object>> changedValues = new HashMap<>();
	protected Map<K, Optional<Object>> newValues = new HashMap<>();

	// No-arg constructor required for OptaPlanner
	public CellListener() {
	}

	@Override
	public SpreadsheetProblem<K> getProblem() {
		return problem;
	}

	@Override
	public void resetWorkingSolution(ScoreDirector<SpreadsheetProblem<K>> scoreDirector) {
		VariableListener.super.resetWorkingSolution(scoreDirector);
		problem = scoreDirector.getWorkingSolution();
	}

	@Override
	public void beforeVariableChanged(ScoreDirector<SpreadsheetProblem<K>> scoreDirector, PlanningCell<K, ?> entity) {
		final K key = entity.getKey();
		if (problem.debug(DebugSpec.DebugLocation.ERROR)) {
			if (changedValues.get(key) != null) {
				System.out.println("!!! Double new value for " + entity.getKey());
				System.exit(-2);
			}
		}
		changedValues.put(key, Optional.ofNullable(entity.getValue()));
		// DEBUG
		if (problem.debug(DebugSpec.DebugLocation.LISTENER))
			System.out.println("--- " + debugIdentifiers(scoreDirector) + entity.getKey() + " changing from " + entity.getValue());
	}

	@Override
	public void afterVariableChanged(ScoreDirector<SpreadsheetProblem<K>> scoreDirector, PlanningCell<K, ?> entity) {
		K key = entity.getKey();
		// DEBUG
		if (problem.debug(DebugSpec.DebugLocation.LISTENER))
			System.out.println("+++ " + debugIdentifiers(scoreDirector) + key + " changed to " + entity.getValue());
		if (problem.debug(DebugSpec.DebugLocation.ERROR)) {
			if (newValues.get(key) != null) {
				System.out.println("!!! Double new value for " + key);
				System.exit(-3);
			}
		}
		newValues.put(key, Optional.ofNullable(entity.getValue()));
		if (changedValues.keySet().equals(newValues.keySet())) {
			propagate(this, changedValues, newValues, scoreDirector);
			changedValues.clear();
			newValues.clear();
		}
	}

	@Override
	public void beforeEntityAdded(ScoreDirector<SpreadsheetProblem<K>> scoreDirector, PlanningCell<K, ?> entity) {
	}

	@Override
	public void afterEntityAdded(ScoreDirector<SpreadsheetProblem<K>> scoreDirector, PlanningCell<K, ?> entity) {
	}

	@Override
	public void beforeEntityRemoved(ScoreDirector<SpreadsheetProblem<K>> scoreDirector, PlanningCell<K, ?> entity) {
	}

	@Override
	public void afterEntityRemoved(ScoreDirector<SpreadsheetProblem<K>> scoreDirector, PlanningCell<K, ?> entity) {
	}

	@Override
	public boolean requiresUniqueEntityEvents() {
		return true;
	}

	protected String debugIdentifiers(ScoreDirector<SpreadsheetProblem<K>> scoreDirector) {
		return "(lis=" + DebugUtils.objectId(this) + ", dir=" + DebugUtils.objectId(scoreDirector) +
				", sol=" + DebugUtils.objectId(scoreDirector.getWorkingSolution()) + ") ";
	}
}