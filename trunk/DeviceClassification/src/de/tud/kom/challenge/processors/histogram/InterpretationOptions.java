package de.tud.kom.challenge.processors.histogram;

/**
 * InterpretationOptions beinhaltet einige Einstellungen, welche die Normalisierung
 * der Compartments betreffen.
 * 
 * EinstellbareParameter:
 * - _normalizationBase
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

public class InterpretationOptions {
	
	private boolean _normalize = true;
	private int _normalizationBase = 10000;

	/**
	 * Should histogram counts be normalized?
	 * @return
	 */
	public boolean isNormalize() {
		return _normalize;
	}

	public void setNormalize(boolean _normalize) {
		this._normalize = _normalize;
	}

	
	/**
	 * To which base the normalization should be done
	 * @return
	 */
	public int getNormalizationBase() {
		return _normalizationBase;
	}

	public void setNormalizationBase(int normalizationBase) {
		this._normalizationBase = normalizationBase;
	}
	
	

}
