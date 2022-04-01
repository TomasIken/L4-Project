package l4project;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

public class GetPaths {
	// https://tel.archives-ouvertes.fr/tel-01904558v1/file/thesis_final_HECHAM.pdf p 89 (105 in search)
	MultiGraph graph;
	Node query;
	Node root;
	
	public GetPaths(MultiGraph g, Node r, Node q) {
		graph = g;
		query = q;
		root = r;
	}
	
	public  String[] FS() {
		String[] paths = null;
		if (root.equals(query)) {
			return paths;
		}
	}
}
