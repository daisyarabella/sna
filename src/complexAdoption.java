import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import org.graphstream.graph.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.*;

import javax.swing.JTextArea;
import javax.swing.JFrame;

public class complexAdoption {
  static int Yt = 0; // Y(t)
  static int intAdoptionCount = 0;
  static int extAdoptionCount = 0;
  static int t = 0; 
  static int adoptionNotHappenStreak = 0;
  static boolean adoptionHappen;
	
  // go through complex adoption process
  public static Graph adopt(Graph g, double p, int graphSize, int sleepTime, 
                            FileWriter timestepfw, FileWriter regressionAnalysisfw,
                            int decrements, int neighborThreshold) throws IOException {
    g.addAttribute("ui.stylesheet", stylesheet);
    JTextArea textArea = GUI.makeTimestepDataGUI();

    int[] totalAdopters = new int[10*graphSize];
    int[] extAdopters = new int[10*graphSize];
    int[] intAdopters = new int[10*graphSize];

    double pDecrementValue = p/decrements;
    double pDecrementer = p;

    do {
      adoptionHappen = false;
      
      totalAdopters[t] = Yt;
      extAdopters[t] = extAdoptionCount;
      intAdopters[t] = intAdoptionCount;

      // print timestep data to timestep data GUI
      textArea.append("t: " +t+ "\t Y(t): " +Yt+ "\t No. External Adoptions: " +extAdoptionCount+ "\t No. Internal Adoptions: " + intAdoptionCount +"\n");
      if (Yt == 0) {
        g = externalAdoption(g, pDecrementer, sleepTime);
        pDecrementer -= pDecrementValue;
      }
      else {
        if (pDecrementer > 0) {
          g = externalAdoption(g, pDecrementer, sleepTime);
          pDecrementer -= pDecrementValue;
          g = internalAdoption(g, sleepTime, neighborThreshold);
        }
        else {
          g = internalAdoption(g, sleepTime, neighborThreshold);
        }
      }
 
      //exportCSV files
      timestepfw.write(t + "," + Yt + "," + extAdoptionCount + "," + intAdoptionCount + "\n");
      regressionAnalysisfw.write(t + "," + Yt+"\n");      
      t++;
      if (!adoptionHappen) {
        adoptionNotHappenStreak++;
      }
      else {
        adoptionNotHappenStreak = 0;
      }
    } while (adoptionNotHappenStreak < 3);

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
  public static Graph externalAdoption(Graph g, double p, int sleepTime) {
    for (Node n:g) {
      if (Math.random() < p) {
        n.setAttribute("adopted");
        n.setAttribute("ui.class", "adopted");
        adoptionHappen = true;
	sleep(sleepTime);
   	Yt++;
   	extAdoptionCount++;
      }
    }
    return g;
  }
   
  // method to make all neighbors of adopted nodes adopted
  private static Graph internalAdoption(Graph g, int sleepTime, int neighborThreshold) {
    for (Node n:g) {
      int adoptedNeighborCount = 0;
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
    	
    	  if (thisNeighbor.hasAttribute("adopted")) {
            adoptedNeighborCount++;
          }
        }
      }
        
      if (adoptedNeighborCount >= neighborThreshold) {
        n.setAttribute("adopted"); 
    	n.setAttribute("ui.class", "adopted");
        adoptionHappen = true;
	sleep(sleepTime);
        Yt++;
    	intAdoptionCount++;
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
