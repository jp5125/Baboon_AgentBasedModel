package baboon_coalitions_1;

import sim.engine.*;
import sim.field.continuous.*;
import sim.util.Double2D; //for agent positions
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ec.util.MersenneTwisterFast;


public class BaboonSimulation extends SimState 
{
	/**
	 * Default Serializable value
	 */
	private static final long serialVersionUID = 1L;
	public Continuous2D environment = new Continuous2D(1.0, 1000, 1000); //continuous space, 1000 x 1000, serializable UID 1
	private MersenneTwisterFast random = new MersenneTwisterFast(); //random number generator
	Random rand = new Random(); //random # generator
	
	
	public BaboonSimulation(long seed) 
	{
		super(seed);
	}
	
	@Override
	public void start()
	{
		super.start(); //initialization of agents will go here
		environment.clear(); //clear environment from previous runs
		
		addAgents(50, 50); //adds males and females
		
		schedule.scheduleRepeating(new Steppable()
				{
				
					private static final long serialVersionUID = 1L;
					public void step(SimState state)
					{
						runTimeStep();
					}
				});
	}
	
	private void addAgents(int numMales, int numFemales)
	{
		//add male agents
		for(int i = 0; i < numMales; i++) 
		{
			Male male = new Male(environment);
			Double2D location = new Double2D(random.nextDouble() * 1000, random.nextDouble() * 1000); //randomize location
			environment.setObjectLocation(male, location); //place male in environment
			schedule.scheduleRepeating(male);
			
			//print statement to confirm male creation
			System.out.println("Male " + (i + 1) + " added at location: " + location);
			System.out.println("Male " + (i + 1) + " fighting ability is " + male.getFightingAbility()); // there is a bug with generating FA randomly
			System.out.println("Male " + (i + 1) + " trait combination is " + male.getT1() + " " + male.getT2());
		}
		
		//add female agents
		for(int i = 0; i < numFemales; i++)
		{
			Female female = new Female(environment);
			Double2D location = new Double2D(random.nextDouble() * 1000, random.nextDouble() * 1000); //randomize location
			environment.setObjectLocation(female, location); //place female in environment
			schedule.scheduleRepeating(female);
			
			//print statement to confirm female creation
			System.out.println("Female " + (i + 1) + " added at location: " + location);
		}
	}
	
	private void runTimeStep()
	{
		List<Female> fertileFemales = checkFertileFemales(); //step 1, determine which females are fertile
		int desiredFertileFemales = poissonDistribution(0.5); //adjust # fertile females based on poisson distribution
		
		if(fertileFemales.size() > desiredFertileFemales)
		{
			while(fertileFemales.size() > desiredFertileFemales)
			{
			fertileFemales.remove(rand.nextInt(fertileFemales.size())); //remove females from fertility in order to capture dist.
			}
		}
		
        System.out.println("Number of fertile females: " + fertileFemales.size()); // Log the number of fertile females
        
        //assign consort males to fertile females
        assignConsortMales(fertileFemales);
        
        //handle coalition formation (add later)
        handleCoalitionsAndChallenges(fertileFemales);
	}
		
	private void assignConsortMales(List<Female> fertileFemales)
	{
		List<Male> males = getMalesSortedByRank(); //get males sorted by their fighting ability
		
		//assign highest ranking males to fertile females
		for(int i = 0; i < fertileFemales.size() && i < males.size(); i++)
		{
			Female female = fertileFemales.get(i);
			Male consortMale = males.get(i); //the highest ranking available male becomes the consort
			female.setConsortMale(consortMale); //assign consort male to the female 
			
			// Log the assignment
	        System.out.println("Assigned Male " + consortMale + " as consort to Female " + female);
		}
	}
	
