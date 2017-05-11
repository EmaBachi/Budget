package it.consoft.budget.db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import it.consoft.budget.model.CostoBudget;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class BudgetDAO {
	
	private DBConnect connettore;
	
	public BudgetDAO(){
		this.connettore = new DBConnect();
	}
	
	/**
	 * Il metodo serve per caricare i costi effettivamente sostenuti
	 * @param dataInizio data di inizio controllo selezionata dall'utente
	 * @param dataFine data di fine controllo selezionata dall'utente
	 * @return la collezione che contiene e i costi effettivamente sostenuti
	 */
	public Collection<CostoBudget> caricaCostiActual(Timestamp dataInizio, Timestamp dataFine){
		
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return null;
			}
			
			Collection<CostoBudget> ritorno = new ArrayList<CostoBudget>();
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Contabilita_Coges(?, ?, ?)}");
			stm.setTimestamp(1, dataInizio);
			stm.setTimestamp(2, dataFine);
			stm.setString(3, "Consoft Spa");
			
			ResultSet rs = stm.executeQuery(); 
			
			//notare il true passato al costruttore
			while (rs.next()) {
				ritorno.add(new CostoBudget(rs.getString("code"), rs.getString("description"), rs.getDouble("amount"), true));
			}
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return ritorno;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public int contaCostiBudget(){
		
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita.");
				return 0;
			}
			
			String query = "SELECT COUNT(*) AS Conta FROM dbo.Budget";
			
			PreparedStatement stm = conn.prepareStatement(query);
			
			ResultSet rs = stm.executeQuery();
			
			rs.next();
			int ritorno = rs.getInt("Conta");
			
			rs.close();
			stm.close();
			conn.close();
			
			return ritorno;
		} catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Con qesto metodo interrogiamo il database e ci facciamo restituire gli importi riportati nella tabella del budget.
	 * Il metodo viene chiamato una sola volta all'inizio del programma
	 * @return la collezione che contiene i costi con gli importi previsti nel budget
	 */
	public Collection<CostoBudget> caricaCostiBudget(){
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return null;
			}
			
			Collection<CostoBudget> ritorno = new ArrayList<CostoBudget>();
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Budget}");
			
			ResultSet rs = stm.executeQuery(); 
			
			//notare il false passato al costruttore
			//nel budget si divide l'amount per 12 per mensilizzare gli importi
			while (rs.next()) {
				ritorno.add(new CostoBudget(rs.getString("Codice_Conto"), rs.getString("Descrizione"), (rs.getDouble("Importo")/12.0), false));
			}
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return ritorno;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean leggiFileBudget(String percorso){
		
		try (FileInputStream fileInputStream = new FileInputStream(percorso); 
				XSSFWorkbook workbook = new XSSFWorkbook(percorso)) {
			
			Connection conn = connettore.getConnection();
		        
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return false;
			}
			
			String query = "DELETE FROM dbo.Budget";
			
			PreparedStatement stmUno = conn.prepareStatement(query);
			
			stmUno.executeUpdate();
			
			stmUno.close();
			
		    XSSFSheet sheet = workbook.getSheetAt(0);
		    Iterator<Row> rowIterator = sheet.iterator();
		    PreparedStatement stmDue = conn.prepareStatement("INSERT INTO dbo.Budget (Codice_Conto, Descrizione, Importo) VALUES (?, ?, ?)");
		    while (rowIterator.hasNext()) {
		        Row row = rowIterator.next();
		        
		        String codiceConto = row.getCell(1).getStringCellValue();
		        String descrizione = row.getCell(0).getStringCellValue();
		        double importo = row.getCell(2).getNumericCellValue();
		        
		        stmDue.setString(1, codiceConto);
		        stmDue.setString(2, descrizione);
		        stmDue.setDouble(3, importo);
		        
		        stmDue.executeUpdate();
		    }
		    
		    stmDue.close();
	        conn.close();
	        return true;
	        
		} catch (Exception e) {
		    e.printStackTrace();
		    Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Errore");
			alert.setHeaderText("Impossibile cariare il file selezioanto.");
			alert.setContentText("Il file potrebbe essere aperto in excel, oppure contenere caretteri invalidi");
			alert.showAndWait();
			return false;
		}
	}

}
