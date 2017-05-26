package it.consoft.budget.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.consoft.budget.db.BudgetDAO;

public class Model {
	
	private BudgetDAO dao;
	
	private Map<CostoBudget, CostoBudget> mappaCosti;
	
	public Model(){
		this.dao = new BudgetDAO();
		this.mappaCosti = new ConcurrentHashMap<CostoBudget, CostoBudget>();
	}
	
	public int contaCostiBudget(){
		return dao.contaCostiBudget();
	}
	
	//metodo invocato dirattamente dal main. i dati del budget sono statici quindi li cariciamo una sola volta all'avvio dell'applicazione
	public boolean caricaCostiBudget(){
		
		if(dao.caricaCostiBudget() == null)
			return false;
		
		Collection<CostoBudget> collezioneCosti = dao.caricaCostiBudget();
		
		for(CostoBudget temp : collezioneCosti)
			mappaCosti.put(temp, temp);
		
		return true;
	}
	
	/**
	 * il metodo serve per caricare gli importieffettivamente sostenuti dall'azienda
	 * @param dataInizioLocal la data di inizio controllo selezionata dall'utente
	 * @param dataFineLocal la data di fine controllo selezionata dall'utente
	 */
	public void caricaCostiActual(LocalDate dataInizioLocal, LocalDate dataFineLocal){
		Timestamp dataInizio = this.convertitoreData(dataInizioLocal);
		Timestamp dataFine = this.convertitoreData(dataFineLocal);
		
		impostaImportoBudget(dataInizioLocal, dataFineLocal);
		//============ IL CODICE QUI SOTTO FUNZIONA ALLA PERFZIONE=======================
		//Collection<CostoBudget> collezioneCosti = dao.caricaCostiActual(dataInizio, dataFine);
		
		//Con questo ciclo associo gli importi actual agli importi previsti nel budget. effettuo il controllo sul codice identificativo per sicurezza
		/*for(CostoBudget temp : collezioneCosti){
			if(mappaCosti.get(temp)!= null){
				mappaCosti.get(temp).setImportoActual(temp.getImportoActual());
			}
			else
				mappaCosti.put(temp, temp);
		}*/
		//================================================================================
		
		for(CostoBudget costoBudget : mappaCosti.values()){
			if(!costoBudget.getCodiceCosto().equals("E20073115"))
				costoBudget.setImportoActual(dao.getImporto(costoBudget.getCodiceCosto(),
															costoBudget.getCodiceCommessa(),
															dataInizio,
															dataFine));
			 else 
				costoBudget.setImportoActual(dao.getImportoPersonale(costoBudget.getCodiceCommessa(),
																	dataInizio,
																	dataFine));
		}
		
		
		
		impostaDelta();
	}
	
	private void impostaDelta() {
		
		for(CostoBudget costoBudget : mappaCosti.values())
			costoBudget.setDelta();
		
	}

	private void impostaImportoBudget(LocalDate dataInizioLocal, LocalDate dataFineLocal){
		
		int meseInizio = dataInizioLocal.getMonthValue();
		int meseFine = dataFineLocal.getMonthValue();
		
		for(CostoBudget costo : mappaCosti.values()){
			double somma = 0.0;
			
			if(costo.getImportiBudget() != null){
				
				for(int i = meseInizio-2; i<=meseFine-2; i++)
					somma += costo.getImportiBudget().get(i);
				
			costo.setImportoBudget(somma);
			}
		}
	}
	
	/**
	 * metodo per cnvertire il tipo LocalDate in Timestamp. Questa operazione  necessaria per interfacciarsi con il database
	 * @param data il valore da convertire
	 * @return il valore convertito in Timestamp
	 */
	private Timestamp convertitoreData(LocalDate data){
		Timestamp ritorno = Timestamp.valueOf(data.toString()+ " 00:00:00");
		return ritorno;
	}
	
	//metodo per ottenere i i valori contenuti nella mappa
	public Collection<CostoBudget> getCollezioneCosti(){
		return mappaCosti.values();
	}

	/*public void pulisci(){
		
			Iterator<CostoBudget> it = mappaCosti.values().iterator();
			
			while(it.hasNext()){
				CostoBudget temp = it.next();
				mappaCosti.get(temp).setImportoActual(0.0);
			}
		
	}*/

	/**
	 * Il metodo si occupa di demandare il dao per cambiare il file di budget selezionato.
	 * Nel caso in cui il cambiamento avvenga con successo, viene pulita l'intera struttura dati
	 * @param percorsoFile selezionato dall'utente
	 * @return il boolean che indica se le'operazione è andata a buon fine o meno
	 */
	public boolean leggiFileBudget(String percorsoFile){
		if(dao.leggiFileBudget(percorsoFile)){
			pulisciTutto();
			return true;
		} else
			return false;
	}
	
	/**
	 * metodo che pulisce l'intera struttura dati, ovvero cancella tutto ciò che è presente nella mappa.
	 */
	private void pulisciTutto(){
		mappaCosti.clear();
	}
}
