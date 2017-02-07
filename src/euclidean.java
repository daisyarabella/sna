import org.graphstream.algorithm.generator.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class euclidean {
  public static Graph createGraph(Graph g, int graphSize) {
    Generator gen = new RandomEuclideanGenerator();
    gen.addSink(g);
    gen.begin();
    for (int newNodeIndex=1;newNodeIndex<graphSize-1;newNodeIndex++) {
      gen.nextEvents();
    }
    gen.end();
    return g;
  }
}
