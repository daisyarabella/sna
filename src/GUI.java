import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jgrapht.ext.ExportException;

public class GUI {

public static void main(String[] args) {
    JFrame frame = new JFrame("Random Graph Generator & Adoption Tool");
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 800);
    frame.setLocation(200, 1000);
    frame.setLayout(new GridBagLayout());
   
    // Graph Generators
    JPanel graphGenPanel = new JPanel();
    frame.add(graphGenPanel);
    graphGenPanel.setLocation(0,0);
    JLabel graphGenLabel = new JLabel("Graph generator type:");
    graphGenLabel.setVisible(true);
    graphGenPanel.add(graphGenLabel);
    String[] generatorOptions = {"Bernoulli","Preferential Attachment"};
    final JComboBox<String> graphGenerators = new JComboBox<String>(generatorOptions);
    graphGenerators.setVisible(true);
    graphGenPanel.add(graphGenerators);
    
    // Adoption Methods
    JPanel adoptionPanel = new JPanel();
    frame.add(adoptionPanel);
    adoptionPanel.setLocation(0,0);
    JLabel adoptionLabel = new JLabel("Adoption Method: ");
    adoptionLabel.setVisible(true);
    adoptionPanel.add(adoptionLabel);
    String[] adoptionOptions = {"Simple"};
    final JComboBox<String> adoptionMethods = new JComboBox<String>(adoptionOptions);
    adoptionMethods.setVisible(true);
    adoptionPanel.add(adoptionMethods);
    
    // Text inputs - graph size input
    JPanel graphSizePanel = new JPanel();
    frame.add(graphSizePanel);
    JLabel graphSizeLabel = new JLabel("Graph Size: ");
    graphSizeLabel.setVisible(true);
    final JFormattedTextField graphSizeInput = new JFormattedTextField();
    graphSizeInput.setValue(0);
    graphSizeInput.setColumns(10);
    graphSizePanel.add(graphSizeLabel);
    graphSizePanel.add(graphSizeInput);
    graphSizeInput.setVisible(true);
    
    // Text inputs - coefficient of innovation (p) input
    JPanel pPanel = new JPanel();
    frame.add(pPanel);
    JLabel pLabel = new JLabel("Coefficient of innovation (p): ");
    pLabel.setVisible(true);
    final JFormattedTextField pInput = new JFormattedTextField();
    pInput.setValue(0.0);
    pInput.setColumns(10);
    pPanel.add(pLabel);
    pPanel.add(pInput);
    pInput.setVisible(true);
    
    // Text inputs - edge probability
    JPanel edgeProbPanel = new JPanel();
    frame.add(edgeProbPanel);
    JLabel edgeProbLabel = new JLabel("Edge probability (for Bernoulli graph only): ");
    edgeProbLabel.setVisible(true);
    final JFormattedTextField edgeProbInput = new JFormattedTextField();
    edgeProbInput.setValue(0.0);
    edgeProbInput.setColumns(10);
    edgeProbPanel.add(edgeProbLabel);
    edgeProbPanel.add(edgeProbInput);
    edgeProbInput.setVisible(true);
    
    // Button
    JPanel buttonPanel = new JPanel();
    frame.add(buttonPanel);
    JButton generateButton = new JButton("Generate");
    buttonPanel.add(generateButton);
    buttonPanel.setVisible(true);
    
    generateButton.addActionListener(new ActionListener() { 
        public void actionPerformed(ActionEvent e) { 
            try {
            	int graphSize = (int)graphSizeInput.getValue();
            	double p = (double)pInput.getValue();
            	int generatorType = 0;
            	double edgeProb = (double)edgeProbInput.getValue();
            	int adoptionMethod = 0;
            	
                if(graphGenerators.getSelectedItem().toString().equals("Bernoulli")) {
                	generatorType = 1;
                	System.out.println("Bernoulli "+ generatorType);
                }
                if(graphGenerators.getSelectedItem().toString().equals("Preferential Attachment")) {
                	generatorType = 2;
                	System.out.println("Pref Att " + generatorType);
                }
            	if(adoptionMethods.getSelectedItem().toString().equals("Simple")) {
                	adoptionMethod = 1;
                	System.out.println("Simple Adoption " +adoptionMethod);
                }
            	
            	System.out.println("Graph size: " +graphSize);
            	System.out.println("p: " +p);
            	System.out.println("edge prob: " +edgeProb);
            	
				GraphProcessor.graphProcessor.process(graphSize, p, generatorType, edgeProb, adoptionMethod);
			} catch (ExportException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        } 
    });

    }
}