import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.stream.ProxyPipe;

import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;

public class graphProcessor
{
    /*public static void main(String args[]) throws IOException
    {
    	File timestepData = new File("output/timestepData.csv"); // file to record time step data for plotting simple line graph
    	File linearEqs = new File("output/linearEqs.csv"); // file to be read by Python to calculate p and q. Includes S(t+1) data, and coefficients of a, b, c
    	FileWriter timestepfw = new FileWriter(timestepData.getAbsoluteFile());
    	FileWriter linearfw = new FileWriter(linearEqs.getAbsoluteFile());
    	timestepfw.write("t,Y(t),External Adopters,Internal Adopters\n");
    	linearfw.write("S(t+1),aCo,bCo,cCo\n");
    	double edgeProb = 0;
    	int maxLinks = 0;
    	
      Scanner scanner = new Scanner(System.in);
      System.out.println("How many nodes?");
      int graphSize = scanner.nextInt();
      System.out.println("Set coefficient of innovation (p):");
      double p = scanner.nextDouble();

      System.out.println("Select graph generator type - Bernoulli (1) or Preferential Attachment (2):");
      int generatorType = scanner.nextInt();
      if (generatorType == 1) {
        System.out.println("Enter edge probability:");
        edgeProb = scanner.nextDouble();
      }
      if (generatorType == 2) {
        System.out.println("Enter max links per step:");
        maxLinks = scanner.nextInt();
      }
         
      //create a graph
      Graph graph = generateGraph(graphSize, generatorType, edgeProb, maxLinks); 
      // adopt this graph  
      int adoptionType;
      System.out.println("Select an adoption method - Simple (1) or Complex (2):");
      adoptionType = scanner.nextInt();
      graph.display();
      adoptGraph(graph, adoptionType, p, graphSize, timestepfw, linearfw);
    }*/

    public static JPanel process(String graphGenType, String adoptionType, 
                           int graphSize, double p, double edgeProb, int maxLinks,
                           JFrame frame) throws IOException {
      
      // file to record time step data for plotting simple line graph
      File timestepData = new File("output/timestepData.csv");
      // file to be read by Python to calculate p and q. Includes S(t+1) data, and coefficients of a, b, c 
      File linearEqs = new File("output/linearEqs.csv"); 
      
      FileWriter timestepfw = new FileWriter(timestepData.getAbsoluteFile());
      FileWriter linearfw = new FileWriter(linearEqs.getAbsoluteFile());
      timestepfw.write("t,Y(t),External Adopters,Internal Adopters\n");
      linearfw.write("S(t+1),aCo,bCo,cCo\n");
      
      Graph graph = generateGraph(graphSize, graphGenType, edgeProb, maxLinks);

      Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
      viewer.enableAutoLayout();
      viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
      ViewPanel view = viewer.addDefaultView(false);
      ProxyPipe pipe = viewer.newViewerPipe();

      JPanel panel = new JPanel();
      panel.setPreferredSize(new Dimension(500, 500));
      view.setPreferredSize(new Dimension(500, 500));
      panel.add(view);
      frame.getContentPane().add(panel);
      frame.pack();

      Thread adoptionThread = new Thread(new Runnable() {
         public void run() {
           try {
             adoptGraph(graph, adoptionType, p, graphSize, timestepfw, linearfw);
           } catch (Exception e) {}
         }
      });  
      adoptionThread.start();
      
      return panel;
    }
        
    //method to create adopt graphs
    private static Graph adoptGraph(Graph g, String adoptionType, 
        	double p, int graphSize, FileWriter timestepfw, FileWriter linearfw) throws IOException {       
      switch (adoptionType) {
     	  case "Simple": g = simpleAdoption.adopt(g, p, graphSize, timestepfw, linearfw);
          break;
     	
       	//case "Complex": g = complexAdoption.initAdoption(graph, p);
	      
	      default: g = simpleAdoption.adopt(g, p, graphSize, timestepfw, linearfw);
        }
        return g;
    }   	
    
    // method to create initial random graphs 
    private static Graph generateGraph(int graphSize, String generatorType, double edgeProb, int maxLinks) {     
      Graph g = new SingleGraph("Random Graph");
      switch (generatorType) {
      	case "Bernoulli": g = bernoulli.createGraph(g, graphSize, edgeProb);
        	break;
        	
      	case "Preferential Attachment": g = preferentialAttachment.createGraph(g, graphSize, maxLinks);
        	break;
        	
        case "Dorogovtsev": g = dorogovtsev.createGraph(g, graphSize);
        	break;
        	
        case "Square Grid": g = squareGrids.createGraph(g, graphSize);
        	break;
        	
        case "Euclidean": g = euclidean.createGraph(g, graphSize);
        	break;
		
		    default: g = bernoulli.createGraph(g, graphSize, edgeProb);
        }
        return g;
    }  

}
