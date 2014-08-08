package de.hanneseilers.jSimpleMirror;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class SimpleMirror {
	
	public static boolean LOG_ENABLED = true;
	private static final String CONFIG_MIRROR_SEPERATOR = ";";
	private static final String CONFIG_COMMENT = "#";
	
	private File mConfigFile = null;
	private Map<String, String> mMirrors = new HashMap<String, String>();
	
	/**
	 * Constructor
	 * @param aConfigFile	Configuration {@link File}
	 */
	public SimpleMirror(File aConfigFile) {
		if( aConfigFile.exists() && aConfigFile.canRead() ) {
			mConfigFile = aConfigFile;
			log( "Found config file " + mConfigFile.getAbsolutePath() );
		} else {
			log( "Can not find config file " + aConfigFile.getAbsolutePath(), true, true );
		}
		
		mMirrors.clear();
	}
	
	/**
	 * Function to synchronize data between directories.
	 * @return	{@code true} if synchonization was successfull, {@code false} otherwise.
	 */
	public boolean sync(){
		
		try{
			
			if( mConfigFile != null ){
				
				// Read mirrors from config file
				readMirrors();
				
				// Sync mirrors
				for( String src : mMirrors.keySet() ){
					File vSource = new File(src);
					File vDestination = new File(mMirrors.get(src));
					
					// check if source and destination are directories
					if( !vSource.isDirectory() || !vDestination.isDirectory() ){
						throw new IOException( "No directories: " + vSource.getAbsolutePath() + " > " + vDestination.getAbsolutePath() );
					}					
					log( "Checking " + vSource.getAbsolutePath() + " > " + vDestination.getAbsolutePath() );
					
					// sync files
					Collection<File> vSourceFiles = getFiles(vSource);
					Collection<File> vDestinationFiles = getFiles(vDestination);
					syncData(vSource, vDestination, vSourceFiles, vDestinationFiles);
					
					// sync directories
					Collection<File> vSourceDirectories = getDirectories(vSource);
					Collection<File> vDestinationDirectories = getDirectories(vDestination);					
					syncData(vSource, vDestination, vSourceDirectories, vDestinationDirectories);					
					
				}
				
				return true;
				
			}
			
		} catch ( Exception e ) {
			log(e.getMessage(), true, true);
		}
			
		return false;
	}
	
	/**
	 * Extracts entry names from {@link Collection} of {@link File} objects.
	 * @param aCollection	{@link Collection} of {@link File} objects.
	 * @param substract		{@link String} to remove from start of data entry name.
	 * @return				{@link Collection} of data entry name {@link String}.
	 */
	private Collection<String> getDataEntryNames(Collection<File> aCollection, String substract){
		Collection<String> vDataEntries = new ArrayList<String>();
		
		for( File f : aCollection ){
			String vDataEntry = f.getAbsolutePath().replace(substract, "");
			vDataEntries.add(vDataEntry);
		}
		
		return vDataEntries;
	}
	
	/**
	 * Synchronized data between two {@link Collection} of {@link File} objects.
	 * @param aSourcePath			{@link File} of source root path.
	 * @param aDestinationPath		{@link File} of destination root path.
	 * @param aSource				{@link Collection} of source {@link File} objects.
	 * @param aDestination			{@link Collection} of destination {@link File} objects.
	 */
	private void syncData(File aSourcePath, File aDestinationPath, Collection<File> aSource, Collection<File> aDestination){
		
		Collection<String> vSourceEntries = getDataEntryNames(aSource, aSourcePath.getAbsolutePath());
		Collection<String> vDestinationEntries = getDataEntryNames(aDestination, aDestinationPath.getAbsolutePath());
		
		// add new data
		for( String src : vSourceEntries ){
			
			// check if to copy src to destination
			File vSource = new File( aSourcePath + src );
			File vDestination = new File( aDestinationPath + src );
			
			if( !vSource.equals(aSourcePath)
					&& (!vDestination.exists() || vSource.lastModified() > vDestination.lastModified()) ){
				
				try{
					
					// create data if neccessary
					if( vSource.isDirectory() ){
						log( "Adding directory " + vDestination + " ... ", false, false );
						vDestination.mkdirs();
						log( "ok" );
						
					} else {
						log( "Adding file " + vDestination + " ... ", false, false );
						FileUtils.copyFile(vSource, vDestination);
						log( "ok" );
						
					}
					
				} catch( IOException e ){
					log( "failed" );
				}
			}
		}
		
		// remove old data
		for( String dst : vDestinationEntries ){
		
			// check if to delete dst 
			File vSource = new File( aSourcePath + dst );
			File vDestination = new File( aDestinationPath + dst );
			
			if( !vDestination.equals(aDestinationPath)
					&& !vSource.exists() ){
				
				try{
					
					// delete data if necessary
					if( vDestination.isDirectory() ){
						log( "Deleting directory " + vDestination + " ... ", false, false );
						FileUtils.deleteDirectory(vDestination);
						log( "ok" );
					} else {
						log( "Deleting file " + vDestination + " ... ", false, false );
						FileUtils.forceDelete(vDestination);
						log( "ok" );
					}
					
				} catch( IOException e ){
					log( "failed" );
				}
			}
			
		}
	}
	
	/**
	 * Gets files with subdirectories from a source path.
	 * @param aSource	{@link File} source path
	 * @return			{@link Collection} of {@link File} objects within source path.
	 */
	private Collection<File> getFiles(File aSource){
		return FileUtils.listFiles(aSource, null, true);
	}
	
	/**
	 * Gets directories from source path. 
	 * @param aSource	{@link File} source path
	 * @return			{@link Collection} of {@link File} directory objects wothin source path.
	 */
	private Collection<File> getDirectories(File aSource){
		Collection<File> vDirectories = new ArrayList<File>();
		IOFileFilter vFilter = new IOFileFilter() {			
			@Override
			public boolean accept(File arg0, String arg1) {
				return true;
			}			
			@Override
			public boolean accept(File arg0) {
				return true;
			}
		};
		
		for( File dir : FileUtils.listFilesAndDirs(aSource, vFilter, vFilter) ){
			if( dir.isDirectory() ){
				vDirectories.add(dir);
			}
		}
		return vDirectories;
	}
	
	
	/**
	 * Reads mirrors configuration from config file
	 * @throws IOException 
	 */
	private void readMirrors() throws IOException{
		BufferedReader reader = new BufferedReader(
				new InputStreamReader( new DataInputStream( new FileInputStream(mConfigFile) ) ) );
		
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
					
					mMirrors.put(src, dst);
				}
			}
			
		}
		
		reader.close();
	}
	
	public static void log(String msg){
		log(msg, true, false);
	}
	
	/**
	 * Logging function.
	 * Checks for {@code LOG_ENABLED}. Only if true message is shown.
	 * @param msg		{@link String} message to show.
	 * @param err		If {@code true} message is shown using error pipe. 
	 * @param newline	If {@code true} a lien break is added to end of message
	 */
	public static void log(String msg, boolean newline, boolean err){
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
		if( args.length > 0 ){			
			
			// check if to show log
			if( args.length > 1 ){
				if( args[1].equals("-q") || args[1].equals("-quiet") ){
					LOG_ENABLED = false;
				}
			}
			
			SimpleMirror mirror = new SimpleMirror( new File(args[0]) );	
			if( mirror.sync() ){
				log( "Synchonisation successfull." );
				System.exit(0);
			} else {
				log( "Synchonisation failed.", true, true );
			}			
			
		} else {
			System.err.println( "SimpleMirror Syntax: java -jar simplemirror.jar <config-file> [-q | -quiet]" );
			System.err.println( "-quiet\tno console output" );
			System.err.println( "-q\tsee quiet" );
		}
		
		System.exit(-1);
	}

}
