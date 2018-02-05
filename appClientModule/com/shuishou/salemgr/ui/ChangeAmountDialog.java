package com.shuishou.salemgr.ui;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.ui.components.CommonDialog;
import com.shuishou.salemgr.ui.components.IconButton;
import com.shuishou.salemgr.ui.components.NumberTextField;

public class ChangeAmountDialog extends CommonDialog{
	public int inputInteger;
	public boolean isConfirm = false;
	private NumberTextField txt;
	public ChangeAmountDialog(Frame parent, String title, int oldAmount){
		super(parent, title,true);
		JButton btnConfirm = new JButton(Messages.getString("ConfirmDialog"));
		JButton btnClose = new JButton(Messages.getString("CloseDialog"));
		IconButton btnPlus = new IconButton("","/resource/plus.png");
		IconButton btnMinus = new IconButton("", "/resource/minus.png");
		JLabel lbOldAmount = new JLabel(Messages.getString("ChangeAmountDialog.OldAmount"));
		JLabel lbNewAmount = new JLabel(Messages.getString("ChangeAmountDialog.NewAmount"));
		JTextField tfOldAmount = new JTextField();
		btnConfirm.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnPlus.setPreferredSize(new Dimension(80, 80));
		btnMinus.setPreferredSize(new Dimension(80,80));
		
		txt = new NumberTextField(this, false, false);
		tfOldAmount.setText(oldAmount + "");
		txt.setText(oldAmount + "");
		tfOldAmount.setEditable(false);
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbOldAmount, 			new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		c.add(tfOldAmount, 			new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		c.add(btnPlus, 				new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 30, 0, 0), 0, 0));
		c.add(lbNewAmount, 			new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		c.add(txt,		   			new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		c.add(btnMinus, 			new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 30, 0, 0), 0, 0));
		c.add(btnConfirm, 			new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		c.add(btnClose, 			new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		btnConfirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doConfirm();
			}
		});
		btnPlus.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int i = Integer.parseInt(txt.getText());
				txt.setText(String.valueOf(++i));
			}});
		btnMinus.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int i = Integer.parseInt(txt.getText());
				txt.setText(String.valueOf(--i));
			}});
		this.setSize(new Dimension(500, 320));
		this.setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
				(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
	}
	
	private void doConfirm(){
		if (txt.getText() == null || txt.getText().length() == 0)
			return;
		isConfirm = true;
		inputInteger = Integer.parseInt(txt.getText());
		setVisible(false);
	}
	
	public void doEnterClick(){
		doConfirm();
	}
}