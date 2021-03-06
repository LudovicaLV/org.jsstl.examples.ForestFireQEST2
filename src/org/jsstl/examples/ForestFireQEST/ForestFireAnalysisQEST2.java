package org.jsstl.examples.ForestFireQEST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import org.jsstl.core.formula.Formula;
import org.jsstl.core.formula.Signal;
import org.jsstl.core.formula.SignalStatistics2;
import org.jsstl.core.formula.jSSTLScript;
import org.jsstl.core.monitor.SpatialQuantitativeSignal;
import org.jsstl.core.space.GraphModel;
import org.jsstl.core.space.Location;
import org.jsstl.io.FolderSignalReader;
import org.jsstl.io.TxtSpatialQuantSat;
import org.jsstl.io.TxtSpatialQuantSignal;
import org.jsstl.monitor.spatial.ThreeValues;
import org.jsstl.monitor.spatial.SpatialThreeValues;
import org.jsstl.monitor.spatial.SpatialThreeValuesTransducer;
import org.jsstl.monitor.threevalues.ThreeValuesAtomic;
import org.jsstl.xtext.formulas.ScriptLoader;
import org.jsstl.xtext.formulas.sSTLSpecification.Model;

import Model.GlobalManager;

public class ForestFireAnalysisQEST2 {

