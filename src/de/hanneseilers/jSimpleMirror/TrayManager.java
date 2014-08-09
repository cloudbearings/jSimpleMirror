package de.hanneseilers.jSimpleMirror;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

public class TrayManager {
	
	private static TrayIcon mTrayIcon = null;

	/**
	 * Creates a system tray icon.
	 * @param aTrayIcon		{@link Image} of tray icon
	 * @param aTooltip		{@link String} of tray icon tooltip
	 */
	public static void createTrayIcon(Image aTrayIcon, String aTooltip) {
		
		if( !SystemTray.isSupported() ){
			SimpleMirror.err("No system tray supported");
			return;
		}
		
		try {
			
			mTrayIcon = new TrayIcon(aTrayIcon, aTooltip);
			mTrayIcon.setImageAutoSize(true);
			SystemTray.getSystemTray().add(mTrayIcon);
			
		} catch (AWTException e) {
			SimpleMirror.err("Error while creating system tray icon.");
		}
		
	}
	
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
