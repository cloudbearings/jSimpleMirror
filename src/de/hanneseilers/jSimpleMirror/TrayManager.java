package de.hanneseilers.jSimpleMirror;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class TrayManager {
	
	private static TrayIcon mTrayIcon = null;
	private static PopupMenu vPopupMenu;
	private static MenuItem itmClose;
	private static CheckboxMenuItem chkSyncPause;
	private static CheckboxMenuItem chkSyncDelete;
	private static CheckboxMenuItem chkSyncFiles;
	private static CheckboxMenuItem chkSyncDirectories;
    private static Menu mnuSyncDelay;
    private static CheckboxMenuItem chkSyncRate1s;
    private static CheckboxMenuItem chkSyncRate5s;
    private static CheckboxMenuItem chkSyncRate10s;
    private static CheckboxMenuItem chkSyncRate30s;
    private static CheckboxMenuItem chkSyncRate1m;
    private static CheckboxMenuItem chkSyncRate5m;
    private static CheckboxMenuItem chkSyncRate10m;
    private static CheckboxMenuItem chkSyncRate30m;
    private static CheckboxMenuItem chkSyncRate1h;

	/**
	 * Creates a system tray icon.
	 * @param aTrayIcon		{@link Image} of tray icon
	 * @param aTooltip		{@link String} of tray icon tooltip
	 */
	public static void createTrayIcon(Image aTrayIcon, String aTooltip) {
		
		if( !SystemTray.isSupported() || mTrayIcon != null ){
			SimpleMirror.err("No system tray supported");
			return;
		}
		
		try {
			
			// create tray icon
			mTrayIcon = new TrayIcon(aTrayIcon, aTooltip);
			mTrayIcon.setImageAutoSize(true);			
			
			// create popup menu
			vPopupMenu = new PopupMenu();
			itmClose = new MenuItem("Close");
			chkSyncPause = new CheckboxMenuItem("Pause");
			chkSyncDelete = new CheckboxMenuItem("Delete files/dirs on slave");
			chkSyncFiles = new CheckboxMenuItem("Sync files");
			chkSyncDirectories = new CheckboxMenuItem("Sync directories");
	        mnuSyncDelay = new Menu("Sync Rate");
	        chkSyncRate1s = new CheckboxMenuItem("every 1s");
	        chkSyncRate5s = new CheckboxMenuItem("every 5s");
	        chkSyncRate10s = new CheckboxMenuItem("every 10s");
	        chkSyncRate30s = new CheckboxMenuItem("every 30s");
	        chkSyncRate1m = new CheckboxMenuItem("every 1m");
	        chkSyncRate5m = new CheckboxMenuItem("every 5m");
	        chkSyncRate10m = new CheckboxMenuItem("every 10m");
	        chkSyncRate30m = new CheckboxMenuItem("every 30m");
	        chkSyncRate1h = new CheckboxMenuItem("every 1h");
	        
	        mnuSyncDelay.add(chkSyncRate1h);
	        mnuSyncDelay.add(chkSyncRate30m);
	        mnuSyncDelay.add(chkSyncRate10m);
	        mnuSyncDelay.add(chkSyncRate5m);
	        mnuSyncDelay.add(chkSyncRate1m);
	        mnuSyncDelay.add(chkSyncRate30s);
	        mnuSyncDelay.add(chkSyncRate10s);
	        mnuSyncDelay.add(chkSyncRate5s);
	        mnuSyncDelay.add(chkSyncRate1s);
	        
	        vPopupMenu.add(chkSyncFiles);
	        vPopupMenu.add(chkSyncDirectories);
	        vPopupMenu.add(chkSyncDelete);
	        vPopupMenu.add(mnuSyncDelay);
	        vPopupMenu.addSeparator();
	        vPopupMenu.add(chkSyncPause);
	        vPopupMenu.add(itmClose);
	        
	        mTrayIcon.setPopupMenu(vPopupMenu);
	        
	        // adding functions etc. to popup menu
	        updateSyncRate();
	        
	        chkSyncRate1s.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_RATE = 1000;
					updateSyncRate();
				}
			});
	        chkSyncRate5s.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_RATE = 5000;
					updateSyncRate();
				}
			});
	        chkSyncRate10s.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_RATE = 10000;
					updateSyncRate();
				}
			});
	        chkSyncRate30s.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_RATE = 30000;
					updateSyncRate();
				}
			});
	        chkSyncRate1m.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_RATE = 60000;
					updateSyncRate();
				}
			});
	        chkSyncRate5m.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_RATE = 300000;
					updateSyncRate();
				}
			});
	        chkSyncRate10m.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_RATE = 600000;
					updateSyncRate();
				}
			});
	        chkSyncRate30m.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_RATE = 1800000;
					updateSyncRate();
				}
			});
	        chkSyncRate1h.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_RATE = 3600000;
					updateSyncRate();
				}
			});
	        
	        chkSyncPause.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_PAUSE = chkSyncPause.getState();
				}
			});	        
	        chkSyncFiles.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_FILES = chkSyncFiles.getState();
				}
			});
	        chkSyncDirectories.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_DIRECTORIES = chkSyncDirectories.getState();
				}
			});
	        chkSyncDelete.addItemListener(new ItemListener() {				
				@Override
				public void itemStateChanged(ItemEvent e) {
					SimpleMirror.SYNC_DELETE_ON_SLAVE = chkSyncDelete.getState();
				}
			});
	        itmClose.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					SimpleMirror.SYNC_STOP = true;
				}
			});
	        
			
			// add tray icon to system tray
			SystemTray.getSystemTray().add(mTrayIcon);
			
			
		} catch (AWTException e) {
			SimpleMirror.err("Error while creating system tray icon.");
		}
		
	}
	
	/**
	 * Updates tray icon popup menu sync rate check boxes
	 */
	private static void updateSyncRate(){
		chkSyncRate1s.setState( SimpleMirror.SYNC_RATE == 1000 );
        chkSyncRate5s.setState( SimpleMirror.SYNC_RATE == 5000 );
        chkSyncRate10s.setState( SimpleMirror.SYNC_RATE == 10000 );
        chkSyncRate30s.setState( SimpleMirror.SYNC_RATE == 30000 );
        chkSyncRate1m.setState( SimpleMirror.SYNC_RATE == 60000 );
        chkSyncRate5m.setState( SimpleMirror.SYNC_RATE == 300000 );
        chkSyncRate10m.setState( SimpleMirror.SYNC_RATE == 600000 );
        chkSyncRate30m.setState( SimpleMirror.SYNC_RATE == 1800000 );
        chkSyncRate1h.setState( SimpleMirror.SYNC_RATE == 3600000 );
        chkSyncFiles.setState( SimpleMirror.SYNC_FILES );
        chkSyncDirectories.setState( SimpleMirror.SYNC_DIRECTORIES );
        chkSyncDelete.setState( SimpleMirror.SYNC_DELETE_ON_SLAVE );
        chkSyncPause.setState( SimpleMirror.SYNC_PAUSE );
	}
	
	/**
	 * Removes icon from tray
	 */
	public static void removeTrayIcon(){
		if( SystemTray.isSupported() && mTrayIcon != null ){
			SystemTray.getSystemTray().remove(mTrayIcon);
			mTrayIcon = null;
		}
	}
	
	/**
	 * Shows a notification on system tray.
	 * @param aCaption		{@link String} of caption / title of message.
	 * @param aText			{@link String} of text message.
	 * @param aMessageType	{@link MessageType} of notification.
	 * @return				{@code true} if tray icon is available, {@code false} othwerise.
	 */
	public static boolean showNotification(String aCaption, String aText, MessageType aMessageType){
		if( mTrayIcon != null ){
			mTrayIcon.displayMessage(aCaption, aText, aMessageType);
			return true;
		}
		
		return false;
	}
	
}
