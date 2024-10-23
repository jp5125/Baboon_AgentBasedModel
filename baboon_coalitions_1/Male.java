package baboon_coalitions_1;

import sim.engine.*;
import sim.field.continuous.*;
import ec.util.MersenneTwisterFast;
import java.util.Random;


public class Male implements Steppable
{
	/**
	 * Standard serializable version
	 */
	private static final long serialVersionUID = 1L;
	Continuous2D environment;
	int fightingAbility; //Fighting ability will determine rank
	Random rand = new Random();
	MersenneTwisterFast randomGenerator = new MersenneTwisterFast();
	String T1; //Assumptions about others (D1 or I1)
	String T2; //Own motivation (D2 or I2)
	
	
	//constructor for male agents
	public Male(Continuous2D environment)
	{
		this.environment = environment;
		this.fightingAbility = randomGenerator.nextInt(100);
		initializeTraits(); //Initialize T1 and T2 traits for an agent
	}
	
	public void initializeTraits()
	{
		String[] T1Values = {"D1", "I1"};
		String[] T2Values = {"D2", "I2"};
		
		//Randomly select a value for T1 and T2
		T1 = T1Values[rand.nextInt(2)];
		T2 = T2Values[rand.nextInt(2)];
	}
	
	@Override
	public void step(SimState state)
	{
		//Actions for male agent go here
	}
	
	
	
///getters and setters
	public String getT1()
	{
		return T1;
	}
	
	public String getT2()
	{
		return T2;
	}
	
	public int getFightingAbility() 
	{
		return fightingAbility;
	}
	
}
