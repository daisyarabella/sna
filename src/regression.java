import com.panayotis.gnuplot.GNUPlot;
import com.panayotis.gnuplot.plot.*;

public class regression {
  public static void main(String[] args) {
    GNUPlot p = new GNUPlot();
    p.addPlot(new FunctionPlot("sin(x)"));
    p.plot();
    }
}
