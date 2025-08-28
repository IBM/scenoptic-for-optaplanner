package com.ibm.hrl.test.aggregate.main;

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

import com.ibm.hrl.test.aggregate.domain.AggregateInt0To11;
import com.ibm.hrl.test.aggregate.domain.AggregateInt0To3;
import com.ibm.hrl.test.aggregate.domain.AggregateShadowCell;
import com.ibm.hrl.test.aggregate.domain.AggregateIncrementalCell;
import com.ibm.hrl.test.aggregate.solver.AggregateSolution;
import com.ibm.hrl.test.aggregate.solver.AggregateScoreCalculator;

public class AggregateMain extends SpreadsheetOptimizationMain<AbstractCellKey> {
	private static final List<PredecessorCellKey> PREDECESSORS_KEYS = List.of(
            new PredecessorCellKey(new AdHocRangeKey("Sheet1", 1, 2, 4, 5),
					List.of()),
            new PredecessorCellKey(new AdHocRangeKey("Sheet2", 2, 11, 4, 4),
					List.of(new CellDistance("Sheet2", 0, -2))),
            new PredecessorCellKey(new AdHocRangeKey("Sheet2", 3, 12, 5, 5),
					List.of(new CellDistance("Sheet2", 0, -2))));


    private Formula CF1 = values -> (int) values.get(0) * 2;
    private IncrementalFreshFormula IFF1 = (current, newValues, newScalars) -> getOptionalInt((Integer) current) + (getOptionalInt((Integer) newValues.get(1)) > 0 && getOptionalInt((Integer) newValues.get(2)) > 0 ? getOptionalInt((Integer) newValues.get(0)) : 0);
    private IncrementalFreshFormula IFF2 = (current, newValues, newScalars) -> getOptionalInt((Integer) current) + (getOptionalInt((Integer) newValues.get(0)) > 0 && getOptionalInt((Integer) newValues.get(1)) > 0 ? 1 : 0);
    private IncrementalPredecessorFormula<AbstractCellKey> IPF1 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Sheet1", 1 + index, 1), new CellKey("Sheet2", 2 + index, 2), new CellKey("Sheet2", 3 + index, 3) };
    private IncrementalPredecessorFormula<AbstractCellKey> IPF2 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Sheet2", 2 + index, 2), new CellKey("Sheet2", 3 + index, 3) };
    private IncrementalPredecessorFormula<AbstractCellKey> IPF3 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Sheet1", 1 + index, 1), new CellKey("Sheet2", 1 + index, 4), new CellKey("Sheet2", 2 + index, 5) };
    private IncrementalPredecessorFormula<AbstractCellKey> IPF4 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Sheet2", 1 + index, 4), new CellKey("Sheet2", 2 + index, 5) };
    private IncrementalUpdateFormula IUF1 = (current, previousValues, newValues, previousScalars, newScalars) -> getOptionalInt((Integer) current) - (getOptionalInt((Integer) previousValues.get(1)) > 0 && getOptionalInt((Integer) previousValues.get(2)) > 0 ? getOptionalInt((Integer) previousValues.get(0)) : 0) + (getOptionalInt((Integer) newValues.get(1)) > 0 && getOptionalInt((Integer) newValues.get(2)) > 0 ? getOptionalInt((Integer) newValues.get(0)) : 0);
    private IncrementalUpdateFormula IUF2 = (current, previousValues, newValues, previousScalars, newScalars) -> getOptionalInt((Integer) current) - (getOptionalInt((Integer) previousValues.get(0)) > 0 && getOptionalInt((Integer) previousValues.get(1)) > 0 ? 1 : 0) + (getOptionalInt((Integer) newValues.get(0)) > 0 && getOptionalInt((Integer) newValues.get(1)) > 0 ? 1 : 0);

	public Stream<AbstractCellKey> getInputInfo() {
	    return Stream.of(
);
    }

