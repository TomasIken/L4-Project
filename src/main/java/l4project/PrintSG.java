package l4project;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.elder.core.StatementGraph;
import fr.lirmm.graphik.util.stream.IteratorException;
import javafx.application.Application;
import javafx.stage.Stage;


public class PrintSG extends Application {
	private static StatementGraph sg;

	private static DefeasibleKnowledgeBase buildKB() throws AtomSetException, IteratorException, ChaseException, HomomorphismException, ParseException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		
		kb.add("penguin(kowalski), bird(tweety), brokenWings(tweety).");
		kb.add("beautiful(X) <- penguin(X).");
		kb.add("sad(X) <- brokenWings(X).");

		kb.add("notFly(X), bird(X) <- penguin(X).");
		kb.add("[rfly] fly(X) <= bird(X).");
		kb.add("[rbroken] notFly(X) <~ brokenWings(X) .");

		kb.add("! :- fly(X), notFly(X).");

		kb.add("rbroken >> rfly .");
		
		return kb;
	}
	private static StatementGraph initializeSG(DefeasibleKnowledgeBase kb) throws IteratorException, ChaseException, AtomSetException, HomomorphismException {
		sg = new StatementGraph(kb,"PDLwithTD");
		sg.build();
		return sg;
	}
		
	private static String getTable(StatementGraph sg) {
		return sg.toJSONString();	
		}
	private static JSONObject CreateObjects(String JSONString) {
		JSONObject json = new JSONObject(JSONString);
		return json;
	}
	public static void main(String[] args)  throws IteratorException, AtomSetException, ChaseException, HomomorphismException, ParseException, JsonMappingException, JsonProcessingException {
		StatementGraph graph = initializeSG(buildKB());
		String answer = graph.groundQuery("notFly(kowalski)."); // does kowalski fly?System.out.println(answer); // OUT
		String json= getTable(graph);
		System.out.println(json);
		GUIGraphStream gui = new GUIGraphStream(CreateObjects(json));
		
		Application.launch(args);
			

	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Statement Graph");
        primaryStage.show();
		
	}
}
