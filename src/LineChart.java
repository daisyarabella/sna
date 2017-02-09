import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineChart extends ApplicationFrame
{
   public LineChart(String applicationTitle, String chartTitle, int[] totalAdopters, int[] extAdopters, int[] intAdopters, int maxT)
   {
      super(applicationTitle);
      DefaultCategoryDataset dataset = createDataset(totalAdopters, extAdopters, intAdopters, maxT);
      JFreeChart lineChart = ChartFactory.createLineChart(chartTitle, "Time", "No. of adopters", 
                                                          dataset, PlotOrientation.VERTICAL,
                                                          true, true, false);
         
      ChartPanel chartPanel = new ChartPanel(lineChart);
      chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
      setContentPane(chartPanel);
   }

   private DefaultCategoryDataset createDataset(int[] totalAdopters, int[] extAdopters, int[] intAdopters, int maxT)
   {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      //dataset.addValue(y-axis value, line name, x-axis value
      for (int t=0; t<maxT; t++) {
        dataset.addValue(totalAdopters[t], "Total", new Integer(t).toString());
        dataset.addValue(extAdopters[t], "External", new Integer(t).toString());
        dataset.addValue(intAdopters[t], "Internal", new Integer(t).toString());
      }
      return dataset;
   }
}
