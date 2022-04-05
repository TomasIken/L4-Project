package l4project;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Container;
import java.util.ArrayList;

import javax.swing.*;
import org.graphstream.ui.swing_viewer.*;

public class GUIGraphStreamV2 {
	private JSONArray statements;
	private JSONArray edges;
	private JSONArray rulePrefrences;
	private JSONArray queryStatements;
	private Graph graph;
	private String initialID;
	private String queryID;

	public GUIGraphStreamV2(JSONObject jsonObject) {

		//Initialising the object
		statements = new JSONArray();
		edges = new JSONArray();
		rulePrefrences = new JSONArray();
		queryStatements = new JSONArray();
		graph = new MultiGraph("SG");



		//Reading the Json object
		splitJSON(jsonObject);

//		System.out.println(statements);
//		System.out.println(edges);
//		System.out.println(rulePrefrences);
//		System.out.println(queryStatements);

		//We create the nodes and edges
		createNodesAndEdges();
		
		setDepths(getRootID(),0);
		placeQueryOnTop();
//		setWidth(getRootID(),0);
		setWidth2();
		
		placeQueryOnTop();
		//We apply a style for the graph
		applyStyle();

		System.setProperty("org.graphstream.ui", "swing");
		Viewer viewer = graph.display();
		viewer.disableAutoLayout();
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
	
	
	private void createNodesAndEdges () {
		for(int i = 0; i< statements.length();i++) {
			JSONObject currStatement = statements.getJSONObject(i);
			graph.addNode(currStatement.getString("id"));
			graph.getNode(currStatement.getString("id")).setAttribute("ui.label", currStatement.getString("title"));
			graph.getNode(currStatement.getString("id")).setAttribute("type", currStatement.getString("type"));
			graph.getNode(currStatement.getString("id")).setAttribute("ruleType", currStatement.getJSONObject("ruleApplication").getString("type"));
		}
		
		for(int i = 0; i< queryStatements.length();i++) {
			JSONObject currStatement = queryStatements.getJSONObject(i);
			graph.addNode(currStatement.getString("id"));
			graph.getNode(currStatement.getString("id")).setAttribute("ui.label", currStatement.getString("title"));
			graph.getNode(currStatement.getString("id")).setAttribute("ui.class", "query");
			graph.getNode(currStatement.getString("id")).setAttribute("type", currStatement.getString("type"));

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
	
	private String getRootID() {
		for(int i = 0; i< statements.length();i++) {
			JSONObject currStatement = statements.getJSONObject(i);
			if (currStatement.isNull("premises")) {
				initialID = currStatement.getString("id");
			}
		}
		return initialID;
	}
	private String getQueryID() {
		for(int i = 0; i< queryStatements.length();i++) {
			JSONObject currQuery = queryStatements.getJSONObject(i);
			if (currQuery.getString("type").equals("claim")) {
				queryID = currQuery.getString("id");
			}
		}
		return queryID;
	}
	private void setDepths(String root,int parentDepth) {
			for(int i =0; i < edges.length();i++) {
				JSONObject currEdge = edges.getJSONObject(i);
				if (root.equals(currEdge.getString("source"))){
					graph.getNode(currEdge.getString("target")).setAttribute("y", parentDepth+1);
					setDepths(currEdge.getString("target"),parentDepth+1);
				}				
			}
	}
	private void setWidth(String root,int parentWidth) {
		Node currRoot = graph.getNode(root);
		int currWidth =parentWidth - currRoot.getOutDegree()%2; 
		for (int i = 0; i <currRoot.getOutDegree();i++) {
			currRoot.getLeavingEdge(i).getNode1().setAttribute("x", currWidth+i);
			currWidth+=1;
		}
		currWidth =parentWidth - currRoot.getOutDegree()%2; 
		for (int i = 0; i <currRoot.getOutDegree();i++) {
			setWidth(currRoot.getLeavingEdge(i).getNode1().getId(),currWidth+i);
		}
	}
	private void setWidth2() {
		boolean flag = false;
		int currDepth = 0;
		ArrayList<Node> nodesSet = new ArrayList<Node>();
		while (flag == false) {

			ArrayList<Node> currLevel = new ArrayList<Node>();
			for (int i = 0; i <graph.getNodeCount();i++) {
				System.out.println(graph.getNode(i).getAttribute("y"));
				if (graph.getNode(i).getAttribute("y").equals(currDepth)) {
					currLevel.add(graph.getNode(i));
				}
			}

			int iter = 1;
			for (Node node: currLevel) {
				float position = (10 * iter)/ (currLevel.size()+1);
				node.setAttribute("x", position);
				iter = iter+1;
			}

			nodesSet.addAll(currLevel);
			if (nodesSet.size() >= (graph.getNodeCount())) {
				graph.getNode(getRootID()).setAttribute("x", 5);
				graph.getNode(getQueryID()).setAttribute("x", 5);
				flag = true;
			}
			currDepth = currDepth+1;
		}
	}
	private void placeQueryOnTop() {
		graph.getNode(getRootID()).setAttribute("y",0);
		int maxDepth= 0;
		for(int i = 0 ; i<statements.length(); i++) {
			JSONObject currStatement = statements.getJSONObject(i);
			int currDepth = (Integer) graph.getNode(currStatement.getString("id")).getAttribute("y");
			if (currDepth >maxDepth) {
				maxDepth = currDepth;
			}
		}
		maxDepth++;
		graph.getNode(getQueryID()).setAttribute("y", maxDepth);

	}
	public Graph getGraph() {
		return graph;
	}
	public Node getRoot() {
		return graph.getNode(getRootID());
	}
	public Node getQuery() {
		if (queryStatements.length()>1) {
			return null;
		}
		else {
			String id = queryStatements.getJSONObject(0).getString("id");
			return graph.getNode(id);
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
						+ "padding: 10px;"
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
