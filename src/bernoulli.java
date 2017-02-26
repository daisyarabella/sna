import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class bernoulli {
    public static Graph createGraph(Graph g, int graphSize, double edgeProb) {
    	g = addAllNodes(g, graphSize, edgeProb); 
    	return g;
    }
    
    private static Graph addAllNodes(Graph g, int graphSize, double edgeProb) {
      for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
        g.addNode(Integer.toString(newNodeIndex));
      }  
      for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
       g = formEdges(g, newNodeIndex, graphSize, edgeProb);
      }
      return g;
    }
    
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
