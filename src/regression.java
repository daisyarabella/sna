import Jama.Matrix;

import com.opencsv.CSVReader;

import java.text.NumberFormat;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;

import javax.swing.JTextArea;

// Perform a regression on data set using Jama API
public class regression {
  public static void polyRegression(boolean showRegressionChart) {
    // Create text area to report p and q findings
    JTextArea textArea = GUI.createRegressionGUI(); 

    CSVReader dataReader = null;
    CSVReader lineCounter = null;
    
    // Double arrays to be populated with values to be converted to matrices 
    double[][] allYtValues;
    double[][] regressionYtValues;
    double[][] Stadd1Values;
    double[][] solutions = new double[3][1];
    
    int position = 0;
    double totalNoAdopters = 0;

    try {
      // Read the data
      // First read lines to specify size of arrays
      lineCounter = new CSVReader(new FileReader("./output/regressionAnalysis.csv"));
      // Read and store the data
      dataReader = new CSVReader(new FileReader("./output/regressionAnalysis.csv"));

      int lineNo = 0;
      String[] nextLine;
      while ((nextLine = lineCounter.readNext()) != null) {
        lineNo++;
      }

      // Specify sizes for double arrays
      allYtValues = new double[lineNo][1]; 	
      regressionYtValues = new double[lineNo-1][3]; 
      Stadd1Values = new double[lineNo-1][1];	

      // Store Yt values from regression data file
      while ((nextLine = dataReader.readNext()) != null) {
        Double nextYtValue = Double.parseDouble(nextLine[0]);         
        allYtValues[position][0] = nextYtValue;
        totalNoAdopters = nextYtValue;
        position++;
      }

      // Populate Y(t) and S(t+1) arrays with data
      for (int i=0; i<lineNo-1;i++) {
        regressionYtValues[i][0] = 1;
        regressionYtValues[i][1] = allYtValues[i][0]; 
        regressionYtValues[i][2] = (allYtValues[i][0]*allYtValues[i][0]);

        // Find S(t+1) = Y(t+1) - Y(t)
        Stadd1Values[i][0] = allYtValues[i+1][0]-allYtValues[i][0];
      }

    // Convert Y(t) and S(t+1) arrays to matrices for polynomial regression calculation
    // YtTrans = transpose of Yt
    // YtTYt = YtTrans*Yt
    // YtTYtInv = inverse of YtTYt
    // YtTYtInvYtTrans = YtTYtInv*YtTrans
    Matrix Stadd1 = new Matrix(Stadd1Values);    
    Matrix Yt = new Matrix(regressionYtValues);
    Matrix YtTrans = Yt.transpose();
    Matrix YtTYt = YtTrans.times(Yt);
    Matrix YtTYtInv = YtTYt.inverse();
    Matrix YtTYtInvYtTrans = YtTYtInv.times(YtTrans);

    textArea.append("Solution matrix values: \n");
    
    // Solution is YtTYtInvYtTrans*S(t+1)
    Matrix solution = YtTYtInvYtTrans.times(Stadd1);
    
    // Get and print coefficients of polynomial, a + b*x + c*x*x
    double a = solution.get(0,0);
    double b = solution.get(1,0);
    double c = solution.get(2,0);
    textArea.append("a = "+Double.toString(a)+"\n");
    textArea.append("b = "+Double.toString(b)+"\n");
    textArea.append("c = "+Double.toString(c)+"\n");

    textArea.append("\nCoefficients plot curve of best fit\n"+"represented by a + b*x + c*x*x\n");
 
    // Calculate sqrt term of m- and m+, where
    // m- = (-b-(sqrt(b*b-4*a*c))/2c and m+ = (-b+(sqrt(b*b-4*a*c))/2c
    // If this term is negative, the solution to the Bass model is complex
    double bsqminus4ac = ((b*b)-(4*a*c));
    if (bsqminus4ac < 0) {
      textArea.append("Solution is a complex number; cannot solve\n");
      textArea.append("Does not fit the Bass Model");
    }
    else {
      // Calculate and print values of m-, p- = a/m- and q- = p- + b
      double sqrtTerm = Math.sqrt(bsqminus4ac);
      double mNeg = (-b-sqrtTerm)/(2*c);
      textArea.append("\nmNeg = " +mNeg+"\n");
      
      double pNeg = a/mNeg;
      double qNeg = b+pNeg;
      textArea.append("pNeg = a/m-: " +pNeg+ "\nqNeg = b+pNeg: " +qNeg);
      System.out.println(pNeg+ "\n" +qNeg);
      
      // Only fits if p-, q- are real numbers between 0 and 1
      if (0<pNeg && pNeg<1 && 0<qNeg && qNeg<1) {
        textArea.append("\nFits the Bass Model (Using mNeg)");
      } else {
        textArea.append("\nDoes not fit the Bass Model (Using mNeg)");
      }
      
      // Calculate and print values of m+, p- = a/m+ and q+ = p+ + b
      double mPos = (-b+sqrtTerm)/(2*c);
      textArea.append("\n\nmPos = " +mPos);
   
      double pPos = a/mPos;
      double qPos = b+pPos;
      textArea.append("\npPos = a/m-: " +pPos+ "\nqPos = b+pPos: " +qPos);
      System.out.println(pPos+ "\n" +qPos);
      
      // Only fits if p+, q+ are real numbers between 0 and 1
      if (0<pPos && pPos<1 && 0<qPos && qPos<1) {
        textArea.append("\nFits the Bass Model (Using mPos)");
      } else {
        textArea.append("\nDoes not fit the Bass Model (Using mPos)");
      }
    }
    
    // Only show regression chart if specified in GUI
    if (showRegressionChart) {
      regressionChart regressionChart = new regressionChart("Plot - polynomial regression", 
                                                            "Polynomial regression", 
                                                            allYtValues, Stadd1Values, 
                                                            Yt.getRowDimension(), a, b, c);
      regressionChart.setSize(600,350);
      regressionChart.show();
    }
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
