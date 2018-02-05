package com.shuishou.salemgr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import com.shuishou.salemgr.ui.components.CommonDialog;

public class CommonTools {

	public static String transferDouble2Scale(double d){
		return String.format(ConstantValue.FORMAT_DOUBLE, d);
	}
	
	/**
	 * if d is positive, return d with 2 decimal
	 * if d is negative, return -d with 2 decimal, surrounding the bracket()
	 * @param d
	 * @return
	 */
	public static String transferNumberByPM(double d, String currencyIcon){
		if (d >= 0){
			if (currencyIcon != null)
				return transferDouble2Scale(d);
			else 
				return currencyIcon + transferDouble2Scale(d);
		} else {
			if (currencyIcon != null)
				return "(" + currencyIcon + transferDouble2Scale(d * (-1)) + ")";
			else 
				return "(" + transferDouble2Scale(d * (-1)) + ")";
		}
	}
	
	public static void addEscapeListener(final CommonDialog dialog){
		ActionListener escListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}};
			
		dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		ActionListener enterListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.doEnterClick();
			}};
		dialog.getRootPane().registerKeyboardAction(enterListener, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
}
