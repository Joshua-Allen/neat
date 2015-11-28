import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import javax.swing.SpringLayout.Constraints;

@SuppressWarnings("all")
public class Neat {
	Controller controller;
	
	String[] ButtonNames = { "A", "B", "Up", "Down", "Left", "Right"};
	
	Random random; //needed through project, especially in mutate methods
	Pool pool; //needed in nodeMutate method to access the newInnovation method
	Inputs inputs; // TODO value should not be 0. needed in linkMutate and randomNeuron methods
	Outputs outputs; // TODO value should not be 0. needed in randomNeuron method at least
	
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
		
		inputs = new Inputs();
		outputs = new Outputs();
		
		initializePool();
	}
	
	///////////////////////////////////////////////////////////////
	public void update()
	{
		int curScore = getScore();
		// get current room
		//
		
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
		
		genome.maxneuron = 5;
		
		return genome;
	}
	
	public void generateNetwork(Genome genome)
	{
		//TODO
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
	public int randomNeuron(ArrayList<Gene> genes, boolean nonInput) {
		/* I have changed my mind, look below for alternate code
		Boolean[] neurons = new Boolean[1000];
		for(Boolean i : neurons)
			i = null;
			
		if(!nonInput) 
			for(int i=0; i<Inputs; i++) 
				neurons[i] = new Boolean(true);
		for(int o=0; o<Outputs; o++)
			neurons[MaxNodes+o] = new Boolean(true);
		for(int i=0; i<genes.size(); i++) {
			if(!nonInput || genes.get(i).into > Inputs)
				neurons[genes.get(i).into] = new Boolean(true);
			if(!nonInput || genes.get(i).out > Inputs)
				neurons[genes.get(i).out] = new Boolean(true);
		}
		
		
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for(int i=0; i<neurons.length; i++)
			if(neurons[i] == true)
				indexes.add(i);
		
		return indexes.get(random.nextInt(indexes.size()));*/
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		if(!nonInput) 
			for(int i=0; i<inputs.inputs.size(); i++) 
				indexes.add(i);
		for(int o=0; o<outputs.outputs.length; o++)
			indexes.add(MaxNodes+o);
		for(int i=0; i<genes.size(); i++) {
			if(!nonInput || genes.get(i).into > inputs.inputs.size())
				indexes.add(genes.get(i).into);
			if(!nonInput || genes.get(i).out > inputs.inputs.size())
				indexes.add(genes.get(i).out);
		}
		return indexes.get(random.nextInt(indexes.size()));
	}
	
	///////////////////////////////////////////////////////////////
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
		int neuron1 = randomNeuron(genome.genes, false);
		int neuron2 = randomNeuron(genome.genes, true);
		
		Gene newLink = new Gene();
		if(neuron1 <= inputs.inputs.size() && neuron2 <= inputs.inputs.size()) 
			return;
		
		if(neuron2 <= inputs.inputs.size()) {
			//Swap output and input
			int temp = neuron1;
			neuron1 = neuron2;
			neuron2 = temp;
		}
		
		newLink.into = neuron1;
		newLink.out = neuron2;
		if(forceBias)
			newLink.into = inputs.inputs.size();
		
		if(genome.genes.contains(newLink)) //TODO test if we need custom contains method
			return;
		
		newLink.innovation = pool.newInnovation();
		newLink.weight = random.nextDouble() * 4 - 2; //TODO check if execution order is correct
		
		genome.genes.add(newLink);
	}
	
	public void nodeMutate(Genome genome)
	{
		if (genome.genes.size() == 0)
			return;
		
		genome.maxneuron++;
		
		Gene gene = genome.genes.get(random.nextInt(genome.genes.size())); //TODO check if this will error, may be off by one
		if (!gene.enabled)
			return;
		gene.enabled = false;
		
		Gene gene1 = gene.copy();
		gene1.out = genome.maxneuron; 
		gene1.weight = 1.0;
		gene1.innovation = pool.newInnovation();
		gene1.enabled = true;
		genome.genes.add(gene1);
		
		Gene gene2 = gene.copy();
		gene2.into = genome.maxneuron;
		gene2.innovation = pool.newInnovation();
		gene2.enabled = true; 
		genome.genes.add(gene2);
	}
	public void enableDisableMutate(Genome genome, boolean enable)
	{
		ArrayList<Gene> candidates = new ArrayList<Gene>();
		for(Gene gene : genome.genes)
			if (!gene.enabled)
				candidates.add(gene);
		if(candidates.isEmpty())
			return;
		Gene gene = candidates.get(random.nextInt(candidates.size())); //TODO maybe off by one error, must test
		gene.enabled = !gene.enabled;
	}
	
	///////////////////////////////////////////////////////////////
	public double disjoint(ArrayList<Gene> genes1, ArrayList<Gene> genes2) {
		int disjointGenes = 0;
		for(int i=0; i<genes1.size(); i++) 
			if(!genes2.contains(genes1.get(i)))  // may need custom contains
				disjointGenes++;
			
		for(int i=0; i<genes2.size(); i++) 
			if(!genes1.contains(genes2.get(i)))
				disjointGenes++;
		
		int n = Math.max(genes1.size(), genes2.size());
		
		return disjointGenes / n;
	}
	
	public double weights(ArrayList<Gene> genes1, ArrayList<Gene> genes2) {
		ArrayList<Gene> genes = new ArrayList<Gene>();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		for(int i=0; i<genes2.size(); i++) {
			Gene gene = genes2.get(i);
			genes.add(gene);
			indexes.add(gene.innovation);
		}
		
		double sum = 0;
		int coincident = 0;
		for(int i=0; i<genes1.size();i++) {
			Gene gene = genes1.get(i);
			if(indexes.contains(gene.innovation)) {
				Gene gene2 = genes.get(indexes.indexOf(gene.innovation));
				sum += Math.abs(gene.weight - gene2.weight);
				coincident++;
			}
		}
		
		return sum / coincident;
	}
	
	public boolean sameSpecies(Genome genome1, Genome genome2) {
		double dd = DeltaDisjoint*disjoint(genome1.genes, genome2.genes);
		double dw = DeltaWeights*weights(genome1.genes, genome2.genes);
		return dd + dw < DeltaThreshold;
	}
	
	public void rankGlobally() {
		ArrayList<Genome> global = new ArrayList<>();
		for(int s=0; s<pool.species.size(); s++) {
			Species species = pool.species.get(s);
			for(int g=0; g<species.genomes.size(); g++)
				global.add(species.genomes.get(g));
		}
		
		Collections.sort(global);
		
		for(int g=0; g<global.size(); g++) 
			global.get(g).globalRank = g;
	}
	
	public void calculateAverageFitness(Species species)
	{
		int total = 0;
		
		for(Genome gen: species.genomes)
		{
			total += gen.globalRank;
		}
		
		species.averageFitness = total / species.genomes.size();
	}
	
	public int totalAverageFitness()
	{
		int total = 0;
		
		for(Species species: pool.species)
		{
			total += species.averageFitness;
		}
		
		return total;
	}
	
	public void cullSpecies(boolean cutToOne)
	{
		for(Species species: pool.species)
		{
			species.sort();
			
			int remaining = (int) Math.ceil(species.genomes.size());
			
			if (cutToOne)
			{
				remaining = 1;
			}
			
			while (species.genomes.size() > remaining)
			{
				species.genomes.remove(species.genomes.size()-1);
			}
		}
	}
	
	public Genome breedChild(Species species)
	{
		Genome child;
		
		if (random.nextDouble() < CrossoverChance)
		{
			Genome g1 = species.genomes.get(random.nextInt(species.genomes.size()));
			Genome g2 = species.genomes.get(random.nextInt(species.genomes.size()));
			child = crossover(g1, g2);
		} else {
			Genome g = species.genomes.get(random.nextInt(species.genomes.size()));
			child = g.copy();
		}
		
		mutate(child);
		
		return child;
	}
	
	public void removeStaleSpecies()
	{
		ArrayList<Species> survived = new ArrayList<Species>();
		
		for(Species species: pool.species)
		{
			species.sort();
			
			Genome g = species.genomes.get(0);
			
			if (g.fitness > species.topFitness)
			{
				species.topFitness = g.fitness;
				species.staleness = 0;
			} else {
				species.staleness = species.staleness + 1;
			}
			
			if (species.staleness < StaleSpecies || species.topFitness >= pool.maxFitness)
			{
				survived.add(species);
			}
			
		}
		
		pool.species = survived;
		
	}
	
	public void removeWeakSpecies()
	{
		ArrayList<Species> survived = new ArrayList<Species>();
		
		int sum = totalAverageFitness();
		for(Species species: pool.species)
		{
            double breed = Math.floor(species.averageFitness / sum * Population);
            if (breed >= 1)
            {
            	survived.add(species);
            }
		}
		pool.species = survived;
	}
	
	///////////////////////////////////////////////////////////////
	public void addToSpecies(Genome child)
	{
		boolean foundSpecies = false;
		for(Species species: pool.species)
		{
			if (!foundSpecies && sameSpecies(child, species.genomes.get(0)))
			{
				species.genomes.add(child);
				foundSpecies = true;
			}
		}
		
		if (!foundSpecies)
		{
			Species sp = new Species();
			sp.genomes.add(child);
			pool.species.add(sp);
		}
	}
	
	public void newGeneration()
	{
		cullSpecies(false);
		rankGlobally();
		removeStaleSpecies();
		rankGlobally();
		
		for(Species species: pool.species)
		{
			calculateAverageFitness(species);
		}
		
		removeWeakSpecies();
		
		int sum = totalAverageFitness();
		ArrayList<Genome> children = new ArrayList<Genome>();
		
		for(Species species: pool.species)
		{
			double breed = Math.floor(species.averageFitness / sum * Population) - 1;
			for(int i=0; i<breed; i++)
			{
				children.add(breedChild(species));
			}
		}
		
		cullSpecies(true);
		while(children.size()+pool.species.size() < Population)
		{
			Species species = pool.species.get(random.nextInt(pool.species.size()));
			children.add(breedChild(species));
		}
		
		for(Genome child: children)
		{
			addToSpecies(child);
		}
		
		pool.generation++;
	}
	
	public void initializePool()
	{
		pool = new Pool();
		
		for(int i=0; i<Population; i++)
		{
			addToSpecies(basicGenome());
		}
		
		initializeRun();
	}
	
	public void initializeRun()
	{
		// TODO: send load game command
		Species species = pool.species.get(pool.currentSpecies);
		Genome genome = species.genomes.get(pool.currentGenome);
		generateNetwork(genome);
		evaluateCurrent();
	}
	
	public void evaluateCurrent()
	{
		Species species = pool.species.get(pool.currentSpecies);
		Genome genome = species.genomes.get(pool.currentGenome);
		
		Inputs inputs = getInputs();
		Outputs controller = evaluateNetwork(genome.network, inputs);
		//TODO: joypad.set(controller)
	}
	
	///////////////////////////////////////////////////////////////
	public void nextGenome()
	{
		pool.currentGenome++;
		
		if (pool.currentGenome > pool.species.get(pool.currentSpecies).genomes.size())
		{
			pool.currentGenome = 1;
			pool.currentSpecies++;
			if (pool.currentSpecies > pool.species.size())
			{
				newGeneration();
				pool.currentSpecies = 1;
			}
		}
	}
	
	public boolean fitnessAlreadyMeasured()
	{
		Species species = pool.species.get(pool.currentSpecies);
		Genome genome = species.genomes.get(pool.currentGenome);
		
		return genome.fitness != 0;
	}
	
	public void playTop()
	{
		int maxfitness = 0;
		int maxs = 0;
		int maxg = 0;
		
		for(int i=0; i<pool.species.size(); i++)
		{
			Species species = pool.species.get(i);
			
			for(int j=0; j<species.genomes.size(); j++)
			{
				Genome genome = species.genomes.get(j);
				
				if (genome.fitness > maxfitness)
				{
					maxfitness = genome.fitness;
					maxs = i;
					maxg = j;
				}
			}
		}
		
		pool.currentSpecies = maxs;
		pool.currentGenome = maxg;
		pool.maxFitness = maxfitness;
		//TODO:
		//forms.settext(maxFitnessLabel, "Max Fitness: " .. math.floor(pool.maxFitness))
		initializeRun();
		pool.currentFrame++;
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
		
		public int newInnovation()
		{
			return ++innovation;
		}
	}
	public class Species{
		ArrayList<Genome> genomes = new ArrayList<Genome>();
		int topFitness = 0;
		int staleness = 0;
		int averageFitness = 0;
		
		public void sort()
		{
			Collections.sort(genomes);
		}
		
		public void calculateAverageFitness() {
			int total = 0; 
			for(int g=0; g<genomes.size(); g++) {
				Genome genome = genomes.get(g);
				total += genome.globalRank;
			}
			averageFitness = total / genomes.size();
		}
	}
	public class Genome implements Comparable{
		ArrayList<Gene> genes = new ArrayList<Gene>();
		Network network;
		int fitness = 0;
		int adjustedFitness = 0;
		int averageFitness = 0;
		int globalRank = 0;
		
		double connections = MutateConnectionsChance;
		double link = LinkMutationChance;
		double bias = BiasMutationChance;
		double node = NodeMutationChance;
		double enable = EnableMutationChance;
		double disable = DisableMutationChance;
		double step = StepSize;
		
		int maxneuron = 0;//???
		
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
		
		@Override
		public int compareTo(Object other) {
			double otherFitness = ((Genome)other).fitness;
			
			return Double.compare(this.fitness, otherFitness);
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






























