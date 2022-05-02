package l4project;

import java.util.ArrayList;

import org.graphstream.graph.Node;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.json.JSONObject;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.elder.core.StatementGraph;
import fr.lirmm.graphik.util.stream.IteratorException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GUIJavaFX {
	@FXML 
	public Pane statementGraph;
	public TextArea input;
	public Button confirm;
	public TextArea queryInput;
	public TextArea outputText;
	public GUIJavaFX() {
	}
	public void buttonPush(ActionEvent e) throws ParseException, IteratorException, AtomSetException, ChaseException, HomomorphismException {
		try {
		// main action event function
			
		// grabs both text area inputs, stores them in string arrays and clears the text area
		String newLineChar=System.getProperty("line.separator");
		String[] kb = input.getText().split(newLineChar);
		StatementGraph graph = new StatementGraph(buildKB(kb),"BDLwithTD");
		graph.build();
		String[] queries = queryInput.getText().split(newLineChar);
		addQueries(graph,queries);
		
		
		String json= graph.toJSONString();
		// creates the graph object and appends it to the pane object
		GUIGraphStreamV2 gui = new GUIGraphStreamV2();
		gui.buildGraph(CreateObjects(json));
		statementGraph.getChildren().clear();
		statementGraph.getChildren().add(gui.javafxDisplay());
		statementGraph.setVisible(true);
		
		// generates the paths and adds the resulting string to the output text area
		GetPaths GP = new GetPaths(gui.getGraph(),gui.getRoot(),gui.getQuery(queries[0]));
		GetNaturalText GNT = new GetNaturalText(GP.getPathNode());
		outputText.clear();
		outputText.setText(GNT.turnIntoOneString(GNT.getSentences()));
		}
		catch(Exception e1) {
			//catch any errors and displays an error message in the output text area
			outputText.clear();
			outputText.setText("An error has occured please make sure the knowledge base is inputed in the correct format.");
		}
	}
	private static DefeasibleKnowledgeBase buildKB(String[] input) throws AtomSetException, IteratorException, ChaseException, HomomorphismException, ParseException {
		// function to builds the knowledge base
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		for (String line:input) {
			kb.add(line);
		}
		return kb;
	}
	private static void addQueries(StatementGraph graph,String[]queries) throws IteratorException, AtomSetException {
		// add each query into the statement graph
		if (!(queries==null)) {
			for (String line:queries) {
				graph.groundQuery(line);
			}
		}
	}
	private static JSONObject CreateObjects(String JSONString) {
		//turns the JSON string from graal-eldr into a json object
		JSONObject json = new JSONObject(JSONString);
//		System.out.println(json);
		return json;
	}
}
