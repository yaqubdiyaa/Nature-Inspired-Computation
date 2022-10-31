import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

/**
 * Particle class that maintains velocity and position vector by initialising and
 * updating them. The test function is evaluated in this class and the personal
 * best is stored.
 * 
 * @authors Souleman Toure, Diyaa Yaqub, and Jigyasa Subedi
 */
public class Particle {

	// the velocity vector for the particle
	private List<Double> velocity;

	// vector containing probabilities that will evolve as velocity is updated
	private List<Double> probVector;

	// fitness percentage for this particle is the
	// the number of clauses satisfied by the assignment to the boolean variables
	// according to probabilities in probVector
	private double fitness;

	// the position at which the best solution was found for this particle
	private List<Double> pbestVector;

	// an assignment to the boolean variables according to the probabilities
	private List<Boolean> assignments;

	// the personal best value found by the particle over a number of iterations
	private double pbestfitness;

	// personal best acceleration coefficient
	private double phi1 = 2.05;

	// neighborhood best acceleration coefficient
	private double phi2 = 2.05;

	// constriction factor
	private double constrictionFactor = 0.7298;

	// a list containing all the neighbors this particle has
	private List<Particle> neighbors;

	private Random rand = new Random();

	// list of all literals in the MAXSAT problem is saved for efficiency so that
	// file doesn't have to be read numerous times
	private List<String> literals;

	// number of clauses in the MAXSAT problem file
	int clauses;

	// list containing the minimum values found as probVector is updated using PSO equation
	// each index position corresponds to the minimum found for its corresponding
	// variable in probVector up to the current iteration
	List<Double> min;

