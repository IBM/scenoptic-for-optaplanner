package com.ibm.hrl.test.sc.supplychain.countifs.main;

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

import com.ibm.hrl.test.sc.supplychain.countifs.domain.SCCountifsSCInt1To1500;
import com.ibm.hrl.test.sc.supplychain.countifs.domain.SCCountifsSCInt1To2000;
import com.ibm.hrl.test.sc.supplychain.countifs.domain.SCCountifsSCShadowCell;
import com.ibm.hrl.test.sc.supplychain.countifs.domain.SCCountifsSCIncrementalCell;
import com.ibm.hrl.test.sc.supplychain.countifs.solver.SCCountifsSCSolution;
import com.ibm.hrl.test.sc.supplychain.countifs.solver.SCCountifsSCScoreCalculator;

public class SCCountifsSCMain extends SpreadsheetOptimizationMain<AbstractCellKey> {
	private static final List<PredecessorCellKey> PREDECESSORS_KEYS = List.of(
            new PredecessorCellKey(new CellKey("Non-neg COUNTIFS V2", 16, 5),
					List.of()),
            new PredecessorCellKey(new CellKey("Non-neg COUNTIFS V2", 16, 10),
					List.of()),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg COUNTIFS V2", 4, 15, 3, 3),
					List.of(new CellDistance("Non-neg COUNTIFS V2", -1, 1),
                            new CellDistance("Non-neg COUNTIFS V2", -1, 4))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg COUNTIFS V2", 3, 15, 4, 4),
					List.of(new CellDistance("Non-neg COUNTIFS V2", 0, 4))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg COUNTIFS V2", 3, 15, 5, 5),
					List.of(new FixedDistance("Non-neg COUNTIFS V2", 18, 2),
                            new FixedDistance("Non-neg COUNTIFS V2", 18, 4),
                            new RangeDistance("Non-neg COUNTIFS V2", 0, 0, 1, 2))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg COUNTIFS V2", 3, 15, 6, 6),
					List.of(new RangeDistance("Non-neg COUNTIFS V2", 0, 0, -4, -3))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg COUNTIFS V2", 3, 15, 7, 7),
					List.of(new RangeDistance("Non-neg COUNTIFS V2", 0, 0, -5, -4))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg COUNTIFS V2", 3, 15, 8, 8),
					List.of(new FixedDistance("Non-neg COUNTIFS V2", 19, 2),
                            new FixedDistance("Non-neg COUNTIFS V2", 20, 2),
                            new CellDistance("Non-neg COUNTIFS V2", 0, -5))));


    private Formula CF1 = values -> (int) values.get(3) * (int) values.get(0) + (int) values.get(1) * (int) values.get(2);
    private Formula CF2 = values -> (int) values.get(0);
    private Formula CF3 = values -> Math.max(0, (int) values.get(0) - (int) values.get(1));
    private Formula CF4 = values -> Math.max((int) values.get(1) - (int) values.get(0), 0);
    private Formula CF5 = values -> (int) values.get(2) < (int) values.get(0) ? (int) values.get(1) - (int) values.get(2) : 0;
    private Formula CF6 = values -> (int) values.get(1) + (int) values.get(0);
    private IncrementalFreshFormula IFF1 = (current, newValues, newScalars) -> getOptionalInt((Integer) current) + getOptionalInt((Integer) newValues.get(0));
    private IncrementalFreshFormula IFF2 = (current, newValues, newScalars) -> getOptionalInt((Integer) current) + (getOptionalInt((Integer) newValues.get(0)) < 0 ? 1 : 0);
    private IncrementalPredecessorFormula<AbstractCellKey> IPF1 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Non-neg COUNTIFS V2", 3 + index, 5) };
    private IncrementalPredecessorFormula<AbstractCellKey> IPF2 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Non-neg COUNTIFS V2", 3 + index, 4) };
    private IncrementalUpdateFormula IUF1 = (current, previousValues, newValues, previousScalars, newScalars) -> getOptionalInt((Integer) current) - getOptionalInt((Integer) previousValues.get(0)) + getOptionalInt((Integer) newValues.get(0));
    private IncrementalUpdateFormula IUF2 = (current, previousValues, newValues, previousScalars, newScalars) -> getOptionalInt((Integer) current) - (getOptionalInt((Integer) previousValues.get(0)) < 0 ? 1 : 0) + (getOptionalInt((Integer) newValues.get(0)) < 0 ? 1 : 0);

	public Stream<AbstractCellKey> getInputInfo() {
	    return Stream.of(
            new CellKey("Non-neg COUNTIFS V2", 18, 2),
            new CellKey("Non-neg COUNTIFS V2", 3, 3),
            new CellKey("Non-neg COUNTIFS V2", 18, 4),
            new AdHocRangeKey("Non-neg COUNTIFS V2", 3, 15, 2, 2),
            new AdHocRangeKey("Non-neg COUNTIFS V2", 3, 3, 5, 7));
    }

	public List<DecisionCellInfo<AbstractCellKey>> getDecisionInfo() {
		return List.of(
			new DecisionCellInfo<>(new CellKey("Non-neg COUNTIFS V2", 19, 2), SCCountifsSCInt1To1500::new),
			new DecisionCellInfo<>(new CellKey("Non-neg COUNTIFS V2", 20, 2), SCCountifsSCInt1To2000::new));
    }

	public List<ShadowCellInfo<AbstractCellKey>> getShadowCellInfo() {
		return List.of(
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg COUNTIFS V2", 4, 15, 3, 3), CF6),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg COUNTIFS V2", 3, 15, 4, 4), CF2),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg COUNTIFS V2", 4, 15, 5, 5), CF1),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg COUNTIFS V2", 4, 15, 6, 6), CF3),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg COUNTIFS V2", 4, 15, 7, 7), CF4),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg COUNTIFS V2", 3, 15, 8, 8), CF5));
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
			new IncrementalCellInfo<>(new CellKey("Non-neg COUNTIFS V2", 16, 5), 13, IPF1, IUF1, IFF1, IntegerInitializer.SINGLETON),
			new IncrementalCellInfo<>(new CellKey("Non-neg COUNTIFS V2", 16, 10), 13, IPF2, IUF2, IFF2, IntegerInitializer.SINGLETON));
	}

	@Override
	public ShadowCell<AbstractCellKey, ?> createShadowVariable(AbstractCellKey key, List<AbstractCellKey> predecessors, Formula formula) {
		return new SCCountifsSCShadowCell<>(key, predecessors, formula);
	}

	@Override
	public IncrementalCell<AbstractCellKey, ?> createIncrementalVariable(
            AbstractCellKey key, List<AbstractCellKey> predecessors, int numberOfPredecessorLists,
            IncrementalPredecessorFormula<AbstractCellKey> incrementalPredecessors, IncrementalUpdateFormula incrementalUpdateFormula,
            IncrementalFreshFormula incrementalFreshFormula, IncrementalInitializer<?> initializer) {
		return new SCCountifsSCIncrementalCell<>(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula, incrementalFreshFormula, initializer);
	}

	@Override
	public SpreadsheetProblem<AbstractCellKey> createSolution(
	        List<? extends PlanningCell<AbstractCellKey, ?>> decisions,
			List<? extends ShadowCell<AbstractCellKey, ?>> cells,
			List<? extends InputCell<AbstractCellKey, ?>> inputs,
			List<? extends IncrementalCell<AbstractCellKey, ?>> incrementals,
			List<? extends ISummingCell<AbstractCellKey, ?>> summingCells) {
		return new SCCountifsSCSolution(decisions, cells, inputs, incrementals, summingCells);
	}

	@Override
	public Class<? extends SpreadsheetProblem<AbstractCellKey>> getSolutionClass() {
		return (Class<? extends SpreadsheetProblem<AbstractCellKey>>) SCCountifsSCSolution.class;
	}

	@Override
	public Class<Cell<AbstractCellKey, ?>>[] getEntityClasses() {
		return new Class[] { SCCountifsSCInt1To1500.class, SCCountifsSCInt1To2000.class,  SCCountifsSCShadowCell.class, SCCountifsSCIncrementalCell.class };
	}

	public Class<? extends EasyScoreCalculator<? extends SpreadsheetProblem<AbstractCellKey>, BendableScore>> getScoreCalculatorClass() {
		return SCCountifsSCScoreCalculator.class;
	}

	public static void main(String[] args) throws ArgumentParserException {
		new SCCountifsSCMain().mainBody(args, PREDECESSORS_KEYS);
	}
}
