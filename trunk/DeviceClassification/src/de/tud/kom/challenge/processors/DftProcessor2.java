package de.tud.kom.challenge.processors;


import java.util.List;

import de.tud.kom.challenge.csvdatareader.CsvContainer;

/**
 * Calculates the second Version of the DFT
 * 
 * @author Leo Fuhr
 *
 */

public class DftProcessor2  implements FeatureProcessor{

	public String[] processInput(CsvContainer csv) throws Exception {
		
		List<String[]> csvBuffer = csv.getEntries();
		String[] result = new String[1];

		int fehlerAbweichung = 0;
		int schlauchEpsilon = 3;
		boolean equal_over_day = false;
		double durchschnitt = 0;		//average consumption over the day 
		
			double maxValue =0, minValue=0;
		int maxValuePos = 0;
		result[0] = "?";

		int hour = 0, minute = 0,  second = 0;		//time constants
		int newTime=0, oldTime =0, oldValue=0, newValue = 0;	//are used to calculate the consumption in the intervalss
		
		//in the consumption over the day is equal than set the value "equal_over_day" to true, else to false
		if (csvBuffer != null) 
		{	
			double count =0;
			for(int i = 0; i<csvBuffer.size(); i++)
			{
				try
				{
					hour =Integer.valueOf(csvBuffer.get(i)[0].substring(11, 13));
					minute = Integer.valueOf(csvBuffer.get(i)[0].substring(14, 16));
					second = Integer.valueOf(csvBuffer.get(i)[0].substring(17, 19));
					newTime = hour*3600 + minute*60 + second;
					if(newTime>oldTime)
					{
						newValue = Integer.valueOf(csvBuffer.get(i)[1]);
						if(newValue>oldValue)
						{
							count = count + oldValue*(newTime-oldTime) + (newValue-oldValue)*(newTime-oldTime)*0.5;
						}
						else
						{
							count = count + newValue*(newTime-oldTime) + (oldValue-newValue)*(newTime-oldTime)*0.5;
						}
						oldTime = newTime;
						oldValue = newValue;
					}
					//find the maximum value, and the according position
					if(maxValue<newValue)
					{
						maxValue=newValue;
						maxValuePos = i;
					}
				}
				catch(Exception e)
				{
					throw new Exception("linenumber: "+i);
				}
			}
		
			//calculate the first timestamp of the file
			hour = Integer.valueOf(csvBuffer.get(0)[0].substring(11, 13));
			minute = Integer.valueOf(csvBuffer.get(0)[0].substring(14, 16));
			second = Integer.valueOf(csvBuffer.get(0)[0].substring(17, 19));	
			//convert the first timestap in seconds
			int startTime = hour*3600 + minute*60 + second;
			
			//calculate the last timestamp of the file
			hour = Integer.valueOf(csvBuffer.get(csvBuffer.size()-1)[0].substring(11, 13));
			minute = Integer.valueOf(csvBuffer.get(csvBuffer.size()-1)[0].substring(14, 16));
			second = Integer.valueOf(csvBuffer.get(csvBuffer.size()-1)[0].substring(17, 19));
			//convert the last timestap in seconds
			int endTime = hour*3600 + minute*60 + second;
		
			//average consumption over the day
			durchschnitt = count /(endTime-startTime);
			
			//testing if the average is equal over the day
			for(int i = 0; i<csvBuffer.size()&&(fehlerAbweichung<21); i++)
			{
				try
				{
					if((Integer.parseInt(csvBuffer.get(i)[1])<(durchschnitt-schlauchEpsilon))||(Integer.parseInt(csvBuffer.get(i)[1])>(durchschnitt+schlauchEpsilon)))
					{
						fehlerAbweichung = fehlerAbweichung +1;
					}
				}
				catch(Exception e)
				{
					throw new Exception("line "+i);		
				}
			}
			
			if((fehlerAbweichung>20)||(durchschnitt<5))
			{
				equal_over_day = false;
			}
			else
			{
				equal_over_day = true;
			}
			//if average consumption is less than 5 Watt and the consumption is equal over the day
			if ((durchschnitt<5)&&(equal_over_day==false))
				durchschnitt =5;
		
		
		
			/**
			 * @ info	Cut the first possible intervall
		 	* 
		 	* */
			if(equal_over_day == false)
			{
				//DFT
				//create a window N
				int N = 200;
			
				int[] xk = new int[N];
				for(int i =0; i<xk.length;i++)
					xk[i] = 0;
				
				double[] real = new double[N];
				double[] imag = new double[N];
				double[] absolut_value = new double[N];
			
		
				//find the right situation for the window
				//if there 10 elementes lesser than maxValuePos and 91 elements greater than maxValuePos
				
				//System.out.println("buffersize: "+csvBuffer.size()+"\tmax: "+maxValuePos);
				if((csvBuffer.size()>maxValuePos+N))
				{
		//			System.out.println("ifzweig: ");
					for(int i=0; i<N;i++)
					{
						try{
				//			System.out.println("xk: "+ Integer.valueOf(csvBuffer.get(maxValuePos+i)[1]));
							xk[i] = Integer.valueOf(csvBuffer.get(maxValuePos+i)[1]);
						}catch(Exception e)
						{
							throw new Exception("Value not available "+i);		
						}		
					}
			//		System.out.println("draussen");
				}
				else
				{
			//		System.out.println("elsezeig");
					for(int i=N-1; i>=0; i--)
					{
						try{
							xk[i] = Integer.valueOf(csvBuffer.get(maxValuePos-i)[1]);
						}catch(Exception e)
						{
							throw new Exception("Value not available "+i);		
						}
					}
				}
				
/*
				if((csvBuffer.size()>=maxValuePos+N)&&(maxValuePos-10>=0))
				{
					for(int i=0; i<N;i++)
					{
						try{
							xk[i] = Integer.valueOf(csvBuffer.get(maxValuePos-9+i)[1]);
						}catch(Exception e)
						{
							throw new Exception("Value not available "+i);		
						}		
					}
				}
				else
				{
					//if after 
					if(csvBuffer.size()<maxValuePos+90)
					{
						temp = maxValuePos+90-csvBuffer.size();
						int a = 0;
						for(int i = 0; i<csvBuffer.size()-maxValuePos;i++)
							xk[10+i] = Integer.valueOf((csvBuffer.get(maxValuePos+i)[1]));
						for(int i=maxValuePos-10; i<maxValuePos;i++)
						{
							xk[a] = Integer.valueOf((csvBuffer.get(i)[1]));
							a=a+1;
						}
						xk[10] = Integer.valueOf((csvBuffer.get(maxValuePos)[1]));
					}
					if(maxValuePos-10<0)
					{
						temp = 10-maxValuePos+1;
						for(int i = 1; i<=maxValuePos; i++)
							xk[10-i] = Integer.valueOf((csvBuffer.get(maxValuePos-i)[1]));
						for(int i = 0; i<90;i++)
							xk[i+10] = Integer.valueOf((csvBuffer.get(i+maxValuePos)[1]));
						xk[10] = Integer.valueOf((csvBuffer.get(maxValuePos)[1]));
					}
				}
*/
				
				//calculating of the DFT
				for(int k=0; k<N; k++)
				{
					double angel_real = 0.0;
					double angel_imag = 0.0;
					if(k==0)
					{
						for(int n=0;n<N; n++)
						{
							angel_real = angel_real + xk[n];
						}
					}
					else
					{
						for(int n=0; n<N; n++)
						{
							angel_real = angel_real + xk[n]*Math.cos((2*Math.PI*n*k)/N);
							angel_imag = angel_imag - xk[n]*Math.sin((2*Math.PI*n*k)/N);
						}
					}
					real[k] = angel_real*xk[k];
					imag[k] = angel_imag*xk[k];
					absolut_value[k] = Math.sqrt( (real[k]*real[k])+(imag[k]*imag[k]) );
				}

				maxValue = absolut_value[0]; 
				minValue = absolut_value[0];
				
				int maxpos = 0;
				for(int i=0;i<absolut_value.length;i++)
				{
					if(maxValue<absolut_value[i])
					{
						maxValue =(int) absolut_value[i]; 
						maxpos = i;
					}
					if(minValue>absolut_value[i])
						minValue =(int) absolut_value[i]; 
				}
				result[0] = ""+maxpos;
	
				return result;
			}
			else
			{
				//DFT
				//create a window N
				int N = 100;
			
				int[] xk = new int[N];
				double[] real = new double[N];
				double[] imag = new double[N];
				double[] absolut_value = new double[N];
			
				//fill up the window with zeros
				for(int i=0; i<N;i++)
				{
					try{
							xk[i] = Integer.valueOf(csvBuffer.get(50+i)[1]);
					}catch(Exception e)
					{
						throw new Exception("Value not available "+i);		
					}
				}
				
				//calculating of the DFT
				for(int k=0; k<N; k++)
				{
					double angel_real = 0.0;
					double angel_imag = 0.0;
					if(k==0)
					{
						for(int n=0;n<N; n++)
						{
							angel_real = angel_real + xk[n];
						}
					}
					else
					{
						for(int n=0; n<N; n++)
						{
							angel_real = angel_real + xk[n]*Math.cos((2*Math.PI*n*k)/N);
							angel_imag = angel_imag - xk[n]*Math.sin((2*Math.PI*n*k)/N);
						}
					}
					real[k] = angel_real*xk[k];
					imag[k] = angel_imag*xk[k];
					absolut_value[k] = Math.sqrt( (real[k]*real[k])+(imag[k]*imag[k]) );
				}

				maxValue =absolut_value[0]; 
				minValue = absolut_value[0];
				for(int i=0;i<absolut_value.length;i++)
				{
					if(maxValue<absolut_value[i])
						maxValue =(int) absolut_value[i]; 
					if(minValue>absolut_value[i])
						minValue =(int) absolut_value[i]; 
				}
				result[0] = ""+(maxValue/minValue);

				return result;	
			}
			
		
			
		}
		
	return result;
	}
	
	@Override
	public String[] getAttributeNames() {
		// TODO Auto-generated method stub
		String[] result = new String[]{
				"Relation_between_max_and_min"
				};
		return result;
	}

	@Override
	public String[] getAttributeValueranges() {
		// TODO Auto-generated method stub
		String[] result = new String[]{
				"numeric"				
				};
		return result;
	}

	@Override
	public String getProcessorName() {
		// TODO Auto-generated method stub
		return "DFT";
	}

}
