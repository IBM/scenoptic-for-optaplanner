package com.ibm.hrl.test.scalars.main;

import static com.ibm.hrl.scenoptic.utils.ScenopticUtils.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.keys.AdHocRangeKey;
import com.ibm.hrl.scenoptic.keys.PredecessorCellKey;
import com.ibm.hrl.scenoptic.keys.RangeDistance;
import com.ibm.hrl.scenoptic.keys.CellDistance;
import com.ibm.hrl.scenoptic.keys.FixedDistance;
import com.ibm.hrl.scenoptic.domain.Cell;
import com.ibm.hrl.scenoptic.domain.descriptors.Formula;
import com.ibm.hrl.scenoptic.domain.IncrementalCell;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalPredecessorFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalUpdateFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalFreshFormula;
import com.ibm.hrl.scenoptic.domain.InputCell;
import com.ibm.hrl.scenoptic.domain.PlanningCell;
import com.ibm.hrl.scenoptic.domain.ShadowCell;
import com.ibm.hrl.scenoptic.domain.SpreadsheetProblem;
import com.ibm.hrl.scenoptic.domain.initialization.IncrementalInitializer;
import com.ibm.hrl.scenoptic.domain.initialization.IntegerInitializer;
import com.ibm.hrl.scenoptic.domain.initialization.DoubleInitializer;
import com.ibm.hrl.scenoptic.info.DecisionCellInfo;
import com.ibm.hrl.scenoptic.info.ShadowCellInfo;
import com.ibm.hrl.scenoptic.info.IncrementalCellInfo;
import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.AdHocRangeKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.main.SpreadsheetOptimizationMain;
import com.ibm.hrl.scenoptic.info.SummingCellInfo;
import com.ibm.hrl.scenoptic.domain.ISummingCell;

import com.ibm.hrl.test.scalars.domain.ScalarsInt0To11;
import com.ibm.hrl.test.scalars.domain.ScalarsShadowCell;
import com.ibm.hrl.test.scalars.domain.ScalarsIncrementalCell;
import com.ibm.hrl.test.scalars.solver.ScalarsSolution;
import com.ibm.hrl.test.scalars.solver.ScalarsScoreCalculator;

public class ScalarsMain extends SpreadsheetOptimizationMain<AbstractCellKey> {
	private static final List<PredecessorCellKey> PREDECESSORS_KEYS = List.of(
            new PredecessorCellKey(new CellKey("Scalars", 1, 4),
					List.of(new CellDistance("Scalars", 0, -3))));


    private IncrementalFreshFormula IFF1 = (current, newValues, newScalars) -> getOptionalInt((Integer) current) + (getOptionalInt((Integer) newValues.get(0)) == getOptionalInt((Integer) newScalars.get(0)) + 1 ? getOptionalInt((Integer) newValues.get(0)) : 0);
    private IncrementalPredecessorFormula<AbstractCellKey> IPF1 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Scalars", 1 + index, 1) };
    private IncrementalUpdateFormula IUF1 = (current, previousValues, newValues, previousScalars, newScalars) -> getOptionalInt((Integer) current) - (getOptionalInt((Integer) previousValues.get(0)) == getOptionalInt((Integer) previousScalars.get(0)) + 1 ? getOptionalInt((Integer) previousValues.get(0)) : 0) + (getOptionalInt((Integer) newValues.get(0)) == getOptionalInt((Integer) newScalars.get(0)) + 1 ? getOptionalInt((Integer) newValues.get(0)) : 0);

	public Stream<AbstractCellKey> getInputInfo() {
	    return Stream.of(
);
    }

	public List<DecisionCellInfo<AbstractCellKey>> getDecisionInfo() {
		return List.of(
            new DecisionCellInfo<>(new AdHocRangeKey("Scalars", 1, 11, 1, 1), ScalarsInt0To11::new));
    }

	public List<ShadowCellInfo<AbstractCellKey>> getShadowCellInfo() {
		return List.of(
);
    }

    public List<SummingCellInfo<AbstractCellKey>> getSummingCellInfo() {
		return List.of(
);
    }

	@Override
	public Set<AbstractCellKey> getNonStrictCells() {
        return Collections.emptySet();
	}

	public Map<AbstractCellKey, AbstractCellKey[]> getPredecessorInfo() {
		return predecessors;
    }

	public List<IncrementalCellInfo<AbstractCellKey>> getIncrementalInfo() {
		return List.of(
			new IncrementalCellInfo<>(new CellKey("Scalars", 1, 4), 10, IPF1, IUF1, IFF1, IntegerInitializer.SINGLETON));
	}

	@Override
	public ShadowCell<AbstractCellKey, ?> createShadowVariable(AbstractCellKey key, List<AbstractCellKey> predecessors, Formula formula) {
		return new ScalarsShadowCell<>(key, predecessors, formula);
	}

	@Override
	public IncrementalCell<AbstractCellKey, ?> createIncrementalVariable(
            AbstractCellKey key, List<AbstractCellKey> predecessors, int numberOfPredecessorLists,
            IncrementalPredecessorFormula<AbstractCellKey> incrementalPredecessors, IncrementalUpdateFormula incrementalUpdateFormula,
            IncrementalFreshFormula incrementalFreshFormula, IncrementalInitializer<?> initializer) {
		return new ScalarsIncrementalCell<>(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula, incrementalFreshFormula, initializer);
	}

	@Override
	public SpreadsheetProblem<AbstractCellKey> createSolution(
	        List<? extends PlanningCell<AbstractCellKey, ?>> decisions,
			List<? extends ShadowCell<AbstractCellKey, ?>> cells,
			List<? extends InputCell<AbstractCellKey, ?>> inputs,
			List<? extends IncrementalCell<AbstractCellKey, ?>> incrementals,
			List<? extends ISummingCell<AbstractCellKey, ?>> summingCells) {
		return new ScalarsSolution(decisions, cells, inputs, incrementals, summingCells);
	}

	@Override
	public Class<? extends SpreadsheetProblem<AbstractCellKey>> getSolutionClass() {
		return (Class<? extends SpreadsheetProblem<AbstractCellKey>>) ScalarsSolution.class;
	}

	@Override
	public Class<Cell<AbstractCellKey, ?>>[] getEntityClasses() {
		return new Class[] { ScalarsInt0To11.class,  ScalarsShadowCell.class, ScalarsIncrementalCell.class };
	}

	public Class<? extends EasyScoreCalculator<? extends SpreadsheetProblem<AbstractCellKey>, BendableScore>> getScoreCalculatorClass() {
		return ScalarsScoreCalculator.class;
	}

	public static void main(String[] args) throws ArgumentParserException {
		new ScalarsMain().mainBody(args, PREDECESSORS_KEYS);
	}
}
