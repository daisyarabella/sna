import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class graphProcessor
{
    public static void main(String args[]) throws IOException
    {
    	File timestepData = new File("output/timestepData.csv"); // file to record time step data for plotting simple line graph
    	File linearEqs = new File("output/linearEqs.csv"); // file to be read by Python to calculate p and q. Includes S(t+1) data, and coefficients of a, b, c
    	FileWriter timestepfw = new FileWriter(timestepData.getAbsoluteFile());
    	FileWriter linearfw = new FileWriter(linearEqs.getAbsoluteFile());
    	timestepfw.write("t,Y(t),External Adopters,Internal Adopters\n");
    	linearfw.write("S(t+1),aCo,bCo,cCo\n");
    	double edgeProb = 0;
    	int maxLinks = 0;
    	
    	Scanner scanner = new Scanner(System.in);
      System.out.println("How many nodes?");
      int graphSize = scanner.nextInt();
      System.out.println("Set coefficient of innovation (p):");
      double p = scanner.nextDouble();

      System.out.println("Select graph generator type - Bernoulli (1) or Preferential Attachment (2):");
      int generatorType = scanner.nextInt();
      if (generatorType == 1) {
        System.out.println("Enter edge probability:");
        edgeProb = scanner.nextDouble();
      }
      if (generatorType == 2) {
        System.out.println("Enter max links per step:");
        maxLinks = scanner.nextInt();
      }
         
    	//create a graph
      Graph graph = generateGraph(graphSize, generatorType, edgeProb, maxLinks); 
      
      // adopt this graph  
      int adoptionMethod;
      System.out.println("Select an adoption method - Simple (1) or Complex (2):");
      adoptionMethod = scanner.nextInt();
      adoptGraph(graph, adoptionMethod, p, graphSize, timestepfw, linearfw);
    }
        
    //method to create adopt graphs
    private static Graph adoptGraph(Graph g, int adoptionMethod, 
        	double p, int graphSize, FileWriter timestepfw, FileWriter linearfw) throws IOException {       
      switch (adoptionMethod) {
     	case 1: g = simpleAdoption.adopt(g, p, graphSize, timestepfw, linearfw);
     	//case 2: g = complexAdoption.initAdoption(graph, p);
        }
        return g;
    }   	
    
    // method to create initial random graphs 
    private static Graph generateGraph(int graphSize, int generatorType, double edgeProb, int maxLinks) {     
        Graph g = new SingleGraph("Random Graph");
        switch (generatorType) {
        	case 1: g = bernoulli.createGraph(g, graphSize, edgeProb);
        	break;
        	case 2: g = preferentialAttachment.createGraph(g, graphSize, maxLinks);
        	break;
        }
        return g;
    }  
}
