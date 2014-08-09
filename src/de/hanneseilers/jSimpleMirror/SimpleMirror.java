package de.hanneseilers.jSimpleMirror;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SimpleMirror {
	
	public static boolean LOG_ENABLED = true;
	public static boolean START_SERVICE = false;
	public static String CONFIG_FILE = "config.cfg";
	
	private static final String CONFIG_MIRROR_SEPERATOR = ";";
	private static final String CONFIG_COMMENT = "#";
	
	/**
	 * Reads mirrors configuration from config file
	 * @return 
	 * @throws IOException 
	 */
	public static Map<String, String> readMirrors() throws IOException{		
		Map<String, String> vMirrors = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader( new DataInputStream( new FileInputStream(CONFIG_FILE) ) ) );
		
		String line = null;
		while( (line = reader.readLine()) != null ){
			
			line = line.trim();
			if( !line.startsWith(CONFIG_COMMENT) ){
				String[] mirror = line.split(CONFIG_MIRROR_SEPERATOR);
				if( mirror.length > 1 ){
					String src = mirror[0].trim();
					String dst = mirror[1].trim();
					
					if( src.endsWith("\\") ){
						src = src.substring(0, src.length()-1);
					}
					if( dst.endsWith("\\") ){
						dst = dst.substring(0, dst.length()-1);
					}
					
					vMirrors.put(src, dst);
				}
			}
			
		}
		
		reader.close();
		
		return vMirrors;
	}
	
	public static void log(String msg){
		log(msg, true, false);
	}
	
	public static void err(String msg){
		log(msg, true, true);
	}
	
	/**
	 * Logging function.
	 * Checks for {@code LOG_ENABLED}. Only if true message is shown.
	 * @param msg		{@link String} message to show.
	 * @param err		If {@code true} message is shown using error pipe. 
	 * @param newline	If {@code true} a lien break is added to end of message
	 */
	public static synchronized void log(String msg, boolean newline, boolean err){
		if( LOG_ENABLED ){
			if( err ){
				System.err.print( msg + (newline ? "\n" : "") );
			} else {
				System.out.print( msg + (newline ? "\n" : "") );
			}
		}
	}

	/**
	 * MAIN FUNCTION
	 * @param args	{@link String} array of program arguments
	 */
	public static void main(String[] args) {
		
		SyncManager manager = new SyncManager();
		
		// check program arguments
		for( String arg : args ){
			
			if( arg.equals("-q") || arg.equals("-quiet") ){
				SimpleMirror.LOG_ENABLED = false;
			}
			else if( arg.equals("-s") || arg.equals("-service") ){
				SimpleMirror.START_SERVICE = true;
			}
			else if( arg.equals("-h") || arg.equals("-help") ){
				System.err.println( "SimpleMirror Syntax: java -jar simplemirror.jar <config-file> [-q | -quiet]" );
				System.err.println( "-quiet\tno console output" );
				System.err.println( "-q\tsee -quiet" );
				System.err.println( "-service\truns as service after first sync" );
				System.err.println( "-s\tsee -service" );
				System.err.println( "-help\tshow this help" );
				System.err.println( "-h\tsee -help" );
			}
			else{
				SimpleMirror.CONFIG_FILE = arg;
			}
			
		}
		
		// first sync files
		try {
			manager.setMirrors( SimpleMirror.readMirrors() );
			if( manager.sync() ){
				SimpleMirror.log("Synchronized");
			} else {
				SimpleMirror.err("Error while synchronizing");
			}
		} catch (IOException e) {
			SimpleMirror.err("Can not read mirrors from " + SimpleMirror.CONFIG_FILE);
		}
		
	}

}
