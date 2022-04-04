package l4project;

import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class GetPaths {
	// https://tel.archives-ouvertes.fr/tel-01904558v1/file/thesis_final_HECHAM.pdf p 89 (105 in search)
	Graph graph;
	Node query;
	Node root;
	
	public GetPaths(Graph g, Node r, Node q) {
		graph = g;
		query = q;
		root = r;
	}
	
	private ArrayList<ArrayList<String>> FS(Node currNode, Node root) {
		ArrayList<ArrayList<String>> paths = new ArrayList<ArrayList<String>>();
		ArrayList<String> currPath = new ArrayList<String>();
		ArrayList<ArrayList<String>> pathsTo = new ArrayList<ArrayList<String>>();
		if (currNode == root) {
			currPath.add(currNode.getId());
			paths.add(currPath);
			return paths;
		}
		else {
			ArrayList<String> neighbourNodes= getNeighbourghs(currNode);
			for (String nei: neighbourNodes) {
				pathsTo.addAll((FS(graph.getNode(nei),root)));
			}
			for (ArrayList<String> path: pathsTo) {
				path.add(currNode.getId());
				paths.add(path);
			}
		}
		return paths;
		
	}
	private ArrayList<String> getNeighbourghs(Node node){
		ArrayList<String> out = new ArrayList<String>();
		for (int i = 0 ; i< node.getInDegree(); i++) {
			Edge currEdge = node.getEnteringEdge(i);
			out.add(currEdge.getNode0().getId());
		}
		return out;
	}
	public ArrayList<ArrayList<String>> getPathId() {
		ArrayList<ArrayList<String>> path = FS(query,root);
		return path;
	}
	public ArrayList<ArrayList<Node>> getPathNode(){
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
