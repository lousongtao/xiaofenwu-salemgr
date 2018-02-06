package com.shuishou.salemgr.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.shuishou.salemgr.ConstantValue;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.Goods;
import com.shuishou.salemgr.ui.components.CommonDialog;
import com.shuishou.salemgr.ui.components.NumberTextField;

public class ChangeGoodsPriceDialog extends CommonDialog implements ActionListener{
	
	private MainFrame mainFrame;
	private Goods goods;
	private NumberTextField tfModifiedPrice = new NumberTextField(this, true, false);
	private NumberTextField tfQuantity = new NumberTextField(this, false, false);
	private NumberTextField tfDiscount = new NumberTextField(this, true, false);
	private JButton btnConfirm = new JButton(Messages.getString("ConfirmDialog"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private boolean isCancel = false;
	
	public ChangeGoodsPriceDialog(MainFrame mainFrame, Goods goods){
		this.mainFrame = mainFrame;
		this.goods = goods;
		this.setTitle("Adjust");
		this.setModal(true);
		initUI();
	}
	
	private void initUI(){
		JPanel pButton = new JPanel();
		pButton.add(btnConfirm);
		pButton.add(btnClose);
		btnConfirm.addActionListener(this);
		btnClose.addActionListener(this);
		
		JLabel lbName = new JLabel(Messages.getString("Name") + " : " + goods.getName());
		JLabel lbBarcode = new JLabel(Messages.getString("GoodsTableModel.Header.Barcode") + " : " + goods.getBarcode());
		JLabel lbSellPrice = new JLabel(Messages.getString("GoodsTableModel.Header.SellPrice") + " : " + goods.getSellPrice());
//		JLabel lbMemberPrice = new JLabel(Messages.getString("GoodsTableModel.Header.Barcode") + " : " + goods.getMemberPrice());
		JLabel lbModifiedPrice = new JLabel(Messages.getString("ChangeGoodsPriceDialog.ModifyPrice") + " : ");
		JLabel lbQuantity = new JLabel(Messages.getString("GoodsTableModel.Header.Amount") + " : ");
		JLabel lbDiscount = new JLabel(Messages.getString("MemberDialog.DiscountRate") + " (%): ");
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbName,			new GridBagConstraints(0, 0, 2, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		c.add(lbBarcode,		new GridBagConstraints(0, 1, 2, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		c.add(lbSellPrice,		new GridBagConstraints(0, 2, 2, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		c.add(lbModifiedPrice,	new GridBagConstraints(0, 3, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		c.add(tfModifiedPrice,	new GridBagConstraints(1, 3, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		c.add(lbQuantity,		new GridBagConstraints(0, 4, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		c.add(tfQuantity,		new GridBagConstraints(1, 4, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		c.add(lbDiscount,		new GridBagConstraints(0, 5, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		c.add(tfDiscount,		new GridBagConstraints(1, 5, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		c.add(pButton,			new GridBagConstraints(0, 6, 2, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		tfModifiedPrice.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.getID() != KeyEvent.KEY_PRESSED)
					return;
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					if (tfModifiedPrice.getText() == null || tfModifiedPrice.getText().length() == 0){
						JOptionPane.showMessageDialog(ChangeGoodsPriceDialog.this, "Must input the modified price.");
						return;
					}
					if (getModifiedPrice() < 0){
						JOptionPane.showMessageDialog(ChangeGoodsPriceDialog.this, "Don't allow to input a negative price.");
						return;
					}
					setVisible(false);
				}
			}
		});
		tfModifiedPrice.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e) {
				if (tfModifiedPrice.getText() != null && tfModifiedPrice.getText().length() != 0){
					tfModifiedPrice.select(0, tfModifiedPrice.getText().length());
				}
			}
		});
		tfQuantity.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e) {
				if (tfQuantity.getText() != null && tfQuantity.getText().length() != 0){
					tfQuantity.select(0, tfQuantity.getText().length());
				}
			}
		});
		tfDiscount.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e) {
				if (tfDiscount.getText() != null && tfDiscount.getText().length() != 0){
					tfDiscount.select(0, tfDiscount.getText().length());
				}
			}
		});
		tfModifiedPrice.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.getID() != KeyEvent.KEY_PRESSED)
					return;
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					doConfirm();
				} 
			}
		});
		tfDiscount.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.getID() != KeyEvent.KEY_PRESSED)
					return;
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					doConfirm();
				} 
			}
		});
		tfQuantity.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.getID() != KeyEvent.KEY_PRESSED)
					return;
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					doConfirm();
				} 
			}
		});
		tfDiscount.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				calculateDiscountPrice();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				calculateDiscountPrice();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				calculateDiscountPrice();
			}
		});
		this.setSize(new Dimension(350, 350));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}

	private void calculateDiscountPrice(){
		if (tfDiscount.getText() == null || tfDiscount.getText().length() == 0)
			return;
		double discount = 0;
		try{
			discount = Double.parseDouble(tfDiscount.getText());
		} catch(Exception e){}
		tfModifiedPrice.setText(String.format(ConstantValue.FORMAT_DOUBLE, discount /100 * goods.getSellPrice()));
	}
	
	public boolean isCancel() {
		return isCancel;
	}
	
	public double getModifiedPrice(){
		return Double.parseDouble(tfModifiedPrice.getText());
	}
	
	public int getQuantity(){
		return Integer.parseInt(tfQuantity.getText());
	}
	
	public double getDiscount(){
		return Double.parseDouble(tfDiscount.getText());
	}
	
	/**
	 * 
	 * @param price
	 * @param quantity
	 * @param discount a number between 0-100, 百分比数值
	 */
	public void setValue(double price, int quantity, double discount){
		tfDiscount.setText(String.valueOf(discount));//需要先设置折扣值, 否则会刷新掉带入的price值
		tfModifiedPrice.setText(String.valueOf(price));
		tfQuantity.setText(String.valueOf(quantity));
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConfirm){
			doConfirm();
		} else if (e.getSource() == btnClose){
			isCancel = true;
			setVisible(false);
		}
	}
	
	private void doConfirm(){
		if (tfModifiedPrice.getText() == null || tfModifiedPrice.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Must input the modified price.");
			return;
		}
		if (tfQuantity.getText() == null || tfQuantity.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Must input the quantity.");
			return;
		}
		if (tfDiscount.getText() == null || tfDiscount.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Must input the discount.");
			return;
		}
		if (getModifiedPrice() < 0){
			JOptionPane.showMessageDialog(this, "Don't allow to input a negative price.");
			return;
		}
		if (getDiscount() < 0){
			JOptionPane.showMessageDialog(this, "Don't allow to input a negative discount.");
			return;
		}
		setVisible(false);
	}

	public void doEnterClick(){
		doConfirm();
	}
}
