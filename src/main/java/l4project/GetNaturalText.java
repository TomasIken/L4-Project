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
	public ArrayList<String> getSentences(){
		return makeLongSenteces(turnNodesToPartSentences());
	}
	private ArrayList<String> makeLongSenteces(ArrayList<ArrayList<String>> lists){
		ArrayList<String> output= new ArrayList<String>();
		for (ArrayList<String> sentence:lists) {
			String outSentence = "";
			for (String part:sentence) {
				String after = part.trim().replaceAll(" +", " ");
				outSentence= outSentence + after + " ";
			}
			output.add(outSentence);
		}
		
		return output;
		
	}
	private ArrayList<ArrayList<String>> turnNodesToPartSentences(){
		ArrayList<ArrayList<String>> sentencePaths = new ArrayList<ArrayList<String>>();
		for(ArrayList<Node> path: nodePath) {
			ArrayList<String> currSentencePath = new ArrayList<String>();
			Node prevNode= null;
			for (Node node: path) {
				if (!node.getAttribute("type").toString().equals("fact")) {
					String currRule =node.getAttribute("ui.label").toString();
					String object = currRule.substring(currRule.indexOf("(")+1, currRule.indexOf(")"));
//					currRule= currRule.substring(0,currRule.indexOf("("));
					currRule= currRule.replaceAll("[()]", "");
					currRule= splitCamelCase(currRule);
					currRule= currRule.replaceAll(object, "");
					
					
//					currRule= object + " "+currRule;
					if (node.getAttribute("type").toString().equals("statement")) {
						String currRuleType = node.getAttribute("ruleType").toString();
						if (currRuleType.equals("strict")) {
							String target = "->";
							currRule = "since " + object+ " " + currRule;
							currRule =currRule.replace(target, " then " + object);
							
						}
						else if (currRuleType.equals("defeasible") ) {
							String target = "=>";
							currRule = "since "+ object+ " " + currRule;
							currRule =currRule.replace(target, " then maybe " + object);
						}
						else if (currRuleType.equals("defeater")) {
							String target = "~>";
							currRule = "however since " + object+ " "+ currRule;
							currRule =currRule.replace(target, " then "+ object);
						}
					
						String target = ",";
						currRule =currRule.replace(target, " and ");
					}
					else if (node.getAttribute("type").toString().equals("claim")){
					}
					// support attack differentiation
					if ((prevNode!=null) && prevNode.getEdgeBetween(node).getAttribute("ui.class").toString().equals("attackEdge")){
						// if it is an attack edge
						currRule= "which contradicts " + currRule;
					}
					else if (!node.getAttribute("type").toString().equals("fact")) {
						currRule= " and " + currRule;
					}
					currSentencePath.add(currRule);
					prevNode = node;
				}
				else {
					String currRule =node.getAttribute("ui.label").toString();
					currRule = currRule.substring(currRule.indexOf(">")+1);
					String object = currRule.substring(currRule.indexOf("(")+1, currRule.indexOf(")"));
					currRule= currRule.replaceAll("[()]", " ");
					currRule= splitCamelCase(currRule);
					currRule= currRule.replaceAll(object, "");
					currRule=object + " "+ currRule;
					
					
					currSentencePath.add(currRule);
					prevNode = node;
				}
				
			}
			sentencePaths.add(currSentencePath);
		}
		
		return sentencePaths;
	}
	private static String splitCamelCase(String s) {
		   return s.replaceAll(
		      String.format("%s|%s|%s",
		         "(?<=[A-Z])(?=[A-Z][a-z])",
		         "(?<=[^A-Z])(?=[A-Z])",
		         "(?<=[A-Za-z])(?=[^A-Za-z])"
		      ),
		      " "
		   );
		}
}