package edu.iastate.coms641.hw5;

/**
 * Generate CFGs, Dependency Graphs and Callgraphs of a program
 * 
 * @author Danilo Dominguez Perez
 *
 */
public class Main {
	static boolean generateCFG = false;
	static boolean generateDPG = false;
	static boolean generateCG = false;
	static String method;
	static String clazz;
	
	public static void main(String[] args) {
		// first parse the arguments
		parseArgs(args);
		
		// verify whether the use provide the right arguments
		if ((!generateCFG && !generateDPG && !generateCG) || method == null 
				|| clazz == null) {
			usage();
			System.exit(1);
		}
		
		
		
	}
	
	static void usage() {
		System.err.println("Usage: java edu.iastate.coms641.hw5.Main " +
							"[--cfg --dpg --cg] --method=<name> " +
							"--class=<name>");
		System.err.println("At least one of the options should be selected");
		System.err.println("\t--cfg: generate CFG of the method");
		System.err.println("\t--dpg: generate DPG of the method");
		System.err.println("\t--cg: generate callgraph of the method");
		System.err.println("\t--method=<name>: Name of the method to" + 
							" generate. Use --method=* to generate all methods");
		System.err.println("\t--class=<name>: Name of the class to" + 
							" generate. Use --method=* to generate all classes");
	}
	
	static void parseArgs(String[] args) {
		for (String arg : args) {
			if (arg.equals("--cfg")) {
				generateCFG = true;
			} else if (arg.equals("--dpg")) {
				generateDPG = true;
			} else if (arg.equals("--cg")) {
				generateCG = true;
			} else if (arg.startsWith("--method=")) {
				method = arg.substring("--method=".length() + 1);
			} else if (arg.startsWith("--class=")) {
					clazz = arg.substring("--class=".length() + 1);
			}
		}
	}


}
