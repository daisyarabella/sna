import Jama.Matrix;

import com.opencsv.CSVReader;

import java.text.NumberFormat;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;

import javax.swing.JTextArea;

public class regression {
  public static void polyRegression() {
    JTextArea textArea = GUI.createRegressionGUI(); 

    CSVReader dataReader = null;
    CSVReader lineCounter = null;
    double[][] allYtValues;
    double[][] regressionYtValues;
    double[][] Stadd1Values;
    double[][] solutions = new double[3][1];
    int position = 0;
    double totalNoAdopters = 0;

    try {
      dataReader = new CSVReader(new FileReader("./output/regressionAnalysis.csv"));
      lineCounter = new CSVReader(new FileReader("./output/regressionAnalysis.csv"));
      int lineNo = 0;
      String[] nextLine;
      while ((nextLine = lineCounter.readNext()) != null) {
        lineNo++;
      }

      allYtValues = new double[lineNo][1];	
      regressionYtValues = new double[lineNo-1][3];
      Stadd1Values = new double[lineNo-1][1];	

      while ((nextLine = dataReader.readNext()) != null) {
        Double nextYtValue = Double.parseDouble(nextLine[0]);         
        allYtValues[position][0] = nextYtValue;
        totalNoAdopters = nextYtValue;
        position++;
      }

      for (int i=0; i<lineNo-1;i++) {
        regressionYtValues[i][0] = 1;
        regressionYtValues[i][1] = allYtValues[i][0];
        regressionYtValues[i][2] = (allYtValues[i][0]*allYtValues[i][0]);

        Stadd1Values[i][0] = allYtValues[i+1][0]-allYtValues[i][0];
      }

    Matrix Stadd1 = new Matrix(Stadd1Values);    
    Matrix time = new Matrix(regressionYtValues);
    Matrix timeTranspose = time.transpose();
    Matrix timeTimeTranspose = timeTranspose.times(time);
    Matrix timeTimeTransposeInverse = timeTimeTranspose.inverse();
    Matrix timeTimeTransposeInverseTimeTranspose = timeTimeTransposeInverse.times(timeTranspose);

    textArea.append("Solution matrix values: \n");
    Matrix solution = timeTimeTransposeInverseTimeTranspose.times(Stadd1);
    

    double a = solution.get(0,0);
    double b = solution.get(1,0);
    double c = solution.get(2,0);
    textArea.append("a = "+Double.toString(a)+"\n");
    textArea.append("b = "+Double.toString(b)+"\n");
    textArea.append("c = "+Double.toString(c)+"\n");

    textArea.append("\nCoefficients plot curve of best fit\n"+"represented by a + b*x + c*x*x\n");
 
    double bsqminus4ac = ((b*b)-(4*a*c));
    if (bsqminus4ac < 0) {
      textArea.append("Solution is a complex number; cannot solve");
      textArea.append("\nDoes not fit the Bass Model");
    }
    else {
      double sqrtTerm = Math.sqrt(bsqminus4ac);
      double mNeg = (-b-sqrtTerm)/(2*c);
      textArea.append("\nmNeg = " +mNeg+"\n");
      
      double pNeg = a/mNeg;
      double qNeg = b+pNeg;
      textArea.append("pNeg = a/m-: " +pNeg+ "\nqNeg = b+pNeg: " +qNeg);
      System.out.println(pNeg+ "\n" +qNeg);
      
      if (0<pNeg && pNeg<1 && 0<qNeg && qNeg<1) {
        textArea.append("\nFits the Bass Model (Using mNeg)");
      } else {
        textArea.append("\nDoes not fit the Bass Model (Using mNeg)");
      }
      
      double mPos = (-b+sqrtTerm)/(2*c);
      textArea.append("\n\nmPos = " +mPos);
   
      double pPos = a/mPos;
      double qPos = b+pPos;
      textArea.append("\npPos = a/m-: " +pPos+ "\nqPos = b+pPos: " +qPos);
      System.out.println(pPos+ "\n" +qPos);
      
      if (0<pPos && pPos<1 && 0<qPos && qPos<1) {
        textArea.append("\nFits the Bass Model (Using mPos)");
      } else {
        textArea.append("\nDoes not fit the Bass Model (Using mPos)");
      }
    }
    regressionChart regressionChart = new regressionChart("Plot - polynomial regression", 
                                                          "Polynomial regression", allYtValues, 
                                                          Stadd1Values, time.getRowDimension(), a, b, c);
    regressionChart.setSize(600,350);
    regressionChart.show();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