	public List<DecisionCellInfo<AbstractCellKey>> getDecisionInfo() {
		return List.of(
			new DecisionCellInfo<>(new CellKey("Sheet2", 1, 4), AggregateInt0To3::new),
			new DecisionCellInfo<>(new CellKey("Sheet2", 2, 5), AggregateInt0To3::new),
            new DecisionCellInfo<>(new AdHocRangeKey("Sheet1", 1, 11, 1, 1), AggregateInt0To11::new),
            new DecisionCellInfo<>(new AdHocRangeKey("Sheet2", 1, 12, 2, 3), AggregateInt0To3::new));
    }

	public List<ShadowCellInfo<AbstractCellKey>> getShadowCellInfo() {
		return List.of(
            new ShadowCellInfo<>(new AdHocRangeKey("Sheet2", 2, 11, 4, 4), CF1),
            new ShadowCellInfo<>(new AdHocRangeKey("Sheet2", 3, 12, 5, 5), CF1));
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
			new IncrementalCellInfo<>(new CellKey("Sheet1", 1, 4), 10, IPF1, IUF1, IFF1, IntegerInitializer.SINGLETON),
			new IncrementalCellInfo<>(new CellKey("Sheet1", 2, 4), 10, IPF2, IUF2, IFF2, IntegerInitializer.SINGLETON),
			new IncrementalCellInfo<>(new CellKey("Sheet1", 1, 5), 11, IPF3, IUF1, IFF1, IntegerInitializer.SINGLETON),
			new IncrementalCellInfo<>(new CellKey("Sheet1", 2, 5), 11, IPF4, IUF2, IFF2, IntegerInitializer.SINGLETON));
	}

	@Override
	public ShadowCell<AbstractCellKey, ?> createShadowVariable(AbstractCellKey key, List<AbstractCellKey> predecessors, Formula formula) {
		return new AggregateShadowCell<>(key, predecessors, formula);
	}

	@Override
	public IncrementalCell<AbstractCellKey, ?> createIncrementalVariable(
            AbstractCellKey key, List<AbstractCellKey> predecessors, int numberOfPredecessorLists,
            IncrementalPredecessorFormula<AbstractCellKey> incrementalPredecessors, IncrementalUpdateFormula incrementalUpdateFormula,
            IncrementalFreshFormula incrementalFreshFormula, IncrementalInitializer<?> initializer) {
		return new AggregateIncrementalCell<>(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula, incrementalFreshFormula, initializer);
	}

	@Override
	public SpreadsheetProblem<AbstractCellKey> createSolution(
	        List<? extends PlanningCell<AbstractCellKey, ?>> decisions,
			List<? extends ShadowCell<AbstractCellKey, ?>> cells,
			List<? extends InputCell<AbstractCellKey, ?>> inputs,
			List<? extends IncrementalCell<AbstractCellKey, ?>> incrementals,
			List<? extends ISummingCell<AbstractCellKey, ?>> summingCells) {
		return new AggregateSolution(decisions, cells, inputs, incrementals, summingCells);
	}

	@Override
	public Class<? extends SpreadsheetProblem<AbstractCellKey>> getSolutionClass() {
		return (Class<? extends SpreadsheetProblem<AbstractCellKey>>) AggregateSolution.class;
	}

	@Override
	public Class<Cell<AbstractCellKey, ?>>[] getEntityClasses() {
		return new Class[] { AggregateInt0To11.class, AggregateInt0To3.class,  AggregateShadowCell.class, AggregateIncrementalCell.class };
	}

	public Class<? extends EasyScoreCalculator<? extends SpreadsheetProblem<AbstractCellKey>, BendableScore>> getScoreCalculatorClass() {
		return AggregateScoreCalculator.class;
	}

	public static void main(String[] args) throws ArgumentParserException {
		new AggregateMain().mainBody(args, PREDECESSORS_KEYS);
	}
}
