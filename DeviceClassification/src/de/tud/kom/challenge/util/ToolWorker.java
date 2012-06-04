package de.tud.kom.challenge.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ListIterator;
import org.apache.log4j.Logger;
import de.tud.kom.challenge.util.RandomInteger;


public class ToolWorker {
	
	private final static Logger log = Logger.getLogger(ToolWorker.class.getSimpleName());
	
	//to creat a Random Integer
	private static RandomInteger rand = new RandomInteger();
	
	  public static void fillUpMissingSamples(List<File> f){
		  ListIterator<File> it = f.listIterator();

		  while(it.hasNext()){
			  File originalFile = it.next();
			  
			 
			  String myFileName= originalFile.getName();
			  System.out.println("MyFileName_vorher: "+myFileName);
			  myFileName = myFileName.substring(0, myFileName.lastIndexOf("."));
			  System.out.println("MyFileName_nachher: "+myFileName);
			  //create folder if not exist
			  String myPath = originalFile.getParent() + File.separator + "timeFilledUp";
			  new File(myPath).mkdir();
			  
			  //go through the file 
			try {

				FileInputStream fileIn = new FileInputStream(originalFile);
				DataInputStream in = new DataInputStream(fileIn);				 
				BufferedReader br = new BufferedReader(new InputStreamReader(in));

				File newFile = new File(myPath + File.separator + myFileName + ".csv");
				OutputStream out = new FileOutputStream(newFile,false);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
	

				String strLine = "";
				String lastLine = null;
				int tmpTime = 1;
				int timeStampSecs = 0;
				boolean isFirstLine=true;
				rand.setRange(1, 5);
				DecimalFormat df = new DecimalFormat("00");
				while ((strLine = br.readLine()) != null)   {					
					try{
						if(isFirstLine){
							int hour = Integer.valueOf(strLine.substring(11, 13));
							int minute = Integer.valueOf(strLine.substring(14, 16));
							int second = Integer.valueOf(strLine.substring(17, 19));
							timeStampSecs = second + (minute * 60) + (hour * 3600);							
							isFirstLine=false;
							if(timeStampSecs>60){
								while(tmpTime<timeStampSecs){
									hour = tmpTime/3600;
									minute = (tmpTime/60)%60;
									second = tmpTime%60;
									bw.write(strLine.substring(0,11) + df.format(hour) + ":"+ df.format(minute) + ":"+ df.format(second) + ";0;0");
									bw.newLine();
									tmpTime = tmpTime + generateNumber();
								}
							}
						}
						bw.write(strLine);
						bw.newLine();						
					}catch(Exception e){
						log.error(e);
						e.printStackTrace();
					}
					lastLine=strLine;					
				}
				if (lastLine!=null){
					int hour = Integer.valueOf(lastLine.substring(11, 13));
					int minute = Integer.valueOf(lastLine.substring(14, 16));
					int second = Integer.valueOf(lastLine.substring(17, 19));
					timeStampSecs = second + (minute * 60) + (hour * 3600);		
					if(timeStampSecs<((24*3600)-120)){
						tmpTime=timeStampSecs+1;
						while(tmpTime<((24*3600)-2)){
							hour = tmpTime/3600;
							minute = (tmpTime/60)%60;
							second = tmpTime%60;
							bw.write(lastLine.substring(0,11) + df.format(hour) + ":"+ df.format(minute) + ":"+ df.format(second) + ";0;0");
							bw.newLine();
							tmpTime = tmpTime + generateNumber();
						}
					}
				}
				
				
				bw.flush();
				bw.close();
				
				
			}catch(Exception e){
				log.error(e);
				e.printStackTrace();
			}
		}	
	}
	  
