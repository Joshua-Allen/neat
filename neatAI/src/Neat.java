import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("all")
public class Neat {
	Controller controller;
	
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
	
	boolean start = false;
	
	///////////////////////////////////////////////////////////////
	public Neat()
	{
		random = new Random();
		random.setSeed(System.currentTimeMillis());
	}
	
	///////////////////////////////////////////////////////////////
	public void update()
	{
		int curScore = getScore();
		
		
	}

	// need to do
	public int getScore()
	{
		int score = 0;
		
		
		
		return score;
	}
	
	// need to do
 	public void draw()
	{
		
	}
	
	///////////////////////////////////////////////////////////////
 	public Inputs getInputs()
 	{
 		WindowPanel window = controller.w;
 		
 		Inputs inputs = new Inputs();
 		//inputs.inputs.add()
 		
 		for(int y=0; y<18; y++)
 		{
 	 		for(int x=0; x<20; x++)
 	 		{
				Map cell = window.getPoint(4+x*8, 4+y*8);
				String tile = (String) cell.get("tile");
				inputs.inputs.add(Integer.parseInt(tile));
 	 		}
 		}
 		
 		return inputs;
 	}
 	
	///////////////////////////////////////////////////////////////
	public double sigmoid(double x)
	{
		return 2/(1+Math.exp(-4.9*x))-1;
	}
	public Genome basicGenome()
	{
		Genome genome = new Genome();
		
		genome.maxneuron = 5.0;
		
		return genome;
	}
	
	public Outputs evaluateNetwork(Network network, Inputs inputs)
	{
		// 
		for(int i=0; i<inputs.inputs.size(); i++)
		{
			network.neurons[i].value = (double) inputs.inputs.get(i);
		}
		
		//
		for(int i=0; i<network.neurons.length; i++)
		{
			int sum = 0;
			Neuron cur_neuron = network.neurons[i];
			
			for(int j=0; j<cur_neuron.incoming.size(); j++)
			{
				Gene incoming = cur_neuron.incoming.get(j);
				Neuron other = network.neurons[incoming.into];
				sum += incoming.weight * other.value;
			}
			
			if (cur_neuron.incoming.size() > 0)
			{
				cur_neuron.value = sigmoid(sum);
			}
		}
		
		//
		Outputs out = new Outputs();
		
        for (int i=0; i<6; i++) 
        {
        	if (network.neurons[MaxNodes+i].value > 0)
        	{
        		out.outputs[i] = true;
        	} else {
        		out.outputs[i] = false;
        	}
        }
		
		return out;
	}

	public Genome crossover(Genome g1, Genome g2)
	{
		//
		Genome genome1 = g1;
		Genome genome2 = g2;
		
		//
		if (g2.fitness > g1.fitness)
		{
			genome1 = g2;
			genome2 = g1;
		}
		
		//
		Genome child = new Genome();
		Gene[] innovations2 = new Gene[5000];
		
		//
		for(int i=0; i<genome2.genes.size(); i++)
		{
			Gene cur_gene = genome2.genes.get(i);
			innovations2[cur_gene.innovation] = cur_gene;
		}
		
		//
		Random randomGen = new Random();
		for(int i=0; i<genome1.genes.size(); i++)
		{
			Gene gene1 = genome1.genes.get(i);
			Gene gene2 = innovations2[gene1.innovation];
			
			if (gene2 != null && randomGen.nextInt(2) == 1 && gene2.enabled)
			{
				child.genes.add(gene2.copy());
			} else {
				child.genes.add(gene1.copy());
			}
		}
		
		//
		child.maxneuron = Math.max(genome1.maxneuron, genome2.maxneuron);
		
		//
		child.connections = genome1.connections;
		child.link = genome1.link;
		child.bias = genome1.bias;
		child.node = genome1.node;
		child.enable = genome1.enable;
		child.disable = genome1.disable;
		child.step = genome1.step;
		
		
		//
		return child;
	}
	
	///////////////////////////////////////////////////////////////
	// Added for mutate methods
	// TODO need to do
	public Neuron randomNeuron(ArrayList<Gene> genes, boolean bool) {
		return null;
	}
	
