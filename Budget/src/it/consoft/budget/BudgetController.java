package it.consoft.budget;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import it.consoft.budget.model.CostoBudget;
import it.consoft.budget.model.Model;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;

public class BudgetController {
	
	private Model model;
	
	private Stage stage;
	
	private String percorsoFile;
	
	final FileChooser fileChooser = new FileChooser();
	
	ObservableList<CostoBudget> obsCosti = FXCollections.observableArrayList();
	
	public void setModel(Model model){
		this.model = model;
	}
	
	public void setStage(Stage stage){
		this.stage = stage;
	}

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<CostoBudget> tabellaTotali;

    @FXML
    private TableColumn<CostoBudget, String> colonnaCodiceCosto;

    @FXML
    private TableColumn<CostoBudget, String> colonnaCommessa;

    @FXML
    private TableColumn<CostoBudget, String> colonnaActual;

    @FXML
    private TableColumn<CostoBudget, String> colonnaBudget;

    @FXML
    private TableColumn<CostoBudget, String> colonnaDelta;
    
    /*@FXML
    private TableColumn<CostoBudget, String> colonnaDescrizione;*/
    
    @FXML
    private TableColumn<CostoBudget, String> colonnaDipartimento;
    
    @FXML
    private DatePicker datePickerInizio;

    @FXML
    private DatePicker datePickerFine;

    @FXML
    private Button bottoneControlla;

    @FXML
    private Button bottoneSelezionaFile;
    
    @FXML
    private TextField testoCopia;
    
    @FXML
    private ImageView imageExcel;
    
    @FXML
    private TextField testoFiltro;
    
    @FXML
    private ComboBox<String> comboDipartimento;
    
    @FXML
    private Label labelDipartimento;

    @FXML
    private Label labelFiltroConto;

    
    /**
     * Il metodo viene invocato quando l'utente preme il bottono controlla.
     * Per prima cosa si effettuano alcuni controlli sulle date immesse dall'utente.
     * Successivamente si invocano i metodi del model per reperire i dati di actual e calcolare gli importi corretti di budget.
     * Si procede poi nella visualizzazione del risultato
     * @param event il bottone cliccato dall'utente
     */
    @FXML
    void doControlla(ActionEvent event) {
    	controlla();
    }
    
    private void controlla(){
    	
    	boolean inputValido = true;
    	
    	LocalDate dataInizio = datePickerInizio.getValue();
    	LocalDate dataFine = datePickerFine.getValue();
    	
    	if(dataInizio == null || 
    			dataFine == null || 
    			dataFine.isBefore(dataInizio) || 
    			dataInizio.getMonthValue() < 2
    			){
    		
    		inputValido = false;
    		//messaggio di errore
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Errore");
    		alert.setHeaderText("Errore Data");
    		alert.setContentText("Cosa è successo?\n"
    				+ "1)La data di fine controllo precede la data di inizio controllo;\n"
    				+ "2)Il mese di fine controllo precede Febbraio.");

    		alert.showAndWait();
    	}
    	
    	if(inputValido){
    		obsCosti.clear();
    		
    		model.caricaCostiActual(dataInizio, dataFine);
    		
    		obsCosti.addAll(model.getCollezioneCosti());
    		testoFiltro.setVisible(true);
    		imageExcel.setVisible(true);
    		comboDipartimento.setVisible(true);
    		labelDipartimento.setVisible(true);
    		labelFiltroConto.setVisible(true);
    	}
    }
    
    /**
     * Quando l'utente preme il pulsante 'bottoneSelezionaFile', viene aperto un esplora risorse
     * cosicchè si possa selezionare il file excel di budget per l'anno corretto
     * @param event rappresenta il bottone premuto dall'utente
     */
    @FXML
    void doSelezionaFile(ActionEvent event) {
    	scegli();
    }
    
    //Scrivo questo metodo scegli fuori dal metodo @FXML doSelenioFile in modo tale da poterci accedere anche da altri metodi
    private void scegli(){
    	File file = fileChooser.showOpenDialog(stage);
        if (file != null){
        	if(model.leggiFileBudget(file.getAbsolutePath())){
        		this.bottoneControlla.setDisable(false);
        		model.caricaCostiBudget();
        		Alert alert = new Alert(AlertType.INFORMATION);
        		alert.setTitle("File cambiato");
        		alert.setHeaderText("File cambiato con successo. Selezionare le date e premere Controlla per caricare i nuovi dati.");
        		
        		alert.showAndWait();
        		
        		obsCosti.clear();
        	} else 
        		this.bottoneControlla.setDisable(true);
        }
    }
    
    //getter per otteenre il percorso del file selezionato
    public String getPercorsoFile(){
    	return this.percorsoFile;
    }
    
    /**
     * Metodo invocato quando all'inizio del programma non è stato selezionato alcun file di budget
     */
    public void obbligaSceltaFile(){
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Attenzione!");
		alert.setHeaderText("Nessun file di budget selezionato.");
		alert.setContentText("Per continuare è necessario scegliere un file di budget");

		alert.showAndWait();
		
		scegli();
    }
    
