package com.shuishou.salemgr.ui.components;

import java.awt.AWTEvent;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFormattedTextField;

public class NumberTextField extends JFormattedTextField{
	private boolean showNumPad;
	private NumberKeyboard keyboard;
	public NumberTextField(Dialog parent, final boolean allowDouble, boolean showNumPad){
		this(parent, allowDouble, NumberKeyboard.SHOWPOSITION_BOTTOM, showNumPad);
	}
			
	public NumberTextField(Dialog parent, final boolean allowDouble, int numPadPosition, boolean showNumPad){
		this.showNumPad = showNumPad;
		if (showNumPad){
			keyboard = new NumberKeyboard(parent, this, numPadPosition);
			if (!allowDouble)
				keyboard.getButtonDot().setEnabled(false);
		}
		initUI(allowDouble, numPadPosition);
	}
	
	public NumberTextField(Frame parent, final boolean allowDouble, boolean showNumPad){
		this(parent, allowDouble, NumberKeyboard.SHOWPOSITION_BOTTOM, showNumPad);
	}
			
	public NumberTextField(Frame parent, final boolean allowDouble, int numPadPosition, boolean showNumPad){
		this.showNumPad = showNumPad;
		if (showNumPad){
			keyboard = new NumberKeyboard(parent, this, numPadPosition);
			if (!allowDouble)
				keyboard.getButtonDot().setEnabled(false);
		}
		initUI(allowDouble, numPadPosition);
	}
	
	private void initUI(final boolean allowDouble, int numPadPosition){
		addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (allowDouble){
					if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) 
							|| (c == KeyEvent.VK_DELETE) || (c == '.'))) {
						getToolkit().beep();
						e.consume();
					}
					if (c == '.') {
						if (getText() != null && getText().indexOf(".") >= 0) {
							getToolkit().beep();
							e.consume();
						}
					}
				} else {
					if (!((c >= '0') && (c <= '9'))) {
						getToolkit().beep();
						e.consume();
					}
				}
			}
		});
		
		if (showNumPad){
			this.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					if (!keyboard.isVisible())
						keyboard.setVisible(true);
				}
			});
			
			addHierarchyBoundsListener(new HierarchyBoundsAdapter(){
				public void ancestorMoved(HierarchyEvent e) {
					keyboard.setVisible(false);
				}

			    public void ancestorResized(HierarchyEvent e) {
			    	keyboard.setVisible(false);
			    }
			});
			
			long eventMask = MouseEvent.MOUSE_PRESSED;
	        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener(){

				@Override
				public void eventDispatched(AWTEvent event) {
					if (MouseEvent.MOUSE_CLICKED == event.getID()) {
						if (event.getSource() != NumberTextField.this){
							if (!keyboard.isEventInThis(event)){
								keyboard.setVisible(false);
							}
						}
						
		            }				
				}}, eventMask);
		}
		
	}
}
