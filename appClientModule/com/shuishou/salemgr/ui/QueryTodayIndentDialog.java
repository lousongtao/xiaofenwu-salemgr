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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

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
import com.shuishou.salemgr.ui.components.CommonDialog;

public class QueryTodayIndentDialog extends CommonDialog implements ActionListener{

	private final Logger logger = Logger.getLogger(QueryTodayIndentDialog.class.getName());
	private MainFrame mainFrame;
	
	private JButton btnPrintIndent = new JButton(Messages.getString("Print"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JTable tableIndent = new JTable();
	private IndentModel modelIndent = new IndentModel();
	private JTable tableIndentDetail = new JTable();
	private IndentDetailModel modelIndentDetail = new IndentDetailModel();
	
	private ArrayList<Indent> listIndent = new ArrayList<>();
	
	public QueryTodayIndentDialog(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		this.setModal(true);
		initUI();
		doQuery();
	}
	
	private void initUI(){
		tableIndent.setModel(modelIndent);
		tableIndent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableIndent.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableIndent.getColumnModel().getColumn(1).setPreferredWidth(120);
		tableIndent.getColumnModel().getColumn(2).setPreferredWidth(150);
		tableIndent.getColumnModel().getColumn(3).setPreferredWidth(80);
		tableIndent.getColumnModel().getColumn(4).setPreferredWidth(100);
		tableIndent.getColumnModel().getColumn(5).setPreferredWidth(100);
		tableIndent.getColumnModel().getColumn(6).setPreferredWidth(150);
		tableIndent.getColumnModel().getColumn(7).setPreferredWidth(180);
		tableIndent.setAutoCreateRowSorter(true);
		tableIndent.setRowHeight(35);
		JScrollPane jspTableIndent = new JScrollPane(tableIndent, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableIndent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tableIndentDetail.setModel(modelIndentDetail);
		tableIndentDetail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableIndentDetail.getColumnModel().getColumn(0).setPreferredWidth(250);
		tableIndentDetail.getColumnModel().getColumn(1).setPreferredWidth(150);
		tableIndentDetail.getColumnModel().getColumn(2).setPreferredWidth(150);
		tableIndentDetail.getColumnModel().getColumn(3).setPreferredWidth(150);
		tableIndentDetail.setRowHeight(35);
		JScrollPane jspTableIndentDetail = new JScrollPane(tableIndentDetail, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableIndentDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		btnPrintIndent.setPreferredSize(new Dimension(180, 35));
		btnPrintIndent.setMinimumSize(new Dimension(180, 35));
		btnPrintIndent.setMaximumSize(new Dimension(180, 35));
		btnClose.setPreferredSize(new Dimension(180, 35));
		btnClose.setMinimumSize(new Dimension(180, 35));
		btnClose.setMaximumSize(new Dimension(180, 35));
		
		JPanel pButton = new JPanel();
		pButton.add(btnPrintIndent);
		pButton.add(btnClose);
		btnPrintIndent.addActionListener(this);
		btnClose.addActionListener(this);
		
		JPanel pTable = new JPanel(new GridBagLayout());
		pTable.add(jspTableIndent,		new GridBagConstraints(0, 0, 1, 1, 1, 3, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
		pTable.add(jspTableIndentDetail,new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
		
		
		setLayout(new BorderLayout());
		add(pTable, BorderLayout.CENTER);
		add(pButton, BorderLayout.SOUTH);
		
		tableIndent.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (tableIndent.getSelectedRow() < 0)
					return;
				int modelRow = tableIndent.convertRowIndexToModel(tableIndent.getSelectedRow());
				modelIndentDetail.setData((ArrayList)modelIndent.getObjectAt(modelRow).getItems());
				tableIndentDetail.updateUI();
			}
			
		});
		
		this.setSize(new Dimension(1024, 750));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	public void setIndentData(ArrayList<Indent> indents){
		listIndent = indents;
		tableIndent.updateUI();
	}
	
	private void doQuery(){
		String url = "indent/queryindent";
		Map<String, String> params = new HashMap<>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		params.put("starttime", ConstantValue.DFYMDHMS.format(c.getTime()));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		params.put("endtime", ConstantValue.DFYMDHMS.format(c.getTime()));
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for query indent. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for query indent. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<Indent>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Indent>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query indent. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		listIndent = result.data;
		tableIndent.updateUI();
	}
	
	private void doPrint(Indent indent){
		Map<String,String> keyMap = new HashMap<String, String>();
		if (indent.getMemberCard() != null && indent.getMemberCard().length() > 0){
			//reload member data from server
			Member member = HttpUtil.doLoadMember(QueryTodayIndentDialog.this, mainFrame.getOnDutyUser(), indent.getMemberCard());
			keyMap.put("member", member.getMemberCard() + ", "+ member.getName() + String.format(ConstantValue.FORMAT_DOUBLE, member.getScore()) 
				+ ", " + (member.getDiscountRate() * 100) + "%");
		}else {
			keyMap.put("member", "");
		}
		keyMap.put("cashier", indent.getOperator());
		keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getCreateTime()));
		keyMap.put("totalPrice", String.format(ConstantValue.FORMAT_DOUBLE, indent.getPaidPrice()));
		keyMap.put("gst", String.format(ConstantValue.FORMAT_DOUBLE, indent.getPaidPrice()/11));
		keyMap.put("payWay", indent.getPayWay());
		keyMap.put("orderNo", indent.getIndentCode());
		keyMap.put("paid", "");
		keyMap.put("getcash", "");
		keyMap.put("change", "");
		double originPrice = 0;
		List<Map<String, String>> goods = new ArrayList<>();
		for (int i = 0; i< indent.getItems().size(); i++) {
			IndentDetail detail = indent.getItems().get(i);
			Map<String, String> mg = new HashMap<String, String>();
			mg.put("name", detail.getGoodsName());
			mg.put("price", String.format(ConstantValue.FORMAT_DOUBLE, detail.getGoodsPrice()));
			mg.put("amount", detail.getAmount() + "");
			mg.put("subTotal", String.format(ConstantValue.FORMAT_DOUBLE, detail.getSoldPrice() * detail.getAmount()));
			originPrice += detail.getGoodsPrice() * detail.getAmount();
			goods.add(mg);
		}
		keyMap.put("originPrice", String.format(ConstantValue.FORMAT_DOUBLE, originPrice));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keyMap);
		params.put("goods", goods);
		String printfile = ConstantValue.TICKET_TEMPLATE_PURCHASE;
		if (indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_PAID ||
				indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_UNPAID){
			printfile = ConstantValue.TICKET_TEMPLATE_PREBUY;
		} else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_REFUND){
			printfile = ConstantValue.TICKET_TEMPLATE_REFUND;
		}
		PrintJob job = new PrintJob(printfile, params, mainFrame.printerName);
		PrintQueue.add(job);
	}
	
	public void doEnterClick(){
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClose){
			this.setVisible(false);
		} else if (e.getSource() == btnPrintIndent){
			if (tableIndent.getSelectedRow() < 0)
				return;
			int modelRow = tableIndent.convertRowIndexToModel(tableIndent.getSelectedRow());
			doPrint(((IndentModel)tableIndent.getModel()).getObjectAt(modelRow));
		} 
	}
	
