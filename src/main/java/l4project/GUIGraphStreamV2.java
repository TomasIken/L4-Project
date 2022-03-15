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

public class GUIGraphStreamV2 {
	private JSONArray statements;
	private JSONArray edges;
	private JSONArray rulePrefrences;
	private JSONArray queryStatements;
	private Graph graph;


	public GUIGraphStreamV2(JSONObject jsonObject) {

		//Initialising the object
		statements = new JSONArray();
		edges = new JSONArray();
		rulePrefrences = new JSONArray();
		queryStatements = new JSONArray();
		graph = new MultiGraph("SG");



		//Reading the Json object
		splitJSON(jsonObject);

		System.out.println(statements);
		System.out.println(edges);
		System.out.println(rulePrefrences);
		System.out.println(queryStatements);

		//We create the nodes and edges
		createNodesAndEdges();

		//We apply a style for the graph
		applyStyle();

		System.setProperty("org.graphstream.ui", "swing");
		Viewer viewer = graph.display();
	}
	

	private void splitJSON(JSONObject json) {
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
	
	
	private void createNodesAndEdges() {
		for(int i = 0; i< statements.length();i++) {
			JSONObject currStatement = statements.getJSONObject(i);
			graph.addNode(currStatement.getString("id"));
			graph.getNode(currStatement.getString("id")).setAttribute("ui.label", currStatement.getString("title"));
		}
		for(int i = 0; i< queryStatements.length();i++) {
			JSONObject currStatement = queryStatements.getJSONObject(i);
			graph.addNode(currStatement.getString("id"));
			graph.getNode(currStatement.getString("id")).setAttribute("ui.label", currStatement.getString("title"));
			graph.getNode(currStatement.getString("id")).setAttribute("ui.class", "query");
		}
		for(int i = 0; i< edges.length();i++) {
			JSONObject currEdge = edges.getJSONObject(i);
			graph.addEdge(currEdge.getString("id"), currEdge.getString("source"), currEdge.getString("target"), true);

			if (! (currEdge.get("labelString").toString() == "null")){
				graph.getEdge(currEdge.getString("id")).setAttribute("ui.label", currEdge.getString("labelString"));
			}

			if(!currEdge.getString("type").equals("support")){
				graph.getEdge(currEdge.getString("id")).setAttribute("ui.class", "attackEdge");
			}
		}
	}


	public void applyStyle(){
		graph.setAttribute("ui.stylesheet",
				"node { "
						+ "shape: box;"
						+ "size-mode: fit;"
						+ "fill-color: #F4E4D4;"
						+ "stroke-mode: plain;"
						+ "stroke-color: #4F4E5A;"
						+ "stroke-width: 2px;"
						+ "padding: 1px;"
						+ "}"

						+ "node.query {"
						+ "shape: circle;"
						+ "fill-color: #4F4E5A;"
						+ "}"

						+ " edge {"
						+ "shape: line;"
						+ "size:2px;"
						+ "fill-color: #4F4E5A;"
						+ "arrow-size: 7px, 7px;"
						+ "}"

						+ " edge.attackEdge {"
						+ "shape: line;"
						+ "size:2px;"
						+ "fill-color: #f54242;"
						+ "arrow-size: 7px, 7px;"
						+ "}"

		);


	}
	
	
}
