package it.consoft.budget.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class CostoBudget {
	
	private String codiceCosto;
	private String descrizione;
	private double importoActual;
	private double importoBudget;
	private double delta;
	
	//costruttore completo del CostoBudget
	public CostoBudget(String codiceCosto, String descrizione, double importoActual, double importoBudget){
		this.codiceCosto = codiceCosto;
		this.descrizione = descrizione;
		this.importoActual = importoActual;
		this.importoBudget = importoBudget;
	}
	
	/**
	 * Costruttore del CostoBudget
	 * @param codiceCosto il codice identificativo del costo
	 * @param descrizione la descrizone che troviamo nel database
	 * @param importo l'importo associato a quel costo
	 * @param tipoCosto se tipoCosto = true, allora l'importo deve essere attribuito all'actual;
	 * 					se tipoCosto = false, allora l'importo deve essere attribuito al budget
	 */
	public CostoBudget(String codiceCosto, String descrizione, double importo, boolean tipoCosto){
		this.codiceCosto = codiceCosto;
		this.descrizione = descrizione;
		if(tipoCosto)
			this.importoActual = importo;
		else
			this.importoBudget = importo;
	}
	
	/**
	 * il metodo serve per moltiplicare l'importo del budget per i mesi selezionati dall'utente.
	 * Questo metodo è anche utile quando bisogna 'pulire' le strutture dati: basta infatti passare come valore 1/fattore
	 * @param fattore è il periodo di tempo scelto dall'utente
	 */
	public void moltiplicaImportoBudget(double fattore){
		this.importoBudget = this.importoBudget * fattore;
	}
	
	//Questo metodo calcola finalmente il delta
	public void calcolaDelta(){
		this.setDelta(this.importoBudget-this.importoActual);
	}
	
	
	//i metodi hashcode e equals vengono calcolati sul codice identificativo del costo
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codiceCosto == null) ? 0 : codiceCosto.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CostoBudget other = (CostoBudget) obj;
		if (codiceCosto == null) {
			if (other.codiceCosto != null)
				return false;
		} else if (!codiceCosto.equals(other.codiceCosto))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CostoBudget [codiceCosto=" + codiceCosto + ", descrizione=" + descrizione + ", importoActual="
				+ importoActual + ", importoBudget=" + importoBudget + ", delta=" + delta + "]";
	}

	//getter e setter
	public String getCodiceCosto() {
		return codiceCosto;
	}

	public void setCodiceCosto(String codiceCosto) {
		this.codiceCosto = codiceCosto;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public double getImportoActual() {
		return importoActual;
	}

	public void setImportoActual(double importoActual) {
		this.importoActual = importoActual;
	}

	public double getImportoBudget() {
		return importoBudget;
	}

	public void setImportoBudget(double importoBudget) {
		this.importoBudget = importoBudget;
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	//i tre getter che seguono servono per la visualizzazione nella tabella. I valori vengono formattati con la notazione italiana
	public String getImportoActualPerVisualizzazione(){
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator('.');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(this.importoActual);
	}
	
	public String getImportoBudgetPerVisualizzazione(){
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator('.');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(this.importoBudget);
	}
	
	public String getDeltaPerVisualizzazione(){
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator('.');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(this.delta);
	}
	
	

}
