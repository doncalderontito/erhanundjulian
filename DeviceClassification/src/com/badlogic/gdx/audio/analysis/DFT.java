/*
 *  Copyright (c) 2007 - 2008 by Damien Di Fede <ddf@compartmental.net>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.badlogic.gdx.audio.analysis;

/**
 * DFT stands for Discrete Fourier Transform and is the most widely used Fourier
 * Transform. You will never want to use this class due to the fact that it is a
 * brute force implementation of the DFT and as such is quite slow. Use an FFT
 * instead. This exists primarily as a way to ensure that other implementations
 * of the DFT are working properly. This implementation expects an even <code>timeSize</code> and will throw and IllegalArgumentException if this
 * is not the case.
 * 
 * @author Damien Di Fede
 * 
 * @see FourierTransform
 * @see FFT
 * @see <a href="http://www.dspguide.com/ch8.htm">The Discrete Fourier Transform</a>
 * 
 */
public class DFT extends FourierTransform {
	/**
	 * Constructs a DFT that expects audio buffers of length <code>timeSize</code> that
	 * have been recorded with a sample rate of <code>sampleRate</code>. Will throw an
	 * IllegalArgumentException if <code>timeSize</code> is not even.
	 * 
	 * @param timeSize
	 *            the length of the audio buffers you plan to analyze
	 * @param sampleRate
	 *            the sample rate of the audio samples you plan to analyze
	 */
	public DFT(final int timeSize, final float sampleRate) {
		super(timeSize, sampleRate);
		if((timeSize % 2) != 0) {
			throw new IllegalArgumentException("DFT: timeSize must be even.");
		}
		this.buildTrigTables();
	}
	
	@Override
	protected void allocateArrays() {
		this.spectrum = new float[(this.timeSize / 2) + 1];
		this.real = new float[(this.timeSize / 2) + 1];
		this.imag = new float[(this.timeSize / 2) + 1];
	}
	
	/**
	 * Not currently implemented.
	 */
	public void scaleBand(final int i, final float s) {
		// Not currently implemented.
	}
	
	/**
	 * Not currently implemented.
	 */
	public void setBand(final int i, final float a) {
		// Not currently implemented.
	}
	
	@Override
	public void forward(final float[] samples) {
		if(samples.length != this.timeSize) {
			throw new IllegalArgumentException("DFT.forward: The length of the passed sample buffer must be equal to DFT.timeSize().");
		}
		this.doWindow(samples);
		final int N = samples.length;
		for(int f = 0; f <= (N / 2); f++) {
			this.real[f] = 0.0f;
			this.imag[f] = 0.0f;
			for(int t = 0; t < N; t++) {
				this.real[f] += samples[t] * this.cos(t * f);
				this.imag[f] += samples[t] * -this.sin(t * f);
			}
		}
		this.fillSpectrum();
	}
	
	@Override
	public void inverse(final float[] buffer) {
		final int N = buffer.length;
		this.real[0] /= N;
		this.imag[0] = -this.imag[0] / (N / 2);
		this.real[N / 2] /= N;
		this.imag[N / 2] = -this.imag[0] / (N / 2);
		for(int i = 0; i < (N / 2); i++) {
			this.real[i] /= (N / 2);
			this.imag[i] = -this.imag[i] / (N / 2);
		}
		for(int t = 0; t < N; t++) {
			buffer[t] = 0.0f;
			for(int f = 0; f < (N / 2); f++) {
				buffer[t] += (this.real[f] * this.cos(t * f)) + (this.imag[f] * this.sin(t * f));
			}
		}
	}
	
	// lookup table data and functions
	
	private float[] sinlookup;
	private float[] coslookup;
	
	private void buildTrigTables() {
		final int N = this.timeSize;
		this.sinlookup = new float[N];
		this.coslookup = new float[N];
		for(int i = 0; i < N; i++) {
			
			this.sinlookup[i] = (float) Math.sin((i * FourierTransform.TWO_PI) / this.timeSize);
			this.coslookup[i] = (float) Math.cos((i * FourierTransform.TWO_PI) / this.timeSize);
		}
	}
	
	static class MathX {
		
		private static double f2 = -0.5;
		private static double f4 = -MathX.f2 / (3.0 * 4.0);
		private static double f6 = -MathX.f4 / (5.0 * 6.0);
		private static double f8 = -MathX.f6 / (7.0 * 8.0);
		private static double f10 = -MathX.f8 / (9.0 * 10.0);
		private static double f12 = -MathX.f10 / (11.0 * 12.0);
		private static double f14 = -MathX.f12 / (13.0 * 14.0);
		private static double f16 = -MathX.f14 / (15.0 * 16.0);
		private static double f18 = -MathX.f16 / (17.0 * 18.0);
		private static double f20 = -MathX.f18 / (19.0 * 20.0);
		private static double PI = Math.PI;
		private static double PI2 = 2.0 * MathX.PI;
		private static double PI05 = 0.5 * MathX.PI;
		
		/**
		 * Compute and return sinus of its parameter using taylor serie
		 * 
		 * @param x
		 *            angle in radian to
		 * @return sinus value for the given parameter
		 */
		public static double sin(final double x) {
			return MathX.cos(x - MathX.PI05);
		}
		
		/**
		 * Compute and return cosinus of its parameter using taylor serie
		 * 
		 * @param x
		 *            angle in radian to
		 * @return cosinus value for the given parameter
		 */
		public static double cos(double x) {
			if(x < 0.0) {
				x = -x;
			}
			if(x < MathX.PI2) {
				if(x < MathX.PI) {
					final double x2 = x * x;
					return 1.0 + (x2 * (MathX.f2 + (x2 * (MathX.f4 + (x2 * (MathX.f6 + (x2 * (MathX.f8 + (x2 * (MathX.f10 + (x2 * (MathX.f12 + (x2 * (MathX.f14 + (x2 * (MathX.f16 + (x2 * (MathX.f18 + (x2 * MathX.f20)))))))))))))))))));
				}
				x -= MathX.PI;
				final double x2 = x * x;
				return -(1.0 + (x2 * (MathX.f2 + (x2 * (MathX.f4 + (x2 * (MathX.f6 + (x2 * (MathX.f8 + (x2 * (MathX.f10 + (x2 * (MathX.f12 + (x2 * (MathX.f14 + (x2 * (MathX.f16 + (x2 * (MathX.f18 + (x2 * MathX.f20))))))))))))))))))));
			}
			x %= MathX.PI2;
			x -= MathX.PI;
			final double x2 = x * x;
			return -(1.0 + (x2 * (MathX.f2 + (x2 * (MathX.f4 + (x2 * (MathX.f6 + (x2 * (MathX.f8 + (x2 * (MathX.f10 + (x2 * (MathX.f12 + (x2 * (MathX.f14 + (x2 * (MathX.f16 + (x2 * (MathX.f18 + (x2 * MathX.f20))))))))))))))))))));
		}
		
	}
	
	private float sin(final int i) {
		// return (float) MathX.sin((i * FourierTransform.TWO_PI) / this.timeSize);
		return this.sinlookup[i % this.timeSize];
	}
	
	private float cos(final int i) {
		return this.coslookup[i % this.timeSize];
		// return (float) MathX.cos((i * FourierTransform.TWO_PI) / this.timeSize);
	}
}
