import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.algorithm.Toolkit;

import javax.swing.JTextArea;

public class simpleAdoption {
  static int Yt = 0; // Y(t)
  static int intAdoptionCount = 0;
  static int extAdoptionCount = 0;
  static int t = 0; 
  static boolean internalAdoptionHappen = false;
	
  // go through simple adoption process
  public static Graph adopt(Graph g, double p, int graphSize, String initAdoptionType, int sleepTime, 
                            FileWriter timestepfw, FileWriter regressionAnalysisfw) throws IOException {
    g.addAttribute("ui.stylesheet", stylesheet);
    internalAdoptionHappen = false;

    int[] totalAdopters = new int[10*graphSize];
    int[] extAdopters = new int[10*graphSize];
    int[] intAdopters = new int[10*graphSize];

    JTextArea textArea = GUI.createTimestepDataGUI();   
    
    do {
      internalAdoptionHappen = false;
      Yt = extAdoptionCount+intAdoptionCount;

      totalAdopters[t] = Yt;
      extAdopters[t] = extAdoptionCount;
      intAdopters[t] = intAdoptionCount;

      // print timestep data to timestep data GUI
      textArea.append("t: " +t+ "\t Y(t): " +Yt+ "\t No. External Adoptions: " +extAdoptionCount+ "\t No. Internal Adoptions: " + intAdoptionCount +"\n");

      if (Yt == 0) {
        switch (initAdoptionType) {
     	  case "Random": g = randomExternalAdoption(g, p, sleepTime);
          break;
          case "p Popular Nodes": g = popularExternalAdoption(g, p, sleepTime);
          break;
        }
      }
      g = internalAdoption(g, p, sleepTime);
      if (!internalAdoptionHappen) {       			
        g = randomExternalAdoption(g, p, sleepTime);			
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

  // adopt nodes externally by random
  public static Graph randomExternalAdoption(Graph g, double p, int sleepTime) {
    for (Node n:g) {
      if (Yt == 0) {
        if (Math.random() < p) {
          n.setAttribute("adopted");
          n.setAttribute("ui.class", "adopted");
	        sleep(sleepTime);
   	      extAdoptionCount++;
        }
      }
      else {
        if (!n.hasAttribute("adopted")) {
          if (Math.random() < p) {
    	    n.setAttribute("adopted");
    	    n.setAttribute("ui.class", "adopted");
	        sleep(sleepTime);
    	    extAdoptionCount++;
    	  }
        }
      }
    }
    return g;
  }
   
  // adopt nodes externally by popularity
  public static Graph popularExternalAdoption(Graph g, double p, int sleepTime) {
    Toolkit tk = new Toolkit();
    ArrayList<Node> nodesByDescDegree = tk.degreeMap(g);
    int pPercentOfAllNodes = (int) Math.round(p*g.getNodeCount());
    for (int i=0; i<pPercentOfAllNodes; i++) {
      nodesByDescDegree.get(i).setAttribute("adopted");
      nodesByDescDegree.get(i).setAttribute("ui.class", "adopted");
      sleep(sleepTime);
      extAdoptionCount++;
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
    	    intAdoptionCount++;
    	    internalAdoptionHappen = true;
          }
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
