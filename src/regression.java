import Jama.Matrix;

import com.opencsv.CSVReader;

import java.text.NumberFormat;
import java.io.FileReader;
import java.util.Scanner;
import java.io.IOException;

import javax.swing.JTextArea;

public class regression {
  public static void polyRegression() {
    JTextArea textArea = GUI.createRegressionGUI(); 
    
    CSVReader dataReader = null;
    CSVReader lineCounter = null;
    double[][] YtValues;
    double[][] timeValues;
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
      timeValues = new double[lineNo][3];
      YtValues = new double[lineNo][1];	
      while ((nextLine = dataReader.readNext()) != null) {
        Double nextTimeValue = Double.parseDouble(nextLine[0]);
        Double nextYtValue = Double.parseDouble(nextLine[1]);         
       
        timeValues[position][0] = 1;
        timeValues[position][1] = nextTimeValue;
        timeValues[position][2] = (nextTimeValue*nextTimeValue);
        YtValues[position][0] = nextYtValue;
        totalNoAdopters = nextYtValue;
        position++;
      }
    
    Matrix Yt = new Matrix(YtValues);
    Matrix time = new Matrix(timeValues);
    Matrix timeTranspose = time.transpose();
    Matrix timeTimeTranspose = timeTranspose.times(time);
    Matrix timeTimeTransposeInverse = timeTimeTranspose.inverse();
    Matrix timeTimeTransposeInverseTimeTranspose = timeTimeTransposeInverse.times(timeTranspose);

    textArea.append("Solution matrix for a*x*x + b*x + c [top to bottom]:\n");
    Matrix solution = timeTimeTransposeInverseTimeTranspose.times(Yt);
    double a = solution.get(2,0);
    double b = solution.get(1,0);
    double c = solution.get(0,0);
    textArea.append("a: "+Double.toString(a)+"\n");
    textArea.append("b: "+Double.toString(b)+"\n");
    textArea.append("c: "+Double.toString(c)+"\n");

    textArea.append("\nm = total no. of adopters: " +totalNoAdopters+"\n\n");

    double p = solution.get(0,0)/totalNoAdopters;
    double q = (solution.get(1,0) + p);
    textArea.append("p = a/m: " +p+ "\nq = b+p: " +q+"\n");

    regressionChart regressionChart = new regressionChart("Plot - polynomial regression", 
                                                          "Polynomial regression", YtValues,
                                                          totalNoAdopters, 
                                                          time.getRowDimension(), c, b, a);
    regressionChart.setSize(600,350);
    regressionChart.show();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
