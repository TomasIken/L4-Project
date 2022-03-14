package l4project;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Container;

import javax.swing.*;
import org.graphstream.ui.swing_viewer.*;

public class GUIGraphStream {
	private static JSONArray statements = new JSONArray();
	private static JSONArray edges = new JSONArray();
	private static JSONArray rulePrefrences = new JSONArray();
	private static JSONArray queryStatements = new JSONArray();
	private static Graph graph = new MultiGraph("SG");
	public GUIGraphStream(JSONObject jsonObject) {
		splitJSON(jsonObject);
		//showValues(edges);
		createNodesAndEdges();
		
		JFrame f=new JFrame();//creating instance of JFrame  
		f.setSize(1000,1000);//1000 width and 1000 height  
		f.setLayout(null);//using no layout managers  
		f.setVisible(true);//making the frame visible  
		
		System.setProperty("org.graphstream.ui", "javafx"); 
		Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		View view = viewer.addDefaultView(false);
	}
	

	private static void splitJSON(JSONObject json) { 
			statements = json.getJSONArray("statements");
			edges = json.getJSONArray("edges");
			rulePrefrences = json.getJSONArray("rulePreferences");
			queryStatements = json.getJSONArray("queryStatements");
	}
	
	
	@SuppressWarnings("unused")
	private static void showValues(JSONArray array) {
		for(int i = 0; i< array.length();i++) {
			System.out.println(array.getJSONObject(i));
		}
	}
	
	
	private static void createNodesAndEdges() {
		for(int i = 0; i< statements.length();i++) {
			JSONObject currStatement = statements.getJSONObject(i);
			graph.addNode(currStatement.getString("id"));
		}
		for(int i = 0; i< queryStatements.length();i++) {
			JSONObject currStatement = queryStatements.getJSONObject(i);
			graph.addNode(currStatement.getString("id"));
		}
		for(int i = 0; i< edges.length();i++) {
			JSONObject currEdge = edges.getJSONObject(i);
			graph.addEdge(currEdge.getString("id"), currEdge.getString("source"), currEdge.getString("target"));
		}
	}
	
	
}
