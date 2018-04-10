package com.shuishou.salemgr.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.Goods;
import com.shuishou.salemgr.ui.components.CommonDialog;
import com.shuishou.salemgr.ui.components.DefaultTableCellHeaderRenderer;

public class SearchObjectListDialog extends CommonDialog{

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
		table.setRowHeight(40);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(300);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
//		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		// 设置table表头居中
	    DefaultTableCellHeaderRenderer thr = new DefaultTableCellHeaderRenderer();
	    thr.setHorizontalAlignment(JLabel.CENTER);
	    table.getTableHeader().setDefaultRenderer(thr);
	    // 设置table内容居中
//	    DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
//	    tcr.setHorizontalAlignment(JLabel.LEFT);
//	    table.setDefaultRenderer(Object.class, tcr);
	    
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JButton btnConfirm = new JButton("Choose");
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
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doConfirm();
				}
			}
		});
	}
	
	public void doEnterClick(){
		doConfirm();
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
//				Messages.getString("SearchObjectListDialog.Header.MemberPrice"),
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
//			case 2:
//				return g.getMemberPrice();
			case 2:
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
