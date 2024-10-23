package baboon_coalitions_1;

import sim.engine.*;
import sim.field.continuous.*;
import java.util.Random;

public class Female implements Steppable 
{
	/**
	 * Default Serializable value
	 */
	private static final long serialVersionUID = 1L;
	Continuous2D environment;
	int cycleDay; //Current day of the reproductive cycle
	boolean isFertile = false; //track if the female is fertile
	boolean isPregnant = false; //track the pregnancy status
	int gestationPeriod = 0; //Track the gestation period
	Male consortMale;
	Random rand = new Random(); //random # generator
	
	
	//female constructor
	public Female(Continuous2D environment) 
	{
		this.environment = environment;
		initializeCycleDay(); //initialize the cycle using uniform random dist.
	}
	
	@Override
	public void step(SimState state)
	{
		if(isPregnant)
		{
			//If the female is pregnant, increment gestation period
			gestationPeriod++;
			if(gestationPeriod >= 300)
			{
				giveBirth();
			}
		}
		else
		{
			//if female is not pregnant, advance the reproductive cycle
			cycleDay++;
			if(cycleDay > 33)
			{
				cycleDay = 1; //reset the cycle after day 33
			}
			updateFertileStatus();
		}
		//log the females cycle fertility status
		System.out.println("Female at cycle day: " + cycleDay + " Fertile: " + isFertile + " Pregnant " + isPregnant);
	}
	
	//Initialize the reproductive cycle with a Poisson-distributed cycle day
	private void initializeCycleDay()
	{
		cycleDay = rand.nextInt(33) + 1;
		updateFertileStatus();
	}
	
	
	//update the fertile status based on the current cycle day
	private void updateFertileStatus()
	{
		isFertile = (cycleDay >= 25 && cycleDay <= 32); // Fertile during days 25-32
	}
	
	//simulate birth after pregnancy
	private void giveBirth()
	{
		System.out.println("Female gives birth!");
		isPregnant = false;
		gestationPeriod = 0; //reset the gestation period
		cycleDay = 1; //restart the reproductive cycle
	}
	
	//method to become pregnant
	public void becomePregnant()
	{
		isPregnant = true;
		cycleDay = 1;
		isFertile = false;
	}
	
	public boolean isFertile()
	{
		return isFertile;
	}
	
	public boolean isPregnant()
	{
		return isPregnant;
	}
	// New methods for managing consort males
    public void setConsortMale(Male male) {
        this.consortMale = male;  // Assign the consort male
    }

    public Male getConsortMale() {
        return consortMale;  // Retrieve the consort male
    }
	
}


