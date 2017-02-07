import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;

public class test {
  public static void main(String[] args) {
    Graph graph = new SingleGraph("random euclidean");
    Generator gen = new RandomEuclideanGenerator();
    gen.addSink(graph);
    gen.begin();
    for(int i=1; i<12; i++) {
            gen.nextEvents();
    }
    gen.end();
    graph.display(false);
  }
}
