package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.plaf.ToolTipUI;

import data.DBinterface;
import data.State;


import simulations.P5Canvas;
import simulations.Unit2;
import simulations.models.Compound;
import simulations.models.Molecule;

import static simulations.P5Canvas.*;
import static simulations.models.Compound.*;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

//import main.DynamicGraph.DataGenerator;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import Util.ColorCollection;

/**
 * * A demo application showing a dynamically updated chart that displays the *
 * current JVM memory usage. *
 * <p>
 * * IMPORTANT NOTE: THIS DEMO IS DOCUMENTED IN THE JFREECHART DEVELOPER GUIDE.
 * * DO NOT MAKE CHANGES WITHOUT UPDATING THE GUIDE ALSO!!
 */
public class Canvas extends JPanel {
	/** Time series for total memory used. */
	private XYSeries total;
	/** Time series for free memory. */
	private XYSeries free;
	
	private int count = 0 ;
	
	private AxisChangeListener rangeListener;
	
	private P5Canvas p5Canvas;
	private TableView tableView;
	private Main main;
	private int numGraph;
	private int indexGraph;
	private ArrayList<String[]> labelStrings;
	private boolean paintLineEnabled;
	private final int numGraphMax = 5;
	private DynamicGraph  dynamicGraph;
	private int tickInterval = 1000;   //In millisecond
	Color colorLine[];

	/**
	 * * Creates a new application. * * @param maxAge the maximum age (in
	 * milliseconds).
	 */
	public Canvas( Main parent) {
		super(new BorderLayout());
		main = parent;
		p5Canvas = main.getP5Canvas();
		tableView = main.getTableView();
		colorLine = ColorCollection.getColorGraphLine();
		dynamicGraph = new DynamicGraph(this);
		this.add(dynamicGraph);

	}
	

	
	public void reset(){

		setupGraphNum();
		setupLabels();
		
		//Reset dynamic graph
		dynamicGraph.reset(numGraph);
		
		//Choose to show graph with current index
		showGraphByIndex(indexGraph);
		
		setPaintLineEnable(true);

		this.repaint();
	}
	

	
	private void setupGraphNum()
	{
		int unit = main.getSelectedUnit();
		int sim = main.getSelectedSim();
		numGraph = 1;
		if(unit==8 && (sim==6||sim==7))
			numGraph = 2;
		indexGraph = 0;

	}
	
	protected void setupLabels()
	{
		
		int unit = main.getSelectedUnit();
		int sim = main.getSelectedSim();
		
		//Set up String arrays

		labelStrings = new ArrayList<String []>();
		
		//Set up X Labels
		labelStrings.add(new String[2]);
		labelStrings.get(0)[0]= new String("Time (s)");
		if(unit==8)
		{
			if(sim==6) //Volume of Base - PH
			{
				labelStrings.add(new String[2]);
				labelStrings.get(1)[0]= new String("Time (s)");
			}
			else if(sim==7)
			{
				labelStrings.add(new String[2]);
				labelStrings.get(1)[0] = new String("Time (s)");
			}
		}
		
		//Set up Y Labels
		switch(unit)
		{
		default:
			labelStrings.get(0)[1] = new String("# molecules");
		break;
		case 3:
		case 4:
			labelStrings.get(0)[1] = new String("total mass (g)");
			break;
		case 5:
		case 6:
			labelStrings.get(0)[1] = new String("Concentration (M)");
			break;
		case 7:
			if(sim==2)
				labelStrings.get(0)[1] = new String("total mass (g)");
			else if(p5Canvas.getSim()==8||p5Canvas.getSim()==7)
				labelStrings.get(0)[1] = new String("Moles");
			else
				labelStrings.get(0)[1] = new String("# molecules");
			break;
		case 8:
				labelStrings.get(0)[1] = new String("# molecules");
				if(sim==6)
				{
					
					labelStrings.get(1)[1] = new String("    pH    ");
				}
				else if(sim==7)
					labelStrings.get(1)[1] = new String("    pH    ");
			break;

		}
			
	}
	
	
	//Called by timer every time timer ticks
	public void addDataPerTick()
	{
		//Create and Add dynamic graphs to Canvas
		
		dynamicGraph.addDataObservation();
		
	}
	
	//Choose which graph to show 
	public void showGraphByIndex(int index)
	{
		dynamicGraph.showPlot(index);
	}
	
	//Change graph when user click on switch graph button
	public void switchGraph()
	{
		indexGraph = (++indexGraph)%numGraph;
		showGraphByIndex(indexGraph);
	}
	
	//Called by 
	public void setRange(int index,int lower,int upper)
	{
		
	}
	
	//Define the data input type for different simulations 
	private float dataConversion(int index, int unit,int indexOfCompound)
	{
		float res=0;
		String name = null;
		int sim = p5Canvas.getSim();
		switch(unit)
		{
			default:
				res = Compound.counts.get(indexOfCompound);
				break;
			case 3:
				name = (String)Compound.names.get(indexOfCompound);
				res =  p5Canvas.getUnit3().getMassByName(name);
				break;
			case 5:
				res = Float.parseFloat((String) tableView.data[0].get(indexOfCompound));
				break;
			case 6:
				name = (String)Compound.names.get(indexOfCompound);
				res = p5Canvas.getUnit6().getConByName(name);
				break;
			case 7:
				if(sim==2)
				{
					name = (String)Compound.names.get(indexOfCompound);
					res =  p5Canvas.getUnit7().getMassByName(name);
				}
//				else if(sim==8){
//					name = (String)Compound.names.get(indexOfCompound);
//					res =  p5Canvas.getUnit7().getMoleByName(name);
//				}
				else 
				{
					res = Compound.counts.get(indexOfCompound);
				}
				break;
			case 8:
				if(sim==6)
				{
					if(index==0)
					{
						res = Compound.counts.get(indexOfCompound);
					}
					else if(index==1)
					{
						res= p5Canvas.getUnit8().getPH();
					}
				}
				else if(sim==7)
				{
					if(index==0)
					{
						res = Compound.counts.get(indexOfCompound);
					}
					else if(index==1)
					{
						res= p5Canvas.getUnit8().getPH();
					}
				}
				else
				{
					res = Compound.counts.get(indexOfCompound);
				}
				break;
		}
		return res;
	}
	

	
	public void setPaintLineEnable(boolean flag)
	{
		paintLineEnabled = flag;
		
	}
	public boolean getPaintLineEnable()
	{
		return paintLineEnabled;
	}
	