	private static int generateNumber(){
		return rand.getRandomInteger();
	}
	
	  
	  public static void copyPlainFiles(List<File> f){
		  ListIterator<File> it = f.listIterator();
		  while(it.hasNext()){
			  File tmpfile = it.next();

			  String myFileName= tmpfile.getName();
			  myFileName = myFileName.substring(0, myFileName.lastIndexOf("."))+"_plain_";
			  
			  //create folder if not exist
			  String myPath = tmpfile.getParent() + File.separator + "plainFiles";
			  new File(myPath).mkdir();

			  //go through the file 
			try {
				FileInputStream fileIn = new FileInputStream(tmpfile);
				DataInputStream in = new DataInputStream(fileIn);				 
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				File newFile = null;
				OutputStream out = null;
				BufferedWriter bw = null;
				
				String strLine = "";
				String tmpDate ="";
				int lastHour = 0;
				
				while ((strLine = br.readLine()) != null)   {
					//initial assignment
					if (tmpDate==""){ 
						tmpDate = strLine.substring(0,10);
						
						String date = tmpDate.replace("/", ".");
						date = date.replace("\\", ".");
						date = date.replace("\"" , "");
						date = date.replace("'" , "");
						newFile = new File(myPath + File.separator + myFileName + date  + ".csv");
						out = new FileOutputStream(newFile,false);
						bw = new BufferedWriter(new OutputStreamWriter(out));
					}
					
					String tmpDateStr = strLine.substring(0,10);
					int tmpHour = Integer.parseInt(strLine.substring(strLine.indexOf(":")-2,strLine.indexOf(":")));
					if (tmpDate.contentEquals(tmpDateStr) && (tmpHour >= lastHour)){						
						lastHour = tmpHour;						
						
						bw.append(strLine.substring(strLine.indexOf(";")+1,strLine.lastIndexOf(";")));
						
						bw.newLine();
					}else{
						lastHour = tmpHour;	
						tmpDate = tmpDateStr;
						
						bw.flush();
						bw.close();
						
						String date = tmpDate.replace("/", ".");
						date = date.replace("\\", ".");
						date = date.replace("\"" , "");
						date = date.replace("'" , "");
						newFile = new File(myPath + File.separator + myFileName + date  + ".csv");
						out = new FileOutputStream(newFile,false);
						bw = new BufferedWriter(new OutputStreamWriter(out));
					}				
				}
				bw.flush();
				bw.close();
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}  
		  } 			  			  
	  }
	
	  
	  public static void convertFiles(List<File> f){
		  ListIterator<File> it = f.listIterator();
		  while(it.hasNext()){
			  File tmpfile = it.next();			  			 
			  String myFileName= tmpfile.getName();
			  myFileName = myFileName.substring(0, myFileName.lastIndexOf("."))+"_cleaned_";
			  
			  //create folder if not exist
			  String myPath = tmpfile.getParent() + File.separator + "cleanedFiles";
			  new File(myPath).mkdir();
			  
			  //go through the file 
			try {
				FileInputStream fileIn = new FileInputStream(tmpfile);
				DataInputStream in = new DataInputStream(fileIn);				 
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				File newFile = null;
				OutputStream out = null;
				BufferedWriter bw = null;
				
				String strLine = "";
				String tmpDate ="";
				int lastHour = 0;
				
				while ((strLine = br.readLine()) != null)   {
					//initial assignment
					if (tmpDate==""){ 
						tmpDate = strLine.substring(0,10);
						
						String date = tmpDate.replace("/", ".");
						date = date.replace("\\", ".");
						date = date.replace("\"" , "");
						date = date.replace("'" , "");
						newFile = new File(myPath + File.separator + myFileName + date  + ".csv");
						out = new FileOutputStream(newFile,false);
						bw = new BufferedWriter(new OutputStreamWriter(out));
					}
					
					String tmpDateStr = strLine.substring(0,10);
					int tmpHour = Integer.parseInt(strLine.substring(strLine.indexOf(":")-2,strLine.indexOf(":")));
					if (tmpDate.contentEquals(tmpDateStr) && (tmpHour >= lastHour)){						
						lastHour = tmpHour;						
						bw.append(strLine);
						bw.newLine();
					}else{
						lastHour = tmpHour;	
						tmpDate = tmpDateStr;
						
						bw.flush();
						bw.close();
						
						String date = tmpDate.replace("/", ".");
						date = date.replace("\\", ".");
						date = date.replace("\"" , "");
						date = date.replace("'" , "");
						newFile = new File(myPath + File.separator + myFileName + date  + ".csv");
						out = new FileOutputStream(newFile,false);
						bw = new BufferedWriter(new OutputStreamWriter(out));
					}				
				}
				bw.flush();
				bw.close();
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}  
		  }  
	  }

}
