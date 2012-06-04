package de.tud.kom.challenge.processors.util;

/**
 * MovingInterpolator ergänzt fehlende Werte in der TimeSeries
 * durch lineare Interpolation.
 * (Fehlende Werte können beim Resampling auftreten)
 * 
 * @author Felix Rüttiger
 * @author Vanessa Wührl
 */

public class MovingInterpolator {
	
	private int _x1, _x2;
	private float _y1, _y2;

	
	public final void setPoint1(int x, float y) {
		if (x >= _x2) {
			throw new IllegalArgumentException("x1-value must be smaller than x2-value");
		}
		
		_x1 = x;
		_y1 = y;
	}
	
	public final void setPoint2(int x, float y) {
		if (x <= _x1) {
			throw new IllegalArgumentException("x2-value must be greater than x1-value");
		}
		
		_x2 = x;
		_y2 = y;
	}
	
	public final float interpolateAt(int x) {
		return SimpleLinearInterpolator.interpolate(x, _x1, _x2, _y1, _y2);
	}
	
	
	
	
	private static class SimpleLinearInterpolator {
		
		public static float interpolate(int x, int x1, int x2, float y1, float y2) {
			double delta = (y2-y1) / (x2-x1);
			double t1 = x - x1;
			double result = y1 + t1 * delta;
			return (float)result;
		}
		
	}
	
	
	public MovingInterpolator() {
		_x2 = Integer.MAX_VALUE;
	}
}
