import java.awt.Component;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

//import org.graphstream.ui.swingViewer.Viewer;
 
public class aGUI {
  public static boolean RIGHT_TO_LEFT = false;
  public static JPanel graphPanel = new JPanel();
  
  public static void addComponentsToPane(Container pane, JFrame frame) {         
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    
    addPanel("Graph generator", pane);
    String[] generatorOptions = {"Bernoulli","Preferential Attachment"};
    JComboBox<String> graphGenTypeInput = addComboBox(generatorOptions, pane);

    addPanel("Adoption type", pane);
    String[] adoptionOptions = {"Simple","Complex"};
    JComboBox<String> adoptionTypeInput = addComboBox(adoptionOptions, pane);

    JFormattedTextField graphSizeInput = addIntTextField("Graph size: ", 100, pane);
    JFormattedTextField pInput = addDoubleTextField("Coefficient of innovation (p): ", 0.38, pane);
    JFormattedTextField edgeProbInput = addDoubleTextField("Edge probability (Bernoulli only): ", 0.05, pane);
    JFormattedTextField maxLinksInput = addIntTextField("Maximum links (Pref. Attachment only): ", 3, pane);

    ActionListener al = createActionListener(graphGenTypeInput, adoptionTypeInput, graphSizeInput, pInput, edgeProbInput, maxLinksInput, frame);
    
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
                              JComboBox<String> adoptionTypeInput, 
                              JFormattedTextField graphSizeInput, JFormattedTextField pInput, 
                              JFormattedTextField edgeProbInput, JFormattedTextField maxLinksInput,
                              JFrame frame) {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) { 
        onClick(graphGenTypeInput, adoptionTypeInput, graphSizeInput, pInput, edgeProbInput, maxLinksInput, frame);
      }
    };
  }

  private static void onClick(JComboBox<String> graphGenTypeInput, 
                              JComboBox<String> adoptionTypeInput, 
                              JFormattedTextField graphSizeInput, JFormattedTextField pInput, 
                              JFormattedTextField edgeProbInput, JFormattedTextField maxLinksInput,
			      JFrame frame) {
    String graphGenType = (String)graphGenTypeInput.getSelectedItem();
    String adoptionType = (String)adoptionTypeInput.getSelectedItem();
    int graphSize = (int)graphSizeInput.getValue();
    double p = (double)pInput.getValue(); 
    double edgeProb = (double)edgeProbInput.getValue(); 
    int maxLinks = (int)maxLinksInput.getValue();
    try {
      frame.getContentPane().remove(graphPanel);
      graphPanel = graphProcessor.process(graphGenType, adoptionType, graphSize, p, edgeProb, maxLinks, frame);
     
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
     
  public static void main(String[] args) {
    createAndShowGUI();
  }
}
