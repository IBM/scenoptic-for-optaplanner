package com.ibm.hrl.test.sc.supplychain.nonneg.main;

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

import com.ibm.hrl.test.sc.supplychain.nonneg.domain.SCNonnegSCInt1To1500;
import com.ibm.hrl.test.sc.supplychain.nonneg.domain.SCNonnegSCInt1To2000;
import com.ibm.hrl.test.sc.supplychain.nonneg.domain.SCNonnegSCShadowCell;
import com.ibm.hrl.test.sc.supplychain.nonneg.domain.SCNonnegSCIncrementalCell;
import com.ibm.hrl.test.sc.supplychain.nonneg.solver.SCNonnegSCSolution;
import com.ibm.hrl.test.sc.supplychain.nonneg.solver.SCNonnegSCScoreCalculator;

public class SCNonnegSCMain extends SpreadsheetOptimizationMain<AbstractCellKey> {
	private static final List<PredecessorCellKey> PREDECESSORS_KEYS = List.of(
            new PredecessorCellKey(new CellKey("Non-neg self-contained V2", 16, 5),
					List.of()),
            new PredecessorCellKey(new CellKey("objectives!$J$3:J15", 1, 19),
					List.of()),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg self-contained V2", 4, 15, 3, 3),
					List.of(new CellDistance("Non-neg self-contained V2", -1, 1),
                            new CellDistance("Non-neg self-contained V2", -1, 4))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg self-contained V2", 3, 15, 4, 4),
					List.of(new CellDistance("Non-neg self-contained V2", 0, 4))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg self-contained V2", 3, 15, 5, 5),
					List.of(new FixedDistance("Non-neg self-contained V2", 18, 2),
                            new FixedDistance("Non-neg self-contained V2", 18, 4),
                            new RangeDistance("Non-neg self-contained V2", 0, 0, 1, 2))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg self-contained V2", 3, 15, 6, 6),
					List.of(new RangeDistance("Non-neg self-contained V2", 0, 0, -4, -3))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg self-contained V2", 3, 15, 7, 7),
					List.of(new RangeDistance("Non-neg self-contained V2", 0, 0, -5, -4))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg self-contained V2", 3, 15, 8, 8),
					List.of(new FixedDistance("Non-neg self-contained V2", 19, 2),
                            new FixedDistance("Non-neg self-contained V2", 20, 2),
                            new CellDistance("Non-neg self-contained V2", 0, -5))),
            new PredecessorCellKey(new AdHocRangeKey("Non-neg self-contained V2", 3, 15, 10, 10),
					List.of(new CellDistance("Non-neg self-contained V2", 0, -6))));


    private Formula CF1 = values -> (int) values.get(3) * (int) values.get(0) + (int) values.get(1) * (int) values.get(2);
    private Formula CF2 = values -> Math.min((int) values.get(0), 0);
    private Formula CF3 = values -> Math.max(0, (int) values.get(0) - (int) values.get(1));
    private Formula CF4 = values -> Math.max((int) values.get(1) - (int) values.get(0), 0);
    private Formula CF5 = values -> (int) values.get(0);
    private Formula CF6 = values -> (int) values.get(1) + (int) values.get(0);
    private Formula CF7 = values -> (int) values.get(2) < (int) values.get(0) ? (int) values.get(1) - (int) values.get(2) : 0;
    private IncrementalFreshFormula IFF1 = (current, newValues, newScalars) -> getOptionalInt((Integer) current) + getOptionalInt((Integer) newValues.get(0));
    private IncrementalPredecessorFormula<AbstractCellKey> IPF1 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Non-neg self-contained V2", 3 + index, 10) };
    private IncrementalPredecessorFormula<AbstractCellKey> IPF2 = (key, index) -> new com.ibm.hrl.scenoptic.keys.AbstractCellKey[] { new CellKey("Non-neg self-contained V2", 3 + index, 5) };
    private IncrementalUpdateFormula IUF1 = (current, previousValues, newValues, previousScalars, newScalars) -> getOptionalInt((Integer) current) - getOptionalInt((Integer) previousValues.get(0)) + getOptionalInt((Integer) newValues.get(0));

	public Stream<AbstractCellKey> getInputInfo() {
	    return Stream.of(
            new CellKey("Non-neg self-contained V2", 18, 2),
            new CellKey("Non-neg self-contained V2", 3, 3),
            new CellKey("Non-neg self-contained V2", 18, 4),
            new AdHocRangeKey("Non-neg self-contained V2", 3, 15, 2, 2),
            new AdHocRangeKey("Non-neg self-contained V2", 3, 3, 5, 7));
    }

	public List<DecisionCellInfo<AbstractCellKey>> getDecisionInfo() {
		return List.of(
			new DecisionCellInfo<>(new CellKey("Non-neg self-contained V2", 19, 2), SCNonnegSCInt1To1500::new),
			new DecisionCellInfo<>(new CellKey("Non-neg self-contained V2", 20, 2), SCNonnegSCInt1To2000::new));
    }

	public List<ShadowCellInfo<AbstractCellKey>> getShadowCellInfo() {
		return List.of(
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg self-contained V2", 4, 15, 3, 3), CF6),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg self-contained V2", 3, 15, 4, 4), CF5),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg self-contained V2", 4, 15, 5, 5), CF1),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg self-contained V2", 4, 15, 6, 6), CF3),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg self-contained V2", 4, 15, 7, 7), CF4),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg self-contained V2", 3, 15, 8, 8), CF7),
            new ShadowCellInfo<>(new AdHocRangeKey("Non-neg self-contained V2", 3, 15, 10, 10), CF2));
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
			new IncrementalCellInfo<>(new CellKey("Non-neg self-contained V2", 16, 5), 13, IPF2, IUF1, IFF1, IntegerInitializer.SINGLETON),
			new IncrementalCellInfo<>(new CellKey("objectives!$J$3:J15", 1, 19), 13, IPF1, IUF1, IFF1, IntegerInitializer.SINGLETON));
	}

	@Override
	public ShadowCell<AbstractCellKey, ?> createShadowVariable(AbstractCellKey key, List<AbstractCellKey> predecessors, Formula formula) {
		return new SCNonnegSCShadowCell<>(key, predecessors, formula);
	}

	@Override
	public IncrementalCell<AbstractCellKey, ?> createIncrementalVariable(
            AbstractCellKey key, List<AbstractCellKey> predecessors, int numberOfPredecessorLists,
            IncrementalPredecessorFormula<AbstractCellKey> incrementalPredecessors, IncrementalUpdateFormula incrementalUpdateFormula,
            IncrementalFreshFormula incrementalFreshFormula, IncrementalInitializer<?> initializer) {
		return new SCNonnegSCIncrementalCell<>(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula, incrementalFreshFormula, initializer);
	}

	@Override
	public SpreadsheetProblem<AbstractCellKey> createSolution(
	        List<? extends PlanningCell<AbstractCellKey, ?>> decisions,
			List<? extends ShadowCell<AbstractCellKey, ?>> cells,
			List<? extends InputCell<AbstractCellKey, ?>> inputs,
			List<? extends IncrementalCell<AbstractCellKey, ?>> incrementals,
			List<? extends ISummingCell<AbstractCellKey, ?>> summingCells) {
		return new SCNonnegSCSolution(decisions, cells, inputs, incrementals, summingCells);
	}

	@Override
	public Class<? extends SpreadsheetProblem<AbstractCellKey>> getSolutionClass() {
		return (Class<? extends SpreadsheetProblem<AbstractCellKey>>) SCNonnegSCSolution.class;
	}

	@Override
	public Class<Cell<AbstractCellKey, ?>>[] getEntityClasses() {
		return new Class[] { SCNonnegSCInt1To1500.class, SCNonnegSCInt1To2000.class,  SCNonnegSCShadowCell.class, SCNonnegSCIncrementalCell.class };
	}

	public Class<? extends EasyScoreCalculator<? extends SpreadsheetProblem<AbstractCellKey>, BendableScore>> getScoreCalculatorClass() {
		return SCNonnegSCScoreCalculator.class;
	}

	public static void main(String[] args) throws ArgumentParserException {
		new SCNonnegSCMain().mainBody(args, PREDECESSORS_KEYS);
	}
}
