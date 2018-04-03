package com.shuishou.salemgr.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
	private double change;//找零
	private JDialog parent;
	private PaywayPanel nextSiblingPaywayPanel;
	private PaywayPanel preSiblingPaywayPanel;
	public PaywayPanel(final JDialog parent, PayWay payway, double orderPrice){
		this.payway = payway;
		this.orderPrice = orderPrice;
		this.parent = parent;
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
		
		setShouldPay();
		setGetPay();
		
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
		
		tfGetPay.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.getID() != KeyEvent.KEY_PRESSED)
					return;
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					if (parent instanceof CheckoutDialog)
						((CheckoutDialog)parent).doPay();
					else if (parent instanceof PreOrderCheckoutDialog)
						((PreOrderCheckoutDialog)parent).doMakePreOrder(true);
				} else if (e.getKeyCode() == KeyEvent.VK_UP){
					if (preSiblingPaywayPanel != null){
						preSiblingPaywayPanel.setSelected(true);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN){
					if (nextSiblingPaywayPanel != null){
						nextSiblingPaywayPanel.setSelected(true);
					}
				}
			}
		});
	}
	
	
	public void setNextSiblingPaywayPanel(PaywayPanel nextSiblingPaywayPanel) {
		this.nextSiblingPaywayPanel = nextSiblingPaywayPanel;
	}


	public void setPreSiblingPaywayPanel(PaywayPanel preSiblingPaywayPanel) {
		this.preSiblingPaywayPanel = preSiblingPaywayPanel;
		preSiblingPaywayPanel.setNextSiblingPaywayPanel(this);
	}


	private void setShouldPay(){
		lbShouldPay.setText("Needs " + payway.getSymbol() + CommonTools.transferDouble2Scale(orderPrice * payway.getRate()));
	}
	
	private void setGetPay(){
		tfGetPay.setText(CommonTools.transferDouble2Scale(orderPrice * payway.getRate()));
		tfGetPay.selectAll();
	}
	public void setOrderPrice(double orderPrice){
		this.orderPrice = orderPrice;
		setShouldPay();
		setGetPay();
		change = 0;
		showChangeText();
	}
	
	private void showChangeText(){
		if (tfGetPay.getText() == null || tfGetPay.getText().length() == 0)
			return;
		double value = Double.parseDouble(tfGetPay.getText());
		change = value - orderPrice * payway.getRate();
		if (change < -0.01)
			return;
		lbChange.setText(Messages.getString("CheckoutDialog.Charge") + payway.getSymbol() + CommonTools.transferDouble2Scale(change));
//		System.out.println(lbChange.getText() + this.getPayway().getName());
	}
	
	public void setSelected(boolean b){
		rbName.setSelected(b);
		rbName.requestFocusInWindow();
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
	
	public double getPaidMoney(){
		if (tfGetPay.getText() == null || tfGetPay.getText().length() == 0)
			return 0;
		return Double.parseDouble(tfGetPay.getText());
	}
	
	public double getChange(){
		return change;
	}
}