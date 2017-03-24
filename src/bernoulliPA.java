import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.generator.*;

public class bernoulliPA {

  // Create a Preferential Attachment graph,
  // then form extra random edges on top of the PA graph in a Bernoulli manner
  public static Graph createGraph(Graph g, int graphSize, int maxLinks, double edgeProb) {
    g = preferentialAttachment.createGraph(g, graphSize, maxLinks);
    g = addBernoulliEdges(g, graphSize, edgeProb);
    return g;
  }
  
  // Iterate every possible pair of edges
  //Use fixed probability to determine whether to form an edge between them
  static int edgeID = 1; // Count for edge ids
  private static Graph addBernoulliEdges(Graph g, int graphSize, double edgeProb) {
    for (int firstNodeIndex=0; firstNodeIndex<graphSize; firstNodeIndex++) {
      for (int otherNodeIndex=firstNodeIndex+1; otherNodeIndex<graphSize; otherNodeIndex++) {
        Node firstNode = g.getNode(Integer.toString(firstNodeIndex));
        String otherNode = Integer.toString(otherNodeIndex);
        if (!firstNode.hasEdgeBetween(otherNode) && Math.random() < edgeProb ) {
       	  g.addEdge("Edge"+Integer.toString(edgeID), Integer.toString(firstNodeIndex), Integer.toString(otherNodeIndex));
          edgeID++;
        }
      }
    }
    return g;
  }
}
