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
   public regressionChart(String applicationTitle, String chartTitle, double[][] YtValues, double[][] SValues, int graphSize, double a, double b, double c)
   {
      super(applicationTitle);
      XYPlot plot = new XYPlot();

      XYDataset scatterDataset = createScatterDataset(YtValues, SValues, graphSize);

      plot.setDataset(0, scatterDataset);
      plot.setRenderer(0, new XYLineAndShapeRenderer(false, true));
      plot.setDomainAxis(0, new NumberAxis("Total no. of adopters, Y(t)"));
      plot.setRangeAxis(0, new NumberAxis("S(t+1)"));

      double minXAxis = 0;
      double maxXAxis = graphSize;
      XYDataset regressionDataset = createRegressionDataset(a,b,c,minXAxis,maxXAxis,graphSize);
      plot.setDataset(1, regressionDataset);
      plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

      JFreeChart chart = new JFreeChart("Regression analysis", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
         
      ChartPanel chartPanel = new ChartPanel(chart);
      chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
      setContentPane(chartPanel);
   }

   private XYDataset createScatterDataset(double[][] YtValues, double[][] SValues, int graphSize)
   {
      XYSeriesCollection dataset = new XYSeriesCollection();
      XYSeries scatterPoints = new XYSeries("Yt vs S(t+1)");
      for (int t=0; t<graphSize; t++) {
        scatterPoints.add(YtValues[t][0], SValues[t][0]);
      }
      dataset.addSeries(scatterPoints);
      return dataset;
   }

   private XYDataset createRegressionDataset(double a, double b, double c, double minXAxis, double maxXAxis, int graphSize)
   {
      double[] coeff = {a,b,c};
      Function2D poly = new PolynomialFunction2D(coeff);
      XYSeriesCollection dataset = new XYSeriesCollection();
      XYSeries polySeries = sampleFunctionOverX(poly,minXAxis,maxXAxis,graphSize, "Regression curve");
      dataset.addSeries(polySeries);
      return dataset;
   }

   public static XYSeries sampleFunctionOverX(Function2D poly, double minXAxis,double maxXAxis,int graphSize, Comparable<?> seriesKey)
   {
     XYSeries series = new XYSeries(seriesKey, false);
     double step = (maxXAxis-minXAxis)/(graphSize-1);
     for (int i=0;i<graphSize;i++) {
       double x = minXAxis + step * i;
       series.add(x, poly.getValue(x));
     }
     return series;
   }
}

