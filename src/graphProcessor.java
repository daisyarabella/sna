import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
//import org.graphstream.ui.view.View;
//import org.graphstream.ui.view.Viewer;
//import org.graphstream.ui.swingViewer.ViewPanel;
//import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.stream.ProxyPipe;

import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;

public class graphProcessor
{
    public static void process(String graphGenType, String adoptionType, 
                           int graphSize, double p, double edgeProb, int maxLinks,
                           int sleepTime, int decrements, int neighborThreshold,
                           JFrame frame) throws IOException {
      
      // file to record time step data for plotting simple line graph
      File timestepData = new File("output/timestepData.csv");
      // file to be read by Python to calculate p and q. Includes S(t+1) data, and coefficients of a, b, c 
      File regressionAnalysis = new File("output/regressionAnalysis.csv"); 
      
      FileWriter timestepfw = new FileWriter(timestepData.getAbsoluteFile());
      FileWriter regressionAnalysisfw = new FileWriter(regressionAnalysis.getAbsoluteFile());
      timestepfw.write("t,Y(t),External Adopters,Internal Adopters\n");
      
      Graph graph = generateGraph(graphSize, graphGenType, edgeProb, maxLinks);
      graph.display();
      
      //adoptGraph(graph, adoptionType, p, graphSize, timestepfw, linearfw);

      /*Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
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
      */
       
      Thread adoptionThread = new Thread(new Runnable() {
         public void run() {
           try {
             adoptGraph(graph, adoptionType, p, graphSize, sleepTime, 
                        timestepfw, regressionAnalysisfw, decrements, neighborThreshold);
             regression.polyRegression();
           } catch (Exception e) {}
         }
      });  
      adoptionThread.start();
    }
        
    //method to create adopt graphs
    private static Graph adoptGraph(Graph g, String adoptionType, 
        	                          double p, int graphSize, int sleepTime, 
        	                          FileWriter timestepfw, FileWriter regressionAnalysisfw,
                                          int decrements, int neighborThreshold) throws IOException {       
      switch (adoptionType) {
     	  case "Simple": g = simpleAdoption.adopt(g, p, graphSize, sleepTime, timestepfw, regressionAnalysisfw);
          break;
     	
       	  case "Complex": g = complexAdoption.adopt(g, p, graphSize, sleepTime, timestepfw, regressionAnalysisfw, decrements, neighborThreshold);
	  break;
    
	  default: g = simpleAdoption.adopt(g, p, graphSize, sleepTime, timestepfw, regressionAnalysisfw);
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
