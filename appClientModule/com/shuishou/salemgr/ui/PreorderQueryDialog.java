package com.shuishou.salemgr.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.salemgr.ConstantValue;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.HttpResult;
import com.shuishou.salemgr.beans.Indent;
import com.shuishou.salemgr.beans.IndentDetail;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.http.HttpUtil;
import com.shuishou.salemgr.printertool.PrintJob;
import com.shuishou.salemgr.printertool.PrintQueue;
import com.shuishou.salemgr.ui.components.JDatePicker;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

public class PreorderQueryDialog extends JDialog implements ActionListener{
	private final Logger logger = Logger.getLogger(PreorderQueryDialog.class.getName());
	private MainFrame mainFrame;
	private JTextField tfMemberCard = new JTextField();
	private JDatePicker dpStartDate = new JDatePicker();
	private JDatePicker dpEndDate = new JDatePicker();
	private JButton btnQuery = new JButton("Query");
	private JButton btnClose = new JButton("Close");
	private JButton btnDelete = new JButton("Delete");
	private JButton btnChangeToOrder = new JButton("Change To Order");
	
	private JTable tableIndent = new JTable();
	private IndentModel modelIndent = new IndentModel();
	private JTable tableIndentDetail = new JTable();
	private IndentDetailModel modelIndentDetail = new IndentDetailModel();
	
	
	private ArrayList<Indent> listIndent = new ArrayList<>();
	
	public PreorderQueryDialog(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		this.setModal(true);
		this.setTitle(Messages.getString("MainFrame.PreOrderMgr"));
		initUI();
	}
	
