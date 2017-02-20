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
      //textArea.append("Data read from file:\n ");
      //System.out.println("Data read from file: ");
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

        //textArea.append(nextLine[0]+ "|" + nextLine[1]+"\n");
        //System.out.println(nextLine[0]+ "|" + nextLine[1]);
      }
    
    Matrix Yt = new Matrix(YtValues);
    //Yt.print(1, 2);
    Matrix time = new Matrix(timeValues);
    //time.print(1, 2);
    Matrix timeTranspose = time.transpose();
    //timeTranspose.print(1,2);
    Matrix timeTimeTranspose = timeTranspose.times(time);
    //timeTimeTranspose.print(1,2);
    Matrix timeTimeTransposeInverse = timeTimeTranspose.inverse();
    Matrix timeTimeTransposeInverseTimeTranspose = timeTimeTransposeInverse.times(timeTranspose);
    //timeTimeTransposeInverseTimeTranspose.print(1,2);

    textArea.append("Solution matrix for a*x*x + b*x + c [top to bottom]:\n");
    //System.out.println("\nSolution matrix for a*x*x + b*x + c [top to bottom]:");
    Matrix solution = timeTimeTransposeInverseTimeTranspose.times(Yt);
    double a = solution.get(0,0);
    double b = solution.get(1,0);
    double c = solution.get(2,0);
    textArea.append(Double.toString(a)+"\n");
    textArea.append(Double.toString(b)+"\n");
    textArea.append(Double.toString(c)+"\n\n");
    //solution.print(1,8);

    textArea.append("\nm = total no. of adopters: " +totalNoAdopters+"\n\n");
    //System.out.println("\nm = total no. of adopters: " +totalNoAdopters);
    double p = solution.get(0,0)/totalNoAdopters;
    double q = (solution.get(1,0) + p);
    textArea.append("p = a/m: " +p+ "\nq = b+p: " +q+"\n");
    //System.out.println("p = a/m: " +p+ "\nq = b+p: " +q);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
