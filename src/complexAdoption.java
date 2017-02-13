import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
  static int Ytadd1 = 0; // Y(t+1)
  static int intAdoptionCount = 0;
  static int extAdoptionCount = 0;
  static int t = 0; 
  static int halfAdoptedTime = 0;
	
  // go through complex adoption process
  public static Graph adopt(Graph g, double p, int graphSize, int sleepTime, 
                            FileWriter timestepfw, FileWriter linearfw) throws IOException {
    g.addAttribute("ui.stylesheet", stylesheet);

    int[] totalAdopters = new int[10*graphSize];
    int[] extAdopters = new int[10*graphSize];
    int[] intAdopters = new int[10*graphSize];

    JTextArea textArea = GUI.makeTimestepDataGUI();  
    
    // find popular nodes for termination condition
    TreeMap<String,Integer> nodeDegrees = new TreeMap<String,Integer>();
    for (Node n:g) {
      nodeDegrees.put(n.getId(), n.getDegree());
    }
    Set mapData = nodeDegrees.entrySet();
    LinkedHashMap<String,Integer> sortedNodeDegrees = sortSetByValues(mapData);
    HashMap popularNodes = getPopularNodes(0.3,sortedNodeDegrees);
    
    boolean done = false;

    do {
      if (Yt == 0) {
        g = initAdoption(g, p, sleepTime, graphSize);
      }
      g = internalAdoption(g, p, sleepTime, graphSize);
      if ((Yt>=(graphSize/2)) && checkPopularNeighboursAdopted(g,popularNodes,0.3,graphSize)) { 
        done = true;
      }
 
      //exportCSV files
      timestepfw.write(t + "," + Yt + "," + extAdoptionCount + "," + intAdoptionCount + "\n");
      linearfw.write(Ytadd1-Yt + "," + 1 + "," + Yt + "," + Yt*Yt + "\n");
      
      // print timestep data to terminal and timestep data GUI
      textArea.append("t: " +t+ "\t Y(t+1): " +Ytadd1+ "\t Y(t): " +Yt+ "\t No. External Adoptions: " +extAdoptionCount+ "\t No. Internal Adoptions: " + intAdoptionCount +"\n");

      // add the timestep data to int[] arrays for display in plot
      totalAdopters[t] = Yt;
      extAdopters[t] = extAdoptionCount;
      intAdopters[t] = intAdoptionCount;
      t++;
      if (t == (halfAdoptedTime*2)) {
        done = true;
      }
    } while (!done);

    timestepfw.close();
    linearfw.close();

    LineChart lineChart = new LineChart("Plot - adoption over time", "Number of adoptions over time", 
                                        totalAdopters, extAdopters, intAdopters, t);
    lineChart.setSize(600,350);
    lineChart.show();

    textArea.append("Finished\n");

    return g;
  }
  
  private static LinkedHashMap<String,Integer> sortSetByValues(Set mapData) {
    List<Entry<String,Integer>> linkedList = new LinkedList<Entry<String,Integer>>(mapData);

    // Sort list
    Collections.sort(linkedList, new Comparator<Entry<String,Integer>>() {
      @Override
      public int compare(Entry<String,Integer> ele1, Entry<String, Integer> ele2) {
        return ele2.getValue().compareTo(ele1.getValue());
      }
    });
      
    // Storing the list into TreeMap to preserve the order of insertion. 
    LinkedHashMap<String,Integer> sortedNodeDegrees = new LinkedHashMap<String,Integer>();
    for(Entry<String,Integer> entry: linkedList) {
       sortedNodeDegrees.put(entry.getKey(), entry.getValue());
       //System.out.println("Node: " +entry.getKey()+ " Value: " +entry.getValue());
    }
    return sortedNodeDegrees;
  }

  private static HashMap getPopularNodes(double percentageOfNodes, 
                                                           LinkedHashMap<String,Integer> sortedTreeMap) {
    int mapSize = sortedTreeMap.size();
    int numberToTake = (int) Math.floor(mapSize*percentageOfNodes);
    int index = 0;
    HashMap popularNodes = new HashMap();
    for (String key : sortedTreeMap.keySet()) {
      if (index < numberToTake) {
        popularNodes.put(key, 1);
      }
      index++;
    }
    return popularNodes;
  }

  // adopt the first few nodes externally
  public static Graph initAdoption(Graph g, double p, int sleepTime, int graphSize) {
    for (Node n:g) {
      if (Math.random() < p) {
        n.setAttribute("adopted");
        n.setAttribute("ui.class", "adopted");
	sleep(sleepTime);
   	Yt++;
   	Ytadd1++;
   	extAdoptionCount++;
      }
    }
    if (Yt >= (graphSize/2)) {
      halfAdoptedTime = Yt;
    }
    return g;
  }
   
  // method to make all neighbours of adopted nodes adopted
  private static Graph internalAdoption(Graph g, double p, int sleepTime, int graphSize) {
    Yt = Ytadd1;
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
    	    Ytadd1++;
    	    intAdoptionCount++;
          }
        } 
      }
    }
    if (Yt >= (graphSize/2)) {
      halfAdoptedTime = t;
    }
    return g;
  }

  private static boolean checkPopularNeighboursAdopted(Graph g, HashMap<String,Integer> popularNodes, 
                                                       double percentage, int graphSize) {
    boolean allPopularNodeNeighboursAdopted = true;
    int neighboursToBeAdopted = (int) Math.floor(graphSize*percentage);
    for (String nodeIndex : popularNodes.keySet()) {
      int noNeighboursAdopted = 0;
      Node n = g.getNode(Integer.parseInt(nodeIndex));
      Iterator<? extends Edge> edgesOfNodeN = n.getEdgeIterator();
      List<Node> neighbors = new ArrayList<Node>();
      while (edgesOfNodeN.hasNext()) {
        Edge nextEdge = edgesOfNodeN.next();
        Node thisNeighbor = nextEdge.getOpposite(n);
        if (!neighbors.contains(thisNeighbor)) {
          neighbors.add(thisNeighbor);
        }
      }
      for (Node neighbor : neighbors) {
    	if (neighbor.hasAttribute("adopted")) {
          noNeighboursAdopted++;
        }
      }
      if (noNeighboursAdopted < neighboursToBeAdopted) {
        allPopularNodeNeighboursAdopted = false;
      }
    }
    return allPopularNodeNeighboursAdopted;
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
