package edu.iastate.coms641.hw5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;

/**
 * Generate CFGs, Dependency Graphs and Callgraphs of a program
 * 
 * @author Danilo Dominguez Perez
 *
 */
public class Main {
	static boolean generateCFG = false;
	static boolean generatePDG = false;
	static boolean generateCG = false;
	static String method;
	static String clazz;
	static Map<String, Body> bodies = new HashMap<String, Body>();
	
	
	public static void main(String[] args) {
		// first parse the arguments
		String[] newArgs = parseArgs(args);
		
		// verify whether the use provide the right arguments
		if ((!generateCFG && !generatePDG && !generateCG) || method == null 
				|| clazz == null) {
			usage();
			System.exit(1);
		}
		
		PackManager.v().getPack("jtp").add(
				new Transform("jtp.methodinstrumenter", new BodyTransformer() {

					@Override
					protected void internalTransform(Body body, String arg1,
							Map<String, String> arg2) {
						if (clazz == "*" || (clazz != "*" && 
											body.getMethod().getDeclaringClass().getName().equals(clazz))) {
							bodies.put(body.getMethod().getSignature(), body);
						}
					}
					
				}));
		
		soot.Main.main(newArgs);
		
		System.gc();
		System.out.println("Number of methods: " + bodies.size());
		String outputDir = soot.SourceLocator.v().getOutputDir();
		try {
			List<SootClass> sootClasses = getSootClasses();
			List<SootMethod> sootMethods = getSootMethods(sootClasses);
			
			DotExporter cfgExporter = new DotExporter(sootMethods, bodies, outputDir, generateCFG, generatePDG, generateCG);
			cfgExporter.printGraphs();
			System.out.println("Graphs generated");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	private static List<SootMethod> getSootMethods(List<SootClass> sootClasses) {
		List<SootMethod> methods = null;
		if (sootClasses.size() > 1) {
			
		} else {
			SootClass clazz = sootClasses.get(0);
			if (method.equals("*")) {
				methods = clazz.getMethods();
			} else {
				methods = new ArrayList<SootMethod>();
				for (SootMethod sootMethod : clazz.getMethods()) {
					if (sootMethod.getName().equals(method)) {
						methods.add(sootMethod);
					}
				}
			}
		}
		
		return methods;
	}

	private static List<SootClass> getSootClasses() throws Exception {
		List<SootClass> classes = null;
		if (clazz.equals("*")) {
			classes = new ArrayList<SootClass>(Scene.v().getApplicationClasses());
		} else {
			SootClass sootClazz = Scene.v().loadClassAndSupport(clazz);
			if (sootClazz.isPhantomClass()) {
				throw new Exception("Class " + clazz + " does not exists");
			}
			
			classes = Collections.singletonList(sootClazz);
		}
		
		return classes;
	}

	static void usage() {
		System.err.println("Usage: java edu.iastate.coms641.hw5.Main " +
							"[--cfg --pdg --cg] --method=<name> " +
							"--class=<name>");
		System.err.println("At least one of the options should be selected");
		System.err.println("\t--cfg: generate CFG of the method");
		System.err.println("\t--pdg: generate DPG of the method");
		System.err.println("\t--cg: generate callgraph of the method");
		System.err.println("\t--method=<name>: Name of the method to" + 
							" generate. Use --method=* to generate all methods");
		System.err.println("\t--class=<name>: Name of the class to" + 
							" generate. Use --method=* to generate all classes");
	}
	
	static String[] parseArgs(String[] args) {
		List<String> newArgsList = new ArrayList<String>();
		for (String arg : args) {
			if (arg.equals("--cfg")) {
				generateCFG = true;
			} else if (arg.equals("--pdg")) {
				generatePDG = true;
			} else if (arg.equals("--cg")) {
				generateCG = true;
				newArgsList.add("-w");
			} else if (arg.startsWith("--method=")) {
				method = arg.substring("--method=".length());
			} else if (arg.startsWith("--class=")) {
					clazz = arg.substring("--class=".length());
			} else {
				newArgsList.add(arg);
			}
		}
		
		String[] newArgs = new String[newArgsList.size()];
		
		return newArgsList.toArray(newArgs);
	}


}
