import org.graphstream.algorithm.generator.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class squareGrids {
  public static Graph createGraph(Graph g, int graphSize) {
    Generator gen = new GridGenerator();
    gen.addSink(g);
    gen.begin();
    for (int newNodeIndex=1;newNodeIndex<graphSize;newNodeIndex++) {
      gen.nextEvents();
    }
    gen.end();
    return g;
  }
}
