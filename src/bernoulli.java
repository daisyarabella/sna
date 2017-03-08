import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

// Class to create a randomly generated graph where there is a set probability that an edge is drawn between any two nodes
public class bernoulli {

    // Create the graph by adding specified amount of nodes
    public static Graph createGraph(Graph g, int graphSize, double edgeProb) {
    	g = addAllNodes(g, graphSize, edgeProb); 
    	return g;
    }
    
    // After adding nodes, form edges
    private static Graph addAllNodes(Graph g, int graphSize, double edgeProb) {
      for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
        g.addNode(Integer.toString(newNodeIndex));
      }  
      for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
       g = formEdges(g, newNodeIndex, graphSize, edgeProb);
      }
      return g;
    }
    
    // Iterate between every possible pair of edges and decide whether to form an edge between them. Set the edge id's to be a simple count
    static int edgeID = 1;
    private static Graph formEdges(Graph g, int newNodeIndex, int graphSize, double edgeProb) {
      for (int otherNodeIndex=newNodeIndex+1; otherNodeIndex<graphSize; otherNodeIndex++) {
        if (Math.random() < edgeProb) {
       	  g.addEdge("Edge"+Integer.toString(edgeID), Integer.toString(newNodeIndex), Integer.toString(otherNodeIndex));
          edgeID++;
        }
      }
       return g;
    }
}
