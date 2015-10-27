import java.util.ArrayList;


//enum neuron_type {  input, hidden, bias, output, none };

public class Neat {
	
	String[] ButtonNames = { "A", "B", "Up", "Down", "Left", "Right",};
	
	int Population = 300;
	double DeltaDisjoint = 2.0;
	double DeltaWeights = 0.4;
	double DeltaThreshold = 1.0;
	 
	int StaleSpecies = 15;
	 
	double MutateConnectionsChance = 0.25;
	double PerturbChance = 0.90;
	double CrossoverChance = 0.75;
	double LinkMutationChance = 2.0;
	double NodeMutationChance = 0.50;
	double BiasMutationChance = 0.40;
	double StepSize = 0.1;
	double DisableMutationChance = 0.4;
	double EnableMutationChance = 0.2;
	 
	int TimeoutConstant = 20;
	 
	int MaxNodes = 1000000;
	
	///////////////////////////////////////////////////////////////
	public Neat()
	{
		
	}
	
	///////////////////////////////////////////////////////////////
	public void update()
	{
		
	}
	
	public void draw()
	{
		
	}
	
	///////////////////////////////////////////////////////////////
	public double sigmoid(double x)
	{
		return 2/(1+Math.exp(-4.9*x))-1;
	}
	
	
	
	///////////////////////////////////////////////////////////////
	
	public class Pool{
		ArrayList<Species> species = new ArrayList<Species>();
        int generation = 0;
        int innovation = 0;
        int currentSpecies = 1;
        int currentGenome = 1;
        int currentFrame = 0;
		int maxFitness = 0;
	}
	public class Species{
		ArrayList<Genome> genomes = new ArrayList<Genome>();
		int topFitness = 0;
		int staleness = 0;
		int averageFitness = 0;
	}
	public class Genome{
		ArrayList<Gene> genes = new ArrayList<Gene>();
		ArrayList network = new ArrayList();
		int fitness = 0;
		int adjustedFitness = 0;
		int averageFitness = 0;
		
		double connections = MutateConnectionsChance;
		double link = LinkMutationChance;
		double bias = BiasMutationChance;
		double node = NodeMutationChance;
		double enable = EnableMutationChance;
		double disable = DisableMutationChance;
		double step = StepSize;
		
		public Genome copy()
		{
			Genome newGenome = new Genome();
			
			newGenome.genes = new ArrayList<Gene>(genes);
			newGenome.network = new ArrayList<Gene>(network);
			newGenome.fitness = 0;
			newGenome.adjustedFitness = 0;
			newGenome.averageFitness = 0;
			
			newGenome.connections = connections;
			newGenome.link = link;
			newGenome.bias = bias;
			newGenome.node = node;
			newGenome.enable = enable;
			newGenome.disable = disable;
			newGenome.step = step;
			
			return newGenome;
		}
	}
	public class Gene{
		int into = 0;
		int out = 0;
		double weight = 0.0;
		boolean enabled = true;
		int innovation = 0;
	}
	public class Neuron{
		ArrayList incoming = new ArrayList();
		double value = 0.0;
	}
	
	/*
	public class node{
		
	}
	
	public class link{
		public double weight;
		public int from;
		public int to;
		public boolean enabled;
		public boolean recurrent;
		public int innovation;
		
		public link(int to, int from, int innovation, 
				double weight, boolean enabled, boolean recurrent)
		{
			this.to = to;
			this.from = from;
			this.weight = weight;
			this.innovation = innovation;
			this.enabled = enabled;
			this.recurrent = recurrent;
		}
	}
	
	public class Genome{
		int ID;
		ArrayList<Gene> neurons;
		ArrayList<link> links;
		
		//CNeuralNet* m_pPhenotype;
		
		double fitness;
		double adjustedFitness;
		double amountToSpawn;
		int numInputs;
		int numOutPuts;
		int species;
		
		
	}
	
	public class Gene{
		int id;
		neuron_type type;
		boolean recurrent;
		double ActivationResponse;
		double dSplitX;
		double dSplitY;
		
		public Gene(int id, neuron_type type, boolean recurrent, double dSplitX, double dSplitY)
		{
			this.id = id;
			this.type = type;
			this.recurrent = recurrent;
			this.dSplitX = dSplitX;
			this.dSplitY = dSplitY;
		}
	}
	
	public class Organism{
		
	}
	
	public class Population{
		
	}
	
	public class Species{
		
	}
	
	public class Trait{
		
	}
*/
}


