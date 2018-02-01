package com.shuishou.salemgr.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.shuishou.salemgr.CommonTools;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.PayWay;
import com.shuishou.salemgr.ui.components.NumberTextField;

public class PaywayPanel extends JPanel{
	private JLabel lbShouldPay = new JLabel();
	private JLabel lbChange = new JLabel();
	private NumberTextField tfGetPay = new NumberTextField((JDialog)null, true, false);
	private JRadioButton rbName;
	private PayWay payway;
	private double orderPrice;
	public PaywayPanel(PayWay payway, double orderPrice){
		this.payway = payway;
		this.orderPrice = orderPrice;
		rbName = new JRadioButton(payway.getName());
		tfGetPay.setPreferredSize(new Dimension(120, 35));
		tfGetPay.setMaximumSize(new Dimension(120,35));
		tfGetPay.setMinimumSize(new Dimension(120, 35));
		
		lbShouldPay.setPreferredSize(new Dimension(150, 35));
		lbShouldPay.setMaximumSize(new Dimension(150,35));
		lbShouldPay.setMinimumSize(new Dimension(150, 35));
		
		lbChange.setPreferredSize(new Dimension(120, 35));
		lbChange.setMaximumSize(new Dimension(120,35));
		lbChange.setMinimumSize(new Dimension(120, 35));
		
		lbShouldPay.setText("Needs " + CommonTools.transferDouble2Scale(orderPrice * payway.getRate()));
		tfGetPay.setText(CommonTools.transferDouble2Scale(orderPrice * payway.getRate()));
		tfGetPay.selectAll();
		rbName.setSelected(false);
		lbShouldPay.setVisible(false);
		lbChange.setVisible(false);
		tfGetPay.setVisible(false);
		this.setLayout(new GridBagLayout());
		add(rbName, 		new GridBagConstraints(0, 0, 1, 1, 0.1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(lbShouldPay, 	new GridBagConstraints(1, 0, 1, 1, 0.4, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		add(tfGetPay, 		new GridBagConstraints(2, 0, 1, 1, 0.1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		add(lbChange, 		new GridBagConstraints(3, 0, 1, 1, 0.4, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		
		rbName.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				rbName.setSelected(rbName.isSelected());
				lbShouldPay.setVisible(rbName.isSelected());
				lbChange.setVisible(rbName.isSelected());
				tfGetPay.setVisible(rbName.isSelected());
				if (rbName.isSelected()){
					setBorder(BorderFactory.createLineBorder(Color.lightGray));
				} else {
					setBorder(null);
				}
					
			}});
		
		tfGetPay.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				showChangeText();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				showChangeText();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				showChangeText();
			}
		});
	}
	
	private void showChangeText(){
		if (tfGetPay.getText() == null || tfGetPay.getText().length() == 0)
			return;
		double value = Double.parseDouble(tfGetPay.getText());
		if (value < orderPrice)
			return;
		lbChange.setText(Messages.getString("CheckoutDialog.Charge") + CommonTools.transferDouble2Scale(value - orderPrice));
	}
	
	public void setSelected(boolean b){
		rbName.setSelected(b);
		lbShouldPay.setVisible(b);
		lbChange.setVisible(b);
		tfGetPay.setVisible(b);
		if (b){
			this.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		} else {
			this.setBorder(null);
		}
	}
	
	public void setGetPayValue(String s){
		tfGetPay.setText(s);
		tfGetPay.requestFocusInWindow();
		tfGetPay.selectAll();
	}
	
	public JRadioButton getRadioButton(){
		return rbName;
	}
	
	public PayWay getPayway() {
		return payway;
	}

	public double getMoneyAmount(){
		if (tfGetPay.getText() == null || tfGetPay.getText().length() == 0)
			return 0;
		else return Double.parseDouble(tfGetPay.getText());
	}
	
	public double getChangeAmount(){
		if (tfGetPay.getText() == null || tfGetPay.getText().length() == 0)
			return 0;
		double value = Double.parseDouble(tfGetPay.getText());
		return value / payway.getRate() - orderPrice;
	}
}