	///////////////////////////////////////////////////////////////
	// need to do	
	public void mutate(Genome genome)
	{
		double p; 
		
		genome.connections *= (random.nextDouble() > 0.5) ? 0.95 : 1.05263;
		genome.link *= (random.nextDouble() > 0.5) ? 0.95 : 1.05263;
		genome.bias *= (random.nextDouble() > 0.5) ? 0.95 : 1.05263;
		genome.node *= (random.nextDouble() > 0.5) ? 0.95 : 1.05263;
		genome.enable *= (random.nextDouble() > 0.5) ? 0.95 : 1.05263;
		genome.disable *= (random.nextDouble() > 0.5) ? 0.95 : 1.05263;
		genome.step *= (random.nextDouble() > 0.5) ? 0.95 : 1.05263;
		
		if (random.nextDouble() < genome.connections) 
			pointMutate(genome); 
		
		p = genome.link;
		while(p > 0) {
			if(random.nextDouble() < p)
				linkMutate(genome, false);
			p--;
		}
		
		p = genome.bias;
		while(p > 0) {
			if(random.nextDouble() < p)
				linkMutate(genome, true);
			p--;
		}
		
		p = genome.node;
		while(p > 0) {
			if(random.nextDouble() < p) 
				nodeMutate(genome);
			p--;
		}
		
		p = genome.enable;
		while(p > 0) {
			if(random.nextDouble() < p) 
				enableDisableMutate(genome, true);
			p--;
		}
		
		p = genome.disable;
		while(p > 0) {
			if(random.nextDouble() < p) 
				enableDisableMutate(genome, false);
			p--;
		}
	}
	public void pointMutate(Genome genome)
	{
		double step = genome.step;
		Gene gene = null;
		for(int i=0; i < genome.genes.size(); i++) {
			gene = genome.genes.get(i);
			if(random.nextDouble() < PerturbChance)
				gene.weight = gene.weight + random.nextDouble() * step*2 - step; //TODO check that math is being done in correct order according to the lua example
			else 
				gene.weight = random.nextDouble() * 4 - 2;//TODO Ditto 
		}
	}
	public void linkMutate(Genome genome, boolean forceBias)
	{
		Neuron neuron1 = randomNeuron(genome.genes, false);
		Neuron neuron2 = randomNeuron(genome.genes, true);
		
		Gene newLink = new Gene();
		//if(neuron1 <= Inputs && neuron2 <= Inputs) 
		//	return;
		
	}
	public void nodeMutate(Genome genome)
	{
		
	}
	public void enableDisableMutate(Genome genome, boolean enable)
	{
		
	}
	
	///////////////////////////////////////////////////////////////
	public class Network{
		
		Neuron[] neurons;// = Neuron[];
		//neurons
		
		public Network(Genome genome, int inputNumber, int outputNumber)
		{
			neurons = new Neuron[MaxNodes+outputNumber];
			
			for(int i=0; i<inputNumber; i++)
				neurons[i] = new Neuron();

			for(int i=0; i<inputNumber; i++)
				neurons[MaxNodes+i] = new Neuron();
		
			genome.sort();
			
			for(int i=0; i<genome.genes.size(); i++)
			{
				Gene gene = genome.genes.get(i);
				if(gene.enabled)
				{
					if (neurons[gene.out] == null)
					{
						neurons[gene.out] = new Neuron();
					}
					
					Neuron neuron = neurons[gene.out];
					
					neuron.incoming.add(gene);
					if (neurons[gene.into] == null)
					{
						neurons[gene.into] = new Neuron();
					}
				}
			}
			
			genome.network = this;
		}
	}
	public class Pool{
		ArrayList<Species> species = new ArrayList<Species>();
        int generation = 0;
        int innovation = 0;
        int currentSpecies = 1;
        int currentGenome = 1;
        int currentFrame = 0;
		int maxFitness = 0;
		
		public void newInnovation()
		{
			innovation++;
		}
	}
	public class Species{
		ArrayList<Genome> genomes = new ArrayList<Genome>();
		int topFitness = 0;
		int staleness = 0;
		int averageFitness = 0;
	}
	public class Genome{
		ArrayList<Gene> genes = new ArrayList<Gene>();
		Network network;
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
		
		double maxneuron = 0;//???
		
		public Genome copy()
		{
			Genome newGenome = new Genome();
			
			newGenome.genes = new ArrayList<Gene>(genes);
			newGenome.network = network;
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
	
		public void sort()
		{
			Collections.sort(genes);
		}
	}
	public class Gene implements Comparable{
		int into = 0;
		int out = 0;
		double weight = 0.0;
		boolean enabled = true;
		int innovation = 0;
		
		public Gene copy()
		{
			Gene nuwGene = new Gene();
			
			nuwGene.into = into;
			nuwGene.out = out;
			nuwGene.weight = weight;
			nuwGene.enabled = enabled;
			nuwGene.innovation = innovation;
			
			return nuwGene;
		}

		@Override
		public int compareTo(Object other) {
			int otherOut = ((Gene)other).out;
			
			return this.out-otherOut;
		}
	}
	public class Neuron{
		ArrayList<Gene> incoming = new ArrayList<Gene>();
		double value = 0.0;
	}
	
	// ??????
	public class Inputs{
		public ArrayList inputs = new ArrayList();
	}
	public class Outputs{
		boolean[] outputs = {false, false, false, false, false, false};
	}
	
}


