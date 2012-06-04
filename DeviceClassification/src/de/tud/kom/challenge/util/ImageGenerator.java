package de.tud.kom.challenge.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * 
 * @author Daniel Burgstahler
 *
 */
public class ImageGenerator {
	
	private final static Logger log = Logger.getLogger(ImageGenerator.class.getSimpleName());

	  public static Image drawDetailedGraph(File f){
		  try{
			  int lineCount = 0;
			  double average1 = 0;
			  double average8 = 0;
			  long max1 = 0;
			  long max8 = 0;
			  String errorLines="";
			  
			  long[][] actualValues = new long [1][2];
			  
						  
			  if (f.exists()){
				  String strLine;				  				  				  				  
				  long sample1 = 0;
				  long sample8 = 0;				 
				  String tmpStr = "";
				  
				  FileInputStream fileIn = new FileInputStream(f);
				  DataInputStream in = new DataInputStream(fileIn);				 
				  BufferedReader br = new BufferedReader(new InputStreamReader(in));
				  
				 //1 - count lines in file
				  while ((strLine = br.readLine()) != null)   {
					  lineCount++;
					  if (strLine.length()<23){
						  errorLines+=  lineCount + "; ";
					  }
				  }
				  
				  br.close();
				  
				  int imgHeight = 530;
				  int imgWidth = lineCount/3;
				  
				  if(errorLines.length()>1){
					  errorLines="erroneous lines: "+errorLines;
					  
					  BufferedImage newGraphImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);				  
					  Graphics2D g = newGraphImage.createGraphics();
					  
					  g.setFont(new Font("Arial", Font.BOLD,  12));
					  g.setColor(new Color(222,20,20));
					  g.drawString(errorLines, 50, 305);
					  				  				 
					  return newGraphImage;
				  }				 
				  
				  if (lineCount==0){
					  return null;
				  }
				  
				  fileIn = new FileInputStream(f);
				  in = new DataInputStream(fileIn);				 
				  br = new BufferedReader(new InputStreamReader(in));
				  
				  String begin = "";
				  String end = "";
				  
				  actualValues = new long [lineCount][2];
				  int tmpCount = 0;
				  HashMap<Integer,String> timePositions = new HashMap<Integer,String> ();
				  String oldTime = "";
				  
				  //2 - get max and average values				  
				  while ((strLine = br.readLine()) != null)   {
					  if (begin == ""){
						  begin = strLine.substring(0 ,strLine.indexOf(";"));
					  }
					  
					  //read times to display at correct positions
					  String tmpTimeStr = strLine.substring(strLine.indexOf(":")-2 ,strLine.indexOf(":"));
					  tmpTimeStr += ":00";
					  if (!oldTime.contentEquals(tmpTimeStr)){
						  oldTime = tmpTimeStr;
						  timePositions.put(tmpCount, tmpTimeStr);
					  }
					  
					  end = strLine.substring(0 ,strLine.indexOf(";"));
					  tmpStr = strLine.substring(strLine.indexOf(";")+1 ,strLine.length());
					  sample1 = Long.parseLong(tmpStr.substring(0, tmpStr.indexOf(";")));
					  sample8 = Long.parseLong(tmpStr.substring(tmpStr.indexOf(";")+1 ,tmpStr.length()));
					  
					  //+++++++++++++
					  if(tmpCount<=lineCount){
						  actualValues[tmpCount][0]=sample1;					  		
						  actualValues[tmpCount][1]=sample8;
					  }
					  tmpCount++;
					  //+++++++++++++
					  
					  if (max1<sample1){
						  max1=sample1;
					  }
					  if (max8<sample8){
						  max8=sample8;
					  }					  
					  average1 +=  ((double)sample1/lineCount);
					  average8 +=  ((double)sample8/lineCount);	
					  
				  }
				  
				  
				  String infoTxt = "max. value 1 = "+ (Math.round( max1 * 100. ) / 100.) +"      max value 8 = "+ (Math.round( max8 * 100. ) / 100.) + 
				  		"      average value 1 = "+ (Math.round( average1 * 100. ) / 100.) +"      average value 8 = "+ (Math.round( average8 * 100. ) / 100.) +
				  		"       # of values = " + lineCount + "     from: " + begin+ " to: " + end;
				  
				  br.close();				 

				  long tmpMaxValue = max1;
				  if (max8>tmpMaxValue){
					  tmpMaxValue=max8;
				  }
				  
				  
				  
				  
				  BufferedImage newGraphImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);				  
				  Graphics2D g = newGraphImage.createGraphics();				  
				  g.setColor( Color.white);		
				  g.fillRect(0,0,imgWidth,imgHeight);

				  g.setColor(new Color(60,60,60));
				  g.setFont(new Font("Arial", Font.ITALIC,  12));
				  
				  g.drawChars(new String("0").toCharArray(), 0, 1, 15, 455);
				  for (int i=1; i<11; i++){
					  String tmpValStr = String.valueOf(Math.round(  (tmpMaxValue/10) *(11- i) ));
					  g.drawChars(tmpValStr.toCharArray(), 0, tmpValStr.length(), 15, 55 + ((i-1)*40));
					  g.drawLine(45, 50 + ((i-1)*40), 50, 50 + ((i-1)*40));
				  }

				  
				  double heightFactor = (double)400/tmpMaxValue;
				  double widthFactor = (double)(imgWidth-100)/lineCount;
				  				  
				  average1 = average1 * heightFactor;
				  int tmpIntAverage =  new Double(average1).intValue();
				  g.setColor(Color.red);
				  g.drawLine(50, 450-tmpIntAverage,imgWidth-50, 450-tmpIntAverage);
				  
				  average8 = average8 * heightFactor;
				  tmpIntAverage =  new Double(average8).intValue();
				  g.setColor(Color.magenta);
				  g.drawLine(50, 450-tmpIntAverage,imgWidth-50, 450-tmpIntAverage);
				  
				  int oldY1 = 0;
				  int newY1 = 0;				  
				  int oldX = 0;				  
				  int newX = 0;
				  int oldY8 = 0;
				  int newY8 = 0; 
				  
				  for (int i=0; i< lineCount; i++){
					  newY1 = new Double (actualValues[i][0] * heightFactor).intValue();
					  newX = new Double(i * widthFactor).intValue();
					  newY8 = new Double (actualValues[i][1] * heightFactor).intValue();					  

					  g.setColor(new Color(120,150,60));
					  g.drawLine(oldX+50, 450-oldY1, newX+50, 450-newY1);
					  oldY1=newY1;					  
					  
					  g.setColor(new Color(50,135,155));
					  g.drawLine(oldX+50, 450-oldY8, newX+50, 450-newY8);
					  oldX=newX;
					  oldY8=newY8;
					  
					  if (timePositions.containsKey(i)){
						  g.setColor(new Color(60,60,60));						  
						  g.drawChars(timePositions.get(i).toCharArray(), 0, timePositions.get(i).length(), newX+50, 470);
					  }
				  }

				  g.setColor(new Color(60,60,60));				  				  
				  g.drawChars(infoTxt.toCharArray(), 0, infoTxt.length(), 50, 505);
				  
				  g.setFont(new Font("Arial", Font.BOLD,  12));
				  g.setColor(new Color(222,20,20));
				  g.drawString(errorLines, 50, 521);
				  				  				 
				  return newGraphImage;
			  }
			  return null;
		  }catch (Exception e){
			  e.printStackTrace();
			  log.error(e);
			  return null;
		  }		  
	  }
	  
	  public static Image drawSmallGraph(File f){
		  try{
			  int lineCount = 0;
			  double average1 = 0;
			  double average8 = 0;
			  long max1 = 0;
			  long max8 = 0;
			  String errorLines="";
			  
			  int imgHeight = 315;
			  int imgWidth = 900;
			  
			  long[][] actualValues = new long [1][2];
			  
						  
			  if (f.exists()){
				  String strLine;				  				  				  				  
				  long sample1 = 0;
				  long sample8 = 0;				 
				  String tmpStr = "";
				  
				  FileInputStream fileIn = new FileInputStream(f);
				  DataInputStream in = new DataInputStream(fileIn);				 
				  BufferedReader br = new BufferedReader(new InputStreamReader(in));
				  
				 //1 - count lines in file
				  while ((strLine = br.readLine()) != null)   {
					  lineCount++;				  
					  if (strLine.length()<23){
						  errorLines+=  lineCount + "; ";
					  }
				  }
				  
				  br.close();
				  
				  if(errorLines.length()>1){
					  errorLines="erroneous lines: "+errorLines;
					  
					  BufferedImage newGraphImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);				  
					  Graphics2D g = newGraphImage.createGraphics();
					  
					  g.setColor( Color.white);		
					  g.fillRect(0,0,imgWidth,imgHeight);
					  
					  g.setColor(new Color(60,60,60));	
					  g.setFont(new Font("Arial", Font.PLAIN,  11));
					  g.drawString(f.getName(), 50, 305);
					  
					  
					  g.setFont(new Font("Arial", Font.BOLD,  12));
					  g.setColor(new Color(222,20,20));
					  g.drawString(errorLines, 50, 205);
					  				  				 
					  return newGraphImage;
				  }


				  
				  if (lineCount==0){
					  return null;
				  }
				  
				  fileIn = new FileInputStream(f);
				  in = new DataInputStream(fileIn);				 
				  br = new BufferedReader(new InputStreamReader(in));
				  
				  String begin = "";
				  String end = "";
				  
				  actualValues = new long [lineCount][2];
				  int tmpCount = 0;
				  HashMap<Integer,String> timePositions = new HashMap<Integer,String> ();
				  String oldTime = "";
				  
				  //2 - get max and average values				  
				  while ((strLine = br.readLine()) != null)   {
					  if (begin == ""){
						  begin = strLine.substring(0 ,strLine.indexOf(";"));
					  }
					  
					  
					  //read times to display at correct positions
					  String tmpTimeStr = strLine.substring(strLine.indexOf(":")-2 ,strLine.indexOf(":"));
					  tmpTimeStr += ":00";
					  if (!oldTime.contentEquals(tmpTimeStr)){
						  oldTime = tmpTimeStr;
						  timePositions.put(tmpCount, tmpTimeStr);
					  }
					  
					  end = strLine.substring(0 ,strLine.indexOf(";"));
					  tmpStr = strLine.substring(strLine.indexOf(";")+1 ,strLine.length());
					  sample1 = Long.parseLong(tmpStr.substring(0, tmpStr.indexOf(";")));
					  sample8 = Long.parseLong(tmpStr.substring(tmpStr.indexOf(";")+1 ,tmpStr.length()));
					  
					  //+++++++++++++
					  if(tmpCount<=lineCount){
						  actualValues[tmpCount][0]=sample1;					  		
						  actualValues[tmpCount][1]=sample8;
					  }
					  tmpCount++;
					  //+++++++++++++
					  
					  if (max1<sample1){
						  max1=sample1;
					  }
					  if (max8<sample8){
						  max8=sample8;
					  }					  
					  average1 +=  ((double)sample1/lineCount);
					  average8 +=  ((double)sample8/lineCount);	
					  
				  }
				  
				  String infoTxt1 = "max. value 1 = "+ (Math.round( max1 * 100. ) / 100.) +"      max value 8 = "+ (Math.round( max8 * 100. ) / 100.) + 
			  		"      average value 1 = "+ (Math.round( average1 * 100. ) / 100.) +"      average value 8 = "+ (Math.round( average8 * 100. ) / 100.) ;
			  		String infoTxt2 = "# of values = " + lineCount + "     from: " + begin+ " to: " + end;
				  							  
				  br.close();				 

				  long tmpMaxValue = max1;
				  if (max8>tmpMaxValue){
					  tmpMaxValue=max8;
				  }				  				  
				  
				  BufferedImage newGraphImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);				  
				  Graphics2D g = newGraphImage.createGraphics();				  
				  g.setColor( Color.white);		
				  g.fillRect(0,0,imgWidth,imgHeight);

				  g.setColor(new Color(60,60,60));
				  g.setFont(new Font("Arial", Font.ITALIC ,  11));
				  
				  g.drawChars(new String("0").toCharArray(), 0, 1, 15, 235);
				  for (int i=1; i<6; i++){
					  String tmpValStr = String.valueOf(Math.round(  (tmpMaxValue/5) *(6- i) ));
					  g.drawChars(tmpValStr.toCharArray(), 0, tmpValStr.length(), 10, 35 + ((i-1)*40));
					  g.drawLine(40, 30 + ((i-1)*40), 45, 30 + ((i-1)*40));
				  }
				  g.setFont(new Font("Arial", Font.ITALIC+ Font.BOLD,  10));
				  
				  double heightFactor = (double)200/tmpMaxValue;
				  double widthFactor = (double)(imgWidth-100)/lineCount;
				  				  
				  average1 = average1 * heightFactor;
				  int tmpIntAverage =  new Double(average1).intValue();
				  g.setColor(Color.red);
				  g.drawLine(45, 235-tmpIntAverage,imgWidth-45, 235-tmpIntAverage);
				  
				  average8 = average8 * heightFactor;
				  tmpIntAverage =  new Double(average8).intValue();
				  g.setColor(Color.magenta);
				  g.drawLine(45, 235-tmpIntAverage,imgWidth-45, 235-tmpIntAverage);
				  
				  int oldY1 = 0;
				  int newY1 = 0;				  
				  int oldX = 0;				  
				  int newX = 0;
				  int oldY8 = 0;
				  int newY8 = 0; 
				  
				  for (int i=0; i< lineCount; i++){
					  newY1 = new Double (actualValues[i][0] * heightFactor).intValue();
					  newX = new Double(i * widthFactor).intValue();
					  newY8 = new Double (actualValues[i][1] * heightFactor).intValue();					  

					  g.setColor(new Color(120,150,60));
					  g.drawLine(oldX+50, 235-oldY1, newX+50, 235-newY1);
					  oldY1=newY1;					  
					  
					  g.setColor(new Color(50,135,155));
					  g.drawLine(oldX+50, 235-oldY8, newX+50, 235-newY8);
					  oldX=newX;
					  oldY8=newY8;
					  
					  if (timePositions.containsKey(i)){
						  g.setColor(new Color(60,60,60));						  
						  g.drawChars(timePositions.get(i).toCharArray(), 0, timePositions.get(i).length(), newX+50, 250);
					  }
				  }

				  g.setColor(new Color(60,60,60));	
				  g.setFont(new Font("Arial", Font.PLAIN,  11));
				  g.drawChars(infoTxt1.toCharArray(), 0, infoTxt1.length(), 50, 270);
				  g.drawChars(infoTxt2.toCharArray(), 0, infoTxt2.length(), 50, 288);
				  
				  g.setFont(new Font("Arial", Font.BOLD,  12));
				  g.setColor(new Color(222,20,20));
				  g.drawString(errorLines, 50, 305);
				  				  				 
				  return newGraphImage;
			  }
			  return null;
		  }catch (Exception e){
			  e.printStackTrace();
			  log.error(e);
			  return null;
		  }		  
	  }
}
