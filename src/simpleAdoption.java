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

    // Condition to check if should externally adopt to continue process to completion
    internalAdoptionHappen = false;

    // Create textArea so user can see timestepData
    JTextArea textArea = GUI.createTimestepDataGUI();       
        
    // Created to store timestep data for writing to textArea
    int[] totalAdopters = new int[10*graphSize];
    int[] extAdopters = new int[10*graphSize];
    int[] intAdopters = new int[10*graphSize];
    
    do {
      internalAdoptionHappen = false;
      Yt = extAdoptionCount+intAdoptionCount;

      // Update values for each run
      totalAdopters[t] = Yt;
      extAdopters[t] = extAdoptionCount;
      intAdopters[t] = intAdoptionCount;

      // Print timestep data to timestep data GUI
      textArea.append("t: " +t+ "\t Y(t): " +Yt+ "\t No. External Adoptions: " +extAdoptionCount+ "\t No. Internal Adoptions: " + intAdoptionCount +"\n");

      // Choose between initial adoption types 
      if (Yt == 0) {
        switch (initAdoptionType) {
     	  case "Random": g = randomExternalAdoption(g, p, sleepTime);
          break;
        
        case "p Popular Nodes": g = popularExternalAdoption(g, p, sleepTime);
          break;
          
        default: g = popularExternalAdoption(g, p, sleepTime);
          break;
        }
      }
      
      // If this isn't initial adoption, do internal adoption
      g = internalAdoption(g, p, sleepTime);

      for (Node n:g) {
        if (n.hasAttribute("just_adopted")) {
          n.removeAttribute("just_adopted");
        }
      }
      
      // Do external adoption if internal adoption didn't happen to ensure process runs until every node in graph is adopted
      if (!internalAdoptionHappen) {       			
        g = randomExternalAdoption(g, p, sleepTime);			
      }
      
      // Export CSV files
      timestepfw.write(t + "," + Yt + "," + extAdoptionCount + "," + intAdoptionCount + "\n");
      regressionAnalysisfw.write(Yt+"\n");
      t++;
      } while (Yt < graphSize); // Only terminate adoption process when every node has been adopted
      
      timestepfw.close();
      regressionAnalysisfw.close();

      // Plot the chart for timestep data
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
      if (Yt == 0) {
        if (Math.random() < p) {
          n.setAttribute("adopted");
          n.setAttribute("ui.class", "adopted");
	        sleep(sleepTime);
   	      extAdoptionCount++;
        }
      }
      // To run if internal adoption has not occurred
      else {
        if (!n.hasAttribute("adopted")) {
          if (Math.random() < p) {
    	    n.setAttribute("adopted");
            n.setAttribute("just_adopted");
    	    n.setAttribute("ui.class", "adopted");
	    sleep(sleepTime);
    	    extAdoptionCount++;
    	  }
        }
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
      nodesByDescDegree.get(i).setAttribute("adopted");
      nodesByDescDegree.get(i).setAttribute("ui.class", "adopted");
      sleep(sleepTime);
      extAdoptionCount++;
    }
    return g;
  }
    
  // Adopt ALL neighbours of adopted nodes
  private static Graph internalAdoption(Graph g, double p, int sleepTime) {
    for (Node n:g) {
      // If node is adopted, getNeighbors of node
      if (n.hasAttribute("adopted") & !n.hasAttribute("just_adopted")) {
        Iterator<? extends Edge> edgesOfNodeN = n.getEdgeIterator();
    	List<Node> neighbors = new ArrayList<Node>();
    	while (edgesOfNodeN.hasNext()) {
    	  Edge nextEdge = edgesOfNodeN.next();
    	  Node thisNeighbor = nextEdge.getOpposite(n);
    	  if (!neighbors.contains(thisNeighbor)) {
    	    neighbors.add(thisNeighbor);
    	  }
    	}
    	
        // Adopt neighbors of the node
    	for (Node neighbor : neighbors) {
    	  // Ensure not adopting some nodes twice
    	  if (!neighbor.hasAttribute("adopted")) {
    	    neighbor.setAttribute("adopted"); 
            neighbor.setAttribute("just_adopted"); 
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
