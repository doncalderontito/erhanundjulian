package de.tud.kom.challenge.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;

import de.tud.kom.challenge.arff.FileMapper;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 * @author Hristo Chonov
 */
public class FileUtil {

	private static void copyFile(final File in, final File out) {
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			inChannel = new FileInputStream(in).getChannel();
			outChannel = new FileOutputStream(out).getChannel();

			inChannel.transferTo(0, inChannel.size(), outChannel);

			inChannel.close();
			if (outChannel != null) {
				outChannel.close();
			}
		} catch (final IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public static File copyFileToTesting(final File in) {
		final File out = new File("testing/" + in.getName());
		FileUtil.copyFile(in, out);

		return out;
	}

	public static void deleteFile(final File f) {
		f.delete();
	}

	public static void writeResultToDisk(final String fileName, final Object obj) {
		try {
			// Use a FileOutputStream to send data to a file called
			// myobject.data.
			final FileOutputStream f_out = new FileOutputStream(fileName);

			// Use an ObjectOutputStream to send object data to the
			// FileOutputStream for writing to disk.
			final ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

			// Pass our object to the ObjectOutputStream's writeObject() method
			// to cause it to be written out to disk.
			obj_out.writeObject(obj);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static Object restoreDataFromDisc(final String file) {
		try {
			// Read from disk using FileInputStream.
			final FileInputStream f_in = new FileInputStream(file);

			// Read object using ObjectInputStream.
			final ObjectInputStream obj_in = new ObjectInputStream(f_in);

			// Read an object.
			return obj_in.readObject();

		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static synchronized void simpleWriteToDisc(String file, Object toWrite, boolean append) {
		try {
			FileWriter fstream = new FileWriter(file, append);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(toWrite.toString());
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static Object[] getCombinationOptionsClassifierFromFile(final String filename) {
		Object[] result = new Object[2];
		String output = null;
		FileReader input = null;
		try {
			input = new FileReader(filename);
			BufferedReader bufRead = new BufferedReader(input);
			output = bufRead.readLine();
			bufRead.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.err.println("Filename: " + filename);
		}catch (IOException e) {
			System.err.println(e.getMessage());
			System.err.println("Filename: " + filename);
		}
		
		int first = output.indexOf(';')+1;
		int second = output.indexOf(';', first);
		int third = output.indexOf(';', second+1);
		
		String options = output.substring(second+2, third-1);
		String[] o = options.split(", ");
		
		String classifier = output.substring(first, second);
		
		first = output.indexOf('[')+1;
		second = output.indexOf(']', first);
		String combination = output.substring(first, second);
		String[] co = combination.split(",");
		int[] c = new int[co.length];
		for(int i=0;i<co.length;i++)
			c[i]=Integer.valueOf(co[i]);
		
		result[0] = c;
		result[1] = o;
		result[2] = classifier;
		
		return result;

	}
	
	public static void emptyFolder(String dirName) {
		if(!new File(dirName).exists()) {
			new File(dirName).mkdir();
			return;
		}
		File[] children = new File(dirName).listFiles();
		if (children != null) for(File file: children) {	
		file.delete();
		
		}
	}
	
	public static String getShortPath(String filename) {
		int indTraining = filename.indexOf(FileMapper.trainingPath);
		int indTesting = filename.indexOf(FileMapper.testingPath);

		if(indTraining != -1){
			return filename.substring(indTraining);			
		}
		if(indTesting != -1){
			return filename.substring(indTesting);			
		}
		
		return "";
	}

}
