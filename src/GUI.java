import java.awt.Component;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.io.IOException;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.*;

//import org.graphstream.ui.swingViewer.Viewer;
 
public class GUI {
  public static boolean RIGHT_TO_LEFT = false;
  public static JFrame chartFrame;
  
  public static void addComponentsToPane(Container pane, JFrame frame) {         
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    
    // Graph Generators - combo box for selection of graph generator types and text fields for parameters needed
    addPanel("Graph generator", pane);
    String[] generatorOptions = {"Select", "Bernoulli","Preferential Attachment", "Preferential Attachment with Bernoulli"};
    JComboBox<String> graphGenTypeInput = addComboBox(generatorOptions, pane);
    
    // The number of nodes in the graph
    JFormattedTextField graphSizeInput = addIntTextField("Graph size: ", 100, pane);
    
    // Probability an edge will be formed between any two nodes for Bernoulli generation
    JFormattedTextField edgeProbInput = addDoubleTextField("Berboulli edge probability: ", 0.01, pane);
    
    // Maximum number of edges a node being created in a Preferential Attachment graph will make at it's time of being added
    JFormattedTextField maxLinksInput = addIntTextField("Pref. Attachment max. links per step: ", 2, pane);
    
    // Create action listener so certain text boxes grey out if not needed for graph generator selection made
    ActionListener graphGenAL = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String option = (String) graphGenTypeInput.getSelectedItem();
        switch (option) {
          case "Bernoulli": 
            updateState(1, edgeProbInput, maxLinksInput, null, null, null, null);
            break;
          case "Preferential Attachment": 
            updateState(2, edgeProbInput, maxLinksInput, null, null, null, null);
            break;
          case "Preferential Attachment with Bernoulli":
            updateState(3, edgeProbInput, maxLinksInput, null, null, null, null);
            break;
          // Set default case to be Pref. Attachment with Bernoulli - this creates most realistic networks
          default: 
            updateState(3, edgeProbInput, maxLinksInput, null, null, null, null);
            break;
        }
      }
    };
    graphGenTypeInput.addActionListener(graphGenAL);

    // Adoption Settings - combo box for selection of adoption types 
    addPanel("Adoption type", pane);
    String[] adoptionOptions = {"Select", "Simple","Complex"};
    JComboBox<String> adoptionTypeInput = addComboBox(adoptionOptions, pane);

    // Adoption Settings - combo box for selection of initial adoption types 
    addPanel("Initial Adoption type", pane);
    String[] initAdoptionOptions = {"p Popular Nodes", "Random"};
    JComboBox<String> initAdoptionTypeInput = addComboBox(initAdoptionOptions, pane);

    // Add text field for ideal value for p. Initially set as 0.03 as research has shown to be an optimal value
    JFormattedTextField pInput = addDoubleTextField("Coefficient of innovation (p): ", 0.03, pane);

    // Complex Adoption Settings
    addPanel("Complex adoption settings ", pane);
 
    // Text field for decrementing p - ensures there is still an external influence, but not as influential as internal (as shown by research) 
    JFormattedTextField decrementsInput = addIntTextField("Decrement p for x steps: ", 150, pane);

    // Thresholding Options 
    addPanel("Complex adoption - thresholding", pane);
    String[] adoptionThresholdOptions = {"Select", "x neighbors adopted","No. neighbors adopted >= x% total nodes", "Neighbor degree > average degree distribution"};
    JComboBox<String> adoptionThresholdType = addComboBox(adoptionThresholdOptions, pane);
    
    // Specify number of a node's neighbors to check are adopted before adopting a node
    JFormattedTextField neighborThresholdInput = addIntTextField("x neighbors adopted: ", 2, pane);
    
    // Specify percentage of the total graph's nodes as a neighbour threshold 
    JFormattedTextField neighborThresholdPercentInput = addDoubleTextField("Neighbors adopted >= x% total nodes: ", 0.02, pane);

    // Create action listener so certain text boxes grey out if not needed for adoption type selection made
    ActionListener adoptionTypeAL = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String option = (String) adoptionTypeInput.getSelectedItem();
        switch (option) {
          case "Simple": 
            updateState(4, null, null, decrementsInput, adoptionThresholdType, neighborThresholdInput, neighborThresholdPercentInput);
            break;
          case "Complex": 
            updateState(5, null, null, decrementsInput, adoptionThresholdType, neighborThresholdInput, neighborThresholdPercentInput);
            break;
          // Set default case to be Complex adoption - this process is more realistic than simple adoption
          default:
            updateState(5, null, null, decrementsInput, adoptionThresholdType, neighborThresholdInput, neighborThresholdPercentInput);
            break; 
          
        }
      }
    };
    adoptionTypeInput.addActionListener(adoptionTypeAL);

    // Create action listener so certain text boxes grey out if not needed for complex thresholding selection made
    ActionListener thresholdAL = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String option = (String) adoptionThresholdType.getSelectedItem();
        switch (option) {
          case "x neighbors adopted": 
            updateState(6, null, null, null, null, neighborThresholdInput, neighborThresholdPercentInput);
            break;
          case "No. neighbors adopted >= x% total nodes": 
            updateState(7, null, null, null, null, neighborThresholdInput, neighborThresholdPercentInput);
            break;
          case "Neighbor degree > average degree distribution":
            updateState(8, null, null, null, null, neighborThresholdInput, neighborThresholdPercentInput);
            break;
          // Set default case to be x neighbors adopted 
          default: 
            updateState(6, null, null, null, null, neighborThresholdInput, neighborThresholdPercentInput);
            break;
          
        }
      }
    };
    adoptionThresholdType.addActionListener(thresholdAL);

    // Alters the speed at which the graph displays the dynamic adoption process. 0 is fast, 100000 is slow
    JFormattedTextField sleepTimeInput = addIntTextField("Sleep time: ",200, pane);

    // Create action listener for when all options have been selected; generate graph and adopt it
    ActionListener al = createActionListener(graphGenTypeInput, adoptionTypeInput, initAdoptionTypeInput,
                                             adoptionThresholdType, graphSizeInput, pInput, edgeProbInput, 
                                             maxLinksInput, sleepTimeInput, 
                                             decrementsInput, neighborThresholdInput, neighborThresholdPercentInput, frame);
    
    // Add 'Go' button at bottom of GUI
    addButton("Generate and adopt!", pane, al);
  }

  // Create a panel for positioning of elements
  private static void addPanel(String text, Container container) {
    JPanel panel = new JPanel();
    JLabel label = new JLabel(text);
    panel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(label);        
    container.add(panel);  
  }

  // Add a combo box to make pre-decided selections
  private static JComboBox<String> addComboBox(String[] options, Container container) {
    final JComboBox<String> comboBox = new JComboBox<String>(options);      
    container.add(comboBox);
    return comboBox;  
  }

  // Add a text field for integer values
  private static JFormattedTextField addIntTextField(String text, int defaultValue, Container container) {
    JPanel panel = new JPanel();
    final JFormattedTextField textField = new JFormattedTextField();
    textField.setHorizontalAlignment(textField.LEFT);
    textField.setValue(defaultValue);
    textField.setColumns(5);
    JLabel label = new JLabel(text);
    panel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(label);        
    panel.add(textField);
    container.add(panel);
    return textField;
  }

  // Add a text field for double values
  private static JFormattedTextField addDoubleTextField(String text, double defaultValue, Container container) {
    JPanel panel = new JPanel();
    final JFormattedTextField textField = new JFormattedTextField();
    textField.setHorizontalAlignment(textField.LEFT);
    textField.setValue(defaultValue);
    textField.setColumns(5);
    JLabel label = new JLabel(text);
    panel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(label);        
    panel.add(textField);
    container.add(panel);
    return textField;
  }

  // Add a button to 'Go'
  private static void addButton(String text, Container container, ActionListener listener) {
    JPanel panel = new JPanel();
    JButton button = new JButton(text);
    panel.add(button);
    container.add(panel);
    button.addActionListener(listener);
  }
  
  // Create the action listener for the 'Go' button
  private static ActionListener createActionListener(JComboBox<String> graphGenTypeInput, 
                              JComboBox<String> adoptionTypeInput, JComboBox<String> initAdoptionTypeInput,
                              JComboBox<String> adoptionThresholdTypeInput, JFormattedTextField graphSizeInput, 
                              JFormattedTextField pInput, JFormattedTextField edgeProbInput, 
                              JFormattedTextField maxLinksInput, JFormattedTextField sleepTimeInput, 
                              JFormattedTextField decrementsInput, JFormattedTextField neighborThresholdInput, 
                              JFormattedTextField neighborThresholdPercentInput, JFrame frame) {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) { 
        onClick(graphGenTypeInput, adoptionTypeInput, initAdoptionTypeInput, adoptionThresholdTypeInput, graphSizeInput, pInput, 
                edgeProbInput, maxLinksInput, sleepTimeInput, decrementsInput, neighborThresholdInput, neighborThresholdPercentInput, frame);
      }
    };
  }

  // Get values for all data selections made in the GUI and initiate the random graph generator and adoption process
  private static void onClick(JComboBox<String> graphGenTypeInput, 
                              JComboBox<String> adoptionTypeInput, JComboBox<String> initAdoptionTypeInput,
                              JComboBox<String> adoptionThresholdTypeInput, JFormattedTextField graphSizeInput, 
                              JFormattedTextField pInput, JFormattedTextField edgeProbInput, 
                              JFormattedTextField maxLinksInput, JFormattedTextField sleepTimeInput, 
                              JFormattedTextField decrementsInput, JFormattedTextField neighborThresholdInput, 
                              JFormattedTextField neighborThresholdPercentInput, JFrame frame) {
    // Get values for all data selections
    String graphGenType = (String)graphGenTypeInput.getSelectedItem();
    String adoptionType = (String)adoptionTypeInput.getSelectedItem();
    String initAdoptionType = (String)initAdoptionTypeInput.getSelectedItem();
    String adoptionThresholdType = (String)adoptionThresholdTypeInput.getSelectedItem();
    int graphSize = (int)graphSizeInput.getValue();
    double p = (double)pInput.getValue(); 
    double edgeProb = (double)edgeProbInput.getValue(); 
    int maxLinks = (int)maxLinksInput.getValue();
    int sleepTime = (int)sleepTimeInput.getValue();
    int decrements = (int)decrementsInput.getValue();
    int neighborThreshold = (int)neighborThresholdInput.getValue();
    double neighborThresholdPercent = (double)neighborThresholdPercentInput.getValue();
    
    // Initiate the random graph generator and adoption process
    try {
      
      graphProcessor.process(graphGenType, adoptionType, initAdoptionType, adoptionThresholdType, 
                             graphSize, p, edgeProb, maxLinks, sleepTime, decrements, neighborThreshold, neighborThresholdPercent, frame);
     
    } catch (IOException error) {
      error.printStackTrace();
    }
  }
 
  //Create and set up the initial GUI
  private static void createAndShowGUI() {
    JFrame frame = new JFrame("Product Adoption - Social Network Analysis");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    addComponentsToPane(frame.getContentPane(), frame);
    frame.pack();
    frame.setVisible(true);
  }
  
  // Create the text area where the timestep data is displayed
  public static JTextArea createTimestepDataGUI() {
    chartFrame = new JFrame("Timestep Data");
    chartFrame.setEnabled(false);  
    JPanel panel = new JPanel();
    chartFrame.setSize(700,1000);    
    JTextArea textArea = new JTextArea(); 
    chartFrame.add(panel);
    panel.add(textArea); 
    chartFrame.add(textArea);
    textArea.setText("Key:   Y(t) = number of adopted nodes at time t\n"); 
    chartFrame.show(); 
    chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    return textArea;
  }

  // Create the text area where regression analysis / p and q values are displayed
  public static JTextArea createRegressionGUI() {
    chartFrame = new JFrame("Regression Analysis");
    chartFrame.setEnabled(false);  
    JPanel panel = new JPanel();
    chartFrame.setSize(320,250);    
    JTextArea textArea = new JTextArea(); 
    chartFrame.add(panel);
    panel.add(textArea); 
    chartFrame.add(textArea);
    chartFrame.show(); 
    chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    return textArea;
  }

  // With each alter made with combo box selections, update which fields are usable/greyed out 
  public static void updateState(int option, JFormattedTextField edgeProbInput, JFormattedTextField maxLinksInput,
                                 JFormattedTextField decrementsInput, JComboBox<String> adoptionThresholdType,
                                 JFormattedTextField neighborThresholdInput, JFormattedTextField neighborThresholdPercentInput) {
    switch (option) {
      case 1: edgeProbInput.setEnabled(true); maxLinksInput.setEnabled(false); break;
      case 2: edgeProbInput.setEnabled(false); maxLinksInput.setEnabled(true); break;
      case 3: edgeProbInput.setEnabled(true); maxLinksInput.setEnabled(true); break;
      case 4: decrementsInput.setEnabled(false); adoptionThresholdType.setEnabled(false); 
              neighborThresholdInput.setEnabled(false); neighborThresholdPercentInput.setEnabled(false); break;
      case 5: decrementsInput.setEnabled(true); adoptionThresholdType.setEnabled(true); 
      case 6: neighborThresholdInput.setEnabled(true); neighborThresholdPercentInput.setEnabled(false); break;
      case 7: neighborThresholdInput.setEnabled(false); neighborThresholdPercentInput.setEnabled(true); break;
      case 8: neighborThresholdInput.setEnabled(false); neighborThresholdPercentInput.setEnabled(false); break;
    }
  } 
  
  // Main method to create and show the main GUI(s)
  public static void main(String[] args) {
    createAndShowGUI();
  }
}
