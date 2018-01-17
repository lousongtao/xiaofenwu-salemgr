package com.shuishou.salemgr.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.shuishou.salemgr.beans.Goods;
import com.shuishou.salemgr.ui.components.NumberTextField;

public class ChangeGoodsPriceDialog extends JDialog implements ActionListener{
	
	private MainFrame mainFrame;
	private Goods goods;
	private NumberTextField tfModifiedPrice = new NumberTextField(this, true, false);
	private JButton btnConfirm = new JButton("Confirm");
	private JButton btnClose = new JButton("Close");
	private boolean isCancel = false;
	
	public ChangeGoodsPriceDialog(MainFrame mainFrame, Goods goods){
		this.mainFrame = mainFrame;
		this.goods = goods;
		this.setTitle("Change Price");
		this.setModal(true);
		initUI();
	}
	
	private void initUI(){
		JPanel pButton = new JPanel();
		pButton.add(btnConfirm);
		pButton.add(btnClose);
		btnConfirm.addActionListener(this);
		btnClose.addActionListener(this);
		
		JLabel lbName = new JLabel("Name : "+ goods.getName());
		JLabel lbBarcode = new JLabel("Barcode : " + goods.getBarcode());
		JLabel lbSellPrice = new JLabel("Sell Price : " + goods.getSellPrice());
		JLabel lbMemberPrice = new JLabel("Member Price : " + goods.getMemberPrice());
		JLabel lbModifiedPrice = new JLabel("Modified Price : ");
		Container c = this.getContentPane();
		c.setLayout(new GridLayout(0, 1));
		c.add(lbName);
		c.add(lbBarcode);
		c.add(lbSellPrice);
		c.add(lbMemberPrice);
		c.add(lbModifiedPrice);
		c.add(tfModifiedPrice);
		c.add(pButton);
		this.setSize(new Dimension(350, 350));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}

	public boolean isCancel() {
		return isCancel;
	}
	
	public double getModifiedPrice(){
		return Double.parseDouble(tfModifiedPrice.getText());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConfirm){
			if (tfModifiedPrice.getText() == null || tfModifiedPrice.getText().length() == 0){
				JOptionPane.showMessageDialog(this, "Must input the modified price.");
				return;
			}
			if (getModifiedPrice() < 0){
				JOptionPane.showMessageDialog(this, "Don't allow to input a negative price.");
				return;
			}
			setVisible(false);
		} else if (e.getSource() == btnClose){
			isCancel = true;
			setVisible(false);
		}
	}

}
