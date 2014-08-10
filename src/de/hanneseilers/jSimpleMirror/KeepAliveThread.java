package de.hanneseilers.jSimpleMirror;

/**
 * Thread to keep service alive in background.
 * Refreshes every 200 ms and removes tray icon on close.
 * @author Hannes Eilers
 *
 */
public class KeepAliveThread implements Runnable {

	@Override
	public void run() {
		
		while( !SimpleMirror.SYNC_STOP ){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// remove tray icon
		SimpleMirror.sSyncService.setStop(true);
		TrayManager.removeTrayIcon();
	}

}
