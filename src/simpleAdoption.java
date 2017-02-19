import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.AbstractCollection;

import org.graphstream.graph.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.*;

import javax.swing.JTextArea;
import javax.swing.JFrame;

public class simpleAdoption {
  static int Yt = 0; // Y(t)
  static int intAdoptionCount = 0;
  static int extAdoptionCount = 0;
  static int t = 0; 
  static boolean internalAdoptionHappen = false;
	
  // go through simple adoption process
  public static Graph adopt(Graph g, double p, int graphSize, int sleepTime, 
                            FileWriter timestepfw, FileWriter regressionAnalysisfw) throws IOException {
    g.addAttribute("ui.stylesheet", stylesheet);
    Yt = 0; // Y(t)
    intAdoptionCount = 0;
    extAdoptionCount = 0;
    t = 0; 
    internalAdoptionHappen = false;

    int[] totalAdopters = new int[10*graphSize];
    int[] extAdopters = new int[10*graphSize];
    int[] intAdopters = new int[10*graphSize];

    JTextArea textArea = GUI.makeTimestepDataGUI();    

    do {
      internalAdoptionHappen = false; 

      totalAdopters[t] = Yt;
      extAdopters[t] = extAdoptionCount;
      intAdopters[t] = intAdoptionCount;

      // print timestep data to timestep data GUI
      textArea.append("t: " +t+ "\t Y(t): " +Yt+ "\t No. External Adoptions: " +extAdoptionCount+ "\t No. Internal Adoptions: " + intAdoptionCount +"\n");

      if (Yt == 0) {
        g = initAdoption(g, p, sleepTime);
      }
      g = internalAdoption(g, p, sleepTime);
      if (!internalAdoptionHappen) {       			
        g = externalAdoption(g, p, sleepTime);			
      }

      //exportCSV files
      timestepfw.write(t + "," + Yt + "," + extAdoptionCount + "," + intAdoptionCount + "\n");
      regressionAnalysisfw.write(t + "," + Yt+"\n");
      t++;
      } while (Yt < graphSize);
      
      timestepfw.close();
      regressionAnalysisfw.close();

      LineChart lineChart = new LineChart("Plot - adoption over time", "Number of adoptions over time", 
                                          totalAdopters, extAdopters, intAdopters, t);
      lineChart.setSize(600,350);
      lineChart.show();

      textArea.append("Finished\n");
      return g;
  }

  // adopt the first few nodes externally
  public static Graph initAdoption(Graph g, double p, int sleepTime) {
    for (Node n:g) {
      if (Math.random() < p) {
        n.setAttribute("adopted");
        n.setAttribute("ui.class", "adopted");
	sleep(sleepTime);
   	Yt++;
   	extAdoptionCount++;
      }
    }
    return g;
  }
    
  // method to make all neighbours of adopted nodes adopted
  private static Graph internalAdoption(Graph g, double p, int sleepTime) {
    for (Node n:g) {
      // if node has adopted, getNeighbors of node
      if (!n.hasAttribute("adopted")) {
        Iterator<? extends Edge> edgesOfNodeN = n.getEdgeIterator();
    	List<Node> neighbors = new ArrayList<Node>();
    	while (edgesOfNodeN.hasNext()) {
    	  Edge nextEdge = edgesOfNodeN.next();
    	  Node thisNeighbor = nextEdge.getOpposite(n);
    	  if (!neighbors.contains(thisNeighbor)) {
    	    neighbors.add(thisNeighbor);
    	  }
    	}
    	
        // assign all neighbors with adopted attribute
    	for (Node neighbor : neighbors) {
    	  if (!neighbor.hasAttribute("adopted")) {
    	    neighbor.setAttribute("adopted"); 
    	    neighbor.setAttribute("ui.class", "adopted");
	    sleep(sleepTime);
            Yt++;
    	    intAdoptionCount++;
    	    internalAdoptionHappen = true;
          }
        } 
      }
    }
    return g;
  }

  private static Graph externalAdoption(Graph g, double p, int sleepTime) {
    for (Node n:g) {
      if (!n.hasAttribute("adopted")) {
        if (Math.random() < p) {
    	  n.setAttribute("adopted");
    	  n.setAttribute("ui.class", "adopted");
	  sleep(sleepTime);
    	  extAdoptionCount++;
    	}
      }
    }
    return g;
  } 

  protected static void sleep(int sleepTime) {
    try {
      Thread.sleep(sleepTime); 
      //Thread.currentThread().interrupt(); 
    } catch (Exception e) {}
  }

  protected static String stylesheet =
    "node {" +
    "	fill-color: navy;" +
    "}" +
    "node.adopted {" +
    "	fill-color: orange;" +
    "}";      
}
