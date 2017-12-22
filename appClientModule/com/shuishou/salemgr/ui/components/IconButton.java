package com.shuishou.salemgr.ui.components;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

public class IconButton extends JButton {
	public final static Logger logger = Logger.getLogger(IconButton.class.getName());
	private int gapIconText = 10; // the gap between icon and text in button for horizontal
	
	public IconButton(String txt, String iconfile){
		super(txt);
		try {
			if (iconfile != null){
				Image image = ImageIO.read(getClass().getResource(iconfile));
				setIcon(new ImageIcon(image));
				setHorizontalAlignment(SwingConstants.LEFT);
				setIconTextGap(gapIconText);
			}
			
		} catch (IOException e) {
			logger.error("", e);
		}
	}
}

