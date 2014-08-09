package de.hanneseilers.jSimpleMirror;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class SyncManager {
	
	private Map<String, String> mMirrors = null;
	private Map<String, Collection<File>> mCache = new HashMap<String, Collection<File>>();

	/**
	 * Constructor
	 */
	public SyncManager() {}
	
	/**
	 * Function to synchronize data between directories.
	 * @return	{@code true} if synchonization was successfull, {@code false} otherwise.
	 */
	public boolean sync(){
		
		try{			
			if( mMirrors != null ){
				
				// Sync mirrors
				for( String src : mMirrors.keySet() ){
					File vSource = new File(src);
					File vDestination = new File(mMirrors.get(src));
					
					// check if source and destination are directories
					if( !vSource.isDirectory() || !vDestination.isDirectory() ){
						throw new IOException( "No directories: " + vSource.getAbsolutePath() + " > " + vDestination.getAbsolutePath() );
					}					
					SimpleMirror.log( "Checking " + vSource.getAbsolutePath() + " > " + vDestination.getAbsolutePath() );
					
					// sync files
					Collection<File> vSourceFiles = getFiles(vSource);
					Collection<File> vSourceFilesChanges = getChangesFromCache(src, vSourceFiles);
					Collection<File> vDestinationFiles = getFiles(vDestination);
					syncData(vSource, vDestination, vSourceFilesChanges, vSourceFiles, vDestinationFiles);
					
					// update file cache
					updateCache(src, vSourceFiles);
					
					// sync directories
					Collection<File> vSourceDirectories = getDirectories(vSource);
					Collection<File> vDestinationDirectories = getDirectories(vDestination);					
					syncData(vSource, vDestination, vSourceDirectories, vSourceDirectories, vDestinationDirectories);					
					
				}
				
				return true;
				
			}			
		} catch ( Exception e ) {
			SimpleMirror.err(e.getMessage());
		}
			
		return false;
	}
	
	/**
	 * Checks if a {@link Collection} of {@link File} objects differs from cache.
	 * @param aSource	{@link String} of source root path
	 * @param aFiles	{@link Collection} of current {@link File} objects. 
	 * @return			{@link Collection} of {@link File} objects that changed.
	 */
	private Collection<File> getChangesFromCache(String aSource, Collection<File> aFiles){
		Collection<File> vChanges = new ArrayList<File>();
		
		if( mCache.containsKey(aSource) ){
			for( File fNew : aFiles ){	
				
				boolean change = true;				
				for( File fCache : mCache.get(aSource) ){					
					if( fCache.equals(fNew) && fCache.lastModified() == fNew.lastModified() ){
						change = false;
						break;
					}
				}
				
				if( change ){
					vChanges.add(fNew);
				}
				
			}
		} else {
			return aFiles;
		}
		
		return vChanges;
	}
	
	/**
	 * Updates cache with new {@link Collection} of {@link File} objects.
	 * @param aSource	{@link String} of source root path.
	 * @param aFiles	{@link Collection} of {@link File} objects to update.
	 */
	private void updateCache(String aSource, Collection<File> aFiles){
		mCache.put(aSource, aFiles);
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
	 * @param aSourceChanges		{@link Collection} of changed source {@link File} objects.
	 * @param aSource				{@link Collection} of source {@link File} objects.
	 * @param aDestination			{@link Collection} of destination {@link File} objects.
	 */
	private void syncData(File aSourcePath, File aDestinationPath, Collection<File> aSourceChanges, Collection<File> aSource, Collection<File> aDestination){
		
		Collection<String> vSourceEntries = getDataEntryNames(aSourceChanges, aSourcePath.getAbsolutePath());
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
						SimpleMirror.log( "Adding directory " + vDestination + " ... ", false, false );
						vDestination.mkdirs();
						SimpleMirror.log( "ok" );
						
					} else {
						SimpleMirror.log( "Adding file " + vDestination + " ... ", false, false );
						FileUtils.copyFile(vSource, vDestination);
						SimpleMirror.log( "ok" );
						
					}
					
				} catch( IOException e ){
					SimpleMirror.log( "failed" );
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
						SimpleMirror.log( "Deleting directory " + vDestination + " ... ", false, false );
						FileUtils.deleteDirectory(vDestination);
						SimpleMirror.log( "ok" );
					} else {
						SimpleMirror.log( "Deleting file " + vDestination + " ... ", false, false );
						FileUtils.forceDelete(vDestination);
						SimpleMirror.log( "ok" );
					}
					
				} catch( IOException e ){
					SimpleMirror.log( "failed" );
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
	 * Sets mirrors from config file
	 * @param mMirrors
	 */
	public void setMirrors(Map<String, String> aMirrors) {
		mMirrors = aMirrors;
	}
	
}
