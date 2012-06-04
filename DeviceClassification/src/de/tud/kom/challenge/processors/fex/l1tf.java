package de.tud.kom.challenge.processors.fex;

import java.util.ArrayList;

import Jama.LUDecomposition;
import Jama.Matrix;
import de.tud.kom.challenge.processors.ProcessorFeX;

public class l1tf {
	/* constant variables for fortran call */
	final static double done       = 1.0;
	final static double dtwo       = 2.0;
	final static double dminusone  =-1.0;
	final static int    ione       = 1;
	final static int    itwo       = 2;
	final static int    ithree     = 3;
	final static int    iseven     = 7;

	final static int MIN_TIME_INTERVALS = 10;//10
	final static int MIN_TIME_INTERVALS_WHEN_DELAY_IS_POSSIBLE = 200;//200
	final static int MAX_TIME_INTERVALS = 100;//300
	
	// use l1 trendfilter
	public static ArrayList<DeviceUsage> filter( ArrayList<DeviceUsage> _usages, double lambda) {
		ArrayList<DeviceUsage> usages = ProcessorUtilities.copyDeviceUsages(_usages);
		for(int i = 0; i < usages.size(); i++){
			DeviceUsage u = usages.get(i);
			ArrayList<TimeInterval> intervals = u.getIntervals();
			if(intervals.size() < MIN_TIME_INTERVALS){
				continue;
			}
			
			ArrayList<TimeInterval> onInterval = new ArrayList<TimeInterval>();
			TimeInterval tiLast = null;
			onInterval.add(tiLast);
			for(int j = 0; j <= intervals.size(); j++){
				
				//check if we have to do TVM.
				if(onInterval.size() >= MAX_TIME_INTERVALS){
					subFilter(onInterval, lambda);
					tiLast = null; 
					onInterval.clear();
				}
				if(j == intervals.size()){
					if(onInterval.size() >= MIN_TIME_INTERVALS){
						subFilter(onInterval, lambda);
						tiLast = null; 
						onInterval.clear();
					}
					break;
				}
				
				TimeInterval tiCurrent = intervals.get(j);
				
				/*we have three cases: 
					1- this is the first interval.
					2- this TimeInterval belongs to a previous OnInterval.
					3- this TimeInterval is the first interval in a new OnInterval and we are bellow MIN_TIME_INTERVALS_WHEN_DELAY_IS_POSSIBLE.
					4- this TimeInterval is the first interval in a new OnInterval and we are above MIN_TIME_INTERVALS_WHEN_DELAY_IS_POSSIBLE.
				*/
				
				//case 1: this is the first interval.
				if(tiLast == null){
					onInterval.clear();
					onInterval.add(tiCurrent);
					tiLast = tiCurrent;
					continue; // no more to do.
				}
				//case 2: this TimeInterval belongs to a previous OnInterval.
				if((tiLast.getEnd() + 1) == tiCurrent.getStart()){
					onInterval.add(tiCurrent);
					tiLast = tiCurrent;
					continue;
				}

				//case 3: this TimeInterval is the first interval in a new OnInterval and we are bellow MIN_TIME_INTERVALS_WHEN_DELAY_IS_POSSIBLE.
				if(onInterval.size() < MIN_TIME_INTERVALS_WHEN_DELAY_IS_POSSIBLE){
					onInterval.add(new TimeInterval(0.0, tiLast.getEnd() + 1, tiCurrent.getStart() - tiLast.getEnd() - 1, - tiLast.getLevel()));
					onInterval.add(tiCurrent);
					tiLast = tiCurrent;
					continue;
				}

				//case 4: this TimeInterval is the first interval in a new OnInterval and we are above MIN_TIME_INTERVALS_WHEN_DELAY_IS_POSSIBLE.
				subFilter(onInterval, lambda);
				tiLast = null; 
				onInterval.clear();
					
				
				onInterval.add(tiCurrent);
				tiLast = tiCurrent;
			}
		}

		return usages;
	}
	

	//do the l1tf filter on the intervals, the output is changing the level of the intervals
	private static void subFilter(ArrayList<TimeInterval> intervals, double lambda) {
		//initialize input for do_l1tf and call it.
		
	     double[] y = new double[intervals.size()];
	     for(int i = 0;i < intervals.size(); i++){
	    	 y[i] = intervals.get(i).getLevel();
	     }
	     double[] x = new double[y.length];
	     Boolean status = false;
	     
	     do_l1tf(y, lambda, x, status);

	     //if status is ok change the levels of the intervals.
	     if(status){
	    	 System.out.println("l1tf sub filter ok!\n");
	     }else{
	    	 System.out.println("l1tf sub filter failed!\n");
	     }
	     for(int i = 0;i < intervals.size(); i++){
	    	 intervals.get(i).setLevel(x[i]);
	     }
	}

