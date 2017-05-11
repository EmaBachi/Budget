package it.consoft.budget.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.consoft.budget.db.BudgetDAO;

public class Model {
	
	private BudgetDAO dao;
	
	double fattore;
	
	private Map<String, CostoBudget> mappaCosti;
	
	public Model(){
		this.dao = new BudgetDAO();
		this.mappaCosti = new ConcurrentHashMap<String, CostoBudget>();
		
		fattore = 1.0;
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
			mappaCosti.put(temp.getCodiceCosto(), temp);
		
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
		
		Collection<CostoBudget> collezioneCosti = dao.caricaCostiActual(dataInizio, dataFine);
		
		//Con questo ciclo associo gli importi actual agli importi previsti nel budget. effettuo il controllo sul codice identificativo per sicurezza
		for(CostoBudget temp : collezioneCosti){
			if(mappaCosti.get(temp.getCodiceCosto())!= null){
				mappaCosti.get(temp.getCodiceCosto()).setImportoActual(temp.getImportoActual());
			}
			else
				mappaCosti.put(temp.getCodiceCosto(), temp);
		}
		
		this.moltiplicaCostiBudget(dataInizioLocal, dataFineLocal);
	}
	
	private void moltiplicaCostiBudget(LocalDate dataInizioLocal, LocalDate dataFineLocal){
		
		this.setFattore((double)(dataFineLocal.getMonthValue()-dataInizioLocal.getMonthValue()+1));
		
		for(CostoBudget temp : mappaCosti.values()){
			mappaCosti.get(temp.getCodiceCosto()).moltiplicaImportoBudget(this.fattore);
			mappaCosti.get(temp.getCodiceCosto()).calcolaDelta();
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
	
	private void setFattore(double fattore){
		this.fattore = fattore;
	}
	
	//metodo per ottenere i i valori contenuti nella mappa
	public Collection<CostoBudget> getCollezioneCosti(){
		return mappaCosti.values();
	}
	
	/**
	 * Questo metodo è necessario per pulire la struttura dati.
	 * Se il costo considerato non presenta un importo di budget, allora lo rimuovo dalla struttura dati;
	 * altrimenti azzero il suo importo di actual e menisilizzo nuovamente l'importo di budget
	 */
	public void pulisci(){
		Iterator<CostoBudget> it = mappaCosti.values().iterator();
		
		while(it.hasNext()){
			CostoBudget temp = it.next();
			mappaCosti.get(temp.getCodiceCosto()).setImportoActual(0.0);
			mappaCosti.get(temp.getCodiceCosto()).moltiplicaImportoBudget((double)1.0/this.fattore);
			System.out.println(mappaCosti.get(temp.getCodiceCosto()));
		}
		
		this.setFattore(1.0);
	}

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
		this.setFattore(1.0);
	}
}