	private void handleCoalitionsAndChallenges(List<Female> fertileFemales) 
	{
		List<Male> nonConsortMales = getNonConsortMales();
		
		for (Female female : fertileFemales)
		{
			Male consortMale = female.getConsortMale(); //Get the current consort male for this female
			
			//form coalitions among non-consort males
			Male coalitionMale1 = selectCoalitionMale(nonConsortMales);
			Male coalitionMale2 = selectCoalitionMale(nonConsortMales);
			
			//simulate a challenge
			boolean success = challengeConsort(consortMale, coalitionMale1, coalitionMale2);
			
			if(success)
			{
				//if coalition succeeds, assign one of the coalition males as the new consort
				System.out.println("Coalition succeeded! Assigning new consort for Female.");
	            female.setConsortMale(selectNewConsort(coalitionMale1, coalitionMale2));
			}
			else
			{
				System.out.println("Coalition failed to overthrow consort male.");
			}
		}
	}
	
	// Method to get non-consort males (males who are not currently assigned as consorts)
	private List<Male> getNonConsortMales() {
	    List<Male> allMales = getAllMales();  // Get all males
	    List<Male> consortMales = getConsortMales();  // Get currently assigned consort males
	    List<Male> nonConsortMales = new ArrayList<>(allMales);

	    // Remove all consort males from the list of all males
	    nonConsortMales.removeAll(consortMales);
	    return nonConsortMales;  // Return the list of males who are not consorts
	}

	// Method to get all males in the environment
	private List<Male> getAllMales() {
	    List<Male> males = new ArrayList<>();
	    for (Object obj : environment.getAllObjects()) {
	        if (obj instanceof Male) {
	            males.add((Male) obj);
	        }
	    }
	    return males;
	}

	// Method to get all consort males (males assigned as consorts to fertile females)
	private List<Male> getConsortMales() {
	    List<Male> consortMales = new ArrayList<>();
	    for (Object obj : environment.getAllObjects()) {
	        if (obj instanceof Female) {
	            Female female = (Female) obj;
	            if (female.getConsortMale() != null) {
	                consortMales.add(female.getConsortMale());
	            }
	        }
	    }
	    return consortMales;
	}
	
	private Male selectCoalitionMale(List<Male> nonConsortMales)
	{
		return nonConsortMales.get(rand.nextInt(nonConsortMales.size())); //randomly select a coalitional male
	}
	
	private boolean challengeConsort(Male consortMale, Male coalitionMale1, Male coalitionMale2) 
	{
	    double coalitionStrength = coalitionMale1.getFightingAbility() + coalitionMale2.getFightingAbility();
	    return coalitionStrength > consortMale.getFightingAbility();  // Coalition succeeds if stronger
	}
	
	private Male selectNewConsort(Male coalitionMale1, Male coalitionMale2) 
	{
	    // Logic for determining which coalition male becomes the consort
	    if (coalitionMale1.getT2().equals("D2")) {
	        return coalitionMale1;  // Direct benefit male gets consortship
	    } else if (coalitionMale2.getT2().equals("D2")) {
	        return coalitionMale2;
	    } else {
	        // If both are indirect benefit, randomly select
	        return (rand.nextBoolean()) ? coalitionMale1 : coalitionMale2;
	    }
	}
	
	private List<Male> getMalesSortedByRank()
	{
		List<Male> males = new ArrayList<>();
		for(Object obj : environment.getAllObjects())
		{
			if(obj instanceof Male)
			{
				males.add((Male) obj);
			}
		}
		
		//sort the males by their fighting ability (descending order)
		males.sort((m1, m2) -> Double.compare(m2.getFightingAbility(), m1.getFightingAbility()));
		return males;
	}
	
	//Generate a Poisson-distributed random number
		private int poissonDistribution(double lambda)
		{
			double L = Math.exp(-lambda);
			int k = 0;
			double p = 1.0;
			while (p > L)
			{
				k++;
				p *= rand.nextDouble();
			}
			return k-1;
		}
	
	//method to check which females are fertile and return a list of fertile females
	private List<Female> checkFertileFemales()
	{
		List<Female> fertileFemales = new ArrayList<>();
		for(Object obj : environment.getAllObjects())
		{
			if(obj instanceof Female)
			{
				Female female = (Female) obj;
				if(female.isFertile())
				{
					fertileFemales.add(female); //add fertile females to list
				}
			}
		}
		return fertileFemales;
	}

	
	
	public static void main(String[] args) 
	{
		doLoop(BaboonSimulation.class, args);
		System.exit(0);

	}

}
