package l4project;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.Scene;

import java.awt.Container;
import java.util.ArrayList;

import javax.swing.*;

import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.swing_viewer.*;

public class GUIGraphStreamV2 {
	private JSONArray statements;
	private JSONArray edges;
	private JSONArray rulePrefrences;
	private JSONArray queryStatements;
	private Graph graph;
	private String initialID;
	private String queryID;
	private Viewer viewer;
	public GUIGraphStreamV2() {
		graph = new MultiGraph("SG");
	}
	public void buildGraph(JSONObject jsonObject) {

		//Initialising the object
		statements = new JSONArray();
		edges = new JSONArray();
		rulePrefrences = new JSONArray();
		queryStatements = new JSONArray();
		//Reading the Json object
		splitJSON(jsonObject);

		//We create the nodes and edges
		createNodesAndEdges();
		
		setDepths(getRootID(),0);
		placeQueryOnTop();
//		setWidth(getRootID(),0);
		setWidth2();
		
		placeQueryOnTop();
		//We apply a style for the graph
		applyStyle();

	}
	public FxViewPanel javafxDisplay() {  
		//function to turn the graphstream viewer into a javafx compatible panel
        FxViewer v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD); 
        v.disableAutoLayout();
        FxViewPanel panel = (FxViewPanel)v.addDefaultView(false);
        return panel;
	}
	private void splitJSON(JSONObject json) {
		//turns the json file produced by graal-eldr into 4 different arrays able to be iterated through
			statements = json.getJSONArray("statements");
			edges = json.getJSONArray("edges");
			rulePrefrences = json.getJSONArray("rulePreferences");
			queryStatements = json.getJSONArray("queryStatements");
	}
	private void createNodesAndEdges () {
		// iterates though each JSON array and creates the appropriate edges and nodes with all the necessary information as attributes in each
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
			graph.getNode(currStatement.getString("id")).setAttribute("ui.label", currStatement.getString("title") + " " + currStatement.getString("labelString"));
//			graph.getNode(currStatement.getString("id")).setAttribute("ui.label", currStatement.getString("title"));
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
			else {
				graph.getEdge(currEdge.getString("id")).setAttribute("ui.class", "supportEdge");
			}
		}
	}
	private String getRootID() {
		//find the root node (T-> true)
		for(int i = 0; i< statements.length();i++) {
			JSONObject currStatement = statements.getJSONObject(i);
			if (currStatement.isNull("premises")) {
				initialID = currStatement.getString("id");
			}
		}
		return initialID;
	}
	private String getQueryID() {
		// returns the query ID. Only works with a single query KB
		for(int i = 0; i< queryStatements.length();i++) {
			JSONObject currQuery = queryStatements.getJSONObject(i);
			if (currQuery.getString("type").equals("claim")) {
				queryID = currQuery.getString("id");
			}
		}
		return queryID;
	}
	private void setDepths(String root,int parentDepth) {
		//orders nodes y position with the root at the bottom and each edge increasing the value of y by 1
			for(int i =0; i < edges.length();i++) {
				JSONObject currEdge = edges.getJSONObject(i);
				if (root.equals(currEdge.getString("source"))){
					graph.getNode(currEdge.getString("target")).setAttribute("y", parentDepth+1);
					setDepths(currEdge.getString("target"),parentDepth+1);
				}				
			}
	}

	private void setWidth2() {
		// places the nodes in their x positions overlaps and crossovers can still happen
		// but since UI allows for the reorganization of nodes this is considered not important
		boolean flag = false;
		int currDepth = 0;
		ArrayList<Node> nodesSet = new ArrayList<Node>();
		while (flag == false) {

			ArrayList<Node> currLevel = new ArrayList<Node>();
			for (int i = 0; i <graph.getNodeCount();i++) {
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
				flag = true;
			}
			currDepth = currDepth+1;
		}
	}
	private void placeQueryOnTop() {
		// sets the query y position to 1 above the highest node
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
		//returns the graph object
		return graph;
	}
	public Node getRoot() {
		//returns the root node object
		return graph.getNode(getRootID());
	}
	public Node getQuery(String query) {
		// returns the query node object
		// if no queries null is returned
		// if more than 1 queries the first one is returned
		if (queryStatements.length()>1) {
			for (int i = 0; i<queryStatements.length();i++) {
				if( queryStatements.getJSONObject(i).getString("title").equals(query.substring(0, query.length() -1))) {
					return graph.getNode(queryStatements.getJSONObject(i).getString("id"));
				}
			}
			return null;
		}
		else if (queryStatements.length()==1) {
			String id = queryStatements.getJSONObject(0).getString("id");
			return graph.getNode(id);
		}
		else {
			return null;
		}
		
	}
	private void applyStyle(){
		// CSS formating for the nodes and edges
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
						+ "fill-color: #a8a1ff;"
						+ "size-mode: fit;"
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