	//Get molecules number from simulation before painting
	public void updateMoleculeCount(){
		
		int unit = p5Canvas.getUnit();
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		
		
		
		//For particular cases
		if(unit==1)
		{
			if (sim==4||(sim==2&&set==2)){
				int H2OIndex = names.indexOf("Water");
				int OIndex = names.indexOf("Oxygen");
				int H2O2Index = names.indexOf("Hydrogen-Peroxide");
				int H2OCount =0;
				int OCount =0;
				int H2O2Count =0;
				for (int i=0; i<State.molecules.size();i++){
					Molecule m = (Molecule) State.molecules.get(i);
					if (m.getName().equals("Water")){
						H2OCount++;
					}
					else if (m.getName().equals("Oxygen")){
						OCount++;
					}
					else if (m.getName().equals("Hydrogen-Peroxide")){
						H2O2Count++;
					}
				}
				Compound.counts.set(H2OIndex,H2OCount);
				Compound.counts.set(OIndex, OCount);
				Compound.counts.set(H2O2Index, H2O2Count);
				
			}
		}
		else if(unit==2)
		{
			main.getP5Canvas().getUnit2().updateMoleculeCount(sim, set);
		}
		
		
	}

	/**
	 * * Adds an observation to the �total memory� time series. * * @param y the
	 * total memory used.
	 */
	private void addTotalObservation(double y) {
		this.total.add(count, y);
	}

	/**
	 * * Adds an observation to the �free memory� time series. * * @param y the
	 * free memory.
	 */
	private void addFreeObservation(double y) {
		this.free.add(count, y);
		count++;
	}
	
	class NumberAxisAd extends NumberAxis{
		
		public NumberAxisAd(String str)
		{
			super(str);
		}
		
		protected void autoAdjustRange(){
	        Plot plot = getPlot();
	                if (plot == null) {
	                    return;  // no plot, no data
	                }
	        
	                if (plot instanceof ValueAxisPlot) {
	                    ValueAxisPlot vap = (ValueAxisPlot) plot;
	        
	                    Range r = vap.getDataRange(this);
	                    if (r == null) {
	                        r = getDefaultAutoRange();
	                    }
	        
	                    double upper = r.getUpperBound();
	                    double lower = r.getLowerBound();
	                    if (this.getRangeType() == RangeType.POSITIVE) {
	                        lower = Math.max(0.0, lower);
	                        upper = Math.max(0.0, upper);
	                    }
	                    else if (this.getRangeType() == RangeType.NEGATIVE) {
	                        lower = Math.min(0.0, lower);
	                        upper = Math.min(0.0, upper);
	                    }
	        
	                    if (getAutoRangeIncludesZero()) {
	                        lower = Math.min(lower, 0.0);
	                        upper = Math.max(upper, 0.0);
	                    }
	                    double range = upper - lower;
	                    double minRange = getAutoRangeMinimumSize();
	                    if(range>=minRange)
	                    	this.setAutoRangeMinimumSize(2*minRange);
	                   
	                }
	                super.autoAdjustRange();
	                }
	}

	//GetLabelString
	//Input: index - index of graph
	public ArrayList<String []> getLabelStrings()
	{
		return labelStrings;
	}
	public double getDataTickX(int index)
	{
		double xValue=0;
		switch(index)
		{
		case 0:
			xValue = (double)main.time;
			break;
		case 1:
			xValue= (double)main.time;
			break;
		}
		return xValue;
	}
	
	public Color getColorLine(int index)
	{
		return colorLine[index];
	}
	public int getColorLineNum()
	{
		return colorLine.length;
	}
	
	public ArrayList<Double> getDataTickY(int index)
	{
		//0 is x Value, 1 is Y value 
		ArrayList<Double > dataTick= new ArrayList<Double>();
		int unit = p5Canvas.getUnit();
		int sim = p5Canvas.getSim();
		
		switch(index)
		{
		case 0:
				int size = tableView.getItemNum(); 
				
				for (int i=0; i<size;i++){
					double value = dataConversion(index, unit, i);	
					dataTick.add(value);
				}
				break;
		case 1:

			if((unit==8&&sim==6) ||(unit==8&&sim==7))
			{
				//Show Time - PH graph
				double value = dataConversion(index,unit,0);
				dataTick.add(value);
			}
			break;
		}
		return dataTick;
		
	}
	
	//Get the name of series for dynamic graph
	public ArrayList<String> getDataNames(int index)
	{
		ArrayList<String> nameList = null;
		switch(index)
		{
		case 0:
			nameList = tableView.getCompoundNames();
			break;
		case 1:
			nameList = new ArrayList<String>();
			nameList.add("PH");
			break;
		}
		return nameList;
	}
	
	
	

	
	public Main getMain(){
		return this.main;
	}

}

