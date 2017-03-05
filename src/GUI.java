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
  //public static JPanel graphPanel = new JPanel();
  public static JFrame chartFrame;
  
  public static void addComponentsToPane(Container pane, JFrame frame) {         
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    
    // Graph Generators
    addPanel("Graph generator", pane);
    String[] generatorOptions = {"Select", "Bernoulli","Preferential Attachment", "Preferential Attachment with Bernoulli"};
    JComboBox<String> graphGenTypeInput = addComboBox(generatorOptions, pane);
    JFormattedTextField graphSizeInput = addIntTextField("Graph size: ", 100, pane);
    JFormattedTextField edgeProbInput = addDoubleTextField("Berboulli edge probability: ", 0.05, pane);
    JFormattedTextField maxLinksInput = addIntTextField("Pref. Attachment max. links per step: ", 2, pane);
    
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
        }
      }
    };
    graphGenTypeInput.addActionListener(graphGenAL);

    // Adoption Settings
    addPanel("Adoption type", pane);
    String[] adoptionOptions = {"Select", "Simple","Complex"};
    JComboBox<String> adoptionTypeInput = addComboBox(adoptionOptions, pane);

    addPanel("Initial Adoption type", pane);
    String[] initAdoptionOptions = {"Select", "Random", "p Popular Nodes"};
    JComboBox<String> initAdoptionTypeInput = addComboBox(initAdoptionOptions, pane);

    JFormattedTextField pInput = addDoubleTextField("Coefficient of innovation (p): ", 0.03, pane);

    // Thresholding for Complex Adoption
    addPanel("Complex adoption settings ", pane);
    JFormattedTextField decrementsInput = addIntTextField("Decrement p for x steps: ", 2, pane);

    addPanel("Complex adoption - thresholding", pane);
    String[] adoptionThresholdOptions = {"Select", "x neighbors adopted","No. neighbors adopted >= x% total nodes", "Neighbor degree > average degree distribution"};
    JComboBox<String> adoptionThresholdType = addComboBox(adoptionThresholdOptions, pane);
    JFormattedTextField neighborThresholdInput = addIntTextField("x neighbors adopted: ", 2, pane);
    JFormattedTextField neighborThresholdPercentInput = addDoubleTextField("Neighbors adopted >= x% total nodes: ", 0.2, pane);


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
        }
      }
    };
    adoptionTypeInput.addActionListener(adoptionTypeAL);

   
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
        }
      }
    };
    adoptionThresholdType.addActionListener(thresholdAL);


    JFormattedTextField sleepTimeInput = addIntTextField("Sleep time: ",1, pane);

    ActionListener al = createActionListener(graphGenTypeInput, adoptionTypeInput, initAdoptionTypeInput,
                                             adoptionThresholdType, graphSizeInput, pInput, edgeProbInput, 
                                             maxLinksInput, sleepTimeInput, 
                                             decrementsInput, neighborThresholdInput, neighborThresholdPercentInput, frame);
    
    addButton("Generate and adopt!", pane, al);
  }

  private static void addPanel(String text, Container container) {
    JPanel panel = new JPanel();
    JLabel label = new JLabel(text);
    panel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(label);        
    container.add(panel);  
  }

  private static JComboBox<String> addComboBox(String[] options, Container container) {
    final JComboBox<String> comboBox = new JComboBox<String>(options);      
    container.add(comboBox);
    return comboBox;  
  }

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

  private static void addButton(String text, Container container, ActionListener listener) {
    JPanel panel = new JPanel();
    JButton button = new JButton(text);
    panel.add(button);
    container.add(panel);
    button.addActionListener(listener);
  }
  
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

  private static void onClick(JComboBox<String> graphGenTypeInput, 
                              JComboBox<String> adoptionTypeInput, JComboBox<String> initAdoptionTypeInput,
                              JComboBox<String> adoptionThresholdTypeInput, JFormattedTextField graphSizeInput, 
                              JFormattedTextField pInput, JFormattedTextField edgeProbInput, 
                              JFormattedTextField maxLinksInput, JFormattedTextField sleepTimeInput, 
                              JFormattedTextField decrementsInput, JFormattedTextField neighborThresholdInput, 
                              JFormattedTextField neighborThresholdPercentInput, JFrame frame) {
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
    try {
      graphProcessor.process(graphGenType, adoptionType, initAdoptionType, adoptionThresholdType, 
                             graphSize, p, edgeProb, maxLinks, sleepTime, decrements, neighborThreshold, neighborThresholdPercent, frame);
     
    } catch (IOException error) {
      error.printStackTrace();
    }
  }
 
  private static void createAndShowGUI() {
    //Create and set up the window.
    JFrame frame = new JFrame("Product Adoption - Social Network Analysis");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    addComponentsToPane(frame.getContentPane(), frame);
    frame.pack();
    frame.setVisible(true);
  }
  
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
  
  public static void main(String[] args) {
    createAndShowGUI();
  }
}