	//y: input array
	//x: output array
	public static void do_l1tf(double []y, double lambda, double [] x, Boolean status){
		
		//return values.
		status = false;
		for(int i = 0;i < y.length; i++){
			x[0] = 0;
		}
		
		//init Interior Point Method (IPM) variables
		double ALPHA     = 0.01;   // backtracking linesearch parameter (0,0.5]
		double BETA      = 0.5;    // backtracking linesearch parameter (0,1)
		double MU        = 2;      // IPM parameter: t update
		double MAXITER   = 40;     // IPM parameter: max iteration of IPM
		double MAXLSITER = 20;     // IPM parameter: max iteration of line search
		double TOL       = 1e-4;   // IPM parameter: tolerance
				
		int n = y.length;
	    int m = n - 2; 
	    double [][] d_dt = new double[m][];
	    double [][] S = new double[m][];
	 
	    int i_2 = 0;
	    d_dt[0] = new double[m];
	    d_dt[0][i_2 ] = 6.0;
    	d_dt[0][i_2 + 1] =-4.0;
    	d_dt[0][i_2 + 2] = 1.0;
    	
	    d_dt[1] = new double[m];
    	d_dt[1][i_2 ] =-4.0;
    	d_dt[1][i_2 + 1] = 6.0;
    	d_dt[1][i_2 + 2] =-4.0;
    	d_dt[1][i_2 + 3] = 1.0;

    	int m_2 = m - 2;
	    d_dt[m_2] = new double[m];
    	d_dt[m_2][m_2 - 2] = 1.0;
    	d_dt[m_2][m_2 - 1] =-4.0;
    	d_dt[m_2][m_2 ] = 6.0;
    	d_dt[m_2][m_2 + 1] =-4.0;
    	
	    d_dt[m_2+1] = new double[m];
    	d_dt[m_2+1][m_2 - 1] = 1.0;
    	d_dt[m_2+1][m_2 ] =-4.0;
	    d_dt[m_2+1][m_2 + 1] = 6.0;

	    i_2 = 0;
	    S[0] = new double[m];
	    S[0][i_2 ] = 6.0;
    	S[0][i_2 + 1] =-4.0;
    	S[0][i_2 + 2] = 1.0;
    	
	    S[1] = new double[m];
    	S[1][i_2 ] =-4.0;
    	S[1][i_2 + 1] = 6.0;
    	S[1][i_2 + 2] =-4.0;
    	S[1][i_2 + 3] = 1.0;

    	m_2 = m - 2;
	    S[m_2] = new double[m];
    	S[m_2][m_2 - 2] = 1.0;
    	S[m_2][m_2 - 1] =-4.0;
    	S[m_2][m_2 ] = 6.0;
    	S[m_2][m_2 + 1] =-4.0;
    	
	    S[m_2+1] = new double[m];
    	S[m_2+1][m_2 - 1] = 1.0;
    	S[m_2+1][m_2 ] =-4.0;
	    S[m_2+1][m_2 + 1] = 6.0;

	    
	    m_2 = m - 2;
	    i_2 = 0;
	    for (int i = 2; i < m_2; i++){
	    	d_dt[i] = new double[m];
	    	d_dt[i][i_2] = 1.0;
	    	d_dt[i][i_2 + 1] =-4.0;
	    	d_dt[i][i_2 + 2] = 6.0;
	    	d_dt[i][i_2 + 3] =-4.0;
	    	d_dt[i][i_2 + 4] = 1.0;
	    	
	    	S[i] = new double[m];
	    	S[i][i_2] = 1.0;
	    	S[i][i_2 + 1] =-4.0;
	    	S[i][i_2 + 2] = 6.0;
	    	S[i][i_2 + 3] =-4.0;
	    	S[i][i_2 + 4] = 1.0;
	    	
	    	i_2++;
	    }

	    Matrix matrix_d_dt = new Matrix(d_dt);
	    Matrix matrix_S = new Matrix(S);
	    
	    double [] d_y = new double[m];
	    Dx(n,y,d_y);
	    
	    //calculate lambda_max.
	    LUDecomposition lu_d_dt = new LUDecomposition(matrix_d_dt);
	    Matrix matrix_d_y = new Matrix(d_y, d_y.length); 
	    Matrix result_r = lu_d_dt.solve(matrix_d_y);
	    double [][]r_r = result_r.getArray();
	   
	    double maxval = -Double.MAX_VALUE;
	    for (int i = 0; i < m; i++){
	        if (Math.abs(r_r[i][0]) > maxval) maxval = Math.abs(r_r[i][0]);
	    }
	    
	    lambda = maxval* lambda;

	    //%----------------------------------------------------------------------
	    //%               MAIN LOOP
	    //%----------------------------------------------------------------------
	  		
		double []z   = new double[m];   // dual variable
		double []mu1 = new double[m];   // dual of dual variable
		double []mu2 = new double[m];   // dual of dual variable

		double t    = 1e-10; 
		double pobj =  Double.MAX_VALUE;
		double pobj1 = 0, pobj2 = 0;
		double dobj =  0;
		double step =  Double.MAX_VALUE;
		double []f1   =  new double[m];
		double []f2   = new double[m];
		
		double []resDual = new double[m];
		double []resCent = new double[m*2];
		double []residual= new double[m*3];
		
		for(int i = 0; i < m; i++){
			mu1[i] = 1.0;
			mu2[i] = 1.0;
			f1[i] =  z[i] - lambda;
			f2[i] = -z[i] - lambda;
		}

		double [] dt_z 		= new double[n];  
		double [] d_dt_z 	= new double [m];
		double [] w 		= new double [m];
		double [] rz		= new double [m];
		double [] r			= new double [m];
		double [] dmu1		= new double [m];
		double [] dmu2		= new double [m];
		Matrix matrix_w = new Matrix(w, w.length);
		Matrix matrix_r = new Matrix(r, r.length);
		
		boolean []negIdx1 = new boolean[m]; 
		boolean []negIdx2 = new boolean[m];
		
		double [] newz    =  new double [m];
		double [] newmu1  =  new double [m];
		double [] newmu2  =  new double [m];
		double [] newf1   =  new double [m];
		double [] newf2   =  new double [m];

        //% UPDATE RESIDUAL
		double [] newResDual  = new double [m];
		double [] newResCent  = new double [m*2];
		double [] newResidual = new double [m*3];
        
		double [] new_dt_z 		= new double[n];  
		double [] new_d_dt_z 	= new double [m];
		int iters = 0;

		for(iters = 0; iters < MAXITER; iters++){

			for(int i = 0; i < m; i++){
				w[i] = d_y[i] - (mu1[i] - mu2[i]);
				matrix_w.set(i, 0, w[i]);
			}
		    
		    //% two ways to evaluate primal objective:
		    //% 1) using dual variable of dual problem
		    //% 2) using optimality condition
			Matrix result_w = lu_d_dt.solve(matrix_w);
			double [][]r_w = result_w.getArray();
			double comulate1 = 0, comulate2 = 0, comulate3 = 0, comulate4 = 0, comulate5 = 0;
			for(int i = 0; i < m; i++){
				comulate1 += w[i] * r_w[i][0];
				comulate2 += mu1[i] + mu2[i];
				
				comulate3 += dt_z[i] * dt_z[i];
				comulate4 += Math.abs(d_y[i] - d_dt_z[i]);
				
				comulate5 += d_y[i] * z[i];
			}
		    pobj1 = 0.5*comulate1 + lambda*comulate2;
		    pobj2 = 0.5*comulate3 + lambda*comulate4;
		    pobj = Math.min(pobj1,pobj2);
		    dobj = -0.5*comulate3 + comulate5;
		    
		    double gap  =  pobj - dobj;


		    //% STOPPING CRITERION
		    if (gap <= TOL){
		        status = true;
		        for(int i = 0;i < n; i++){
		        	x[i] = y[i]-dt_z[i];
		        }
		        return;
		    }

		    if (step >= 0.2){
		        t = Math.max(2*m*MU/gap, 1.2*t);
		    }

		    //% CALCULATE NEWTON STEP
		    double t_1 = (1/t);
		    for(int i = 0;i < m; i++){
		    	rz[i] 	= d_dt_z[i] - w[i];
		    	r[i] 	= -d_dt_z[i] + d_y[i] + t_1/f1[i] - t_1/f2[i];
		    	matrix_r.set(i, 0, r[i]); 
		    }
		    for(int i = 0; i < m; i++){
		    	S[i][i] = d_dt[i][i] - (mu1[i] / f1[i]) - (mu2[i] /f2[i]);
		    	matrix_S.set(i, i, S[i][i]);
		    }
		    
		    Matrix matrix_dz = matrix_S.solve(matrix_r);
		    double[][] dz = matrix_dz.getArray();
		    
		    for(int i = 0; i < m; i++){
			    dmu1[i] = -(mu1[i]+(t_1+dz[i][0]*mu1[i])/f1[i]);
			    dmu2[i] = -(mu2[i]+(t_1-dz[i][0]*mu2[i])/f2[i]);
		    }

		    for(int i = 0; i < m;i++){
			    resDual[i] = rz[i];

			    resCent[i] = -mu1[i]*f1[i]-t_1;
			    resCent[m+i] = -mu2[i]*f2[i]-t_1;
			    
			    residual[i] = resDual[i] ;
			    residual[m+i] = resCent[i] ;
			    residual[2*m+i] = resCent[m+i] ;
		    }

		    //% BACKTRACKING LINESEARCH
		    boolean any_negIdx1 = false;
		    boolean any_negIdx2 = false;
		    for(int i = 0; i < m; i++){
			    negIdx1[i] = (dmu1[i] < 0); 
			    negIdx2[i] = (dmu2[i] < 0);
			    any_negIdx1 |= negIdx1[i];
			    any_negIdx2 |= negIdx2[i];
		    }
		    step = 1;
		    if (any_negIdx1){
		    	double min_negative = Double.MAX_VALUE;
		    	for(int i = 0; i < m; i++){
		    		if(negIdx1[i]){
		    			min_negative = Math.min(-mu1[i]/dmu1[i], min_negative); 
		    		}
		    	}
		        step = Math.min( step,  0.99*min_negative);
		    }
		    if (any_negIdx2){
		    	double min_negative = Double.MAX_VALUE;
		    	for(int i = 0; i < m; i++){
		    		if(negIdx2[i]){
		    			min_negative = Math.min(-mu2[i]/dmu2[i], min_negative); 
		    		}
		    	}
		        step = Math.min( step,  0.99*min_negative);
		    }

		    for(int liter = 1; liter < MAXLSITER; liter++){
		    	
		    	for(int i = 0; i < m; i++){
			        newz[i]    =  z[i]  + step*dz[i][0];
			        newmu1[i]  =  mu1[i] + step*dmu1[i];
			        newmu2[i]  =  mu2[i] + step*dmu2[i];
			        newf1[i]   =  newz[i] - lambda;
			        newf2[i]   = -newz[i] - lambda;
		    	}
		    	//% UPDATE RESIDUAL
		        DTx(m, newz, new_dt_z);
				Dx(n, new_dt_z, new_d_dt_z);
				
				for(int i = 0; i < m; i++){   
			        newResDual[i]  = new_d_dt_z[i] - d_y[i] + newmu1[i] - newmu2[i];
			        
			        newResCent[i]    = -newmu1[i] * newf1[i] - t_1;
			        newResCent[m+i]  = -newmu2[i] * newf2[i] - t_1;
			        
			        newResidual[i] 			= newResDual[i];
			        newResidual[i + m] 		= newResCent[i];
			        newResidual[i + 2*m] 	= newResCent[i+m];
		    	}
		    	
				double max_new = -Double.MAX_VALUE;
		    	for(int i = 0; i < m; i++){
		    		max_new = Math.max(newf1[i], max_new);
		    		max_new = Math.max(newf2[i], max_new);
		    	}
		    	
		    	double norm_residual = 0;
		    	double norm_newResidual = 0;
		    	for(int i = 0; i < residual.length; i++){
		    		norm_residual += residual[i] * residual[i];
		    		norm_newResidual += newResidual[i] * newResidual[i];
		    	}
		    	norm_residual = Math.sqrt(norm_residual);
		    	norm_newResidual = Math.sqrt(norm_newResidual);
		        
		        if ( max_new < 0 && norm_newResidual <= ((1-ALPHA*step)*norm_residual) ){
		            break;
		        }
		        step = BETA*step;
		    }
		    //% UPDATE PRIMAL AND DUAL VARIABLES
		    for(int i = 0; i < m; i++){
		    	z[i]  = newz[i]; 
		    	mu1[i] = newmu1[i]; 
		    	mu2[i] = newmu2[i]; 
		    	f1[i] = newf1[i]; 
		    	f2[i] = newf2[i];
		    }
		    
		    DTx(m, z, dt_z);
			Dx(n, dt_z, d_dt_z);
		}
		//% The solution may be close at this point, but does not meet the stopping
		//% criterion (in terms of duality gap).
		DTx(m, z, dt_z);
		for(int i = 0; i < n; i++){
			x[i] = y[i] - dt_z[i];
		}
		if (iters >= MAXITER){
		    status = false;
		    return;
		}
	}