	private void initUI(){
		JLabel lbTableName = new JLabel("Member Card : ");
		JLabel lbStartDate = new JLabel("Start Date : ");
		JLabel lbEndDate = new JLabel("End Date : ");
		tfMemberCard.setPreferredSize(new Dimension(150, 25));
		dpStartDate.setShowYearButtons(true);
		dpEndDate.setShowYearButtons(true);
		
		tableIndent.setModel(modelIndent);
		tableIndent.setRowHeight(40);
		tableIndent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableIndent.getColumnModel().getColumn(0).setPreferredWidth(150);
		tableIndent.getColumnModel().getColumn(1).setPreferredWidth(80);
		tableIndent.getColumnModel().getColumn(2).setPreferredWidth(120);
		tableIndent.getColumnModel().getColumn(3).setPreferredWidth(120);
		tableIndent.getColumnModel().getColumn(4).setPreferredWidth(210);
		JScrollPane jspTableIndent = new JScrollPane(tableIndent, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableIndent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tableIndentDetail.setModel(modelIndentDetail);
		tableIndentDetail.setRowHeight(40);
		tableIndentDetail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableIndentDetail.getColumnModel().getColumn(0).setPreferredWidth(250);
		tableIndentDetail.getColumnModel().getColumn(1).setPreferredWidth(80);
		tableIndentDetail.getColumnModel().getColumn(2).setPreferredWidth(80);
		JScrollPane jspTableIndentDetail = new JScrollPane(tableIndentDetail, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableIndentDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		JPanel pCondition = new JPanel(new GridBagLayout());
		pCondition.add(lbTableName,	new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(tfMemberCard,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(lbStartDate,	new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(dpStartDate,	new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(lbEndDate,	new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(dpEndDate,	new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(new JLabel(),new GridBagConstraints(6, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(btnQuery,	new GridBagConstraints(7, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(btnChangeToOrder,new GridBagConstraints(8, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(btnDelete,	new GridBagConstraints(9, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(btnClose,	new GridBagConstraints(10, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		btnQuery.addActionListener(this);
		btnClose.addActionListener(this);
		btnDelete.addActionListener(this);
		btnChangeToOrder.addActionListener(this);
		
		JPanel pTable = new JPanel(new GridBagLayout());
		pTable.add(jspTableIndent,		new GridBagConstraints(0, 0, 1, 1, 3, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
		pTable.add(jspTableIndentDetail,new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
		
		setLayout(new BorderLayout());
		add(pTable, BorderLayout.CENTER);
		add(pCondition, BorderLayout.NORTH);
		
		tableIndent.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (tableIndent.getSelectedRow() < 0)
					return;
				modelIndentDetail.setData((ArrayList)modelIndent.getObjectAt(tableIndent.getSelectedRow()).getItems());
				tableIndentDetail.updateUI();
			}
			
		});
		
		this.setSize(new Dimension(mainFrame.getWidth(), 700));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	private void doQuery(){
		if (listIndent != null){
			listIndent.clear();
			modelIndent.fireTableDataChanged();
		}
		
		modelIndentDetail.clearData();
		modelIndentDetail.fireTableDataChanged();
		Date startTime = null;
		Date endTime = null;
		if (dpStartDate.getModel() != null && dpStartDate.getModel().getValue() != null){
			Calendar c = (Calendar)dpStartDate.getModel().getValue();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			startTime = c.getTime();
		}
		if (dpEndDate.getModel() != null && dpEndDate.getModel().getValue() != null){
			Calendar c = (Calendar)dpEndDate.getModel().getValue();
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			endTime = c.getTime();
		}
		listIndent = HttpUtil.doQueryPreOrder(mainFrame, mainFrame.getOnDutyUser(), tfMemberCard.getText(), startTime, endTime);
		tableIndent.updateUI();
	}
	
	private void doChangeToOrder(){
		int row = tableIndent.getSelectedRow();
		if (row < 0){
			JOptionPane.showMessageDialog(this, "Please choose a record from Order table.", "Error", JOptionPane.YES_NO_OPTION);
			return;
		}
		Indent indent = HttpUtil.doChangePreOrderToOrder(this, mainFrame.getOnDutyUser(), modelIndent.getObjectAt(row).getId() + "");
		if (indent != null){
			doPrintTicket(indent);
			modelIndent.deleteRow(row);
			modelIndent.fireTableDataChanged();
		}
	}
	
	private void doPrintTicket(Indent indent){
		Member member = null;
		if (indent.getMemberCard() != null && indent.getMemberCard().length() > 0){
			member = mainFrame.getMember(indent.getMemberCard());
			if (member == null){
				JOptionPane.showMessageDialog(this, "Cannot find member by " + indent.getMemberCard() + ", please restart application.");
				return;
			}
		}
		Map<String,String> keyMap = new HashMap<String, String>();
		if (member != null){
			//reload member data from server
			member = HttpUtil.doLoadMember(PreorderQueryDialog.this, mainFrame.getOnDutyUser(), member.getMemberCard());
			//store into local memory
			mainFrame.getMapMember().put(member.getMemberCard(), member);
			keyMap.put("member", member.getMemberCard() + "  score : "+ String.format(ConstantValue.FORMAT_DOUBLE, member.getScore()) + "  discount rate: " + (member.getDiscountRate() * 100) + "%");
		}else {
			keyMap.put("member", "");
			keyMap.put("discount", "");
		}
		keyMap.put("cashier", mainFrame.getOnDutyUser().getName());
		keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(new Date()));
		keyMap.put("totalPrice", indent.getPaidPrice() + "");
		
		keyMap.put("totalPriceIncludeGST", indent.getPaidPrice() + "");
		keyMap.put("gst", String.format(ConstantValue.FORMAT_DOUBLE, indent.getPaidPrice()/11));
		keyMap.put("payWay", indent.getPayWay());
		List<Map<String, String>> goods = new ArrayList<>();
		for (int i = 0; i< indent.getItems().size(); i++) {
			IndentDetail detail = indent.getItems().get(i);
			Map<String, String> mg = new HashMap<String, String>();
			mg.put("name", detail.getGoodsName());
			mg.put("price", String.format(ConstantValue.FORMAT_DOUBLE, detail.getGoodsPrice()));
			mg.put("amount", detail.getAmount() + "");
			mg.put("totalPrice", String.format(ConstantValue.FORMAT_DOUBLE, detail.getAmount() * detail.getGoodsPrice()));
			goods.add(mg);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keyMap);
		params.put("goods", goods);
		PrintJob job = new PrintJob(ConstantValue.TICKET_TEMPLATE_PURCHASE, params, mainFrame.printerName);
		PrintQueue.add(job);
	}
	
	private void doDelete(){
		int row = tableIndent.getSelectedRow();
		if (row < 0){
			return;
		}
		if (JOptionPane.showConfirmDialog(this, "Do you want to delete the selection preorder?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			return;
		}
		boolean result = HttpUtil.doDeletePreOrder(this, mainFrame.getOnDutyUser(), modelIndent.getObjectAt(row).getId() + "");
		if (result){
			JOptionPane.showMessageDialog(this, "Delete preorder successfully");
			modelIndent.deleteRow(row);
			modelIndent.fireTableDataChanged();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnQuery){
			doQuery();
		} else if (e.getSource() == btnChangeToOrder){
			doChangeToOrder();
		} else if (e.getSource() == btnClose){
			this.setVisible(false);
		} else if (e.getSource() == btnDelete){
			doDelete();
		}
	}
	
	class IndentModel extends DefaultTableModel{

		private String[] header = new String[]{"Member Card", "Price", "Paid Price", "Pay Way", "Time", "Status"};
		
		public IndentModel(){

		}
		@Override
		public int getRowCount() {
			if (listIndent == null) return 0;
			return listIndent.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Indent indent = listIndent.get(rowIndex);
			switch(columnIndex){
			case 0:
				return indent.getMemberCard();
			case 1: 
				return indent.getTotalPrice();
			case 2:
				return indent.getPaidPrice();
			case 3:
				return indent.getPayWay();
			case 4: 
				return ConstantValue.DFYMDHMS.format(indent.getCreateTime());
			case 5:
				if (indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_PAID)
					return "PAID";
				else 
					return "UNPAID";
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public Indent getObjectAt(int row){
			return listIndent.get(row);
		}
		
		public void deleteRow(int row){
			if (listIndent != null && row >=0 && row < listIndent.size())
				listIndent.remove(row);
		}
	}

	class IndentDetailModel extends AbstractTableModel{

		private String[] header = new String[]{"Name", "Amount", "Price"};
		private ArrayList<IndentDetail> details = new ArrayList<>();
		public IndentDetailModel(){
		}
		@Override
		public int getRowCount() {
			if (details == null) return 0;
			return details.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			IndentDetail detail = details.get(rowIndex);
			switch(columnIndex){
			case 0:
				return detail.getGoodsName();
			case 1: 
				return detail.getAmount();
			case 2:
				return detail.getGoodsPrice();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public void setData(ArrayList<IndentDetail> details){
			this.details = details;
		}
		
		public IndentDetail getObjectAt(int row){
			return details.get(row);
		}
		
		public void clearData(){
			details.clear();
		}
	}
}
