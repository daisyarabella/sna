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
	
  // go through complex adoption process
  public static Graph adopt(Graph g, double p, int graphSize, int sleepTime, 
                            FileWriter timestepfw, FileWriter linearfw) throws IOException {
    g.addAttribute("ui.stylesheet", stylesheet);
    
    TreeMap<String,Integer> nodeDegrees = new TreeMap<String,Integer>();

    for (Node n:g) {
      nodeDegrees.put(n.getId(), n.getDegree());
    }
    
    Set mapData = nodeDegrees.entrySet();
    LinkedHashMap<String,Integer> sortedNodeDegrees = sortSetByValues(mapData);
    System.out.println(sortedNodeDegrees); 
    HashMap popularNodes = getPopularNodes(0.3,sortedNodeDegrees);
    System.out.println(popularNodes);

    

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
       System.out.println("Node: " +entry.getKey()+ " Value: " +entry.getValue());
    }
    System.out.println(sortedNodeDegrees);
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
  public static Graph initAdoption(Graph g, double p, int sleepTime) {
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
    return g;
  }

/*public class simpleAdoption {
  static int Yt = 0; // Y(t)
  static int Ytadd1 = 0; // Y(t+1)
  static int intAdoptionCount = 0;
  static int extAdoptionCount = 0;
  static int t = 0; 
  static boolean internalAdoptionHappen = false;
	
  // go through simple adoption process
  public static Graph adopt(Graph g, double p, int graphSize, int sleepTime, 
                            FileWriter timestepfw, FileWriter linearfw) throws IOException {
    g.addAttribute("ui.stylesheet", stylesheet);
    Yt = 0; // Y(t)
    Ytadd1 = 0; // Y(t+1)
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
      if (Yt == 0) {
        g = initAdoption(g, p, sleepTime);
      }
      g = internalAdoption(g, p, sleepTime);
      if (!internalAdoptionHappen) {       			
        g = externalAdoption(g, p, sleepTime);			
      }

      //exportCSV files
      timestepfw.write(t + "," + Yt + "," + extAdoptionCount + "," + intAdoptionCount + "\n");
      linearfw.write(Ytadd1-Yt + "," + 1 + "," + Yt + "," + Yt*Yt + "\n");
      
      // print timestep data to terminal and timestep data GUI
      //System.out.println("t: " +t+ "\t Y(t+1): " +Ytadd1+ "\t Y(t): " +Yt);
      textArea.append("t: " +t+ "\t Y(t+1): " +Ytadd1+ "\t Y(t): " +Yt+ "\t No. External Adoptions: " +extAdoptionCount+ "\t No. Internal Adoptions: " + intAdoptionCount +"\n");

      // add the timestep data to int[] arrays for display in plot
      totalAdopters[t] = Yt;
      extAdopters[t] = extAdoptionCount;
      intAdopters[t] = intAdoptionCount;
      t++;
      } while (Yt < graphSize);
      
      timestepfw.close();
      linearfw.close();

      LineChart lineChart = new LineChart("Plot - adoption over time", "Number of adoptions over time", 
                                          totalAdopters, extAdopters, intAdopters, t);
      lineChart.setSize(600,350);
      lineChart.show();

      //System.out.println("External adoptions: " + extAdoptionCount + ", Internal adoptions: " +intAdoptionCount);
      //System.out.println("Finished");
      //textArea.append("External adoptions: " + extAdoptionCount + ", Internal adoptions: " +intAdoptionCount+"\n");
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
   	Ytadd1++;
   	extAdoptionCount++;
      }
    }
    return g;
  }
    
  // method to make all neighbours of adopted nodes adopted
  private static Graph internalAdoption(Graph g, double p, int sleepTime) {
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
    	    internalAdoptionHappen = true;
          }
        } 
      }
    }
    return g;
  }

  private static Graph externalAdoption(Graph g, double p, int sleepTime) {
    Yt = Ytadd1;
    for (Node n:g) {
      if (!n.hasAttribute("adopted")) {
        if (Math.random() < p) {
    	  n.setAttribute("adopted");
    	  n.setAttribute("ui.class", "adopted");
	      sleep(sleepTime);
    	  Ytadd1++;
    	  extAdoptionCount++;
    	}
      }
    }
    return g;
  }*/ 

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