	/* Computes y = D*x, where x has length n
	 *
	 *     | 1 -2  1  0  0 |
	 * y = | 0  1 -2  1  0 |*x
	 *     | 0  0  1 -2  1 |
	 */
	private static void Dx(final int n, final double [] x, double []y){
	    for (int i = 0; i < n-2; i++){
	        y[i] = x[i] - x[i+1] - x[i+1] + x[i+2];
	    }
	}

	/* Computes y = D^T*x, where x has length n
	 *
	 *     | 1  0  0 |
	 *     |-2  1  0 |
	 * y = | 1 -2  1 |*x
	 *     | 0  1 -2 |
	 *     | 0  0  1 |
	 */
	private static void DTx(final int n, final double[] x, double[] y){
	    y[0] = x[0];                          		/* y[0]     */
	    y[1] = -x[0]- x[0]+ x[1];               	/* y[1]     */
	    for (int i = 2; i < n; i++){
	    	y[i] = x[i-2] - x[i-1] - x[i-1] + x[i];
	    }
	    y[n] = x[n-2] - x[n-1]- x[n-1];        		/* y[n]     */
	    y[n+1] = x[n-1];                           	/* y[n+1]   */    
	    
	} 

	public static void test(){
		System.out.println("testing l1tf using data from washingmachine \n");
		
		int [] time_i1 = new int [] {42042,42051,42055,42062,42068,42070,42077,42082,42089,42093,42098,42103,42107,42112,42121,42126,42136,42143,42145,42152,42162,42171,42176,
				42180,42185,42194,42204,42208,42213,42217,42222,42232,42237,42244,42249,42253,42258,42265,42278,42280,42287,42302,42310,42315,42319,42324,42338,42342,42347,
				42352,42356,42370,42384,42388,42398,42402,42407,42412,42416,42421,42425,42430,42434,42439,42444,42448,42453,42458,42462,42469,42474,42479,42483,42488,42493,
				42502,42507,42511,42516,42525,42530,42535,42549,42553,42558,42563,42566,42573,42579,42582,42588,42593,42598,42603,42612,42617,42621,42626,42631,42635,42640,
				42645,42649,42654,42658,42663,42668,42672,42677,42684,42689,42694,42699,42704,42708,42713,42718,42724,42729,42734,42739,42745,42756,42761,42768,42775,42777,
				42783,42794,42801,42806,42811,42816,42820,42825,42830,42834,42839,42844,42848,42853,42857,42862,42867,42871,42876,42882,42887,42890,42895,42898,42902,42905,
				42913,42916,42920,42925,42930,42934,42938,42941,42945,42949,42953,42957,42961,42966,42970,42974,42978,42982,42986,42991,42995,43000,43001,43005,43013,43021,
				43025,43030,43034,43038,43042,43047,43051,43056,43060,43065,43069,43074,43079,43084,43088,43093,43098,43109,43114,43118,43122,43129,43136,43137,43144,43153,
				43157,43162,43172,43182,43186,43200,43205,43210,43227,43231,43241,43245,43250,43259,43268,43273,43276,43287,43290,43297,43316,43319,43329,43333,43338,43341,
				43346,43349,43353,43370,43374,43379,43384,43391,43396,43404,43407,43414,43418,43426,43435,43440,43445,43448,43453,43457,43462,43467,43471,43476,43481,43485,
				43488,43493,43498,43502,43507,43512,43516,43521,43527,43535,43540,43549,43553,43587,43590,43595,43602,43606,43618,43625,43630,43634,43639,43646,43651,43658,
				43663,43670,43677,43679,43692,43694,43705,43711,43718,43723,43735,43740,43749,43753,43758,43763,43767,43777,43781,43795,43800,43805,43812,43821,43825,43835,
				43841,43846,43849,43854,43863,43868,43880,43885,43889,43894,43899,43908,43912,43926,43931,43936,43942,43952,43957,43972,43977,43982,43987,43993,43997,44002,
				44013,44018,44022,44027,44036,44041,44045,44059,44064,44078,44082,44087,44093,44102,44107,44111,44122,44127,44130,44137,44148,44155,44160,44165,44170,44174,
				44179,44184,44189,44193,44199,44205,44211,44217,44221,44226,44231,44235,44240,44245,44249,44294,44297,44302,44311,44316,44321,44326,44331,44335,44344,44353,
				44358,44365,44370,44377,44382,44387,44391,44401,44405,44410,44418,44422,44427,44433,44438,44447,44452,44466,44475,44489,44493,44498,44506,44511,44515,44520,
				44525,44534,44538,44552,44557,44569,44581,44594,44596,44603,44610,44612,44623,44628,44637,44642,44646,44651,44655,44665,44669,44674,44689,44694,44700,44714,
				44727,44732,44737,44742,44746,44751,44756,44765,44769,44783,44788,44792,44797,44804,44809,44814,44820,44825,44830,44834,44839,44844,44847,44850,44853,44857,
				44860,44864,44870,44875,44880,44884,44889,44893,44898,44903,44912,44919,44937,44942,44956,44960,44965,44979,44983,44996,45001,45006,45010,45015,45019,45024,
				45029,45034,45039,45044,45053,45072,45076,45081,45085,45095,45099,45104,45115,45119,45125,45130,45139,45144,45159,45164,45174,45183,45187,45192,45201,45211,
				45229,45234,45245,45249,45254,45260,45272,45278,45287,45292,45303,45318,45325,45348,45353,45358,45362,45367,45371,45376,45381,45385,45401,45405,45414,45429,
				45433,45438,45442,45447,45451,45456,45461,45466,45470,45475,45479,45484,45489,45496,45501,45506,45511,45520,45525,45558,45567,45577,45581,45596,45602,45607,
				45625,45630,45636,45641,45646,45650,45655,45660,45664,45669,45676,45681,45686,45691,45696,45700,45705,45710,45714,45719,45728,45733,45737,45742,45747,45753,
				45767,45779,45786,45796,45802,45807,45812,45822,45828,45831,45843,45848,45853,45858,45862,45887,45892,45897,45910,45929,45938,45948,45951,45957,45964,45967,
				45972,45978,45984,45989,45993,45998,46012,46021,46026,46040,46048,46054,46066,46078,46095,46105,46110,46123,46128,46132,46152,46159,46161};
		
		double[] level_i1 = new double[] {73.0,51.0,53.0,17.0,62.0,66.0,70.0,66.0,17.0,55.0,19.0,117.0,124.0,128.0,11.0,141.0,90.0,126.0,128.0,124.0,111.0,126.0,149.0,160.0,
				175.0,21.0,205.0,128.0,154.0,168.0,75.0,149.0,36.0,156.0,175.0,168.0,55.0,49.0,175.0,173.0,183.0,164.0,177.0,179.0,164.0,179.0,166.0,100.0,183.0,171.0,181.0,
				203.0,136.0,132.0,1947.0,2060.0,2051.0,2054.0,2051.0,1928.0,2083.0,2039.0,2005.0,1919.0,2064.0,2041.0,2045.0,2041.0,1930.0,2118.0,2045.0,1921.0,1913.0,2026.0,
				2030.0,2032.0,1921.0,1913.0,2028.0,1913.0,1930.0,2028.0,2024.0,1904.0,2156.0,2037.0,1936.0,1900.0,2026.0,2028.0,2026.0,1932.0,1911.0,2019.0,1904.0,1896.0,
				2024.0,2015.0,2017.0,2009.0,1915.0,1985.0,2013.0,2015.0,1906.0,2011.0,2007.0,2024.0,1090.0,126.0,117.0,1938.0,2041.0,2019.0,2024.0,2032.0,1977.0,1904.0,2019.0,
				1949.0,1909.0,2009.0,2015.0,1962.0,1900.0,2009.0,2017.0,1900.0,2002.0,2017.0,1911.0,1896.0,2013.0,2019.0,1902.0,1913.0,2013.0,2007.0,2009.0,2005.0,1909.0,
				2132.0,2019.0,1926.0,1898.0,1992.0,2000.0,2011.0,2022.0,1909.0,1904.0,2100.0,2009.0,1906.0,1900.0,2007.0,2009.0,2011.0,2002.0,1900.0,1896.0,1977.0,2005.0,
				1990.0,1896.0,1979.0,2000.0,2024.0,2009.0,2011.0,1906.0,1896.0,1996.0,2009.0,1900.0,1889.0,2011.0,2009.0,2013.0,1906.0,1896.0,2054.0,2017.0,1970.0,1896.0,
				2032.0,2002.0,2013.0,2011.0,1902.0,1887.0,1996.0,2005.0,1892.0,226.0,115.0,15.0,115.0,109.0,117.0,38.0,23.0,124.0,109.0,28.0,113.0,111.0,28.0,115.0,124.0,
				109.0,113.0,107.0,122.0,113.0,70.0,107.0,109.0,98.0,200.0,102.0,96.0,111.0,100.0,113.0,115.0,111.0,134.0,111.0,23.0,26.0,23.0,26.0,17.0,145.0,85.0,66.0,68.0,
				60.0,55.0,53.0,55.0,58.0,53.0,55.0,68.0,77.0,81.0,85.0,87.0,252.0,136.0,145.0,149.0,158.0,254.0,296.0,324.0,371.0,403.0,450.0,482.0,301.0,309.0,303.0,299.0,
				294.0,17.0,15.0,17.0,51.0,53.0,17.0,122.0,66.0,75.0,17.0,79.0,87.0,17.0,107.0,113.0,17.0,136.0,139.0,143.0,141.0,132.0,134.0,168.0,128.0,134.0,145.0,141.0,
				139.0,143.0,134.0,130.0,136.0,38.0,149.0,130.0,132.0,141.0,134.0,13.0,141.0,136.0,73.0,130.0,139.0,124.0,132.0,94.0,134.0,245.0,141.0,134.0,132.0,113.0,232.0,
				132.0,143.0,132.0,134.0,94.0,147.0,134.0,134.0,130.0,136.0,75.0,9.0,139.0,134.0,136.0,122.0,124.0,126.0,26.0,23.0,26.0,23.0,17.0,139.0,92.0,68.0,58.0,53.0,
				55.0,53.0,51.0,53.0,51.0,53.0,73.0,79.0,87.0,205.0,128.0,134.0,143.0,141.0,245.0,273.0,337.0,377.0,437.0,480.0,290.0,294.0,288.0,286.0,281.0,279.0,17.0,15.0,
				119.0,53.0,17.0,66.0,75.0,77.0,87.0,17.0,85.0,17.0,96.0,113.0,17.0,132.0,139.0,149.0,154.0,28.0,145.0,132.0,36.0,143.0,141.0,145.0,30.0,145.0,136.0,141.0,
				134.0,70.0,154.0,141.0,85.0,147.0,139.0,143.0,134.0,247.0,132.0,143.0,139.0,109.0,145.0,222.0,134.0,141.0,136.0,141.0,143.0,132.0,9.0,128.0,134.0,139.0,
				134.0,222.0,136.0,73.0,23.0,26.0,23.0,17.0,203.0,128.0,73.0,49.0,68.0,60.0,58.0,55.0,58.0,53.0,55.0,70.0,73.0,81.0,87.0,90.0,136.0,145.0,149.0,151.0,237.0,
				288.0,318.0,352.0,388.0,418.0,463.0,497.0,356.0,345.0,339.0,335.0,331.0,324.0,19.0,17.0,19.0,17.0,15.0,17.0,43.0,51.0,23.0,104.0,23.0,154.0,151.0,23.0,145.0,
				158.0,23.0,309.0,160.0,145.0,154.0,23.0,147.0,151.0,147.0,143.0,158.0,92.0,164.0,151.0,156.0,149.0,151.0,145.0,260.0,162.0,145.0,147.0,154.0,162.0,134.0,104.0,
				154.0,151.0,158.0,147.0,190.0,151.0,145.0,149.0,181.0,151.0,254.0,141.0,143.0,149.0,26.0,17.0,19.0,17.0,168.0,119.0,70.0,21.0,68.0,55.0,53.0,55.0,53.0,68.0,
				75.0,79.0,83.0,85.0,260.0,122.0,126.0,128.0,130.0,237.0,275.0,316.0,358.0,198.0,200.0,196.0,194.0,192.0,17.0,15.0,70.0,85.0,41.0,36.0,79.0,41.0,21.0,60.0,
				66.0,53.0,62.0,53.0,107.0,128.0,162.0,196.0,239.0,288.0,324.0,360.0,405.0,241.0,252.0,256.0,254.0,252.0,254.0,252.0,254.0,249.0,252.0,249.0,247.0,249.0,247.0,
				249.0,247.0,249.0,247.0,249.0,247.0,249.0,247.0,252.0,249.0,245.0,247.0,249.0,245.0,247.0,245.0,247.0,15.0,17.0,15.0,17.0,15.0,17.0,15.0,17.0,15.0,28.0,17.0,
				47.0,21.0,128.0,49.0,126.0,32.0,49.0,128.0,49.0,49.0,51.0,13.0,9.0,66.0,49.0,45.0,73.0,45.0};

		double[] steps_i1 = new double [] {71.0,45.0,2.0,-36.0,45.0,4.0,4.0,-4.0,-49.0,38.0,-36.0,98.0,7.0,4.0,-117.0,130.0,84.0,36.0,2.0,-4.0,105.0,122.0,23.0,11.0,15.0,15.0,
				201.0,-77.0,26.0,14.0,-93.0,143.0,-113.0,120.0,19.0,-7.0,-113.0,43.0,169.0,-2.0,10.0,160.0,173.0,2.0,-15.0,15.0,162.0,-66.0,83.0,-12.0,10.0,199.0,130.0,-4.0,
				1941.0,113.0,-9.0,3.0,-3.0,-123.0,155.0,-44.0,-34.0,-86.0,145.0,-23.0,4.0,-4.0,-111.0,188.0,-73.0,-124.0,-8.0,113.0,4.0,2.0,-111.0,-8.0,115.0,-115.0,17.0,98.0,
				-4.0,-120.0,252.0,-119.0,-101.0,-36.0,126.0,2.0,-2.0,-94.0,-21.0,108.0,-115.0,-8.0,128.0,-9.0,2.0,-8.0,-94.0,70.0,28.0,2.0,-109.0,105.0,-4.0,17.0,-934.0,
				-964.0,-9.0,1821.0,103.0,-22.0,5.0,8.0,-55.0,-73.0,115.0,-70.0,-40.0,100.0,6.0,-53.0,-62.0,109.0,8.0,-117.0,102.0,15.0,-106.0,-15.0,117.0,6.0,-117.0,11.0,
				100.0,-6.0,2.0,-4.0,-96.0,223.0,-113.0,-93.0,-28.0,94.0,8.0,11.0,11.0,-113.0,-5.0,196.0,-91.0,-103.0,-6.0,107.0,2.0,2.0,-9.0,-102.0,-4.0,81.0,28.0,-15.0,-
				94.0,83.0,21.0,24.0,-15.0,2.0,-105.0,-10.0,100.0,13.0,-109.0,-11.0,122.0,-2.0,4.0,-107.0,-10.0,158.0,-37.0,-47.0,-74.0,136.0,-30.0,11.0,-2.0,-109.0,-15.0,
				109.0,9.0,-113.0,-1666.0,-111.0,9.0,100.0,-6.0,8.0,-79.0,-15.0,101.0,-15.0,22.0,85.0,-2.0,-83.0,109.0,9.0,103.0,4.0,-6.0,116.0,-9.0,66.0,37.0,2.0,-11.0,194.0,
				-98.0,-6.0,107.0,-11.0,13.0,109.0,-4.0,128.0,-23.0,-88.0,3.0,-3.0,3.0,-9.0,128.0,-60.0,-19.0,2.0,-8.0,-5.0,-2.0,2.0,3.0,-5.0,2.0,13.0,9.0,4.0,4.0,2.0,165.0,
				-116.0,9.0,4.0,9.0,96.0,42.0,28.0,47.0,32.0,47.0,32.0,-181.0,8.0,-6.0,-4.0,-5.0,-277.0,-2.0,2.0,34.0,2.0,-36.0,105.0,-56.0,9.0,-58.0,62.0,8.0,-70.0,90.0,6.0,
				-96.0,119.0,3.0,4.0,-2.0,126.0,2.0,162.0,-40.0,6.0,11.0,137.0,-2.0,139.0,-9.0,-4.0,6.0,-98.0,143.0,-19.0,126.0,9.0,-7.0,-121.0,135.0,-5.0,69.0,57.0,9.0,-15.0,
				8.0,90.0,40.0,239.0,-104.0,-7.0,-2.0,-19.0,226.0,-100.0,139.0,-11.0,2.0,-40.0,141.0,-13.0,130.0,-4.0,6.0,-61.0,-66.0,130.0,-5.0,130.0,-14.0,2.0,2.0,20.0,-3.0,
				3.0,-3.0,-6.0,122.0,-47.0,-24.0,-10.0,-5.0,2.0,-2.0,-2.0,2.0,-2.0,2.0,20.0,6.0,8.0,118.0,-77.0,6.0,9.0,-2.0,104.0,28.0,64.0,40.0,60.0,43.0,-190.0,4.0,-6.0,
				-2.0,-5.0,-2.0,-262.0,-2.0,104.0,-66.0,-36.0,49.0,9.0,2.0,10.0,-70.0,68.0,-68.0,79.0,17.0,-96.0,115.0,7.0,10.0,5.0,22.0,117.0,-13.0,30.0,107.0,-2.0,4.0,-115.0,
				141.0,-9.0,137.0,-7.0,64.0,84.0,-13.0,79.0,62.0,-8.0,4.0,-9.0,241.0,-115.0,137.0,-4.0,-30.0,139.0,216.0,-88.0,7.0,-5.0,5.0,137.0,-11.0,3.0,119.0,6.0,5.0,-5.0,
				216.0,-86.0,-63.0,19.0,3.0,-3.0,-6.0,186.0,-75.0,-55.0,-24.0,19.0,-8.0,-2.0,-3.0,3.0,-5.0,2.0,15.0,3.0,8.0,6.0,3.0,46.0,9.0,4.0,2.0,86.0,51.0,30.0,34.0,36.0,
				30.0,45.0,34.0,-141.0,-11.0,-6.0,-4.0,-4.0,-7.0,-305.0,-2.0,2.0,-2.0,-2.0,2.0,26.0,8.0,-28.0,81.0,-81.0,131.0,-3.0,-128.0,122.0,13.0,-135.0,286.0,-149.0,-15.0,
				9.0,-131.0,143.0,145.0,-4.0,-4.0,15.0,86.0,72.0,-13.0,150.0,-7.0,2.0,-6.0,254.0,-98.0,139.0,2.0,7.0,158.0,-28.0,-30.0,150.0,-3.0,154.0,-11.0,184.0,-39.0,-6.0,
				4.0,177.0,-30.0,250.0,-113.0,2.0,143.0,-123.0,-9.0,2.0,-2.0,151.0,-49.0,-49.0,-49.0,47.0,-13.0,-2.0,2.0,-2.0,15.0,7.0,4.0,4.0,2.0,175.0,-138.0,4.0,2.0,2.0,
				107.0,38.0,41.0,42.0,-160.0,2.0,-4.0,-2.0,-2.0,-175.0,-2.0,55.0,79.0,-44.0,32.0,43.0,-38.0,15.0,39.0,6.0,-13.0,9.0,-9.0,54.0,21.0,34.0,34.0,43.0,49.0,36.0,
				36.0,45.0,-164.0,11.0,4.0,-2.0,-2.0,2.0,-2.0,2.0,-5.0,3.0,-3.0,-2.0,2.0,-2.0,2.0,-2.0,2.0,-2.0,2.0,-2.0,2.0,-2.0,5.0,-3.0,-4.0,2.0,2.0,-4.0,2.0,-2.0,2.0,
				-232.0,2.0,-2.0,2.0,-2.0,2.0,-2.0,2.0,-2.0,13.0,-11.0,30.0,17.0,124.0,-79.0,122.0,26.0,17.0,122.0,45.0,43.0,47.0,-38.0,3.0,57.0,-17.0,41.0,28.0,-28.0};

		int [] lengths_i1 = new int [] {5,4,7,6,2,7,5,7,4,5,5,4,5,9,5,4,7,2,7,7,5,5,4,5,5,5,4,5,4,5,6,5,7,5,4,5,4,6,2,7,7,4,5,4,5,4,4,5,5,4,9,5,4,5,4,5,5,4,5,4,5,4,5,5,4,5,5,
				4,7,5,5,4,5,5,9,5,4,5,9,5,5,14,4,5,5,3,7,6,3,6,5,5,5,9,5,4,5,5,4,5,5,4,5,4,5,5,4,5,7,5,5,5,5,4,5,5,6,5,5,5,6,11,5,7,7,2,6,11,7,5,5,5,4,5,5,4,5,5,4,5,4,5,5,4,5,
				6,5,3,5,3,4,3,8,3,4,5,5,4,4,3,4,4,4,4,4,5,4,4,4,4,4,5,4,5,1,4,8,8,4,5,4,4,4,5,4,5,4,5,4,5,5,5,4,5,5,7,5,4,4,7,7,1,7,7,4,5,10,5,4,5,5,5,9,4,5,4,5,9,5,5,3,5,3,
				7,12,3,5,4,5,3,5,3,4,17,4,5,5,7,5,8,3,7,4,8,9,5,5,3,5,4,5,5,4,5,5,4,3,5,5,4,5,5,4,5,6,8,5,9,4,34,3,5,7,4,12,7,5,4,5,7,5,7,5,7,7,2,6,2,7,6,7,5,6,5,4,4,5,5,4,5,
				4,5,5,5,7,4,4,5,6,5,3,5,4,5,4,5,4,5,5,4,4,5,5,5,6,6,5,6,5,5,5,6,4,5,6,5,4,5,4,5,4,14,5,14,4,5,6,9,5,4,11,5,3,7,11,7,5,5,5,4,5,5,5,4,6,6,6,6,4,5,5,4,5,5,4,45,3,
				5,9,5,5,5,5,4,9,9,5,7,5,7,5,5,4,5,4,5,5,4,5,6,5,4,5,4,9,9,4,5,4,5,4,5,5,4,4,5,5,12,7,7,2,7,7,2,6,5,4,5,4,5,4,5,4,5,5,5,6,14,13,5,5,5,4,5,5,9,4,14,5,4,5,7,5,5,
				6,5,5,4,5,5,3,3,3,4,3,4,6,5,5,4,5,4,5,5,9,7,18,5,14,4,5,14,4,13,5,5,4,5,4,5,5,5,5,5,4,10,4,5,4,5,4,5,6,4,6,5,6,5,5,5,10,4,4,5,4,10,9,5,5,4,5,6,7,6,5,5,11,5,7,
				23,5,5,4,5,4,5,5,4,16,4,9,15,4,5,4,5,4,5,5,5,4,5,4,5,5,7,5,5,5,9,5,33,9,5,4,6,6,5,5,5,6,5,5,4,5,5,4,5,7,5,5,5,5,4,5,5,4,5,9,5,4,5,5,6,14,12,7,10,6,5,5,10,6,3,
				12,5,5,5,4,25,5,5,13,19,9,10,3,6,7,3,5,6,6,5,4,5,5,5,5,5,4,6,3,5,5,5,5,4,5,4,5,7,2,5};

		double lambda = ProcessorFeX.LAMBDA;
		double[] x = new double[level_i1.length];
		Boolean status = false;
		do_l1tf(level_i1, lambda, x, status);
		
		//export every thing to MATLAB file;
		MATLABVisualizer.saveMATLAB_l1tfTest(time_i1, level_i1, steps_i1, lengths_i1, x, status, lambda);
	}
}
