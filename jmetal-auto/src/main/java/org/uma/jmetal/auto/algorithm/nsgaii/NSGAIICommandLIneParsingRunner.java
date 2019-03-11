package org.uma.jmetal.auto.algorithm.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;
import picocli.CommandLine;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class to configure NSGA-II.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIICommandLIneParsingRunner {

  public static void main(String[] args) throws FileNotFoundException {

    String[] arguments = {
      "--createInitialSolutions",
      "random",
      "--offspringPopulationSize",
      "100",
      "--selection",
      "tournament",
      "--selectionTournamentSize",
      "2",
      "--crossover",
      "sbx",
      "--crossoverProbability",
      "1.0",
      "--mutation",
      "polynomial",
      "--mutationProbability",
      "0.001",
      "--variation",
      "rankingAndCrowding"
    };

    DoubleProblem problem = new ZDT1();
    String paretoFrontFile = "pareto_fronts/ZDT1.pf";

    AutoNSGAII configurator = CommandLine.populateCommand(new AutoNSGAII(), arguments);

    EvolutionaryAlgorithm<DoubleSolution> autoNSGAII = configurator.configureAndGetAlgorithm();
    autoNSGAII.run();

    Front referenceFront = new ArrayFront(paretoFrontFile);
    FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
    Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);
    Front normalizedFront = frontNormalizer.normalize(new ArrayFront(autoNSGAII.getResult()));
    List<PointSolution> normalizedPopulation =
        FrontUtils.convertFrontToSolutionList(normalizedFront);

    double referenceFrontHV =
        new PISAHypervolume<PointSolution>(normalizedReferenceFront)
            .evaluate(FrontUtils.convertFrontToSolutionList(normalizedReferenceFront));
    double obtainedFrontHV =
        new PISAHypervolume<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation);
    System.out.println(obtainedFrontHV);
    System.out.println((referenceFrontHV - obtainedFrontHV) / referenceFrontHV);

    // DefaultAlgorithmOutputData.generateMultiObjectiveAlgorithmOutputData(
    //    autoNSGAII.getResult(), autoNSGAII.getTotalComputingTime());
  }
}