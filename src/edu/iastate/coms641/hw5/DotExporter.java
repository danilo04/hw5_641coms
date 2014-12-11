package edu.iastate.coms641.hw5;

import java.io.File;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.SootMethod;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.tools.CFGViewer;
import soot.util.cfgcmd.CFGGraphType;
import soot.util.cfgcmd.CFGToDotGraph;
import soot.util.dot.DotGraph;

public class DotExporter extends CFGViewer {
	protected final List<SootMethod> methods;
	protected final Map<String, Body> bodies;
	protected final String outputDir;
	protected final boolean cfg;
	protected final boolean pdg;
	protected final boolean cg;
	private CFGGraphType graphtype;
	private CFGToDotGraph drawer;

	public DotExporter(List<SootMethod> methods, 
						Map<String, Body> bodies, 
						String outputDir,
						boolean cfg,
						boolean pdg,
						boolean cg) {
		this.methods = methods;
		this.bodies = bodies;
		this.outputDir = outputDir;
		this.cfg = cfg;
		this.pdg = pdg;
		this.cg = cg;
		graphtype = CFGGraphType.getGraphType("BriefUnitGraph");
	}
	
	public void printGraphs() {
		for (SootMethod method : methods) {
			if (bodies.containsKey(method.getSignature())) {
				Body body = bodies.remove(method.getSignature());
				if (cfg) {
					drawer = new CFGToDotGraph();
					drawer.setBriefLabels(false);
					drawer.setOnePage(false);
					drawer.setUnexceptionalControlFlowAttr("color", "black");
					drawer.setExceptionalControlFlowAttr("color", "red");
					drawer.setExceptionEdgeAttr("color", "lightgray");
					drawer.setShowExceptions(Options.v().show_exception_dests());
					String dir = outputDir + File.separator + "cfgs";
					createDirectory(dir);
					printCFG(body, dir);
				}
				if (pdg) {
					String dir = outputDir + File.separator + "pdgs";
					createDirectory(dir);
					printPDG(body, dir);
				}
				
				if (cg) {
					String dir = outputDir + File.separator + "cgs";
					createDirectory(dir);
					printCG(body, dir);
				}
				
				body = null;
			}
		}
	}
	
	private void printCG(Body body, String dir) {
		CallgraphDot.printGraph(body, dir, bodies);
		
	}

	private void printPDG(Body body, String dir) {
		DependenceGraphDot.printGraph(body, dir);
	}

	@SuppressWarnings("rawtypes")
	private void printCFG(Body body, String dir) {
		DirectedGraph graph = graphtype.buildGraph(body);
		DotGraph canvas = graphtype.drawGraph(drawer, graph, body);

		String methodname = body.getMethod().getSubSignature();
		String filename = dir;
		if (filename.length() > 0) {
			filename = filename + java.io.File.separator;
		}
		filename = filename
				+ methodname.replace(java.io.File.separatorChar, '.')
				+ DotGraph.DOT_EXTENSION;

		canvas.plot(filename);
	}

	private static void createDirectory(String dir) {
		new File(dir).mkdir();
	}
}
