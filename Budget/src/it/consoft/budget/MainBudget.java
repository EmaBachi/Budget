package it.consoft.budget;
	
import java.awt.Dimension;

import it.consoft.budget.model.Model;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import javafx.fxml.FXMLLoader;


public class MainBudget extends Application {
	
	Scene scene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Budget.fxml"));
			AnchorPane root = (AnchorPane)loader.load();
			
			BudgetController controller = loader.getController();
			Model model = new Model();
			controller.setModel(model);
			controller.setStage(primaryStage);
			controller.impostazioniTabella();
			
			Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

			scene = new Scene(root, dim.getWidth()-100, dim.getHeight()-100);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("BUDGET");
			primaryStage.show();
			
			//controllo se nel databse è presenta la tabella di budget. Se non è presente obbligo l'utente a selezionare un file di budget
			if(model.contaCostiBudget() == 0) controller.obbligaSceltaFile();
			else model.caricaCostiBudget();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Scene getScene(){
		return scene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
