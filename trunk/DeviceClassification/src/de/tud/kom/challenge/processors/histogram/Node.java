package de.tud.kom.challenge.processors.histogram;

/**
 * Ein Node stellt einen Knoten in einer Baumstruktur da,
 * welche f�r die Zuordnung einzelner Werte in die zugeh�rigen
 * Compartments zust�ndig ist.
 * 
 * @author Felix R�ttiger
 * @author Vanessa W�hrl
 */

public class Node {
	
	private Node _left;
	private Node _rigth;
	private int _threshold;
	public final int Min;
	public final int Max;
	
	public Node(Node left, Node right, int threshold, int min, int max) {
		super();
		this._left = left;
		this._rigth = right;
		this._threshold = threshold;
		Min = min;
		Max = max;
	}
	
	public void traverse(RowCarrier row) {
		if (row.getPower1() > _threshold) {
			_rigth.traverse(row);
		}
		else {
			_left.traverse(row);
		}
	}

}