	// list containing max values found for each variable as probVecotr is updated
	// using PSO equation.
	List<Double> max;

	
	/**
	 * Constructor for a particle.
	 * 
	 * @param function is the function that will be evaluated by the particle.
	 */
	public Particle(int variables, int clauses, List<String> literalsList) {

		velocity = new ArrayList<Double>();
		probVector = new ArrayList<Double>();
		neighbors = new ArrayList<Particle>();
		pbestVector = new ArrayList<Double>();
		literals = new ArrayList<String>();
		assignments = new ArrayList<Boolean>();

		min = new ArrayList<Double>();
		max = new ArrayList<Double>();

		this.clauses = clauses;
		for (int i = 0; i < literalsList.size(); i++) {
			this.literals.add(literalsList.get(i));
		}

		pbestfitness = Double.MIN_VALUE;
		min = new ArrayList<Double>();
		max = new ArrayList<Double>();

		double value;
		for (int i = 0; i < variables; i++) {
			value = rand.nextDouble();

			// initialise prob vector with random values
			probVector.add(value);

			// initially, best probVector is the first probVector created
			pbestVector.add(value);

			// initialised random velocity within range of -2 and 4
			velocity.add(Math.floor(Math.random() * (4 + 2) - 2));

			// initially, the min and max seen so far is the current value
			min.add(value);
			max.add(value);
		}

		// finds fitness percentage/percentage of clauses satisfied by probabilities in probVector
		assign();
		this.fitness = calculateFitness();
	}

	
	/**
	 * Generate an assignment to the boolean variables according to the
	 * probabilities as stored in probVector and saves them in the instance variable
	 * assignments. This is later used to determine percentage of clauses satisfied
	 * in MAXSAT problem.
	 */
	private void assign() {
		Random rand = new Random();
		assignments = new ArrayList<Boolean>();
		for (int i = 0; i < pbestVector.size(); i++) {
			float index = rand.nextFloat();
			if (index <= probVector.get(i)) {
				assignments.add(true);
			} else {
				assignments.add(false);
			}
		}
	}

	
	/**
	 * Method that calculates the fitness percentage (percentage of clauses
	 * satisfied in MAXSAT problem) based on this current probability vector.
	 * 
	 * @return the fitness percentage, which is the percentage of clauses satisfied
	 *         in the MAXSAT problem with the current probability vector.
	 */
	private double calculateFitness() {

		int numSatisfied = 0; // number of clauses satisfied; updated as each line and clause is evaluated.
		int literal; // the literal within the clause.
		boolean satisfied = false; // whether or not a clause is satisfied.

		for (int i = 0; i < literals.size(); i++) {
			if (!literals.get(i).equals("0")) {
				literal = Integer.parseInt(literals.get(i));
				if (literal < 0 && !assignments.get(Math.abs(literal) - 1)) {
					satisfied = true;
				} else if (literal > 0 && assignments.get(Math.abs(literal) - 1)) {
					satisfied = true;
				}
			}

			if (literals.get(i).equals("0")) {
				if (satisfied) {
					numSatisfied += 1;
					satisfied = false;
				}
			}
		}
		float percentageDecimal = (float) numSatisfied / clauses; // makes number of clauses satisfied into a percentage																
		return percentageDecimal * 100;
	}

	
	/**
	 * Updates this particle's velocity and position vector according to an equation
	 * that considers the neighborhood best, the personal best, and some
	 * randomisation. After updating the velocity and position, the method uses the
	 * new position to evaluate the function and updates the pbest value and
	 * pbestPosition of the particle accordingly.
	 */
	public void update() {
		double pBestAttract;
		double nBestAttract;

		for (int i = 0; i < probVector.size(); i++) {
			// compute acceleration based on personal best.
			pBestAttract = pbestVector.get(i) - probVector.get(i);
			pBestAttract *= rand.nextDouble() * phi1;

			// compute acceleration due to neighborhood best
			Particle nbest = findNBest(); // finds particle with the best pbest in the neighborhood
			nBestAttract = nbest.getPBestVector(i) - probVector.get(i);
			nBestAttract *= rand.nextDouble() * phi2;

			// constrict the new velocity and reset the current velocity
			double curVelocity = velocity.get(i);
			double newVelocity = (curVelocity + (nBestAttract + pBestAttract)) * constrictionFactor;
			velocity.set(i, newVelocity);

			// update probVector
			double curProb = probVector.get(i);
			double newProb = newVelocity + curProb;

			// if the value found is a negative position, it needs to be positive so that it
			// can be normalised
			// and translated into a probability
			// therefore, if the value is negative, add the absolute value to all in the
			// data set(min, max, and current)
			if (newProb < 0) {
				min.set(i, 0.0);
				newProb = 0.0;
				probVector.set(i, newProb);
				max.set(i, max.get(i) + Math.abs(probVector.get(i)));
			} else {
				double normProb = normalize(i, newProb);
				probVector.set(i, normProb);
			}
		}

		
		// find the fitness percentage/ percentage of clauses satisfied by this particle
		// given the new probVector
		assign();
		double curValue = calculateFitness();

		// updates personal best
		if (curValue > pbestfitness) {
			pbestfitness = curValue;
			for (int i = 0; i < probVector.size(); i++) {
				pbestVector.set(i, pbestVector.get(i));
			}
		}
	}

	
	/**
	 * This method uses the minimum and maximum values found so far by the PSO
	 * equation for variable to normalise the value cur and make it into a
	 * probability that can be in probVector. Values found after velocity/positin
	 * update with PSO equation can be far from range of 0-1 but it needs to be a
	 * probability that can be used for assignments to variables for the MAXSAT
	 * problem.
	 * 
	 *
	 * @param variable is the current variable assignment in question for the MAXSAT
	 *                 problem.
	 * @param cur      is the value that needs to be normalised to turn into a
	 *                 probability.
	 * @return cur after normalisation based on max and min seen so far; this
	 *         translates to the probability stored within the probVector for this
	 *         variable.
	 */
	public double normalize(int variable, double cur) {

		// if the current value for this variable found after evolving the probability
		// using PSO equation is minimum value found so far for this variable, update
		// min in the list at index position corresponding to variable.
		if (cur < min.get(variable)) {
			min.set(variable, cur);
		}

		// if current value for this variable found after PSO equation is max found so
		// far, update.
		if (cur > max.get(variable)) {
			max.set(variable, cur);
		}

		// normalisation equation
		double numerator = cur - min.get(variable);
		double denom = max.get(variable) - min.get(variable);
		// prevents NaN error
		if (numerator == 0 || denom == 0) {
			return 0;
		}
		return numerator / denom;
	}

	
	/**
	 * This function is used to assign neighbors to each particle. Depending on the
	 * topology, each particle will have a different set of neighbors and this
	 * function is called so that each particle can store its list of neighbors and
	 * have it influence the way the velocity and position vector is updated.
	 * 
	 * @param neighbor is a Particle that is this particle's neighbor and needs to
	 *                 be added to the list.
	 */
	public void assignN(Particle neighbor) {
		neighbors.add(neighbor);
	}

	
	/**
	 * This function is used only for global topology. It takes in the full list of
	 * particles and assings them to the neighborhood since the neighborhood for the
	 * global topology is just all the other particles in the swarm.
	 * 
	 * @param neighbors is the full list of particles in the swarm.
	 */
	public void assignNList(List<Particle> neighbors) {
		for (int i = 0; i < neighbors.size(); i++) {
			this.neighbors.add(neighbors.get(i));
		}
	}

	
	/**
	 * Method that returns the best solution found within the particle's
	 * neighborhood.
	 * 
	 * @return the neighborhood best value.
	 */
	public double findNBestValue() {
		return findNBest().getPBestFitness();
	}

	
	/**
	 * Helper method that iterates through the list of neighbors to find the
	 * neighbor with the best personal best solution.
	 * 
	 * @return the particle that is the neighborhood best (has the best personal
	 *         best solution within the neighborhood).
	 */
	public Particle findNBest() {
		double nBest = Double.MIN_VALUE;
		Particle nBestParticle = neighbors.get(0);
		for (int i = 0; i < neighbors.size(); i++) {
			if (neighbors.get(i).getPBestFitness() > nBest) {
				nBestParticle = neighbors.get(i);
				nBest = neighbors.get(i).getPBestFitness();
			}
		}
		return nBestParticle;
	}

	
	/**
	 * Getter method for the particle's personal best.
	 * 
	 * @return the value of the personal best solution.
	 */
	public double getPBestFitness() {
		return pbestfitness;
	}

	
	public List<Double> getProbVector() {
		return probVector;
	}

	
	/**
	 * Getter method for the position at which the particle found its personal best
	 * solution.
	 * 
	 * @param dimension is the dimension for which the v
	 * @return double the value in the position vector at specified dimension.
	 */
	public double getPBestVector(int variable) {
		return pbestVector.get(variable);
	}

	
	/**
	 * This is a contains method. Given a particle, it checks if the particle
	 * already exists within the neighborhood list.
	 * 
	 * @param particle is the particle being checked for in the neighborhood list.
	 * @return whether or not the particle in the parameter is already a neighbor.
	 */
	public boolean nContains(Particle particle) {
		return neighbors.contains(particle);
	}

}