package com.shuishou.salemgr.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.Goods;

public class SearchObjectListDialog extends JDialog{

	private Goods choosedGoods;
	private JTable table = new JTable();
	private GoodsTableModel model = new GoodsTableModel();
	private MainFrame parent;
	
	public SearchObjectListDialog(MainFrame parent, ArrayList<Goods> searchResult, int width, int height){
		setTitle("Choose Goods");
		setSize(width, height);
		this.setModal(true);
		this.parent = parent;
		initUI(searchResult);
		
	}
	
	private void initUI(ArrayList<Goods> searchResult){
		model.setData(searchResult);
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(200);
		table.getColumnModel().getColumn(3).setPreferredWidth(200);
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JButton btnConfirm = new JButton("Confirm");
		JButton btnCancel = new JButton("Cancel");
		JPanel pButton = new JPanel();
		pButton.add(btnConfirm);
		pButton.add(btnCancel);
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(jspTable, BorderLayout.CENTER);
		c.add(pButton, BorderLayout.SOUTH);
		setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
				(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
		btnConfirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doConfirm();
				
			}});
		btnCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				SearchObjectListDialog.this.setVisible(false);
			}});
	}
	
	private void doConfirm(){
		if (table.getSelectedRow() < 0){
			JOptionPane.showMessageDialog(this, "No select any record");
			return;
		}
		choosedGoods = ((GoodsTableModel)table.getModel()).getObjectAt(table.getSelectedRow());
		this.setVisible(false);
	}
	
	public Goods getChoosedGoods(){
		return choosedGoods;
	}
	
	class GoodsTableModel extends AbstractTableModel{
		private ArrayList<Goods> items = new ArrayList<>();
		private String[] header = new String[]{
				Messages.getString("SearchObjectListDialog.Header.Name"),
				Messages.getString("SearchObjectListDialog.Header.Barcode"),
				Messages.getString("SearchObjectListDialog.Header.MemberPrice"),
				Messages.getString("SearchObjectListDialog.Header.SellPrice"),
		};

		public GoodsTableModel(){
		}
		
		public GoodsTableModel(ArrayList<Goods> items){
			this.items = items;
		}
		@Override
		public int getRowCount() {
			if (items == null)
				return 0;
			return items.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Goods g = getObjectAt(rowIndex);
			switch(columnIndex){
			case 0:
				return g.getName();
			case 1:
				return g.getBarcode();
			case 2:
				return g.getMemberPrice();
			case 3:
				return g.getSellPrice();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int column) {
			return header[column];
	    }
		
		public void setData(ArrayList<Goods> items){
			this.items = items;
		}
		
		public ArrayList<Goods> getData(){
			return items;
		}
		
		public Goods getObjectAt(int index){
			return items.get(index);
		}
	}
}
