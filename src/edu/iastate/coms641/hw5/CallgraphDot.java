package edu.iastate.coms641.hw5;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import soot.Body;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphConstants;
import soot.util.dot.DotGraphNode;
import edu.iastate.coms641.hw5.DependenceGraphDot.DotNamer;

public class CallgraphDot {

	public static void printGraph(Body body, String dir, Map<String, Body> bodies) {
		CallGraph cg = Scene.v().getCallGraph();
		DotNamer namer = new DotNamer((int) (cg.size() / 0.7f), 0.7f);
		DotGraph dotGraph = initDotGraph(body);
		
		Set<Body> visited = new HashSet<Body>();
		Queue<Body> worklist = new LinkedList<Body>();
		worklist.add(body);
		DotGraphNode dotnode = dotGraph.drawNode(namer.getName(body.getMethod().getSignature()));
		dotnode.setLabel(body.getMethod().getSignature());
		dotnode.setStyle(DotGraphConstants.NODE_STYLE_FILLED);
		
		while (!worklist.isEmpty()) {
			Body b = worklist.poll();
			visited.add(b);
			String nameSrc = namer.getName(b.getMethod().getSignature());
			
			for (Unit u : b.getUnits()) {
				Iterator<Edge> it = cg.edgesOutOf(u);
				if (it.hasNext()) {
					while (it.hasNext()) {
						Edge e = it.next();
						
						SootMethod m = e.getTgt().method();
						Body tgtBody = m.hasActiveBody() ? m.getActiveBody() : bodies.get(m.getSignature());
						if (tgtBody != null) {
							String nameTgt = m.getSignature();
							
							if (!visited.contains(tgtBody)) {
								DotGraphNode tgtDotnode = dotGraph.drawNode(namer.getName(nameTgt));
								tgtDotnode.setLabel(nameTgt);
								worklist.add(tgtBody);
							}
							
							dotGraph.drawEdge(nameSrc, namer.getName(nameTgt));
						}
					}
				}
			}
		}
		
		saveDot(body, dir, dotGraph);
	}
	
	private static DotGraph initDotGraph(Body body) {
		String graphname = "pdg";
		if (body != null) {
			graphname = body.getMethod().getSubSignature();
		}
		DotGraph canvas = new DotGraph(graphname);
		canvas.setGraphLabel(graphname);
		canvas.setPageSize(8.5, 11.0);
		canvas.setNodeShape(DotGraphConstants.NODE_SHAPE_BOX);

		return canvas;
	}
	
	private static void saveDot(Body body, String dir, DotGraph dotGraph) {
		String filename = dir;
		if (filename.length() > 0) {
			filename = filename + java.io.File.separator;
		}
		filename = filename
				+ body.getMethod().getSubSignature().replace(java.io.File.separatorChar, '.')
				+ DotGraph.DOT_EXTENSION;

		dotGraph.plot(filename);
	}

}
