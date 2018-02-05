package com.shuishou.salemgr.ui.components;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import com.shuishou.salemgr.CommonTools;

/**
 * This dialog can response ESCAPE to close, so normally every dialog should extends from this
 * @author Administrator
 *
 */
public abstract class CommonDialog extends JDialog{
	public CommonDialog() {
        super((Frame)null, false);
        CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Frame owner) {
        super(owner, false);
        CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Frame owner, boolean modal) {
        super(owner, modal);
        CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Frame owner, String title) {
        super(owner, title);
        CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Frame owner, String title, boolean modal,
                   GraphicsConfiguration gc) {
        super(owner,title, modal, gc);
        CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Dialog owner) {
    	super(owner, false);
    	CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Dialog owner, boolean modal) {
    	super(owner, modal);
    	CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Dialog owner, String title) {
    	super(owner, title, false);
    	CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Dialog owner, String title, boolean modal,
                   GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Window owner) {
    	super(owner);
    	CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Window owner, ModalityType modalityType) {
    	super(owner, modalityType);
    	CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Window owner, String title) {
    	super(owner, title);
    	CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Window owner, String title, Dialog.ModalityType modalityType) {
    	super(owner, title, modalityType);
    	CommonTools.addEscapeListener(this);
    }

    public CommonDialog(Window owner, String title, Dialog.ModalityType modalityType,
                   GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        CommonTools.addEscapeListener(this);
    }
    
    public abstract void doEnterClick();
}
