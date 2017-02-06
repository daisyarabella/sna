import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.AbstractCollection;

import org.graphstream.graph.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.*;


public class simpleAdoption {
	static int Yt = 0; // Y(t)
 	static int Ytadd1 = 0; // Y(t+1)
 	static int intAdoptionCount = 0;
 	static int extAdoptionCount = 0;
 	static int t = 0; 
 	static boolean internalAdoptionHappen = false;
 	
 	public static Graph adopt(Graph g, double p, int graphSize, FileWriter timestepfw, FileWriter linearfw) throws IOException {
 	g.addAttribute("ui.stylesheet", "node.adopted {fill-color:red;}");
 	do {
     	internalAdoptionHappen = false;
     	if (Yt == 0) {
     		g = initAdoption(g, p);
     	}
    	g = internalAdoption(g,p);
    	if (!internalAdoptionHappen) {       			
    		g = externalAdoption(g,p);			
    	}
      //exportCSV files
      timestepfw.write(t + "," + Yt + "," + extAdoptionCount + "," + intAdoptionCount + "\n");
      linearfw.write(Ytadd1-Yt + "," + 1 + "," + Yt + "," + Yt*Yt + "\n");
      System.out.println("t: " +t+ "\t Y(t+1): " +Ytadd1+ "\t Y(t): " +Yt);
      t++;
      g.display();
      } while (Yt < graphSize);
     	  timestepfw.close();
     	  linearfw.close();
     	  System.out.println("External adoptions: " + extAdoptionCount + ", Internal adoptions: " +intAdoptionCount);
     	System.out.println("Finished");
		return g;
	}

	public static Graph initAdoption(Graph g, double p) {
    for (Node n:g) {
    	if (Math.random() < p) {
       	n.setAttribute("adopted");
       	n.setAttribute("ui.class", "adopted");
   			Yt++;
   			Ytadd1++;
   			extAdoptionCount++;
   		}
   	}
   	return g;
  }
    
    // method to make all neighbours of adopted nodes adopted
    private static Graph internalAdoption(Graph g, double p) {
    	Yt = Ytadd1;
    	for (Node n:g) {
    		// if node has adopted, getNeighbors of node
    		if (!n.hasAttribute("adopted")) {
    			Iterator<? extends Edge> edgesOfNodeN = n.getEdgeIterator();
    			List<Node> neighbors = new ArrayList<Node>();
    			while (edgesOfNodeN.hasNext()) {
    			  Edge nextEdge = edgesOfNodeN.next();
    			  Node thisNeighbor = nextEdge.getOpposite(n);
    			  if (!neighbors.contains(thisNeighbor)) {
    			    neighbors.add(thisNeighbor);
    			  }
    			}
    			// assign all neighbors with adopted attribute
    			for (Node neighbor : neighbors) {
    				if (!neighbor.hasAttribute("adopted")) {
    					neighbor.setAttribute("adopted"); 
    					neighbor.setAttribute("ui.class", "adopted");
    					//System.out.println("Node " +neighbor+ "is Adopted internally");
    					Ytadd1++;
    					intAdoptionCount++;
    					internalAdoptionHappen = true;
    				}
    			} 
    		}
    	}
		return g;
    }

    private static Graph externalAdoption(Graph g, double p) {
    	Yt = Ytadd1;
    	for (Node n:g) {
    		if (!n.hasAttribute("adopted")) {
    				if (Math.random() < p) {
    					n.setAttribute("adopted");
    					n.setAttribute("ui.class", "adopted");
    					//System.out.println("Node " +n+ "is Adopted externally");
    					Ytadd1++;
    					extAdoptionCount++;
    			}
    		}
    	}
		return g;
    } 
}
