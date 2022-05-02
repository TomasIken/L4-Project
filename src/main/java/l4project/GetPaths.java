package l4project;

import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class GetPaths {
	Graph graph;
	Node query;
	Node root;
	// get all possible paths from two nodes (root and query)
	public GetPaths(Graph g, Node r, Node q) {
		graph = g;
		query = q;
		root = r;
	}
	// recursive function
	private ArrayList<ArrayList<String>> FS(Node currNode, Node root) {
		// variable initialization
		ArrayList<ArrayList<String>> paths = new ArrayList<ArrayList<String>>();
		ArrayList<String> currPath = new ArrayList<String>();
		ArrayList<ArrayList<String>> pathsTo = new ArrayList<ArrayList<String>>();
		// base case
		if (currNode == root) {
			// return
			currPath.add(currNode.getId());
			paths.add(currPath);
			return paths;
		}
		//else 
		else {
			//find neighboring nodes
			ArrayList<String> neighbourNodes= getNeighbourghs(currNode);
			// get all paths from each neighbor  to the root
			for (String nei: neighbourNodes) {
				pathsTo.addAll((FS(graph.getNode(nei),root)));
			}
			for (ArrayList<String> path: pathsTo) {
				// convert the node objects retrieved to ID strings
				path.add(currNode.getId());
				paths.add(path);
			}
		}
		return paths;
		
	}
	private ArrayList<String> getNeighbourghs(Node node){
		// returns all IDs of neighboring nodes
		ArrayList<String> out = new ArrayList<String>();
		for (int i = 0 ; i< node.getInDegree(); i++) {
			Edge currEdge = node.getEnteringEdge(i);
			out.add(currEdge.getNode0().getId());
		}
		return out;
	}
	public ArrayList<ArrayList<String>> getPathId() {
		// Global function for string ID paths
		ArrayList<ArrayList<String>> path = FS(query,root);
		return path;
	}
	public ArrayList<ArrayList<Node>> getPathNode(){
		// Global function taht converts the stringId paths into node objects paths
		ArrayList<ArrayList<Node>> pathNode = new ArrayList<ArrayList<Node>>();
		ArrayList<ArrayList<String>> pathId = FS(query,root);
		for (ArrayList<String> path: pathId) {
			ArrayList<Node> currPathNode = new ArrayList<Node>();
			for (String node: path) {
				currPathNode.add(graph.getNode(node));
			}
			pathNode.add(currPathNode);
		}
		return pathNode;
	}
}
