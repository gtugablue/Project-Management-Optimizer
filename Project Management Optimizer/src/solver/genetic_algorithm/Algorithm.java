package solver.genetic_algorithm;

import java.util.ArrayList;
import java.util.Random;

import optimizer.Problem;

public class Algorithm {
	private Problem problem;
	private double mutationRate;
	private int elitism;
	private static Random random = new Random();
	private int numBitsTaskID;
	public Algorithm(Problem problem, double mutationRate, int elitism) {
		this.problem = problem;
		this.mutationRate = mutationRate;
		this.elitism = elitism;
		this.numBitsTaskID = minNumBits(problem.getTasks().size());
	}
	
	public Population randomStartingPopulation(int size) {
		System.out.println(numBitsTaskID);
		return new Population(size, calculateChromosomeSize());
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public int getElitism() {
		return elitism;
	}

	/**
	 * Create the next generation of the population
	 * 
	 * @return Population
	 */
	public Population evolve(Population population) {
		Population p = (Population) population.clone();
		p.evaluate(problem);

		selection(p);
		// TODO Crossover
		mutation(p);

		return p;
	}



	private void selection(Population population) {
		Chromosome[] selected = rouletteWheelSelection(population);
		for (int i = 0; i < population.getSize() - this.elitism; i++) {
			population.setChromosome(i, selected[i]);
		}
	}

	private Chromosome[] rouletteWheelSelection(Population population) {
		int amountToSelect = population.getSize() - this.elitism;
		Chromosome[] selected = new Chromosome[amountToSelect];
		for (int i = 0; i < amountToSelect; i++) {
			double d = random.nextDouble() * population.getTotalFitness();	
			for(int j = 0; j < population.getSize(); j++) {		
				d -= population.getChromosome(j).getFitness();		
				if (d <= 0)
				{
					selected[i] = population.getChromosome(j);
					break;
				}
			}
			if (selected[i] == null)
				selected[i] = population.getChromosome(population.getSize() - 1); // in case of rounding errors
		}
		return selected;
	}

	/**
	 * Crossover of two chromosomes
	 * 
	 * @param firstCh
	 * @param secondCh
	 * @param initialBit
	 * @param finalBit
	 * @return ArrayList<Chromosome>
	 */
	private ArrayList<Chromosome> crossover(Chromosome firstCh, Chromosome secondCh, int initialBit, int finalBit) {

		ArrayList<Chromosome> chromosomes = new ArrayList<Chromosome>();

		for (int i = initialBit; i < finalBit; i++) {
			firstCh.getGenes()[i] = secondCh.getGenes()[i];
			secondCh.getGenes()[i] = firstCh.getGenes()[i];
		}

		chromosomes.add(firstCh);
		chromosomes.add(secondCh);

		return chromosomes;

	}

	private void mutation(Population population) {
		int chromosomeSize = population.getChromosomeSize();
		int totalBits = population.getSize() * chromosomeSize;
		for (int i = 0; i < totalBits; i++)
		{
			double d = random.nextDouble();
			if (d < mutationRate) {
				population.getChromosome(i / chromosomeSize).flipGene(i % chromosomeSize);
			}
		}
	}
	
	private int calculateChromosomeSize() {
		return problem.getTasks().size() * (numBitsTaskID + problem.getElements().size());
	}
	
	private int minNumBits(int size) {
		if (size <= 1) return size;
		return (int) Math.floor(Math.log(problem.getTasks().size() - 1)/Math.log(2) + 1);
	}
}
