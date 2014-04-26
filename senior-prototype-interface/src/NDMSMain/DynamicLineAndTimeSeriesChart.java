package NDMSMain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import SNMPHandler.SNMPWalk;
import javax.swing.JLabel;

/**
 * An example to show how we can create a dynamic chart.
*/
public class DynamicLineAndTimeSeriesChart extends ApplicationFrame implements ActionListener {
	static int subsitution;

    /** The time series data. */
    private TimeSeries series;

    /** The most recent value added. */
    private double lastValue = 100.0;
   
    /** Timer to refresh graph after every 1/4th of a second */
    private Timer timer = new Timer(3000, this);
   
    /**
     * Constructs a new dynamic chart application.
     *
     * @param title  the frame title.
     */
    public DynamicLineAndTimeSeriesChart(final String title) {

        super(title);
        this.series = new TimeSeries("Random Data", Millisecond.class);
       
        final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
        final JFreeChart chart = createChart(dataset);
       
        timer.setInitialDelay(1000);
       
        //Sets background color of chart
        chart.setBackgroundPaint(Color.LIGHT_GRAY);
       
        //Created JPanel to show graph on screen
        final JPanel content = new JPanel(new BorderLayout());
       
        //Created Chartpanel for chart area
        final ChartPanel chartPanel = new ChartPanel(chart);
       
        //Added chartpanel to main panel
        content.add(chartPanel);
        
        //Sets the size of whole window (JPanel)
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        
        JLabel lblK = new JLabel("k");
        chartPanel.add(lblK);
       
        //Puts the whole content on a Frame
        setContentPane(content);
       
        timer.start();

    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return A sample chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            "Monitoring Table of",
            "Time",
            "Mb",
            dataset,
            false,
            false,
            false
        );
       
        final XYPlot plot = result.getXYPlot();
       
        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);
                
        ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);
       
        //Domain axis would show data of 60 seconds for a time
        xaxis.setFixedAutoRange(60000.0);  // 60 seconds
        xaxis.setVerticalTickLabels(true);
       
        ValueAxis yaxis = plot.getRangeAxis();
        yaxis.setRange(0.0, 300.0);
       
        return result;
    }
    /**
     * Generates an random entry for a particular call made by time for every 1/4th of a second.
     *
     * @param e  the action event.
     */
    public static int outputParser(String output)
    {
    	int sum = 0;
    	String[] line = output.split("\n");
    	String[] equal = null;

    	int ind = 0;
    	for (int i = 0; i < line.length; i++) {
    		equal = null;
    		equal = line[i].split("=");

    		if (equal[ind].trim().matches("........................10..."))
    		{
    			sum += Integer.parseInt(equal[1].trim());
    		}

    	}

    	return sum;

    }
    public static int inputParser(String output)
    {
    	int sum = 0;
    	String[] line = output.split("\n");
    	String[] equal = null;

    	int ind = 0;
    	for (int i = 0; i < line.length; i++) {
    		equal = null;
    		equal = line[i].split("=");

    		if (equal[ind].trim().matches(".......................10..."))
    		{
    			sum += Integer.parseInt(equal[1].trim());
    		}

    	}

    	return sum;

    }
    static int index =0;
	static ArrayList sumOfInt = new ArrayList();
    public void actionPerformed(final ActionEvent e) {
    	
    	frame1 fr = new frame1();
		SNMPWalk walk = new SNMPWalk();
		int subsitution2=0;
		int swap=0;
		
		String ip = "192.168.1.35";

		String outputIN = walk.main(ip, "1.3.6.1.2.1.31.1.1.1.6");
		String outputOUT = walk.main(ip, "1.3.6.1.2.1.31.1.1.1.10");
		 
		
		 sumOfInt.add(inputParser(outputIN));
		
		 
	//	System.out.println(sumOfIn+"\n\n\n");
	//System.out.println(sumOfOut);
		
	       if(index >=2)
	       {
	    	   subsitution=Integer.parseInt(sumOfInt.get(1).toString())-Integer.parseInt(sumOfInt.get(0).toString());
	    	   
	    	   sumOfInt.set(0, sumOfInt.get(1));
	    	   sumOfInt.remove(1);
	    	   
	   
	       }
        subsitution2 =subsitution-1000;
        
        
        
        final double factor = 0.9 + 0.2*Math.random();
        this.lastValue = this.lastValue * factor;
       
        final Millisecond now = new Millisecond();
        this.series.add(new Millisecond(), subsitution2);
       index++;

        System.out.println("Current Time in Milliseconds = " + now.toString()+", Current Value : "+this.lastValue);
    }

    /**
     * Starting point for the dynamic graph application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final DynamicLineAndTimeSeriesChart demo = new DynamicLineAndTimeSeriesChart("Dynamic Line And TimeSeries Chart");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

} 