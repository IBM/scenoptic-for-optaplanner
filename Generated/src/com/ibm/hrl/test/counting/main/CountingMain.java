package com.ibm.hrl.test.counting.main;

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
import com.ibm.hrl.scenoptic.domain.CountingShadowCellImpl;
import com.ibm.hrl.scenoptic.domain.SummingShadowCellImpl;
import com.ibm.hrl.scenoptic.domain.ConstructingFormula;
import com.ibm.hrl.scenoptic.domain.processors.SingleIntSummingCreator;

import com.ibm.hrl.test.counting.domain.CountingInt0To11;
import com.ibm.hrl.test.counting.domain.CountingShadowCell;
import com.ibm.hrl.test.counting.domain.CountingIncrementalCell;
import com.ibm.hrl.test.counting.domain.CountingCountingShadowCell;
import com.ibm.hrl.test.counting.solver.CountingSolution;
import com.ibm.hrl.test.counting.solver.CountingScoreCalculator;

public class CountingMain extends SpreadsheetOptimizationMain<AbstractCellKey> {
	private static final List<PredecessorCellKey> PREDECESSORS_KEYS = List.of(
            new PredecessorCellKey(new CellKey("objectives!F1:G10", 1, 19),
					List.of()),
            new PredecessorCellKey(new CellKey("summing!counting!G1", 1, 19),
					List.of()),
            new PredecessorCellKey(new AdHocRangeKey("counting", 1, 10, 2, 2),
					List.of(new CellDistance("counting", 0, -1))),
            new PredecessorCellKey(new AdHocRangeKey("counting", 1, 10, 3, 3),
					List.of(new CellDistance("counting", 0, -2))),
            new PredecessorCellKey(new AdHocRangeKey("counting", 1, 10, 6, 6),
					List.of(new CellDistance("counting", 0, -5),
                            new CellDistance("counting", 0, -3))),
            new PredecessorCellKey(new AdHocRangeKey("counting", 1, 10, 7, 7),
					List.of(new FixedDistance("summing!counting!G1", 1, 19),
                            new RangeDistance("counting", 0, 0, -6, -4))));


