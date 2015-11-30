import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import javax.swing.SpringLayout.Constraints;

@SuppressWarnings("all")
public class Neat extends GameBoy_AI{
	String[] ButtonNames = { "A", "B", "Up", "Down", "Left", "Right"};
	
	Random random; //needed through project, especially in mutate methods
	Pool pool; //needed in nodeMutate method to access the newInnovation method
	Inputs inputs; // TODO value should not be 0. needed in linkMutate and randomNeuron methods
	Outputs outputs; // TODO value should not be 0. needed in randomNeuron method at least
	
	int Population = 2;
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
	 
	//int TimeoutConstant = 20;
	 
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
	
	public void run()
	{
		//addTasks();
		
		//
		Species species = pool.species.get(pool.currentSpecies);
		Genome genome = species.genomes.get(pool.currentGenome);		
		
		if (pool.currentFrame % 5 == 0) evaluateCurrent();
		
        //
        int fitness = getScore();
        
        // need to look for when the game is over
        if (!inGame && AI_running){
        	genome.fitness = fitness;
        	if (fitness > pool.maxFitness){
        		pool.maxFitness = fitness;
        	}
        	pool.currentSpecies = 0;
        	pool.currentGenome = 0;
        	
        	while (fitnessAlreadyMeasured()){
                nextGenome();
        	}
        	
        	initializeRun();
        }

        addTasks();

        
        pool.currentFrame++;
        
	}
	
	// need to do
 	public void draw(Graphics2D g)
	{
 		int draw_x = 500;
 		int draw_y = 15;
 		int cell_size = 10;
 		
 		// find the network to draw
 		Species species = pool.species.get(pool.currentSpecies);
 		Genome genome = species.genomes.get(pool.currentGenome);
 		Network network = genome.network;
 		
 		//
 		ArrayList<Cell> cells = new ArrayList<Cell>();
 		
 		// get all the cells from the screen
 		int neuron_index = 0;
 		for(int x=0; x<10; x++){
 	 		for(int y=0; y<18; y++){
 	 			Cell cell = new Cell();
 	 			cell.x = x*cell_size;
 	 			cell.y = y*cell_size;
 	 			cell.value = network.neurons.get(neuron_index).value;
 	 			cells.add(cell);
 	 		}
 		}
 		
 		// Do we need a biasCell???
 		
 		
 		
 		// Outputs // draw the buttons
 		for(int i=0; i<outputs.outputs.length; i++)
 		{
 			Cell cell = new Cell();
 			cell.x = 400;
 			cell.y = 15*i;
 			cell.value = network.neurons.get(MaxNodes + i).value;
 			cells.add(cell);
 			
 			if (cell.value > 0)
 				g.setColor(Color.blue);
		    else
		    	g.setColor(Color.BLACK);
 			
		    //gui.drawText(223, 24+8*o, ButtonNames[o], color, 9);
	 		g.drawString(outputs.getKeyName(i).toUpperCase(), draw_x+cell.x+15, draw_y+cell.y+10); 
 		}
 		
 		// make a cell for all the network neurons
 		for(int i=0; i<network.neurons.size(); i++)
 		{
 			Neuron neuron = network.neurons.get(i);
			if (neuron != null)
			{
	 			Cell cell = new Cell();
	 			cell.x = 140;
	 			cell.y = 40;
	 			cell.value = neuron.value;
	 			cells.add(cell);
			}
 		}
 		
 		// fix all the placements of the cells
 		/*for(int r=0; r<4; r++){// just repeat 4 times
 			for(Gene gene: genome.genes){
 				if (gene.enabled){
 					Cell into = cells.get(gene.into);
 					Cell out = cells.get(gene.out);
 					
 					if (gene.into > inputs.inputs.size() && gene.into <= MaxNodes){
 						into.x = (int) (0.75*into.x + 0.25*out.x);
 						
                        if (into.x >= out.x) into.x = into.x - 40;
                        if (into.x < 90) into.x = 90;
                        if (into.x > 220) into.x = 220;
                        
                        into.y = (int) (0.75*into.y + 0.25*into.y);
 					}
 					if (gene.out > inputs.inputs.size() && gene.out <= MaxNodes){
 						out.x = (int) (0.25*into.x + 0.75*out.x);
 						
                        if (into.x >= out.x) out.x = out.x + 40;
                        if (out.x < 90) out.x = 90;
                        if (out.x > 220) out.x = 220;
                        
                        out.y = (int) (0.25*into.y + 0.75*out.y);
 					}
 				}
 			}
 		}*/
 		
 		////////////////////////////////////////
 		// Actually draw the cells
 		////////////////////////////////////////
 		
 		drawBox(g, draw_x, draw_y, cell_size*10, cell_size*18, Color.LIGHT_GRAY, Color.BLACK);
 		
 		// draw the cells
 		for(int n=0; n<cells.size(); n++)
 		{
 			Cell cell = cells.get(n);
 			if (n > inputs.inputs.size() || cell.value != 0){
 				
 				int alpha = 1;
 				if (cell.value == 0) alpha = 20;
 				
 				drawBox(g, draw_x+cell.x, draw_y+cell.y, cell_size, cell_size, Color.white, new Color(255,255,255,alpha));
 			}
 		}
 		/*
 		// draw the connecting lines
 		for(Gene gene: genome.genes){
 			if (gene.enabled){
 				Cell into = cells.get(gene.into);
				Cell out = cells.get(gene.out);
				
				g.drawLine(into.x, into.y, out.x, out.y);
 			}
 		}
 		*/
 		
 		
 		//////////////////////////////////////////////
 		
 		//
 		double fitness = getScore();

 		// find percent measured
        int measured = 0;
        int total = 0;
        
        for(Species spe: pool.species){
            for(Genome gen: spe.genomes){
            	total++;
            	if (gen.fitness != 0){
            		measured++;
            	}
            }
        }
        
 		int percent = measured/total*100;
        
 		// draw the pool info
 		String poolInfo = "";
 		poolInfo += "Generation: " + pool.generation +"\n";
 		poolInfo += "Species: " + pool.currentSpecies +"\n";
 		poolInfo += "Genome: " + pool.currentGenome +"  ("+percent+"%)"+"\n";
 		poolInfo += "Fitness: " + fitness +"\n";
 		poolInfo += "Max Fitness: " + pool.maxFitness;
 		
 		g.setColor(Color.BLACK);
 		draw_text(g, poolInfo, 330, 5);
 		
	}
 	
