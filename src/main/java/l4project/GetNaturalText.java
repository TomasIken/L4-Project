package l4project;

import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Node;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetNaturalText {
	private ArrayList<ArrayList<Node>> nodePath = new ArrayList<ArrayList<Node>>();
	

	public GetNaturalText(ArrayList<ArrayList<Node>> nP) {
		nodePath = nP;
		for(ArrayList<Node> path: nodePath) {
			path.remove(0);
		}
	}
	
	public ArrayList<ArrayList<String>> turnNodesToPartSentences(){
		ArrayList<ArrayList<String>> sentencePaths = new ArrayList<ArrayList<String>>();
		for(ArrayList<Node> path: nodePath) {
			ArrayList<String> currSentencePath = new ArrayList<String>();
			Node prevNode= null;
			for (Node node: path) {
				String currRule =node.getAttribute("ui.label").toString();
				if (node.getAttribute("type").toString().equals("statement")) {
					String currRuleType = node.getAttribute("ruleType").toString();
					if (currRuleType.equals("strict")) {
						String target = "->";
						currRule = "since " + currRule;
						currRule =currRule.replace(target, " then must ");
						
					}
					else if (currRuleType.equals("defeasible") ) {
						String target = "=>";
						currRule = "since " + currRule;
						currRule =currRule.replace(target, " then can ");
					}
					else if (currRuleType.equals("defeater")) {
						String target = "~>";
						currRule = "however since " + currRule;
						currRule =currRule.replace(target, " then must ");
					}
				
					String target = ",";
					currRule =currRule.replace(target, " and ");
				}
				else if (node.getAttribute("type").toString().equals("fact")) {
					currRule = currRule.substring(currRule.indexOf(">")+1);
				}
				else if (node.getAttribute("type").toString().equals("claim")){
				}
				// support attack differentiation
				if ((prevNode!=null) && prevNode.getEdgeBetween(node).getAttribute("ui.class").toString().equals("attackEdge")){
					// if it is an attack edge
					currRule= "which contradicts " + currRule;
				}
				else {
					currRule= " and " + currRule;
				}
				currSentencePath.add(currRule);
				prevNode = node;
			}
			sentencePaths.add(currSentencePath);
		}
		
		return sentencePaths;
	}
}