	public static void main(String[] args) throws Exception {
		
		int runs = 10;
		String propertyName1 = "GSafe";
		String propertyName2 = "notFire";
		
	    //Run simulation (to get the spatial structure)
     	TotalFire.TotalFire2.main(null);
					
		// %%%%%%%%%%  GRAPH  %%%%%%%%% //		
		// Designing the grid

		int valueX = GlobalManager.getLocationManager().TwoDx;
		int valueY = GlobalManager.getLocationManager().TwoDy;
		
		GraphModel graph = GraphModel.createGrid(valueX, valueY, 1.0);
		// Computing of the distance matrix
		graph.dMcomputation();

	// %%%%%%%%% PROPERTY %%%%%%% //		
	// loading the formulas files
	ScriptLoader loader  = new ScriptLoader();
	jSSTLScript script = loader.load("data/spreadFireQEST2.sstl");
	// Loading the variables. That we have defined in the formulas files.
	
//	/// %%%%%%%  DATA import %%%%%%%%%%%%/////////
	//String [] var = script.getVariables();
	
/////////////  many RUNS  //////////

	double endT = TotalFire.TotalFire2.simulationTime;	
	double deltat = 0.1;
	//int steps = (int) (endT/deltat)+1;

			TotalFire.TotalFire2.main(null);
			double [][][] trajInit1 = ModelFire2.SimulatorFire2.data;
			double [] timeToInsertInit1 = ModelFire2.SimulatorFire2.timeArray;					

			double [][][] trajInit2 = ModelFire2.SimulatorFire2.data;
    		double [] timeToInsertInit2 = ModelFire2.SimulatorFire2.timeArray;		

			//			double [] val000= new double[traj.length];
//			double [] val0time= new double[traj[0].length];
//			for(int i=0; i < traj.length; i++){
//				val000[i]=traj[i][0][1];
//			}
//			for(int i=0; i < traj[0].length; i++){
//				val0time[i]=traj[0][i][0];
//			}
//			System.out.println("loc "+Arrays.toString(val000));
//			System.out.println("trajloc0 "+Arrays.toString(val0time));
			
			Signal signalInit1 = new Signal(graph, timeToInsertInit1, trajInit1);
			String[] varInit1 = {"D", "B", "EX", "S", "H", "T", "L", "P", "PA"};
			signalInit1.setVariables(varInit1);
			signalInit1.transfomTimeStep(endT,deltat);
			
			Signal signalInit2 = new Signal(graph, timeToInsertInit2, trajInit2);
			String[] varInit2 = {"D", "B", "EX", "S", "H", "T", "L", "P", "PA"};
			signalInit2.setVariables(varInit2);
			signalInit2.transfomTimeStep(endT,deltat);
			
		    SpatialQuantitativeSignal qSignalInit1 = script.quantitativeCheck(new HashMap<>(), propertyName1, graph, signalInit1);
		    int steps1 = qSignalInit1.getSteps();
			SignalStatistics2 statistic1 = new SignalStatistics2(graph.getNumberOfLocations(),steps1);	
		    statistic1.add(qSignalInit1.quantTraj());
		    
		    SpatialQuantitativeSignal qSignalInit2 = script.quantitativeCheck(new HashMap<>(), propertyName2, graph, signalInit2);
		    int steps2 = qSignalInit2.getSteps();
			SignalStatistics2 statistic2 = new SignalStatistics2(graph.getNumberOfLocations(),steps2);	
		    statistic2.add(qSignalInit2.quantTraj());
		    
	for ( int j=1 ; j<=runs ; j++) {
		TotalFire.TotalFire2.main(null);
			
		double [][][] traj = ModelFire2.SimulatorFire2.data;
		double [] timeToInsert = ModelFire2.SimulatorFire2.timeArray;		
		
//		double [] val000= new double[traj.length];
//		double [] val0time= new double[traj[0].length];
//		for(int i=0; i < traj.length; i++){
//			val000[i]=traj[i][0][1];
//		}
//		for(int i=0; i < traj[0].length; i++){
//			val0time[i]=traj[0][i][0];
//		}
//		System.out.println("loc "+Arrays.toString(val000));
//		System.out.println("trajloc0 "+Arrays.toString(val0time));
		
		Signal signal = new Signal(graph, timeToInsert, traj);
		String[] var = {"D", "B", "EX", "S", "H", "T", "L", "P", "PA"};
		signal.setVariables(var);
		signal.transfomTimeStep(endT,deltat);
	    SpatialQuantitativeSignal qSignal1 = script.quantitativeCheck(new HashMap<>(), propertyName1, graph, signal);
		statistic1.add(qSignal1.quantTraj());	
		
	    SpatialQuantitativeSignal qSignal2 = script.quantitativeCheck(new HashMap<>(), propertyName2, graph, signal);
		statistic2.add(qSignal2.quantTraj());	
	    
    }
	double [][] meanTraj1 = statistic1.getAverageTraj();
	System.out.println(Arrays.toString(meanTraj1[0]));
	
	double [][] meanTraj2 = statistic2.getAverageTraj();
	System.out.println(Arrays.toString(meanTraj2[0]));

	
//	double [][] sq = statistic.getSquareTraj();
//	System.out.println(Arrays.toString(sq[0]));
	
	double [][] sdTraj1 = statistic1.getStandardDeviationTraj();
	System.out.println(Arrays.toString(sdTraj1[0]));
	
	double [][] sdTraj2 = statistic2.getStandardDeviationTraj();
	System.out.println(Arrays.toString(sdTraj2[0]));

	for (int i=0; i< graph.getNumberOfLocations(); i++){
		System.out.println(graph.getLocation(i) + " -> " + meanTraj1[i][10]);
	}
	
	for (int i=0; i< graph.getNumberOfLocations(); i++){
		System.out.print(meanTraj1[i][0] + ", ");
	}
	System.out.println(" ");
	
	for (int i=0; i< graph.getNumberOfLocations(); i++){
		System.out.print(meanTraj2[i][0] + ", ");
	}
	System.out.println(" ");

	
	for (int i=0; i< graph.getNumberOfLocations(); i++){
		System.out.print(sdTraj1[i][0] + ", ");
	}
	
	System.out.println(" ");

	for (int i=0; i< graph.getNumberOfLocations(); i++){
		System.out.print(sdTraj2[i][0] + ", ");
	}
	
	System.out.println(" ");
	

	HashMap<Integer,SpatialThreeValues> TimeTSLValues1 = new HashMap<>();
	
	for (int j=0; j < meanTraj1[0].length; j++){
	SpatialThreeValues resultFireTSL1 = new SpatialThreeValues(graph);
	for (int i=0; i < meanTraj1.length; i++){		
		double a = meanTraj1[i][j] - sdTraj1[i][j];
		double b = meanTraj1[i][j] + sdTraj1[i][j];
		double k = 0.01;
		String check = ">";
		ThreeValues value1 = ThreeValuesAtomic.checkIneq(a, b, k, check);
		resultFireTSL1.addLoc(graph.getLocation(i), value1);
		TimeTSLValues1.put(j, resultFireTSL1);
	}
	}
	

	HashMap<Integer,SpatialThreeValues> TimeTSLValues2 = new HashMap<>();
	
	for (int j=0; j < meanTraj2[0].length; j++){
	SpatialThreeValues resultFireTSL2 = new SpatialThreeValues(graph);
	for (int i=0; i < meanTraj2.length; i++){		
		double a = meanTraj2[i][j] - sdTraj2[i][j];
		double b = meanTraj2[i][j] + sdTraj2[i][j];
		double k = 0.2;
		String check = "<";
		ThreeValues value = ThreeValuesAtomic.checkIneq(a, b, k, check);
		resultFireTSL2.addLoc(graph.getLocation(i), value);
		TimeTSLValues2.put(j, resultFireTSL2);
	}
	}
	
	
//	for (int i=0; i< graph.getNumberOfLocations(); i++){
//		if (resultFire1.spatialThreeValues.get(graph.getLocation(i))==ThreeValues.FALSE){
//		System.out.print(0 + ", ");}else{
//			if(resultFire1.spatialThreeValues.get(graph.getLocation(i))==ThreeValues.TRUE){
//				System.out.print(2 + ", ");
//			}else{
//				System.out.print(1 + ", ");
//			}
//		}
//	}
	
	
//	for (int i=0; i< graph.getNumberOfLocations(); i++){
//		if (resultFire2.spatialThreeValues.get(graph.getLocation(i))==ThreeValues.FALSE){
//		System.out.print(0 + ", ");}else{
//			if(resultFire2.spatialThreeValues.get(graph.getLocation(i))==ThreeValues.TRUE){
//				System.out.print(2 + ", ");
//			}else{
//				System.out.print(1 + ", ");
//			}
//		}
//	}


	//SpatialThreeValues formulaEvery = SpatialThreeValuesTransducer.everywhere(resultFire2, 0, 5);
	//SpatialThreeValues formulaFinal = SpatialThreeValuesTransducer.surround(resultFire1, formulaEvery, 0, 5);
	
	HashMap<Integer,SpatialThreeValues> TimeTSL1 = new HashMap<>();
//	SpatialThreeValues formula1 = SpatialThreeValuesTransducer.not(resultFire1);
	
	for (int j=0; j < meanTraj1[0].length; j++){
	for (int i=0; i < meanTraj1.length; i++){		
		SpatialThreeValues formula1 = SpatialThreeValuesTransducer.not(TimeTSLValues1.get(j));	
		TimeTSL1.put(j, formula1);
	}
	}
	System.out.print("fatto 1 ");

	
	HashMap<Integer,SpatialThreeValues> TimeTSL2 = new HashMap<>();
//	SpatialThreeValues formula2 = SpatialThreeValuesTransducer.and(resultFire2, formula1);
	
	for (int j=0; j < meanTraj2[0].length; j++){
	for (int i=0; i < meanTraj2.length; i++){		
		SpatialThreeValues formula2 = SpatialThreeValuesTransducer.and(TimeTSLValues2.get(j), TimeTSL1.get(j));
		TimeTSL2.put(j, formula2);
	}
	}
	System.out.print("fatto 2 ");
	
	HashMap<Integer,SpatialThreeValues> TimeTSL3 = new HashMap<>();
//	SpatialThreeValues formula3 = SpatialThreeValuesTransducer.not(resultFire2);

	for (int j=0; j < meanTraj2[0].length; j++){
	for (int i=0; i < meanTraj2.length; i++){		
		SpatialThreeValues formula3 = SpatialThreeValuesTransducer.not(TimeTSLValues2.get(j));
		TimeTSL3.put(j, formula3);
	}
	}
	System.out.print("fatto 3 ");
	
	HashMap<Integer,SpatialThreeValues> TimeTSL4 = new HashMap<>();
//	SpatialThreeValues formula4 = SpatialThreeValuesTransducer.surround(formula2, formula3, 0, 5);

	for (int j=0; j < meanTraj2[0].length; j++){
	for (int i=0; i < meanTraj2.length; i++){		
		SpatialThreeValues formula4 = SpatialThreeValuesTransducer.surround(TimeTSL2.get(j), TimeTSL3.get(j), 1, 2);
		TimeTSL4.put(j, formula4);
	}
	}
	
	System.out.print("fatto 4 ");

	HashMap<Integer,SpatialThreeValues> TimeTSLFinal = new HashMap<>();
//	SpatialThreeValues formulaFinal = SpatialThreeValuesTransducer.not(formula4);

	for (int j=0; j < meanTraj2[0].length; j++){
	for (int i=0; i < meanTraj2.length; i++){		
		SpatialThreeValues formulaFinal = SpatialThreeValuesTransducer.not(TimeTSL4.get(j));
		TimeTSLFinal.put(j, formulaFinal);
	}
	}
	
	System.out.print("fatto ");

	//it worked changing meanTraj1 with meanTraj2 in the formulas verification
    //they have different length since one have a time interval inside, that's why :)
	
//	System.out.println(" ");
//	
//	for (int i=0; i< graph.getNumberOfLocations(); i++){
//		if (TimeTSLFinal.get(i).spatialThreeValues.get(graph.getLocation(i))==ThreeValues.FALSE){
//		System.out.print(0 + ", ");}else{
//			if(TimeTSLFinal.get(i).spatialThreeValues.get(graph.getLocation(i))==ThreeValues.TRUE){
//				System.out.print(2 + ", ");
//			}else{
//				System.out.print(1 + ", ");
//			}
//		}
//	}
//	

	
	/////  write  (I commented this for the moment)
//		String text = "";
//		for (int i=0; i<meanTraj.length;i++) {
//			for (int j = 0; j < meanTraj[0].length; j++) {
//					text += String.format(Locale.US, " %20.10f", meanTraj[i][j]);
//			}
//			text += "\n";
//		}
//		PrintWriter printer = new PrintWriter("data/meanDataQuantSignalFireInitial.txt");
//		printer.print(text);
//		printer.close();
	
	String text = "";	
	
		
		for (int i=0; i< graph.getNumberOfLocations(); i++){
			for (int j=0; j<TimeTSLFinal.size();j++) {
			if (TimeTSLFinal.get(j).spatialThreeValues.get(graph.getLocation(i))==ThreeValues.FALSE){
				text += String.format(Locale.US, " %20.10f", 0.2);}else{
					if(TimeTSLFinal.get(j).spatialThreeValues.get(graph.getLocation(i))==ThreeValues.TRUE){
						text += String.format(Locale.US, " %20.10f", 0.8);
					}else{
						text += String.format(Locale.US, " %20.10f", 0.5);
					}
				}			
		}
		text += "\n";
	}
	PrintWriter printer = new PrintWriter("data/TSLFireInitial.txt");
	printer.print(text);
	printer.close();
	

}
	
	
}	
	
	