	class IndentModel extends AbstractTableModel{

		private String[] header = new String[]{"Time", "Member Card", "Member Name", "Price", "Paid Price", "Pay Way", "Order Code", "Type"};
		
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
				return ConstantValue.DFYMDHMS.format(indent.getCreateTime());
			case 1:
				return indent.getMemberCard();
			case 2:
				Member m = mainFrame.getMemberByMemberCard(indent.getMemberCard());
				
				return m == null ? "" : m.getName();
			case 3: 
				return indent.getTotalPrice();
			case 4:
				return indent.getPaidPrice();
			case 5:
				return indent.getPayWay();
			
			case 6:
				return String.valueOf(indent.getIndentCode());
			case 7:
				if (indent.getIndentType() == ConstantValue.INDENT_TYPE_ORDER)
					return "ORDER";
				else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_PAID)
					return "PRE-ORDER-PAID";
				else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_UNPAID)
					return "PRE-ORDER-UNPAID";
				else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_REFUND)
					return "REFUND";
				else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_FINISHED)
					return "PRE-ORDER-FINISHED";
				else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_ORDER_FROMPREBUY)
					return "ORDER-FROM-PREORDER";
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
	}

	class IndentDetailModel extends AbstractTableModel{

		private String[] header = new String[]{"Name", "Amount", "Price", "Sold Price"};
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
			case 3:
				return detail.getSoldPrice();
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
	}
	
}
