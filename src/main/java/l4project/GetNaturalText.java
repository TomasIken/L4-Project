package l4project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetNaturalText {
	private ArrayList<ArrayList<Node>> nodePath = new ArrayList<ArrayList<Node>>();
	private Node queryNode;
	// class to generate the natural text
	// rules and facts have to be in a specific format for best results
	public GetNaturalText(ArrayList<ArrayList<Node>> nP) {
		nodePath = nP;
		// remove the root node that simply says "T-> true" in all paths since it is not used
		for(ArrayList<Node> path: nodePath) {
			path.remove(0);
		}
		queryNode= nodePath.get(0).get(nodePath.get(0).size() - 1);
	}
	public String turnIntoOneString(ArrayList<String> input) {
		// turns an arrayList of strings into a large string separated by two lines
		String output="";
		
		for(String sentence:input) {
			output= output + sentence + System.getProperty("line.separator")+ System.getProperty("line.separator");			
		}
		
		return output;
	}
	public ArrayList<String> getSentences(){
		// global function
		return makeLongSenteces(turnNodesToPartSentences());
	}
	private ArrayList<String> makeLongSenteces(ArrayList<ArrayList<String>> lists){
		// turns the array of sentences for each rule into a single sentence for each path
		ArrayList<String> output= new ArrayList<String>();
		for (ArrayList<String> sentence:lists) {
			String outSentence = "";
			for (String part:sentence) {
				String after = part.trim().replaceAll(" +", " ");
				outSentence= outSentence + after + " ";
			}
			output.add(outSentence);
		}
		output.add(createQueryParagraph());
		return output;
		
	}
	private ArrayList<ArrayList<String>> turnNodesToPartSentences(){
		// principal class function
		ArrayList<ArrayList<String>> sentencePaths = new ArrayList<ArrayList<String>>();
		for(ArrayList<Node> path: nodePath) {
			ArrayList<String> currSentencePath = new ArrayList<String>();
			Node prevNode= null;
			for (Node node: path) {
				// check if the node is not a fact
				if (!node.getAttribute("type").toString().equals("fact")) {

					String currRule =node.getAttribute("ui.label").toString();
					

					
					if (node.getAttribute("type").toString().equals("statement")) {
						// take the variable inside the brackets
						String object = currRule.substring(currRule.indexOf("(")+1, currRule.indexOf(")"));
						// delete the brackets
						currRule= currRule.replaceAll("[()]", "");
						
						// get the rule and turn the camel case into separate words 
						currRule= splitCamelCase(currRule);
						// delete the variable from the string
						currRule= currRule.replaceAll(object, "");
						
						// if the rule is a statement and not a query
						// change string to match type of rule
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
						// if it is a query remove everything past the brackets
						currRule= currRule.substring(0,currRule.indexOf(")")+1);
						// take the variable inside the brackets
						String object = currRule.substring(currRule.indexOf("(")+1, currRule.indexOf(")"));
						// delete the brackets
						currRule= currRule.replaceAll("[()]", "");
						
						// get the rule and turn the camel case into separate words 
						currRule= splitCamelCase(currRule);
						// delete the variable from the string
						currRule= currRule.replaceAll(object, "");
						currRule = " " + object + " "+ currRule;
					}
					// support attack differentiation
					if ((prevNode!=null) && prevNode.getEdgeBetween(node).getAttribute("ui.class").toString().equals("attackEdge")){
						// if it is an attack edge
						currRule= " which contradicts that " + currRule;
					}
					else if (!node.getAttribute("type").toString().equals("fact")) {
						currRule= " therefore " + currRule;
					}
					currSentencePath.add(currRule);
					prevNode = node;
				}
				else {
					// if it is a simple rule simply place variable in the beginning and delete brackets
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
	private String createQueryParagraph() {
		String result =  queryNode.getAttribute("ui.label").toString();
		String currRule = result;
		result= result.substring(result.indexOf(")")+1,result.length());
		
		currRule= currRule.substring(0,currRule.indexOf(")")+1);
		// take the variable inside the brackets
		String object = currRule.substring(currRule.indexOf("(")+1, currRule.indexOf(")"));
		// delete the brackets
		currRule= currRule.replaceAll("[()]", "");
		
		// get the rule and turn the camel case into separate words 
		currRule= splitCamelCase(currRule);
		// delete the variable from the string
		currRule= currRule.replaceAll(object, "");
		currRule = " " + object + " "+ currRule;
		currRule = currRule + " is" + result + " because ";
		String output = " ";
		for(int i = 0 ; i<queryNode.getDegree();i++) {
			String edgeType= queryNode.getEdge(i).getAttribute("ui.class").toString();
			edgeType= edgeType.substring(0, edgeType.length()-4);
			String edgeLabel = queryNode.getEdge(i).getAttribute("ui.label").toString();
			String currEdge = "a " + edgeLabel + " edge "+ edgeType+ "s" + " and ";
			output=output + currEdge; 
		}
		output=output.substring(0,output.length()-4);
		currRule= currRule.substring(1) + output;
		return currRule;
	}
	private static String splitCamelCase(String s) {
		// function to turn a camelcase word into multiple words separated buy spaces
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