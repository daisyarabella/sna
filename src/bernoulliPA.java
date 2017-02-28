import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.generator.*;

public class bernoulliPA {
  static int currentGraphSize = 0;
  static int edgeID = 1;
  static int edgeCount = 0;

  public static Graph createGraph(Graph g, int graphSize, int maxLinks, double edgeProb) {
    g = preferentialAttachment.createGraph(g, graphSize, maxLinks);
    g = addBernoulliEdges(g, graphSize, edgeProb);
    return g;
  }

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
