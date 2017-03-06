import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.stream.ProxyPipe;

import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;

public class graphProcessor
{
    public static void process(String graphGenType, String adoptionType, String initAdoptionType,
                           String adoptionThresholdType, int graphSize, double p, double edgeProb, 
                           int maxLinks, int sleepTime, int decrements, int neighborThreshold,
                           double neighborThresholdPercent, JFrame frame) throws IOException {
      
      // file to record time step data for plotting simple line graph
      File timestepData = new File("output/timestepData.csv");
      // file to be read for regression analysis
      File regressionAnalysis = new File("output/regressionAnalysis.csv"); 
      
      FileWriter timestepfw = new FileWriter(timestepData.getAbsoluteFile());
      FileWriter regressionAnalysisfw = new FileWriter(regressionAnalysis.getAbsoluteFile());
      timestepfw.write("t,Y(t),External Adopters,Internal Adopters\n");
      
      Graph graph = generateGraph(graphSize, graphGenType, edgeProb, maxLinks);
      graph.display();
       
      Thread adoptionThread = new Thread(new Runnable() {
         public void run() {
           try {
             adoptGraph(graph, adoptionType, initAdoptionType, adoptionThresholdType, p, graphSize, sleepTime, 
                        timestepfw, regressionAnalysisfw, decrements, neighborThreshold, neighborThresholdPercent);
             regression.polyRegression();
           } catch (Exception e) {}
         }
      });  
      adoptionThread.start();
    }
        
    //method to create adopt graphs
    private static Graph adoptGraph(Graph g, String adoptionType, String initAdoptionType, 
                                    String adoptionThresholdType, double p, int graphSize, int sleepTime, 
        	                          FileWriter timestepfw, FileWriter regressionAnalysisfw,
                                          int decrements, int neighborThreshold, double neighborThresholdPercent) throws IOException {       
      switch (adoptionType) {
     	  case "Simple": g = simpleAdoption.adopt(g, p, graphSize, initAdoptionType, sleepTime, timestepfw, regressionAnalysisfw);
          break;
     	
       	  case "Complex": g = complexAdoption.adopt(g, p, graphSize, initAdoptionType, adoptionThresholdType, 
                                                    sleepTime, timestepfw, regressionAnalysisfw, decrements, 
                                                    neighborThreshold, neighborThresholdPercent);
	  break;
          default: g = simpleAdoption.adopt(g, p, graphSize, initAdoptionType, sleepTime, timestepfw, regressionAnalysisfw);
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

      	case "Preferential Attachment with Bernoulli": g = bernoulliPA.createGraph(g, graphSize, maxLinks, edgeProb);
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
