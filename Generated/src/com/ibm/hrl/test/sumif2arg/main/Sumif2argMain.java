package com.ibm.hrl.test.sumif2arg.main;

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

import com.ibm.hrl.test.sumif2arg.domain.Sumif2argInt0To2048;
import com.ibm.hrl.test.sumif2arg.domain.Sumif2argShadowCell;
import com.ibm.hrl.test.sumif2arg.domain.Sumif2argIncrementalCell;
import com.ibm.hrl.test.sumif2arg.solver.Sumif2argSolution;
import com.ibm.hrl.test.sumif2arg.solver.Sumif2argScoreCalculator;

public class Sumif2argMain extends SpreadsheetOptimizationMain<AbstractCellKey> {
	private static final List<PredecessorCellKey> PREDECESSORS_KEYS = List.of(
            new PredecessorCellKey(new CellKey("Sheet2", 1, 5),
					List.of(new CellDistance("subexpr!Sheet2!E1", 0, 17))),
            new PredecessorCellKey(new CellKey("Sheet2", 1, 6),
					List.of(new CellDistance("subexpr!Sheet2!F1", 0, 16))),
            new PredecessorCellKey(new CellKey("objectives!E1:F1", 1, 19),
					List.of()),
            new PredecessorCellKey(new CellKey("subexpr!Sheet2!E1", 1, 22),
					List.of()),
            new PredecessorCellKey(new CellKey("subexpr!Sheet2!F1", 1, 22),
					List.of()));


    private Formula CF1 = values -> (int) values.get(0) + 100;
    private IncrementalFreshFormula IFF1 = (current, newValues, newScalars) -> getOptionalInt((Integer) current) + getOptionalInt((Integer) newValues.get(0));
    private IncrementalFreshFormula IFF2 = (current, newValues, newScalars) -> getOptionalInt((Integer) current) + (getOptionalInt((Integer) newValues.get(0)) < 8 ? getOptionalInt((Integer) newValues.get(0)) : 0);
    private IncrementalFreshFormula IFF3 = (current, newValues, newScalars) -> getOptionalInt((Integer) current) + (getOptionalInt((Integer) newValues.get(0)) < 8 ? 1 : 0);
    private IncrementalPredecessorFormula<AbstractCellKey> IPF1 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Sheet2", 1, 5 + index) };
    private IncrementalPredecessorFormula<AbstractCellKey> IPF2 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Sheet2", 2 + index, 1) };
    private IncrementalUpdateFormula IUF1 = (current, previousValues, newValues, previousScalars, newScalars) -> getOptionalInt((Integer) current) - getOptionalInt((Integer) previousValues.get(0)) + getOptionalInt((Integer) newValues.get(0));
    private IncrementalUpdateFormula IUF2 = (current, previousValues, newValues, previousScalars, newScalars) -> getOptionalInt((Integer) current) - (getOptionalInt((Integer) previousValues.get(0)) < 8 ? getOptionalInt((Integer) previousValues.get(0)) : 0) + (getOptionalInt((Integer) newValues.get(0)) < 8 ? getOptionalInt((Integer) newValues.get(0)) : 0);
    private IncrementalUpdateFormula IUF3 = (current, previousValues, newValues, previousScalars, newScalars) -> getOptionalInt((Integer) current) - (getOptionalInt((Integer) previousValues.get(0)) < 8 ? 1 : 0) + (getOptionalInt((Integer) newValues.get(0)) < 8 ? 1 : 0);

	public Stream<AbstractCellKey> getInputInfo() {
	    return Stream.of(
);
    }

	public List<DecisionCellInfo<AbstractCellKey>> getDecisionInfo() {
		return List.of(
            new DecisionCellInfo<>(new AdHocRangeKey("Sheet2", 2, 6, 1, 1), Sumif2argInt0To2048::new));
    }

	public List<ShadowCellInfo<AbstractCellKey>> getShadowCellInfo() {
		return List.of(
            new ShadowCellInfo<>(new AdHocRangeKey("Sheet2", 1, 1, 5, 6), CF1));
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
			new IncrementalCellInfo<>(new CellKey("objectives!E1:F1", 1, 19), 2, IPF1, IUF1, IFF1, IntegerInitializer.SINGLETON),
			new IncrementalCellInfo<>(new CellKey("subexpr!Sheet2!E1", 1, 22), 5, IPF2, IUF2, IFF2, IntegerInitializer.SINGLETON),
			new IncrementalCellInfo<>(new CellKey("subexpr!Sheet2!F1", 1, 22), 5, IPF2, IUF3, IFF3, IntegerInitializer.SINGLETON));
	}

	@Override
	public ShadowCell<AbstractCellKey, ?> createShadowVariable(AbstractCellKey key, List<AbstractCellKey> predecessors, Formula formula) {
		return new Sumif2argShadowCell<>(key, predecessors, formula);
	}

	@Override
	public IncrementalCell<AbstractCellKey, ?> createIncrementalVariable(
            AbstractCellKey key, List<AbstractCellKey> predecessors, int numberOfPredecessorLists,
            IncrementalPredecessorFormula<AbstractCellKey> incrementalPredecessors, IncrementalUpdateFormula incrementalUpdateFormula,
            IncrementalFreshFormula incrementalFreshFormula, IncrementalInitializer<?> initializer) {
		return new Sumif2argIncrementalCell<>(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula, incrementalFreshFormula, initializer);
	}

	@Override
	public SpreadsheetProblem<AbstractCellKey> createSolution(
	        List<? extends PlanningCell<AbstractCellKey, ?>> decisions,
			List<? extends ShadowCell<AbstractCellKey, ?>> cells,
			List<? extends InputCell<AbstractCellKey, ?>> inputs,
			List<? extends IncrementalCell<AbstractCellKey, ?>> incrementals,
			List<? extends ISummingCell<AbstractCellKey, ?>> summingCells) {
		return new Sumif2argSolution(decisions, cells, inputs, incrementals, summingCells);
	}

	@Override
	public Class<? extends SpreadsheetProblem<AbstractCellKey>> getSolutionClass() {
		return (Class<? extends SpreadsheetProblem<AbstractCellKey>>) Sumif2argSolution.class;
	}

	@Override
	public Class<Cell<AbstractCellKey, ?>>[] getEntityClasses() {
		return new Class[] { Sumif2argInt0To2048.class,  Sumif2argShadowCell.class, Sumif2argIncrementalCell.class };
	}

	public Class<? extends EasyScoreCalculator<? extends SpreadsheetProblem<AbstractCellKey>, BendableScore>> getScoreCalculatorClass() {
		return Sumif2argScoreCalculator.class;
	}

	public static void main(String[] args) throws ArgumentParserException {
		new Sumif2argMain().mainBody(args, PREDECESSORS_KEYS);
	}
}
