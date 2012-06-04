package de.tud.kom.challenge.processors.histogram;

/**
 * In HistogramBuilder werden die Mechanismen angestoßen, die für die Erstellung
 * des Histogramms inklusive der zeitlichen Unterteilungen pro Compartment zuständig sind.
 * 
 * @author Felix Rüttiger 
 * @author Vanessa Wührl
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

public class HistogramBuilder {
	
	private class InternalContainer {
		public List<String[]> Entries;
	}
	
	private CsvContainer _csvContainer;
	private BuildOptions _buildOptions;
	
	

	public HistogramBuilder(BuildOptions buildOptions) {
		_buildOptions = buildOptions;
	}
	
	public List<Histogram> build(CsvContainer csvContainer) {
		_csvContainer = csvContainer;
				
		int maxValue = getMaxValue();
		
		List<InternalContainer> _listCsvContainer = splitCsvContainer();
		List<Histogram> h = new ArrayList<Histogram>();
		
		for (InternalContainer ic : _listCsvContainer) {
			
			List<Compartment> emptyCompartments = _buildOptions.getBuildStrategy().buildCompartments(maxValue);
			List<Compartment> filledCompartments = fillCompartments(emptyCompartments, ic);
			emptyCompartments = null;
			
			Histogram histogram = new Histogram(filledCompartments);
			histogram.setBuildOptions(_buildOptions);
			h.add(histogram);
		}
		
		return h;
	}
	
	private List<InternalContainer> splitCsvContainer() {
		List<InternalContainer> l = new ArrayList<InternalContainer>();

		for (TimeInterval ti : _buildOptions.getIntervalls()) {
			int start = getIndexOfMostMatchingTime(ti.getFormattedStart());
			int end = getIndexOfMostMatchingTime(ti.getFormattedEnd());
			InternalContainer ic = new InternalContainer();
			ic.Entries = _csvContainer.getEntries().subList(start, end);
			l.add(ic);
		}
		
		return l;
	}
	
	private int getIndexOfMostMatchingTime(String searchterm) {
		int index = 0;
		boolean indexfound = false;
		int i = 0;
		int distanceOfSearchterm = 0;
				
		List<String[]> entries = _csvContainer.getEntries();
		
		while (distanceOfSearchterm <= 0 && indexfound == false && i < entries.size()) {
			String time = entries.get(i)[0];
			distanceOfSearchterm = time.substring(11).compareTo(searchterm);
			index = i;
			if (distanceOfSearchterm == 0) indexfound = true;
			i++;
		}
		
		return index;
	}

	private List<Compartment> fillCompartments(List<Compartment> emptyCompartments, InternalContainer itemsToFill) {
		Compartment c1 = emptyCompartments.get(0); //Treat lowest Compartment separately, because most values expected here
		List<Compartment> remainingCompartments = emptyCompartments.subList(1, emptyCompartments.size());
		
		
		Node root = buildSortingTree(remainingCompartments);
		
		RowCarrier carrier = new RowCarrier();
		for (String[] row : itemsToFill.Entries) {
			carrier.set(row);
			
			if (carrier.getPower1() <= c1.get_maxPower()) {
				c1.addValue(carrier.getCarriedObject());
			}
			else {
				root.traverse(carrier);
			}
		}
		
		return emptyCompartments;
	}

	private Node buildSortingTree(List<Compartment> remainingCompartments) {
		
		ArrayList<Node> nextNodeLayer = new ArrayList<Node>();
		
		Iterator<Compartment> i = remainingCompartments.iterator();
		while (i.hasNext()) {
			Compartment c1 = i.next();
			NodeCompartmentAdapter nc1 = new NodeCompartmentAdapter(c1);
			
			if (i.hasNext()) {
				Compartment c2 = i.next();
				NodeCompartmentAdapter nc2 = new NodeCompartmentAdapter(c2);
				
				Node n = new Node(nc1, nc2, c1.get_maxPower(), c1.get_minPower(), c2.get_maxPower());
				nextNodeLayer.add(n);
			}
			else {
				nextNodeLayer.add(nc1);
			}
		}
		
		
		while (nextNodeLayer.size() > 1) {
			ArrayList<Node> temp = new ArrayList<Node>();
			
			Iterator<Node> j = nextNodeLayer.iterator();
			while (j.hasNext()) {
				Node n1 = j.next();
				if (j.hasNext()) {
					Node n2 = j.next();
					Node parent = new Node(n1, n2, n1.Max, n1.Min, n2.Max);
					temp.add(parent);
				}
				else {
					temp.add(n1);
				}
			}
			
			nextNodeLayer = temp;
		}
		
		return nextNodeLayer.get(0);
	}


	private int getMaxValue() {
		if (_buildOptions.isUsePerInstanceMaximum()) {
			return getRelativeMaxPerInstance();
		}
		else {
			return _buildOptions.getMaxAbsolutePower();
		}
	}
	
	private int getRelativeMaxPerInstance() {
		int max = 0;
		
		for (String[] row : _csvContainer.getEntries()) {
			int value = Integer.parseInt(row[1]);
			if (value > max)
				max = value;
		}
		
		return max;
	}
	
	public BuildOptions getBuildOptions() {
		return _buildOptions;
	}
}
