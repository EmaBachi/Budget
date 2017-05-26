package it.consoft.budget.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CostoBudget {
	
	private String dipartimento;
	private String codiceCosto;
	private String codiceCommessa;
	private double importoActual;
	private double importoBudget;
	private List<Double> importiBudget;
	private double delta;
	
	//costruttore completo del CostoBudget
	public CostoBudget(String dipartimento, String codiceCosto, String codiceCommessa, double importoActual, List<Double> importiBudget){
		this.dipartimento = dipartimento;
		this.codiceCosto = codiceCosto;
		this.codiceCommessa = codiceCommessa;
		this.importoActual = importoActual;
		this.importiBudget = importiBudget;
		this.importoBudget = 0.0;
		this.delta = 0.0;
	}
	
	/**
	 * Costruttore del CostoBudget
	 * @param codiceCosto il codice identificativo del costo
	 * @param descrizione la descrizone che troviamo nel database
	 * @param importo l'importo associato a quel costo
	 * @param tipoCosto se tipoCosto = true, allora l'importo deve essere attribuito all'actual;
	 * 					se tipoCosto = false, allora l'importo deve essere attribuito al budget
	 */
	public CostoBudget(String dipartimento, String codiceCosto, String codiceCommessa, double importoActual){
		this.dipartimento = dipartimento;
		this.codiceCosto = codiceCosto;
		this.codiceCommessa = codiceCommessa;
		this.importoActual = importoActual;
		this.importoBudget = 0.0;
		this.delta = 0.0;
	}
	
	public CostoBudget(String dipartimento, String codiceCosto, String codiceCommessa, List<Double> importiBudget){
		this.dipartimento = dipartimento;
		this.codiceCosto = codiceCosto;
		this.codiceCommessa = codiceCommessa;
		this.importiBudget = importiBudget;
		this.importoBudget = 0.0;
		this.delta = 0.0;
	}

	//getter e setter
	public String getCodiceCosto() {
		return codiceCosto;
	}

	public void setCodiceCosto(String codiceCosto) {
		this.codiceCosto = codiceCosto;
	}

	public double getImportoActual() {
		return importoActual;
	}

	public void setImportoActual(double importoActual) {
		this.importoActual = importoActual;
	}

	public String getDipartimento() {
		return dipartimento;
	}

	public void setDipartimento(String dipartimento) {
		this.dipartimento = dipartimento;
	}

	public double getImportoBudget() {
		return importoBudget;
	}

	public void setImportoBudget(double importoBudget) {
		this.importoBudget = importoBudget;
	}

	public String getCodiceCommessa() {
		return codiceCommessa;
	}

	public void setCodiceCommessa(String codiceCommessa) {
		this.codiceCommessa = codiceCommessa;
	}

	public List<Double> getImportiBudget() {
		return importiBudget;
	}

	public void setImportiBudget(List<Double> importiBudget) {
		this.importiBudget = importiBudget;
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta() {
		this.delta = this.importoBudget - this.importoActual;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codiceCommessa == null) ? 0 : codiceCommessa.hashCode());
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
		if (codiceCommessa == null) {
			if (other.codiceCommessa != null)
				return false;
		} else if (!codiceCommessa.equals(other.codiceCommessa))
			return false;
		if (codiceCosto == null) {
			if (other.codiceCosto != null)
				return false;
		} else if (!codiceCosto.equals(other.codiceCosto))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CostoBudget [dipartimento=" + dipartimento + ", codiceCosto=" + codiceCosto + ", codiceCommessa="
				+ codiceCommessa + ", importoBudget=" + importoBudget + "]";
	}
	
	

}
