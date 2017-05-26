package it.consoft.budget.db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Budget_Costi_Actual(?, ?, ?)}");
			stm.setTimestamp(1, dataInizio);
			stm.setTimestamp(2, dataFine);
			stm.setString(3, "Consoft Spa");
			
			ResultSet rs = stm.executeQuery(); 
			
			//notare il true passato al costruttore
			while (rs.next()) {
				ritorno.add(new CostoBudget("Ente Centrale", rs.getString("code"), rs.getString("commessa"), rs.getDouble("amount")));
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
			
			String query = "SELECT COUNT(*) AS Conta FROM dbo.Budget_Vero";
			
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
			
			while (rs.next()) {
				List<Double> importiBudget = new ArrayList<Double>(Arrays.asList(rs.getDouble(4),
																		  rs.getDouble(5),
																		  rs.getDouble(6),
																		  rs.getDouble(7),
																		  rs.getDouble(8),
																		  rs.getDouble(9),
																		  rs.getDouble(10),
																		  rs.getDouble(11),
																		  rs.getDouble(12),
																		  rs.getDouble(13),
																		  rs.getDouble(14)));
				ritorno.add(new CostoBudget(rs.getString("dipartimento"), rs.getString("codice_conto"), rs.getString("codice_commessa"), importiBudget));
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
	
	/**
	 * Metodo implementato per leggere il file di budget che cambia periodicamente
	 * @param percorso il percorso del file selezionato dall'utente
	 * @return un booleano che indica se il processo è andato a buon fine
	 */
	public boolean leggiFileBudget(String percorso){
		
		try (FileInputStream fileInputStream = new FileInputStream(percorso); 
				XSSFWorkbook workbook = new XSSFWorkbook(percorso)) {
			
			Connection conn = connettore.getConnection();
		        
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return false;
			}
			
			String query = "DELETE FROM dbo.Budget_Vero";
			
			PreparedStatement stmUno = conn.prepareStatement(query);
			
			stmUno.executeUpdate();
			
			stmUno.close();
			
		    XSSFSheet sheet = workbook.getSheetAt(0);
		    Iterator<Row> rowIterator = sheet.iterator();
		    PreparedStatement stmDue = conn.prepareStatement("INSERT INTO dbo.Budget_Vero "
		    												+ "(dipartimento, codice_conto, codice_commessa, [2], [3], [4], [5], [6], [7], [8], [9], [10], [11], [12])"
		    												+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		    while (rowIterator.hasNext()) {
		        Row row = rowIterator.next();
		        
		        if(row.getCell(0) != null){
		        String dipartimento = row.getCell(0).getStringCellValue();
		        String codiceConto = row.getCell(1).getStringCellValue();
		        String codiceCommessa = row.getCell(2).getStringCellValue();
		        
		        double feb = row.getCell(3).getNumericCellValue();
		        double mar = row.getCell(4).getNumericCellValue();
		        double avr = row.getCell(5).getNumericCellValue();
		        double may = row.getCell(6).getNumericCellValue();
		        double jun = row.getCell(7).getNumericCellValue();
		        double jul = row.getCell(8).getNumericCellValue();
		        double aug = row.getCell(9).getNumericCellValue();
		        double sep = row.getCell(10).getNumericCellValue();
		        double oct = row.getCell(11).getNumericCellValue();
		        double nov = row.getCell(12).getNumericCellValue();
		        double dec = row.getCell(13).getNumericCellValue();
		        
		        stmDue.setString(1, dipartimento);
		        stmDue.setString(2, codiceConto);
		        stmDue.setString(3, codiceCommessa);
		        stmDue.setDouble(4, feb);
		        stmDue.setDouble(5, mar);
		        stmDue.setDouble(6, avr);
		        stmDue.setDouble(7, may);
		        stmDue.setDouble(8, jun);
		        stmDue.setDouble(9, jul);
		        stmDue.setDouble(10, aug);
		        stmDue.setDouble(11, sep);
		        stmDue.setDouble(12, oct);
		        stmDue.setDouble(13, nov);
		        stmDue.setDouble(14, dec);
		        
		        stmDue.executeUpdate();
		        } else
		        	break;
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

	public double getImporto(String codiceConto, String codiceCommessa, Timestamp dataInizio, Timestamp dataFine) {
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return -1;
			}
			
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Budget_Costi_Non_Personale(?, ?, ?, ?)}");
			stm.setString(1, codiceCommessa);
			stm.setString(2, codiceConto);
			stm.setTimestamp(3, dataInizio);
			stm.setTimestamp(4, dataFine);
			
			
			ResultSet rs = stm.executeQuery(); 
			
			double ritorno = 0.0;
			
			if(rs.next()) ritorno = rs.getDouble("amount");
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return ritorno;
			
		} catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}

	public double getImportoPersonale(String codiceCommessa, Timestamp dataInizio, Timestamp dataFine) {
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return -1;
			}
			
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Budget_Costi_Personale(?, ?, ?)}");
			stm.setString(1, codiceCommessa);
			stm.setTimestamp(2, dataInizio);
			stm.setTimestamp(3, dataFine);
			
			
			ResultSet rs = stm.executeQuery(); 
			
			double ritorno = 0.0;
			
			if(rs.next()) ritorno = rs.getDouble("amount");
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return ritorno;
			
		} catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}

}