    /**
     * Il metodo permette di copiare tutti i dati presenti in tabella nel budget
     * @param event evento scatenato quando l'utente preme sull'immagine di excel
     */
    @FXML
    void doEsportaInExcel(MouseEvent event) {
    	
    	try {
    		  
	        XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("Budget");
	        File tryFile = new File("C:/Users/csto90/Desktop/tir/Budget.xlsx");
	        File excelFile;
	        
	        if(tryFile.exists()) tryFile.delete();
	        
	        excelFile = new File("C:/Users/csto90/Desktop/tir/Budget.xlsx");
	        excelFile.deleteOnExit();
	        FileOutputStream excelOutputStream = new FileOutputStream(excelFile);
	        
	        Row header = sheet.createRow(0);
	        
	        if(tabellaTotali.getColumns().size() == 5){
	        	Cell first = header.createCell(0);
	        	first.setCellValue("Codice Conto");
	        	Cell second = header.createCell(1);
	        	second.setCellValue("Descrizione");
	        	Cell third = header.createCell(2);
	        	third.setCellValue("Contabilità Coges");
	        	Cell fourth = header.createCell(3);
	        	fourth.setCellValue("Budget");
	        	Cell fifth = header.createCell(4);
	        	fifth.setCellValue("Delta");
	        }
	        
	        for (int row = 1; row < tabellaTotali.getItems().size()+1; row++) {
		    	
		    	Row rowExcel = sheet.createRow(row);
		    	
		        for (int column = 0; column < tabellaTotali.getColumns().size(); column++) {
		      
		        	Cell cellExcel = rowExcel.createCell(column);
		        	
		            Object cell = tabellaTotali.getColumns().get(column).getCellData(row-1);
		            
		            String str = (String)cell;
		            str = str.replaceAll("[.]", "");
		            str = str.replaceAll("[,]", ".");
		            try{
		            	double d = Double.parseDouble((String)str);
		            	cellExcel.setCellValue(d);
		            } catch(NumberFormatException nfe){
		            	cellExcel.setCellValue((String)str);
		            }
		        }
		    }
	        
	        workbook.write(excelOutputStream);
	        workbook.close();
	        Desktop dt = Desktop.getDesktop();
	        dt.open(excelFile);
	        
        } catch(Exception e){
        	e.printStackTrace();
        	
        	Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Errore");
    		alert.setHeaderText("Impossibile esportare in Excel");
    		alert.setContentText("Assicurarsi che non esista già un file con lo stesso nome");
    		
    		alert.showAndWait();
        }
    }
    
    
    //metodo per definire le impostazioni delle table column
    public void impostazioniTabella(){
    	
    	colonnaDipartimento.prefWidthProperty().bind(tabellaTotali.widthProperty().divide(6));
    	colonnaCodiceCosto.prefWidthProperty().bind(tabellaTotali.widthProperty().divide(6));
    	colonnaCommessa.prefWidthProperty().bind(tabellaTotali.widthProperty().divide(6));
    	colonnaActual.prefWidthProperty().bind(tabellaTotali.widthProperty().divide(6));
    	colonnaBudget.prefWidthProperty().bind(tabellaTotali.widthProperty().divide(6));
    	colonnaDelta.prefWidthProperty().bind(tabellaTotali.widthProperty().divide(6));
    	
    	colonnaDipartimento.setCellValueFactory(new Callback<CellDataFeatures<CostoBudget, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<CostoBudget, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getDipartimento());
		     }
		  });
    	
    	colonnaCodiceCosto.setCellValueFactory(new Callback<CellDataFeatures<CostoBudget, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<CostoBudget, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getCodiceCosto());
		     }
		  });
    	
    	colonnaCommessa.setCellValueFactory(new Callback<CellDataFeatures<CostoBudget, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<CostoBudget, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getCodiceCommessa());
		     }
		  });
    	
    	colonnaActual.setCellValueFactory(new Callback<CellDataFeatures<CostoBudget, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<CostoBudget, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getImportoActualPerVisualizzazione());
		     }
		  });
    	
    	colonnaBudget.setCellValueFactory(new Callback<CellDataFeatures<CostoBudget, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<CostoBudget, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getImportoBudgetPerVisualizzazione());
		     }
		  });
    	
    	colonnaDelta.setCellValueFactory(new Callback<CellDataFeatures<CostoBudget, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<CostoBudget, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getDeltaPerVisualizzazione());
		     }
		  });
    	
    	colonnaDelta.setCellFactory(column -> {
		    return new TableCell<CostoBudget, String>() {
		    	@Override
		        protected void updateItem(String importo, boolean empty) {
		            super.updateItem(importo, empty);

		            if (empty) {
		                setText(null);
		                setStyle("");
		            } else {
		                // Format date.
		                setText(""+importo);

		                // Style all dates in March with a different color.
		                if (importo.compareTo("0") > 0) {
		                    setTextFill(Color.GREEN);
		                    setStyle("");
		                } else if(importo.compareTo("0") < 0){
		                    setTextFill(Color.RED);
		                    setStyle("");
		                } else {
		                	setText("");
		                }
		            }
		        }
		    };
		});
    	
    	tabellaTotali.getSelectionModel().setCellSelectionEnabled(true);
    	
    	//in questo modo per copiare e incollare un codice di costo basta selezioanrlo con il mouse
    	tabellaTotali.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                testoCopia.setText(newSelection.toString());
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(testoCopia.getText());
                clipboard.setContent(content);
            }
        });
    	
    	//I metodi che seguono permottono all'utente di filtare la visualizzazione nella tabella
    	FilteredList<CostoBudget> listaFiltro = new FilteredList<>(obsCosti, p -> true);
    	
    	testoFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltro.setPredicate(costo -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filtroMinuscolo = newValue.toLowerCase();

                if (costo.getCodiceCosto().toLowerCase().contains(filtroMinuscolo)) {
                    return true;
                }
                return false;
            });
        });
    	
    	testoFiltro.setVisible(false);
    	imageExcel.setVisible(false);
    	labelDipartimento.setVisible(false);
    	labelFiltroConto.setVisible(false);
    	
    	List<String> dipartimenti = new ArrayList<String>(Arrays.asList("COMPLESSIVO", "Progetti Speciali", "Ente centrale", "Amministrazione"));
    	comboDipartimento.getItems().addAll(dipartimenti);
    	
    	comboDipartimento.getSelectionModel().select("COMPLESSIVO");
    	comboDipartimento.setVisible(false);
    	
    	comboDipartimento.valueProperty().addListener((observable, oldValue, newValue) -> {
    		listaFiltro.setPredicate(costo -> {
    			if(newValue == null || newValue.isEmpty() || newValue.equals("COMPLESSIVO"))  return true;
    			
    			if(newValue.equals("Amministrazione")){
    				if(costo.getCodiceCommessa().equals("I08_DA01")) return true;
    			}
    			
    			if(newValue.equals("Progetti Speciali")){
    				if(costo.getCodiceCommessa().equals("I17_DPS01")) return true;
    			}
    			
    			if(newValue.equals("Ente centrale")){
    				if(costo.getCodiceCommessa().equals("I17_CI01")) return true;
    			}
    			
    			return false;
    		});
    	});
    	
    	SortedList<CostoBudget> listaOrdinata = new SortedList<>(listaFiltro);
    	listaOrdinata.comparatorProperty().bind(tabellaTotali.comparatorProperty());
    	tabellaTotali.setItems(listaOrdinata);
    }
    

    @FXML
    void initialize() {
    	 assert tabellaTotali != null : "fx:id=\"tabellaTotali\" was not injected: check your FXML file 'Budget.fxml'.";
         assert colonnaDipartimento != null : "fx:id=\"colonnaDipartimento\" was not injected: check your FXML file 'Budget.fxml'.";
         assert colonnaCodiceCosto != null : "fx:id=\"colonnaCodiceCosto\" was not injected: check your FXML file 'Budget.fxml'.";
         assert colonnaCommessa != null : "fx:id=\"colonnaCommessa\" was not injected: check your FXML file 'Budget.fxml'.";
         //assert colonnaDescrizione != null : "fx:id=\"colonnaDescrizione\" was not injected: check your FXML file 'Budget.fxml'.";
         assert colonnaActual != null : "fx:id=\"colonnaActual\" was not injected: check your FXML file 'Budget.fxml'.";
         assert colonnaBudget != null : "fx:id=\"colonnaBudget\" was not injected: check your FXML file 'Budget.fxml'.";
         assert colonnaDelta != null : "fx:id=\"colonnaDelta\" was not injected: check your FXML file 'Budget.fxml'.";
         assert bottoneSelezionaFile != null : "fx:id=\"bottoneSelezionaFile\" was not injected: check your FXML file 'Budget.fxml'.";
         assert datePickerInizio != null : "fx:id=\"datePickerInizio\" was not injected: check your FXML file 'Budget.fxml'.";
         assert datePickerFine != null : "fx:id=\"datePickerFine\" was not injected: check your FXML file 'Budget.fxml'.";
         assert bottoneControlla != null : "fx:id=\"bottoneControlla\" was not injected: check your FXML file 'Budget.fxml'.";
         assert testoCopia != null : "fx:id=\"testoCopia\" was not injected: check your FXML file 'Budget.fxml'.";
         assert imageExcel != null : "fx:id=\"imageExcel\" was not injected: check your FXML file 'Budget.fxml'.";
         assert testoFiltro != null : "fx:id=\"testoFiltro\" was not injected: check your FXML file 'Budget.fxml'.";
        
        testoCopia.setVisible(false);
    }
}
