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

public class complexAdoption {
  static int Yt = 0; // Y(t)
  static int intAdoptionCount = 0;
  static int extAdoptionCount = 0;
  static int t = 0; 
  static int adoptionNotHappenStreak = 0;
  static boolean adoptionHappen;
	
  // Start the complex adoption process
  public static Graph adopt(Graph g, double p, int graphSize, String initAdoptionType, 
                            String adoptionThresholdType, int sleepTime, 
                            FileWriter timestepfw, FileWriter regressionAnalysisfw,
                            int decrements, int neighborThreshold, double neighborThresholdAsPercentage) throws IOException {
    g.addAttribute("ui.stylesheet", stylesheet);
    
    // Create textArea which displays timestepData
    JTextArea textArea = GUI.createTimestepDataGUI();

    int[] totalAdopters = new int[10*graphSize];
    int[] extAdopters = new int[10*graphSize];
    int[] intAdopters = new int[10*graphSize];

    // Split p into x decrements so can decrement p at a steady rate 
    double pDecrementValue = p/decrements;
    double pDecrementer = p;
    boolean pDecrementerFinished = false;
    
    // Calculate x% all nodes to nearest whole number
    int neighborPercentOfAllNodes = (int) Math.round(neighborThresholdAsPercentage*g.getNodeCount());

    do {
      adoptionHappen = false;
      Yt = extAdoptionCount+intAdoptionCount;
      
      totalAdopters[t] = Yt;
      extAdopters[t] = extAdoptionCount;
      intAdopters[t] = intAdoptionCount;

      // Print timestep data to timestep data GUI
      textArea.append("t: " +t+ "\t Y(t): " +Yt+ "\t No. External Adoptions: " +extAdoptionCount+ "\t No. Internal Adoptions: " + intAdoptionCount +"\n");
      
      // Impose initial adoption externally - either randomly selected nodes or most popular nodes
      if (Yt == 0) {
        switch (initAdoptionType) {
     	  case "Random": g = randomExternalAdoption(g, p, sleepTime);
          break;
          case "p Popular Nodes": g = popularExternalAdoption(g, p, sleepTime);
          break;
        }
        pDecrementer -= pDecrementValue;
      }
      // If isn't initial adoption, either combine external and internal adoption, or just do internal adoption if external decrementer has finished (below 0)
      else {
        if (pDecrementer > 0) {
          g = randomExternalAdoption(g, pDecrementer, sleepTime);
          pDecrementer -= pDecrementValue;
          // choose threshold type
          switch (adoptionThresholdType) {
     	    case "x neighbors adopted": g = neighborInternalAdoption(g, sleepTime, neighborThreshold);
            break;
            case "No. neighbors adopted >= x% total nodes": g = neighborInternalAdoption(g, sleepTime, neighborPercentOfAllNodes);
            break;
            case "Neighbor degree > average degree distribution": g = degreeInternalAdoption(g, sleepTime);
            break;
          }
        }
        else {
          pDecrementerFinished = true;
          switch (adoptionThresholdType) {
     	    case "x neighbors adopted": g = neighborInternalAdoption(g, sleepTime, neighborThreshold);
            break;
            case "No. neighbors adopted >= x% total nodes": g = neighborInternalAdoption(g, sleepTime, neighborPercentOfAllNodes);
            break;
            case "Neighbor degree > average degree distribution": g = degreeInternalAdoption(g, sleepTime);
            break;
          }
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
    } while (!pDecrementerFinished || adoptionNotHappenStreak<2);

    timestepfw.close();
    regressionAnalysisfw.close();

    LineChart lineChart = new LineChart("Plot - adoption over time", "Number of adoptions over time", 
                                        totalAdopters, extAdopters, intAdopters, t);
    lineChart.setSize(600,350);
    lineChart.show();

    textArea.append("Finished\n");

    return g;
  }
  
  // Adopt random nodes externally 
  public static Graph randomExternalAdoption(Graph g, double p, int sleepTime) {
    for (Node n:g) {
      // If random adoption has been selected as initial adoption type
      if (!n.hasAttribute("adopted") && Math.random() < p) {
        n.setAttribute("adopted");
        n.setAttribute("ui.class", "adopted");
        adoptionHappen = true;
	      sleep(sleepTime);
   	    extAdoptionCount++;
      }
    }
    return g;
  }
   
  // Adopt a set percentage of most popular nodes externally 
  public static Graph popularExternalAdoption(Graph g, double p, int sleepTime) {
    Toolkit tk = new Toolkit();
    
    // Arrange the nodes in order of descending degree
    ArrayList<Node> nodesByDescDegree = tk.degreeMap(g);
    
    // Take percentage of most popular nodes and adopt them
    int pPercentOfAllNodes = (int) Math.round(p*g.getNodeCount());
    for (int i=0; i<pPercentOfAllNodes; i++) {
      Node nextNode = nodesByDescDegree.get(i);
      if (!nextNode.hasAttribute("adopted")) {
        nextNode.setAttribute("adopted");
        nextNode.setAttribute("ui.class", "adopted");
        adoptionHappen = true;
        sleep(sleepTime);
        extAdoptionCount++;
      }
    }
    return g;
  }
   
  // Method to make neighbors of adopted nodes adopted under a threshold
  private static Graph neighborInternalAdoption(Graph g, int sleepTime, int threshold) {
    for (Node n:g) {
      Iterator<? extends Edge> edgesOfNodeN = n.getEdgeIterator();
      List<Node> neighbors = new ArrayList<Node>();
      int adoptedNeighborCount = 0;
      
      // If node is adopted, getNeighbors of node
      if (!n.hasAttribute("adopted")) {
    	while (edgesOfNodeN.hasNext()) {
    	  Edge nextEdge = edgesOfNodeN.next();
    	  Node thisNeighbor = nextEdge.getOpposite(n);
    	  if (!neighbors.contains(thisNeighbor)) {
    	    neighbors.add(thisNeighbor);
    	  }
    	  // Keep count of how many neighbors are adopted
    	  if (thisNeighbor.hasAttribute("adopted")) {
            adoptedNeighborCount++;
          }
        }
      }
      
      // If a node's number of adopted neighbors exceeds threshold and isn't already adopted, adopt the node
      if (adoptedNeighborCount >= threshold && !n.hasAttribute("adopted")) {
        n.setAttribute("adopted"); 
    	  n.setAttribute("ui.class", "adopted");
        adoptionHappen = true;
	      sleep(sleepTime);
    	  intAdoptionCount++;
      }
    }
    return g;
  }

  // Make a node's neighbor adopted if it has degree > average of all node degrees
  private static Graph degreeInternalAdoption(Graph g, int sleepTime) {
    Toolkit tk = new Toolkit();
    double averageDegree = tk.averageDegree(g);
    for (Node n:g) {
      Iterator<? extends Edge> edgesOfNodeN = n.getEdgeIterator();
      List<Node> neighbors = new ArrayList<Node>();
      
      // If node is adopted, getNeighbors of node
      if (n.hasAttribute("adopted")) {
    	  while (edgesOfNodeN.hasNext()) {
    	    Edge nextEdge = edgesOfNodeN.next();
    	    Node thisNeighbor = nextEdge.getOpposite(n);
    	    if (!neighbors.contains(thisNeighbor)) {
    	      neighbors.add(thisNeighbor);
    	    }
        }
      }
      
      // For each neighbor of this node, if the neighbor is not adopted and 
      //the neighbor's degree exceeds average degree distribution, adopt the neighbor
      for (Node neighbor:neighbors) {
        if (!neighbor.hasAttribute("adopted") && neighbor.getDegree()>averageDegree) {
          neighbor.setAttribute("adopted"); 
      	  neighbor.setAttribute("ui.class", "adopted");
          adoptionHappen = true;
	        sleep(sleepTime);
    	    intAdoptionCount++;
        }
      }
    }
    return g;
  }

  // Speed at which graph dynamically updates to show adoption process happening
  protected static void sleep(int sleepTime) {
    try {
      Thread.sleep(sleepTime); 
    } catch (Exception e) {}
  }

  // Non adopted nodes are blue, adopted nodes are orange
  protected static String stylesheet =
    "node {" +
    "	fill-color: navy;" +
    "}" +
    "node.adopted {" +
    "	fill-color: orange;" +
    "}";      
}
