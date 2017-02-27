import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;
import org.jfree.data.function.*;
import org.jfree.chart.renderer.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.axis.NumberAxis;

public class regressionChart extends ApplicationFrame
{
   public regressionChart(String applicationTitle, String chartTitle, double[][] YtValues, double totalNoAdopters, int maxT, double c, double b, double a)
   {
      super(applicationTitle);
      XYPlot plot = new XYPlot();

      XYDataset scatterDataset = createScatterDataset(YtValues, maxT);

      plot.setDataset(0, scatterDataset);
      plot.setRenderer(0, new XYLineAndShapeRenderer(false, true));
      plot.setDomainAxis(0, new NumberAxis("Time"));
      plot.setRangeAxis(0, new NumberAxis("No. of adopters"));

      double minXAxis = 0;
      double maxXAxis = maxT;
      XYDataset regressionDataset = createRegressionDataset(c,b,a,minXAxis,maxXAxis,maxT);
      plot.setDataset(1, regressionDataset);
      plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

      JFreeChart chart = new JFreeChart("Regression analysis", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
         
      ChartPanel chartPanel = new ChartPanel(chart);
      chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
      setContentPane(chartPanel);
   }

   private XYDataset createScatterDataset(double[][] YtValues, int maxT)
   {
      XYSeriesCollection dataset = new XYSeriesCollection();
      XYSeries scatterPoints = new XYSeries("No. adopters at time t");
      for (int t=0; t<maxT; t++) {
        scatterPoints.add(t, YtValues[t][0]);
      }
      dataset.addSeries(scatterPoints);
      return dataset;
   }

   private XYDataset createRegressionDataset(double c, double b, double a, double minXAxis, double maxXAxis, int maxT)
   {
      double[] coeff = {c,b,a};
      Function2D poly = new PolynomialFunction2D(coeff);
      XYSeriesCollection dataset = new XYSeriesCollection();
      XYSeries polySeries = sampleFunctionOverX(poly,minXAxis,maxXAxis,maxT, "Regression curve");
      dataset.addSeries(polySeries);
      return dataset;
   }

   public static XYSeries sampleFunctionOverX(Function2D poly, double minXAxis,double maxXAxis,int maxT, Comparable<?> seriesKey)
   {
     XYSeries series = new XYSeries(seriesKey, false);
     double step = (maxXAxis-minXAxis)/(maxT-1);
     for (int i=0;i<maxT;i++) {
       double x = minXAxis + step * i;
       series.add(x, poly.getValue(x));
     }
     return series;
   }
}

