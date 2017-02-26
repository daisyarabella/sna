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
	
  // go through complex adoption process
  public static Graph adopt(Graph g, double p, int graphSize, String initAdoptionType, 
                            String adoptionThresholdType, int sleepTime, 
                            FileWriter timestepfw, FileWriter regressionAnalysisfw,
                            int decrements, int neighborThreshold, double neighborThresholdAsPercentage) throws IOException {
    g.addAttribute("ui.stylesheet", stylesheet);
    JTextArea textArea = GUI.createTimestepDataGUI();

    int[] totalAdopters = new int[10*graphSize];
    int[] extAdopters = new int[10*graphSize];
    int[] intAdopters = new int[10*graphSize];

    double pDecrementValue = p/decrements;
    double pDecrementer = p;
    int neighborPercentOfAllNodes = (int) Math.round(neighborThresholdAsPercentage*g.getNodeCount());

    do {
      adoptionHappen = false;
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
        pDecrementer -= pDecrementValue;
      }
      else {
        if (pDecrementer > 0) {
          g = randomExternalAdoption(g, pDecrementer, sleepTime);
          pDecrementer -= pDecrementValue;
          switch (adoptionThresholdType) {
     	    case "x neighbors Adopted": g = neighborInternalAdoption(g, sleepTime, neighborThreshold);
            break;
            case "No. neighbors adopted >= x% total nodes": g = neighborInternalAdoption(g, sleepTime, neighborPercentOfAllNodes);
            break;
            case "Neighbor degree > average degree distribution": g = degreeInternalAdoption(g, sleepTime);
            break;
          }
        }
        else {
          switch (adoptionThresholdType) {
     	    case "x neighbors Adopted": g = neighborInternalAdoption(g, sleepTime, neighborThreshold);
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
  
  // adopt nodes externally by random
  public static Graph randomExternalAdoption(Graph g, double p, int sleepTime) {
    for (Node n:g) {
      if (Math.random() < p) {
        n.setAttribute("adopted");
        n.setAttribute("ui.class", "adopted");
        adoptionHappen = true;
	sleep(sleepTime);
   	extAdoptionCount++;
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
      adoptionHappen = true;
      sleep(sleepTime);
      extAdoptionCount++;
    }
    return g;
  }
   
  // method to make neighbors of adopted nodes adopted under a threshold
  private static Graph neighborInternalAdoption(Graph g, int sleepTime, int threshold) {
    for (Node n:g) {
      Iterator<? extends Edge> edgesOfNodeN = n.getEdgeIterator();
      List<Node> neighbors = new ArrayList<Node>();
      int adoptedNeighborCount = 0;
      // if node has adopted, getNeighbors of node
      if (!n.hasAttribute("adopted")) {
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
      if (adoptedNeighborCount >= threshold) {
        n.setAttribute("adopted"); 
    	n.setAttribute("ui.class", "adopted");
        adoptionHappen = true;
	sleep(sleepTime);
    	intAdoptionCount++;
      }
    }
    return g;
  }

  // method to make neighbors of adopted nodes adopted if have degree > average of all node degrees
  private static Graph degreeInternalAdoption(Graph g, int sleepTime) {
    Toolkit tk = new Toolkit();
    double averageDegree = tk.averageDegree(g);
    for (Node n:g) {
      Iterator<? extends Edge> edgesOfNodeN = n.getEdgeIterator();
      List<Node> neighbors = new ArrayList<Node>();
      // if node has adopted, getNeighbors of node
      if (!n.hasAttribute("adopted")) {
    	while (edgesOfNodeN.hasNext()) {
    	  Edge nextEdge = edgesOfNodeN.next();
    	  Node thisNeighbor = nextEdge.getOpposite(n);
    	  if (!neighbors.contains(thisNeighbor)) {
    	    neighbors.add(thisNeighbor);
    	  }
        }
      }
      for (Node neighbor:neighbors) {
        if (!neighbor.hasAttribute("adopted") && neighbor.getDegree() > averageDegree) {
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
