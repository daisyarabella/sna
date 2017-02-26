import org.graphstream.algorithm.generator.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class preferentialAttachment {
  public static Graph createGraph(Graph g, int graphSize, int maxLinks) {
    Generator gen = new BarabasiAlbertGenerator(maxLinks);
    gen.addSink(g);
    gen.begin();
    for (int newNodeIndex=1;newNodeIndex<graphSize-1;newNodeIndex++) {
      gen.nextEvents();
    }
    gen.end();
    return g;
  }
}

	
