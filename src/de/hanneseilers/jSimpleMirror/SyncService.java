package de.hanneseilers.jSimpleMirror;

import java.io.IOException;

/**
 * Class to run as backgroudn service thread.
 * @author Hannes Eilers
 *
 */
public class SyncService implements Runnable {
	
	private boolean mStop = false;
	private SyncManager mManager = null;
	
	/**
	 * Constructor
	 * @param aManager	{@link SyncManager}
	 */
	public SyncService(SyncManager aManager) {
		mManager = aManager;
	}

	@Override
	public void run() {
		
		SimpleMirror.log("Sync service started");
		
		try {
			
			while( !mStop && mManager != null ){
				
					// update mirrors and synchronize
					mManager.setMirrors( SimpleMirror.readMirrors() );
					if( mManager.sync() ){
						SimpleMirror.log("Synchronized");
					} else {
						SimpleMirror.err("Error while synchronizing");
					}
					
					// wait until next sync
					while( SimpleMirror.SYNC_PAUSE );
					try {						
						Thread.sleep( SimpleMirror.SYNC_RATE );
					} catch (InterruptedException e) {}
					
			}
			
		} catch (IOException e) {
			SimpleMirror.err("Can not read mirrors from " + SimpleMirror.CONFIG_FILE);
		}
		
		SimpleMirror.sSyncService = null;
		SimpleMirror.log("Sync service stopped");		
		
	}

	/**
	 * @param mStop Set {@code true} to stop sync service.
	 */
	public void setStop(boolean aStop) {
		mStop = aStop;
	}

}
