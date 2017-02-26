import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class bernoulliPA {
    static int currentGraphSize = 0;
    static int edgeID = 1;
    static int edgeCount = 0;

    public static Graph createGraph(Graph g, int graphSize) {
    	g = addAllNodes(g, graphSize); 
    	return g;
    }
    
    private static Graph addAllNodes(Graph g, int graphSize) {
      for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
        g.addNode(Integer.toString(newNodeIndex));
      }  
      for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
       g = formEdges(g, newNodeIndex, graphSize);
      }
      return g;
    }
    
    private static Graph formEdges(Graph g, int newNodeIndex, int graphSize) {
      for (int otherNodeIndex=0; otherNodeIndex<newNodeIndex; otherNodeIndex++) {
        if (edgeCount == 0) {
       	  g.addEdge("Edge"+Integer.toString(edgeCount), Integer.toString(newNodeIndex), Integer.toString(otherNodeIndex));
          edgeCount++;
        }
        else {
          Node otherNode = g.getNode(otherNodeIndex);
          double degreeOfOtherNode = (double) otherNode.getDegree();
          double twiceEdgeCount = edgeCount*2;
          double edgeProb = (degreeOfOtherNode/twiceEdgeCount); //as required by Pref. Att. algorithm
          if (Math.random() < edgeProb) {
            g.addEdge("Edge"+Integer.toString(edgeCount), Integer.toString(newNodeIndex), Integer.toString(otherNodeIndex));
            edgeCount++;
          }
        }
      }
       return g;
    }
}
