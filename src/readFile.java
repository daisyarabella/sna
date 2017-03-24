// File to read real data and draw and display the graph it represents
// Not used in final project as did not have as not much real product diffusion data
// with network data is readily available

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

public class readFile {
  static int graphSize = 0;
  static int lineNo = 0;
  static int edgeNo = 0;
  static int[] sourceNodes;
  static int[] targetNodes;
  static int edgeID = 0;
    
  public static void readFile() {
    CSVReader dataReader = null;
    CSVReader lineCounter = null;
    
    try {
      dataReader = new CSVReader(new FileReader("../output/data/realData.csv"),',', '\'', 1);
      lineCounter = new CSVReader(new FileReader("../output/data/realData.csv"));
      String[] nextLine;
      
      while ((nextLine = lineCounter.readNext()) != null) {
        lineNo++;
      }

      graphSize =  38;
      
      sourceNodes = new int[lineNo-1];
      targetNodes = new int[lineNo-1];	
      
      while ((nextLine = dataReader.readNext()) != null) {
        int thisSourceNode = Integer.parseInt(nextLine[0]);
        int thisTargetNode = Integer.parseInt(nextLine[1]);        
 
        sourceNodes[edgeNo] = thisSourceNode;
        targetNodes[edgeNo] = thisTargetNode;
        edgeNo++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static Graph createGraph(Graph g, int graphSize, int[] sourceNodes, int[] targetNodes) {
    g = addNodes(g, graphSize);
    g = formEdges(g, sourceNodes, targetNodes);
    return g;
  } 

  private static Graph addNodes(Graph g, int graphSize) {
    for (int newNodeIndex=1; newNodeIndex<=graphSize; newNodeIndex++) {
      Node n = g.addNode(Integer.toString(newNodeIndex));
    }  
    return g;
  }
    
  private static Graph formEdges(Graph g, int[] sourceNodes, int[] targetNodes) {
    for (int position=0; position<sourceNodes.length; position++) {
     	  g.addEdge("Edge"+Integer.toString(edgeID), sourceNodes[position], targetNodes[position]);
     	  edgeID++;
    }
    return g;
  }
  
    public static void main(String[] args) {
    readFile();
    Graph graph = new SingleGraph("Read Graph");
    graph = createGraph(graph, graphSize, sourceNodes, targetNodes);
    Viewer viewer = graph.display();
  }
}