    private Formula CF1 = values -> ((ISummingCell) values.get(0)).getInt(List.of((int) values.get(1) + 1, (int) values.get(2) + 1, (int) values.get(3) + 1));
    private Formula CF2 = values -> (int) values.get(0) + 10;
    private Formula CF3 = values -> (int) values.get(0) + 20;
    private IncrementalFreshFormula IFF1 = (current, newValues, newScalars) -> utils.getOptionalInt((Integer) current) + utils.getOptionalInt((Integer) newValues.get(0));
    private IncrementalFreshFormula IFF2 = (current, newValues, newScalars) -> utils.getOptionalInt((Integer) current) + (utils.getOptionalInt((Integer) newScalars.get(0)) == utils.getOptionalInt((Integer) newValues.get(0)) && utils.getOptionalInt((Integer) newValues.get(1)) > 15 && utils.getOptionalInt((Integer) newValues.get(2)) != utils.getOptionalInt((Integer) newScalars.get(1)) + 1 ? 1 : 0);
    private IncrementalPredecessorFormula<AbstractCellKey> IPF1 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("counting", 1 + index % 10, 6 + index / 10) };
    private IncrementalPredecessorFormula<AbstractCellKey> IPF2 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("counting", 1 + index, 1), new CellKey("counting", 1 + index, 2), new CellKey("counting", 1 + index, 3) };
    private IncrementalUpdateFormula IUF1 = (current, previousValues, newValues, previousScalars, newScalars) -> utils.getOptionalInt((Integer) current) - utils.getOptionalInt((Integer) previousValues.get(0)) + utils.getOptionalInt((Integer) newValues.get(0));
    private IncrementalUpdateFormula IUF2 = (current, previousValues, newValues, previousScalars, newScalars) -> utils.getOptionalInt((Integer) current) - (utils.getOptionalInt((Integer) previousScalars.get(0)) == utils.getOptionalInt((Integer) previousValues.get(0)) && utils.getOptionalInt((Integer) previousValues.get(1)) > 15 && utils.getOptionalInt((Integer) previousValues.get(2)) != utils.getOptionalInt((Integer) previousScalars.get(1)) + 1 ? 1 : 0) + (utils.getOptionalInt((Integer) newScalars.get(0)) == utils.getOptionalInt((Integer) newValues.get(0)) && utils.getOptionalInt((Integer) newValues.get(1)) > 15 && utils.getOptionalInt((Integer) newValues.get(2)) != utils.getOptionalInt((Integer) newScalars.get(1)) + 1 ? 1 : 0);
    private ConstructingFormula<AbstractCellKey, ? extends ISummingCell<AbstractCellKey, ?>> SIF1 = cellKey -> new CountingCountingShadowCell(cellKey, List.of(), 10, (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("counting", 1 + index, 1), new CellKey("counting", 1 + index, 2), new CellKey("counting", 1 + index, 3) }, List.of(new SingleIntSummingCreator()));

	public Stream<AbstractCellKey> getInputInfo() {
	    return Stream.of(
);
    }

	public List<DecisionCellInfo<AbstractCellKey>> getDecisionInfo() {
		return List.of(
            new DecisionCellInfo<>(new AdHocRangeKey("counting", 1, 10, 1, 1), CountingInt0To11::new));
    }

	public List<ShadowCellInfo<AbstractCellKey>> getShadowCellInfo() {
		return List.of(
            new ShadowCellInfo<>(new AdHocRangeKey("counting", 1, 10, 2, 2), CF2),
            new ShadowCellInfo<>(new AdHocRangeKey("counting", 1, 10, 3, 3), CF3),
            new ShadowCellInfo<>(new AdHocRangeKey("counting", 1, 10, 7, 7), CF1));
    }

    public List<SummingCellInfo<AbstractCellKey>> getSummingCellInfo() {
		return List.of(
            new SummingCellInfo<>(new CellKey("summing!counting!G1", 1, 19), SIF1));
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
			new IncrementalCellInfo<>(new CellKey("objectives!F1:G10", 1, 19), 20, IPF1, IUF1, IFF1, IntegerInitializer.SINGLETON),
            new IncrementalCellInfo<>(new AdHocRangeKey("counting", 1, 10, 6, 6), 10, IPF2, IUF2, IFF2, IntegerInitializer.SINGLETON));
	}

	@Override
	public ShadowCell<AbstractCellKey, ?> createShadowVariable(AbstractCellKey key, List<AbstractCellKey> predecessors, Formula formula) {
		return new CountingShadowCell<>(key, predecessors, formula);
	}

	@Override
	public IncrementalCell<AbstractCellKey, ?> createIncrementalVariable(
            AbstractCellKey key, List<AbstractCellKey> predecessors, int numberOfPredecessorLists,
            IncrementalPredecessorFormula<AbstractCellKey> incrementalPredecessors, IncrementalUpdateFormula incrementalUpdateFormula,
            IncrementalFreshFormula incrementalFreshFormula, IncrementalInitializer<?> initializer) {
		return new CountingIncrementalCell<>(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula, incrementalFreshFormula, initializer);
	}

	@Override
	public SpreadsheetProblem<AbstractCellKey> createSolution(
	        List<? extends PlanningCell<AbstractCellKey, ?>> decisions,
			List<? extends ShadowCell<AbstractCellKey, ?>> cells,
			List<? extends InputCell<AbstractCellKey, ?>> inputs,
			List<? extends IncrementalCell<AbstractCellKey, ?>> incrementals,
			List<? extends ISummingCell<AbstractCellKey, ?>> summingCells) {
		return new CountingSolution(decisions, cells, inputs, incrementals, summingCells);
	}

	@Override
	public Class<? extends SpreadsheetProblem<AbstractCellKey>> getSolutionClass() {
		return (Class<? extends SpreadsheetProblem<AbstractCellKey>>) CountingSolution.class;
	}

	@Override
	public Class<Cell<AbstractCellKey, ?>>[] getEntityClasses() {
		return new Class[] { CountingInt0To11.class,  CountingShadowCell.class, CountingIncrementalCell.class, CountingCountingShadowCell.class };
	}

	public Class<? extends EasyScoreCalculator<? extends SpreadsheetProblem<AbstractCellKey>, BendableScore>> getScoreCalculatorClass() {
		return CountingScoreCalculator.class;
	}

	public static void main(String[] args) throws ArgumentParserException {
		new CountingMain().mainBody(args, PREDECESSORS_KEYS);
	}
}