 	// draw helpers
 	void draw_text(Graphics2D g, String text, int x, int y) {
 	    for (String line : text.split("\n"))
 	        g.drawString(line, x, y += g.getFontMetrics().getHeight());
 	}
 	void drawBox(Graphics2D g, int x, int y, int width, int height, Color in, Color out)
 	{
 		g.setColor(in);
 		g.fillRect(x, y, width, height); 	
 		g.setColor(out);
 		g.drawRect(x, y, width, height);
 	}
 	
 	
 	
	///////////////////////////////////////////////////////////////
 	
	public void addTasks()
	{
		//System.out.println("test");
		//int i=0;
		//for(;;)	{ i++; }

		//System.out.println("test");
		//return;
		/*
		if (outputs.getKey("a")){tasks.add("rotate_left");}
		if (outputs.getKey("b")){tasks.add("rotate_right");}
		//if (outputs.getKey("up")){tasks.add("rotate_right");}
		if (outputs.getKey("down")){tasks.add("down");}
		if (outputs.getKey("left")){tasks.add("left");}
		if (outputs.getKey("right")){tasks.add("right");}*/
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
				if (tile != null)
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
		new Network(genome, inputs.inputSize, outputs.outputs.length);
	}

	public Outputs evaluateNetwork(Network network, Inputs inputs_in)
	{
		// 
		
		
		//System.out.println("size: " + inputs.inputs.size());
		for(int i=0; i<inputs.inputs.size(); i++)
		{
			//network.neurons[i].value = (double) ((Integer) inputs.inputs.get(i)).intValue();
			//System.out.println("index: " + i);
			network.neurons.get(i).value = (double) ((Integer) inputs_in.inputs.get(i)).intValue();
		}
		
		//
		for(int i=0; i<network.neurons.size(); i++)
		{
			Neuron cur_neuron = network.neurons.get(i);
			if (cur_neuron != null)
			{
				int sum = 0;
				
				//System.out.println(cur_neuron);
				for(int j=0; j<cur_neuron.incoming.size(); j++){
					Gene incoming = cur_neuron.incoming.get(j);
					//Neuron other = network.neurons[incoming.into];
					Neuron other = network.neurons.get(incoming.into);
					
					sum += incoming.weight * other.value;
				}
				
				if (cur_neuron.incoming.size() > 0)
				{
					cur_neuron.value = sigmoid(sum);
				}
			}
		}
		
		//
		Outputs out = new Outputs();
		
        for (int i=0; i<6; i++) 
        {
        	//if (network.neurons[MaxNodes+i].value > 0)
        	if (network.neurons.get(MaxNodes+i).value > 0)
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
		{
			if(!genes2.contains(genes1.get(i)))  // may need custom contains
			{
				disjointGenes++;
			}
		}
		
		for(int i=0; i<genes2.size(); i++) 
		{
			if(!genes1.contains(genes2.get(i)))
			{
				disjointGenes++;
			}
		}
		
		int n = Math.max(genes1.size(), genes2.size());
		if (n == 0) n = 1;
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
		System.out.println("r sum: " + sum);///////////
		
		for(Species species: pool.species)
		{
            double breed = Math.floor(species.averageFitness / sum * Population);
            //System.out.println("averageFitness: " + species.averageFitness + "   breed: " + breed);///////////
            if (breed >= 1) {
            	System.out.println("r breed >= 1: " + breed);///////////
            	survived.add(species);
            }
		}
		pool.species = survived;
		
		System.out.println("r pool.species.size(): " + pool.species.size());///////////
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
		
		System.out.println("4 pool.species.size(): " +pool.species.size());///////////
		
		while(children.size()+pool.species.size() < Population)
		{
			//System.out.println(pool.species.size());
			//int rInt = random.nextInt(pool.species.size()+1);
			//System.out.println(children.size() +" - " +pool.species.size()+" - "+Population);
			//.nextInt(High-Low) + Low;
			int rInt = 0;
			if (pool.species.size() > 0) rInt = random.nextInt(pool.species.size());
			
			//System.out.println("size: "+ pool.species.size() +" - " + rInt);
			
			Species species = pool.species.get(rInt);
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
		
		if (pool.currentGenome > pool.species.get(pool.currentSpecies).genomes.size()-1)
		{
			pool.currentGenome = 0;
			pool.currentSpecies++;
			if (pool.currentSpecies > pool.species.size()-1)
			{
				newGeneration();
				pool.currentSpecies = 0;
			}
		}
	}
	
	public boolean fitnessAlreadyMeasured()
	{
		if (pool.currentSpecies < pool.species.size())
		{
			Species species = pool.species.get(pool.currentSpecies);
			if (pool.currentGenome < species.genomes.size())
			{
				Genome genome = species.genomes.get(pool.currentGenome);
				
				return genome.fitness != 0;
			}
		}
		return false;
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
		HashMap<Integer, Neuron> neurons;// = new HashMap<Integer, String>();
		//Neuron[] neurons = new Neuron[50];
		//neurons
		
		public Network(Genome genome, int inputNumber, int outputNumber)
		{

			//neurons = new Neuron[MaxNodes+outputNumber];
			neurons = new HashMap<Integer, Neuron>();
			
			for(int i=0; i<inputNumber; i++)
				neurons.put(i, new Neuron());

			for(int i=0; i<outputNumber; i++)
				neurons.put(MaxNodes+i, new Neuron());
		
			genome.sort();
			
			for(int i=0; i<genome.genes.size(); i++)
			{
				Gene gene = genome.genes.get(i);
				if(gene.enabled)
				{
					if (neurons.get(gene.out) == null)
					{
						//neurons[gene.out] = new Neuron();
						neurons.put(gene.out, new Neuron());
					}
					
					Neuron neuron = neurons.get(gene.out);
					
					neuron.incoming.add(gene);
					if (neurons.get(gene.out) == null)
					{
						//neurons[gene.into] = new Neuron();
						neurons.put(gene.into, new Neuron());
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
        int currentSpecies = 0;
        int currentGenome = 0;
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
	
	public class Inputs{
		public ArrayList inputs = new ArrayList();
		
		int inputSize = 11*19;
		
	}
	public class Outputs{
		//"A","B","Up","Down","Left","Right"
		boolean[] outputs = {false, false, false, false, false, false};
		public boolean getKey(String key)
		{
			switch(key)
			{
				case "a": return outputs[0];
				case "b": return outputs[1];
				case "up": return outputs[2];
				case "down": return outputs[3];
				case "left": return outputs[4];
				case "right": return outputs[5];
			}
			return false;
		}
		public void setKey(String key, boolean set)
		{
			switch(key)
			{
				case "a": outputs[0] = set;
				case "b": outputs[1] = set;
				case "up": outputs[2] = set;
				case "down": outputs[3] = set;
				case "left": outputs[4] = set;
				case "right": outputs[5] = set;
			}
		}
		public String getKeyName(int index)
		{
			switch(index)
			{
				case 0: return "a";
				case 1: return "b";
				case 2: return "up";
				case 3: return "down";
				case 4: return "left";
				case 5: return "right";
			}
			return "";
		}
	}
	

	// for drawing
	public class Cell{
		public int x = 0;
		public int y = 0;
		public double value = 0;
	}
}






























