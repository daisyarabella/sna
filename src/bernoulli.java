import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

// Class to create a randomly generated Bernoulli graph
public class bernoulli {

    // Create the graph by adding specified amount of nodes
    public static Graph createGraph(Graph g, int graphSize, double edgeProb) {
    	g = addAllNodes(g, graphSize, edgeProb); 
    	return g;
    }
    
    // Add nodes to the graph, with node number as id
    private static Graph addAllNodes(Graph g, int graphSize, double edgeProb) {
      for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
        g.addNode(Integer.toString(newNodeIndex));
      }  
      // After adding nodes, form edges
      for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
       g = formEdges(g, newNodeIndex, graphSize, edgeProb);
      }
      return g;
    }
    
    // Iterate every possible pair of edges
    //Use fixed probability to determine whether to form an edge between them
    static int edgeID = 1; // Count for edge ids
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
