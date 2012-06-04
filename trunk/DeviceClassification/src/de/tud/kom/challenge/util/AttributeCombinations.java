package de.tud.kom.challenge.util;

import java.math.BigInteger;
import java.util.HashSet;

import org.apache.log4j.Logger;


public class AttributeCombinations {
	
	/**
	 * @author Andreas Reinhardt
	 */
	private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private BigInteger max;
	private BigInteger counter;
	private int size;
	
	public AttributeCombinations(int n) {
		max = BigInteger.ONE.shiftLeft(n).subtract(BigInteger.ONE);
		counter = BigInteger.ZERO;
		BigInteger bn = BigInteger.valueOf(n);
		log.info("We have "+(max.subtract(bn))+" feature combinations...");
		this.size = n;
	}

	public boolean hasNext() {
		return counter.compareTo(max) < 0;
	}
	
	public int[] next() {
		int[] outs = new int[0];
		HashSet<Integer> out = new HashSet<Integer>();
		
		while (true) {
			out.clear();
			for (int i=0;i<size;i++) {
				boolean set = !(counter.shiftRight(i).and(BigInteger.ONE).equals(BigInteger.ZERO));
				if (set) out.add(new Integer(i));
			}
			counter = counter.add(BigInteger.ONE);
			if (out.size() <= 1 && hasNext()) continue;
			
			outs = new int[out.size()];
			int c = 0;
			for (Integer e:out) outs[c++] = e.intValue();
			break;
		}
		return outs;
	}

	public double getProgress() {
		if (counter.shortValue() == 0) {
			long l = 1000000;
			BigInteger out = BigInteger.valueOf(l).multiply(counter.divide(BigInteger.valueOf(65536)));
			long perc = out.longValue();
			
			double prc = 0.01 * (double) l * (double) perc;
			return prc;
		}
		return -1;
	}
}
