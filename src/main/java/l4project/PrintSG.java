package l4project;

import java.util.ArrayList;

import org.graphstream.graph.Node;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.GraphRenderer;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class PrintSG extends Application {
	// all code until the next comment is unused since the javafx implementation.
//	private static DefeasibleKnowledgeBase buildKB() throws AtomSetException, IteratorException, ChaseException, HomomorphismException, ParseException {
//		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
//		// Base KB does not label all edges for some reason
//		
//		kb.add("isAPenguin(kowalski).");
//		kb.add("isABird(tweety)<-.");
//		kb.add("hasBrokenWings(tweety)<-.");
//		
//		kb.add("isBeautiful(X) <- isAPenguin(X).");
//		kb.add("isSad(X) <- hasBrokenWings(X).");
//
//		kb.add("canNotFly(X) <- isAPenguin(X).");
//		kb.add("isABird(X) <- isAPenguin(X).");
//		kb.add("[rfly] canFly(X) <= isABird(X).");
//		kb.add("[rbroken] canNotFly(X) <~ hasBrokenWings(X) .");
//
//		kb.add("! :- canFly(X), canNotFly(X).");
//
//		kb.add("rbroken >> rfly .");
//		
//		
//		
//		return kb;
//	}
//	private static DefeasibleKnowledgeBase buildNewKB() throws AtomSetException, IteratorException, ChaseException, HomomorphismException, ParseException {
//		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
//		// most simple KB no change in all 
//		
//		kb.add("lowTemprature(weather)<=.");
//		kb.add("rain(weather)<= .");
//		kb.add("cloudy(weather)<=.");
//		
//		kb.add("cold(X) <- lowTemprature(X).");
//		kb.add("badWeather(X) <= cloudy(X).");
//		kb.add("badWeather(X) <= rain(X).");
//		
//		kb.add("stayHome(X) <=  cold(X), badWeather(X).");
//
//
//		
//		return kb;
//	}
//	private static DefeasibleKnowledgeBase buildKB3() throws AtomSetException, IteratorException, ChaseException, HomomorphismException, ParseException {
//		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
//		// KB for difference between TD and noTD
//		
//		kb.add("cheap(phone)<-.");
//		kb.add("detrimental(phone)<-.");
//		kb.add("goodReviews(phone)<-.");
//		kb.add("slowDelivery(phone)<-.");
//		
//		kb.add("[r1]buy(X) <= cheap(X).");
//		kb.add("[r2]buy(X) <= goodReviews(X).");
//		kb.add("[r3]notBuy(X) <= detrimental(X).");
//		kb.add("[r4]notBuy(X) <= slowDelivery(X).");
//		
//		kb.add("! :- buy(X), notBuy(X).");
//		
//		kb.add("r1 >> r3 .");
//		kb.add("r2 >> r4 .");
//
//
//		
//		return kb;
//	}
//	private static DefeasibleKnowledgeBase buildKB4() throws AtomSetException, IteratorException, ChaseException, HomomorphismException, ParseException {
//		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
//		// tutorial KB
//		
//		kb.add("incriminating(evidence1,alice)<=.");
//		kb.add("absolving(evidence2,alice)<=.");
//		kb.add("alibi(alice)<=.");
//		
//		kb.add("[r1]responsible(Y) <- incriminating(X,Y).");
//		kb.add("[r2]notResponsible(Y) <- absolving(X,Y).");
//		kb.add("[r3]guilty(X) <- responsible(X).");
//		kb.add("[r4]innocent(X) <- alibi(X).");
//		
//		
//		kb.add("! :- guilty(X), innocent(X).");
//		kb.add("! :- responsible(X), notResponsible(X).");
//
//		
//		return kb;
//	}
//	private static DefeasibleKnowledgeBase buildKB5() throws AtomSetException, IteratorException, ChaseException, HomomorphismException, ParseException {
//		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
//		// large
//		
//		// facts
//		kb.add("alone(jack).");
//		kb.add("hasCollar(jack).");
//		kb.add("hasMicrochip(jack).");
//		
//		
//		// rules
//		kb.add("[r1]keep(X) <- hasOwner(X,Y).");
//		kb.add("[r2]hasOwner(X,Y) <= hasCollar(X).");
//		kb.add("[r3]hasOwner(X,Y) <= hasMicrochip(X).");
//		kb.add("[r4]stray(X) <= alone(X).");
//		kb.add("[r5]adoption(X) <- stray(X).");
//		
//		// negative constraints
//		kb.add("! :- adoption(X), keep(X).");
//
//		// rule preferences
//		kb.add("r5>>r2.");
//		kb.add("r3>>r5.");
//
//		
//		return kb;
//	}
//	private static StatementGraph initializeSG(DefeasibleKnowledgeBase kb) throws IteratorException, ChaseException, AtomSetException, HomomorphismException {
//		sg = new StatementGraph(kb,"BDLwithTD");
//		sg.build();
//		return sg;
//	}
//		
//	private static String getTable(StatementGraph sg) {
//		return sg.toJSONString();	
//		}
//	private static JSONObject CreateObjects(String JSONString) {
//		JSONObject json = new JSONObject(JSONString);
//		return json;
//	}
	public static void main(String[] args)  throws IteratorException, AtomSetException, ChaseException, HomomorphismException, ParseException, JsonMappingException, JsonProcessingException {
		
//		StatementGraph graph = initializeSG(buildKB());
		
//		String query1= "canFly(kowalski).";
//		String query2 = "canFly(tweety).";
//		graph.groundQuery(query1); // does kowalski fly?System.out.println(answer); // OUT
//		graph.groundQuery(query2);
		
//		StatementGraph graph = initializeSG(buildNewKB());
//		String answer = graph.groundQuery("stayHome(weather).");
		
//		StatementGraph graph = initializeSG(buildKB3());
//		String answer = graph.groundQuery("buy(phone).");
		
//		StatementGraph graph = initializeSG(buildKB4());
//		String answer = graph.groundQuery("guilty(alice).");
		
//		StatementGraph graph = initializeSG(buildKB5());
//		String answer = graph.groundQuery("keep(jack).");
//		String answer2 = graph.groundQuery("adoption(jack).");
		
//		String json= getTable(graph);
//		System.out.println(json);
//		gui = new GUIGraphStreamV2();
//		gui.buildGraph(CreateObjects(json));
//		
//		GetPaths GP = new GetPaths(gui.getGraph(),gui.getRoot(),gui.getQuery(query1));
//		ArrayList<ArrayList<Node>> path = GP.getPathNode();
//		GetNaturalText GNT = new GetNaturalText(GP.getPathNode());
//		System.out.println(GNT.getSentences());
//		

			//begins javafx application
		Application.launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		//create root pane
		Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
		//creates controller class
		GUIJavaFX controller = new GUIJavaFX();
		//displays UI
		Scene scene = new Scene(root);
        primaryStage.setScene(scene);
		primaryStage.setTitle("Statement Graph");
		primaryStage.setResizable(false);
        primaryStage.show();
	}

}
