import Jama.Matrix;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;

import javax.swing.JTextArea;

public class regression3 {
  public static void main(String[] args) {
    CSVReader dataReader = null;
    CSVReader lineCounter = null;
    double[][] timeValues;
    double[][] matrixMValues = new double[3][3];
    double[][] matrixM0Values = new double[3][3];
    double[][] matrixM1Values = new double[3][3];
    double[][] matrixM2Values = new double[3][3];
    double[][] matrixSYValues = new double[3][1];
    double[][] YtValues;
    double[][] SValues;
    double[][] solutions = new double[3][1];
    
    int position = 0;
    double totalNoAdopters = 0;
    
    double currentSValue;
    double currentYtValue;
    double currentYtPower2;
    double currentYtPower3;
    double currentYtPower4;
    
    double currentYtTimesS;
    double currentYtPower2TimesS;
    
    double sumAllYt = 0;
    double sumAllYtPower2 = 0;
    double sumAllYtPower3 = 0;
    double sumAllYtPower4 = 0;
    
    double sumAllS = 0;
    double sumAllYtTimesS = 0;
    double sumAllYtPower2TimesS = 0;
    
    try {
      dataReader = new CSVReader(new FileReader("./output/regressionAnalysis.csv"));
      lineCounter = new CSVReader(new FileReader("./output/regressionAnalysis.csv"));
      int lineNo = 0;
      String[] nextLine;
      while ((nextLine = lineCounter.readNext()) != null) {
        lineNo++;
      }
      
      int n = lineNo-1;
     
      YtValues = new double[n][1];	
      SValues = new double[n][1];
      timeValues = new double[n][1];
      
      while ((nextLine = dataReader.readNext()) != null && position<n) {
        timeValues[position][0] = position;
        double currentValue = Integer.parseInt(nextLine[0]);
        YtValues[position][0] = currentValue;
        position++;
      }
      
      for (int i=n-1; i>0; i--) {
        if ((YtValues[i][0]-YtValues[i-1][0]) > 0) {
          currentSValue = (YtValues[i][0]-YtValues[i-1][0]);
        }
        else  {
          currentSValue = 0;
        }
        SValues[i][0] = currentSValue;
      }
    
      for (int i=0; i<n; i++) {
        currentYtValue = YtValues[i][0];
        currentYtPower2 = Math.pow(currentYtValue,2);
        currentYtPower3 = Math.pow(currentYtValue,3);
        currentYtPower4 = Math.pow(currentYtValue,4);
        
        sumAllYt += currentYtValue;
        sumAllYtPower2 += currentYtPower2;
        sumAllYtPower3 += currentYtPower3;
        sumAllYtPower4 += currentYtPower4;
        
        currentSValue = SValues[i][0];
        currentYtTimesS = (currentYtValue*currentSValue);
        currentYtPower2TimesS = (currentYtPower2*currentSValue);
        
        sumAllS += currentSValue;
        sumAllYtTimesS += currentYtTimesS;
        sumAllYtPower2TimesS += currentYtPower2TimesS;
      }    

      for (int i=0; i<3; i++) {
        for (int j=0; j<3; j++) {
          if (i==0 && j==0) {
            matrixMValues[i][j] = n;
            matrixSYValues[i][j] = sumAllS;
            matrixM0Values[i][j] = sumAllS;
            matrixM1Values[i][j] = n;
            matrixM2Values[i][j] = n;
          }
          else if (i==0 && j==1) {
            matrixMValues[i][j] = sumAllYt;
            matrixM0Values[i][j] = sumAllYt;
            matrixM1Values[i][j] = sumAllS;
            matrixM2Values[i][j] = sumAllYt;
          } 
          else if (i==0 && j==2) {
            matrixMValues[i][j] = sumAllYtPower2;
            matrixM0Values[i][j] = sumAllYtPower2;
            matrixM1Values[i][j] = sumAllYtPower2;
            matrixM2Values[i][j] = sumAllS;
          }
          else if (i==1 && j==0) {
            matrixMValues[i][j] = sumAllYt;
            matrixSYValues[i][j] = sumAllYtTimesS;  
            matrixM0Values[i][j] = sumAllYtTimesS;
            matrixM1Values[i][j] = sumAllYt;
            matrixM2Values[i][j] = sumAllYt; 
          } 
          else if (i==1 && j==1) {
            matrixMValues[i][j] = sumAllYtPower2;
            matrixM0Values[i][j] = sumAllYtPower2;
            matrixM1Values[i][j] = sumAllYtTimesS;
            matrixM2Values[i][j] = sumAllYtPower2; 
          }
          else if (i==1 && j==2) {
            matrixMValues[i][j] = sumAllYtPower3;  
             matrixM0Values[i][j] = sumAllYtPower3;
            matrixM1Values[i][j] = sumAllYtPower3;
            matrixM2Values[i][j] = sumAllYtTimesS;
          }
          else if (i==2 && j==0) {
            matrixMValues[i][j] = sumAllYtPower2;
            matrixSYValues[i][j] = sumAllYtPower2TimesS; 
            matrixM0Values[i][j] = sumAllYtPower2TimesS;
            matrixM1Values[i][j] = sumAllYtPower2;
            matrixM2Values[i][j] = sumAllYtPower2;          
          }
          else if (i==2 && j==1) {
            matrixMValues[i][j] = sumAllYtPower3;
            matrixM0Values[i][j] = sumAllYtPower3;
            matrixM1Values[i][j] = sumAllYtPower2TimesS;
            matrixM2Values[i][j] = sumAllYtPower3;  
          } 
          else if (i==2 && j==2) {
            matrixMValues[i][j] = sumAllYtPower4;
            matrixM0Values[i][j] = sumAllYtPower4;
            matrixM1Values[i][j] = sumAllYtPower4;
            matrixM2Values[i][j] = sumAllYtPower2TimesS; 
          } 
        }
      }

      Matrix Yt = new Matrix(YtValues);
      Matrix S = new Matrix(SValues);
      Matrix M = new Matrix(matrixMValues);
      Matrix SY = new Matrix(matrixSYValues);
      Matrix M0 = new Matrix(matrixM0Values);
      Matrix M1 = new Matrix(matrixM1Values);
      Matrix M2 = new Matrix(matrixM2Values);
      Matrix time = new Matrix(timeValues);
     
      double detM = M.det();
      double detM0 = M0.det();
      double detM1 = M1.det();
      double detM2 = M2.det();
    
      double a = detM0/detM;
      double b = detM1/detM;
      double c = detM2/detM;
    
      System.out.println("Solution matrix values: \n");

      System.out.println("a = "+a+"\n");
      System.out.println("b = "+b+"\n");
      System.out.println("c = "+c+"\n");

      double bsqminus4ac = ((b*b)-(4*a*c));
      if (bsqminus4ac < 0) {
        System.out.println("Solution is a complex number; cannot solve");
        System.out.println("\nDoes not fit the Bass Model");
      }
      else {
        double sqrtTerm = Math.sqrt(bsqminus4ac);
        double mNeg = (-b-sqrtTerm)/(2*c);
        System.out.println("\nmNeg = " +mNeg+"\n");
      
        double pNeg = a/mNeg;
        double qNeg = b+pNeg;
        System.out.println("pNeg = a/m-: " +pNeg+ "\nqNeg = b+pNeg: " +qNeg);
        System.out.println(pNeg+ "\n" +qNeg);
        
        if (0<pNeg && pNeg<1 && 0<qNeg && qNeg<1) {
          System.out.println("\nFits the Bass Model (Using mNeg)");
        } else {
          System.out.println("\nDoes not fit the Bass Model (Using mNeg)");
        }
        
        double mPos = (-b+sqrtTerm)/(2*c);
        System.out.println("\n\nmPos = " +mPos);
      
        double pPos = a/mPos;
        double qPos = b+pPos;
        System.out.println("\npPos = a/m-: " +pPos+ "\nqPos = b+pPos: " +qPos);
        System.out.println(pPos+ "\n" +qPos);
    }
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
