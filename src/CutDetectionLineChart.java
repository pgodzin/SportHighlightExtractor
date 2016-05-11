import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CutDetectionLineChart extends ApplicationFrame {
    double[] data;

    public CutDetectionLineChart(double[] data) {
        super("Histogram Difference Between Frames ");
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);

        this.data = data;
        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Histogram Difference Between Frames ",
                "Frame", "Difference",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        XYPlot plot = (XYPlot) lineChart.getPlot();
        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setRange(0, 2000.0);
        ValueAxis xAxis = plot.getDomainAxis();
        xAxis.setRange(0, 3000.0);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);

    }

    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Object 1");
        for(int i = 0; i < data.length; i++) {
            series.add(i, data[i]);
        }
        dataset.addSeries(series);
        return dataset;
    }
}