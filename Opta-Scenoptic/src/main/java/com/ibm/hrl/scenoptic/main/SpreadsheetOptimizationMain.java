package com.ibm.hrl.scenoptic.main;

import com.ibm.hrl.scenoptic.debug.DebugSpec;
import com.ibm.hrl.scenoptic.domain.Cell;
import com.ibm.hrl.scenoptic.domain.ISummingCell;
import com.ibm.hrl.scenoptic.domain.IncrementalCell;
import com.ibm.hrl.scenoptic.domain.InitializationTriggerCell;
import com.ibm.hrl.scenoptic.domain.InputCell;
import com.ibm.hrl.scenoptic.domain.PlanningCell;
import com.ibm.hrl.scenoptic.domain.ShadowCell;
import com.ibm.hrl.scenoptic.domain.SpreadsheetProblem;
import com.ibm.hrl.scenoptic.domain.descriptors.Formula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalFreshFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalPredecessorFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalUpdateFormula;
import com.ibm.hrl.scenoptic.domain.initialization.IncrementalInitializer;
import com.ibm.hrl.scenoptic.excel.ExcelValueReader;
import com.ibm.hrl.scenoptic.info.DecisionCellInfo;
import com.ibm.hrl.scenoptic.info.IncrementalCellInfo;
import com.ibm.hrl.scenoptic.info.ShadowCellInfo;
import com.ibm.hrl.scenoptic.info.SummingCellInfo;
import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.keys.PredecessorCellKey;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SpreadsheetOptimizationMain<K extends Comparable<K>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetOptimizationMain.class);
	protected ExcelValueReader excelReader;
	public Map<AbstractCellKey, AbstractCellKey[]> predecessors;

	/**
	 * All parameters of the problem (decisions directly made by the solver; genuine planning variables in OptaPlanner)
	 */
	abstract public List<DecisionCellInfo<K>> getDecisionInfo();

	/**
	 * All variables of the problem that depend on the problem parameters and represent strict functions (implemented as
	 * shadow variables in OptaPlanner)
	 */
	abstract public List<ShadowCellInfo<K>> getShadowCellInfo();

	/**
	 * All generated variables that correspond to conditional sums and counts
	 */
	abstract public List<SummingCellInfo<K>> getSummingCellInfo();

	/**
	 * All variables of the problem that depend on the problem parameters and represent non-strict functions (implemented as
	 * shadow variables in OptaPlanner)
	 */
	abstract public Set<K> getNonStrictCells();

	/**
	 * All constant inputs of the problem.
	 */
	abstract public Stream<K> getInputInfo();

	/**
	 * For each key representing as variable, all keys of variables on which it depends.
	 */
	abstract public Map<K, K[]> getPredecessorInfo();

	/**
	 * All key cells information representing the incremental formulas and depenedencies*
	 */
	abstract public List<IncrementalCellInfo<K>> getIncrementalInfo();

	abstract public ShadowCell<K, ?> createShadowVariable(K key, List<K> predecessors, Formula formula);

	abstract public IncrementalCell<K, ?> createIncrementalVariable(
			K key, List<K> predecessors, int numberOfPredecessorLists, IncrementalPredecessorFormula<K> incrementalPredecessors,
			IncrementalUpdateFormula incrementalUpdateFormula,
			IncrementalFreshFormula incrementalFreshFormula, IncrementalInitializer<?> initializer);

	abstract public SpreadsheetProblem<K> createSolution(
			List<? extends PlanningCell<K, ?>> decisions,
			List<? extends ShadowCell<K, ?>> cells,
			List<? extends InputCell<K, ?>> inputs,
			List<? extends IncrementalCell<K, ?>> incrementals,
			List<? extends ISummingCell<K, ?>> summingCells);

	abstract public Class<? extends SpreadsheetProblem<K>> getSolutionClass();

	abstract public Class<? extends EasyScoreCalculator<? extends SpreadsheetProblem<K>, ?>> getScoreCalculatorClass();

	abstract public Class<Cell<K, ?>>[] getEntityClasses();


	protected Map<AbstractCellKey, AbstractCellKey[]> preComputePredecessors(List<PredecessorCellKey> predecessors) {
		return predecessors.stream().flatMap(predecessor -> predecessor.getPredecessorInfo().entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue));
	}

	public static ArgumentParser argumentParser() {
		ArgumentParser parser = ArgumentParsers
				.newFor("ScenopticMain")
				.defaultFormatWidth(120)
				.terminalWidthDetection(false)
				.build()
				.description("Solve problem from spreadsheet");
		parser.addArgument("-t", "--time")
				.type(Integer.class)
				.setDefault(30)
				.help("time limit (sec.)")
				.dest("time");
		parser.addArgument("-g", "--goal")
				.type(String.class)
				.setDefault("")
				.help("optimization goal (e.g., 0hard/0soft)")
				.dest("goal");
		parser.addArgument("-f", "--full-assert")
				.action(Arguments.storeTrue())
				.help("enable full assertions (performance penalty)")
				.dest("full-assert");
		parser.addArgument("-d", "--debug")
				.type(String.class)
				.setDefault("")
				.help("print debug information")
				.dest("debug");
		parser.addArgument("-C", "--comment")
				.type(String.class)
				.action(Arguments.append())
				.help("comment, ignored");
		parser.addArgument("-i", "--input-excel")
				.type(String.class)
				.help("Excel optimization problem file")
				.dest("excel");
		return parser;
	}

	public static Namespace parseArgs(String[] args, ArgumentParser parser) throws ArgumentParserException {
		return parser.parseArgs(args);
	}


	public boolean loadExcel(String excelFile) {
		Path file = Paths.get(excelFile);
		// File does not exist
		if (!Files.exists(file))
			return false;
		if (!Files.isRegularFile(file))
			return false;
		if (!Files.isReadable(file))
			return false;
		// everything is ok, process the file
		try {
			excelReader = new ExcelValueReader(excelFile);
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

	public void mainBody(String[] args, List<PredecessorCellKey> predecessorKeys) throws ArgumentParserException {
		mainBody(parseArgs(args, argumentParser()), predecessorKeys);
	}

	public void mainBody(Namespace parsedArgs, List<PredecessorCellKey> predecessorKeys) {
		int time_limit = parsedArgs.getInt("time");
		String goal = parsedArgs.getString("goal");
		String excelFile = parsedArgs.getString("excel");
		if (excelFile == null) {
			LOGGER.error("Missing Excel input file");
			System.exit(1);
		}
		if (predecessorKeys != null) {
			predecessors = preComputePredecessors(predecessorKeys);
		}
		List<InputCell<K, ?>> inputCells;
		try {
			LOGGER.info("Excel Input File: " + excelFile);
			if (!loadExcel(excelFile)) {
				LOGGER.error("Excel Input File: " + excelFile + " cannot be loaded");
				System.exit(1);
			}

			// Load the problem
			inputCells = getInputInfo()
					.flatMap(element -> ((AbstractCellKey) element).getElements())
					.map(key -> new InputCell<>((K) key,
							excelReader.readCell(key.getSheet(),
									((CellKey) key).getRow(),
									((CellKey) key).getCol())))
					.collect(Collectors.toList());
		} finally {
			this.excelReader.close();
		}

		List<IncrementalCell<K, ?>> incrementalCells =
				getIncrementalInfo().stream()
						.flatMap(IncrementalCellInfo::getElements)
						.map(info -> createIncrementalVariable(info.getKey(),
								Arrays.asList(getPredecessorInfo().get(info.getKey())),
								info.getNumberOfPredecessorLists(),
								info.getIncrementalPredecessorsFormula(),
								info.getIncrementalUpdateFormula(),
								info.getIncrementalFreshFormula(),
								info.getInitializer()))
						.collect(Collectors.toList());

		final List<? extends ISummingCell<K, ?>> summingCells = getSummingCellInfo().stream()
				.flatMap(SummingCellInfo::getElements)
				.map(info -> info.getCreator().create(info.getKey()))
				.collect(Collectors.toList());

		SpreadsheetProblem<K> problem = createSolution(
				getDecisionInfo().stream()
						.flatMap(DecisionCellInfo::getValues)
						.collect(Collectors.toList()),
				getShadowCellInfo().stream()
						.flatMap(ShadowCellInfo::getElements)
						.map(info -> createShadowVariable(info.getKey(),
								Arrays.asList(getPredecessorInfo().get(info.getKey())),
								info.getFormula()))
						.collect(Collectors.toList()),
				inputCells,
				incrementalCells,
				summingCells);
		problem.setDebug(new DebugSpec(parsedArgs.getString("debug"), true));

		// Propagate constants
//        new ConstantCellPropagator<>(problem).propagateChaotic(null, inputCells.toArray(new InputCell[0]));

		Class<Cell<K, ?>>[] entityClasses = Stream.concat(
						Stream.of(InitializationTriggerCell.class), Arrays.stream(getEntityClasses()))
				.toArray(Class[]::new);
		TerminationConfig terminationConfig = new TerminationConfig()
				.withSecondsSpentLimit((long) time_limit);
		if (goal.length() > 0)
			terminationConfig = terminationConfig.withBestScoreLimit(goal);
		SolverConfig config = new SolverConfig()
				.withSolutionClass(getSolutionClass())
				.withEntityClasses(entityClasses)
				// FIXME! generalize score-calculator class
				.withEasyScoreCalculatorClass(getScoreCalculatorClass())
//                .withPhases(
//                        new CustomPhaseConfig().withCustomPhaseCommands(new InitIncrementalsPhaseCommand<>()),
//                        new ConstructionHeuristicPhaseConfig(),
//                        constructionHeuristicPhaseConfig,
//                        new LocalSearchPhaseConfig())
				.withTerminationConfig(terminationConfig);
		if (parsedArgs.getBoolean("full-assert")) {
			config = config.withEnvironmentMode(EnvironmentMode.FULL_ASSERT);
			System.out.println("Running with FULL_ASSERT");
		}
		SolverFactory<SpreadsheetProblem<K>> solverFactory = SolverFactory.create(config);

		// Solve the problem
		Solver<SpreadsheetProblem<K>> solver = solverFactory.buildSolver();
		SpreadsheetProblem<K> solution = solver.solve(problem);

		// Print the solution
		System.out.println("Shadows\n=======");
		solution.printShadows();
		System.out.println("\nSumming\n=======");
		for (ISummingCell<K, ?> summingCell : summingCells) {
			System.out.println(summingCell.details());
		}
		System.out.println("\nResults\n=======");
		solution.printResults();
		System.out.println();
		LOGGER.info("Score: " + solution.getScore());
		LOGGER.info("Done.");
	}
}