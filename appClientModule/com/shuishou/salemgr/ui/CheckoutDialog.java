package com.shuishou.salemgr.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.salemgr.CommonTools;
import com.shuishou.salemgr.ConstantValue;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.DiscountTemplate;
import com.shuishou.salemgr.beans.Goods;
import com.shuishou.salemgr.beans.HttpResult;
import com.shuishou.salemgr.beans.Indent;
import com.shuishou.salemgr.beans.IndentDetail;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.beans.PayWay;
import com.shuishou.salemgr.http.HttpUtil;
import com.shuishou.salemgr.printertool.PrintJob;
import com.shuishou.salemgr.printertool.PrintQueue;
import com.shuishou.salemgr.ui.components.JBlockedButton;
import com.shuishou.salemgr.ui.components.NumberTextField;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

public class CheckoutDialog extends JDialog{
	private final Logger logger = Logger.getLogger(CheckoutDialog.class.getName());
	private MainFrame mainFrame;
	
	private JLabel lbDiscountPrice = new JLabel();
//	private JRadioButton rbPayCash = new JRadioButton(Messages.getString("CheckoutDialog.Cash"), true); //$NON-NLS-1$
//	private JRadioButton rbPayBankCard = new JRadioButton(Messages.getString("CheckoutDialog.BandCard"), false); //$NON-NLS-1$
//	private JRadioButton rbPayMember = new JRadioButton(Messages.getString("CheckoutDialog.MemberCard"), false); //$NON-NLS-1$
	private JRadioButton rbDiscountNon = new JRadioButton(Messages.getString("CheckoutDialog.NoDiscount"), true); //$NON-NLS-1$
	private JRadioButton rbDiscountTemp = new JRadioButton(Messages.getString("CheckoutDialog.TempDiscount"), false); //$NON-NLS-1$
	private JRadioButton rbDiscountDirect = new JRadioButton(Messages.getString("CheckoutDialog.DirectDiscount"), false); //$NON-NLS-1$
	private ArrayList<PaywayPanel> listPaywayPanel = new ArrayList<>();
	private NumberTextField tfDiscountPrice = null;
	private JBlockedButton btnPay = new JBlockedButton(Messages.getString("CheckoutDialog.PayButton")+"[ENTER]", "/resource/checkout.png"); //$NON-NLS-1$
	private JButton btnClose = new JButton(Messages.getString("CloseDialog")+"[ESC]"); //$NON-NLS-1$
	private JButton btnCancelOrder = new JButton(Messages.getString("CheckoutDialog.CancelOrderButton")); //$NON-NLS-1$
//	private NumberTextField tfGetCash;
	private JLabel lbMemberInfo = new JLabel();
	private double discountPrice = 0;
	private double sellPrice = 0;
	private ArrayList<ChoosedGoods> choosedGoods;
	private Member member;
	
	private List<DiscountTemplateRadioButton> discountTempRadioButtonList = new ArrayList<DiscountTemplateRadioButton>();
	public CheckoutDialog(MainFrame mainFrame,String title, boolean modal, ArrayList<ChoosedGoods> choosedGoods, Member m){
		super(mainFrame, title, modal);
		this.mainFrame = mainFrame;
		this.choosedGoods = choosedGoods;
		this.member = m;
		for(ChoosedGoods cg : choosedGoods){
			/**
			 * if modifiedPrice >= 0, then use the modifiedPrice;
			 * else if member != null, then use the member discount price;
			 * else use the goods.sellPrice.
			 */
			if (cg.modifiedPrice >= 0)
				sellPrice += cg.modifiedPrice * cg.amount;
			else if (member != null)
				sellPrice += cg.goods.getSellPrice() * member.getDiscountRate() * cg.amount;
			else 
				sellPrice += cg.goods.getSellPrice() * cg.amount;
		}
		discountPrice = sellPrice;
		initUI();
	}
	
	private void initUI(){
		JLabel lbMember = new JLabel(Messages.getString("CheckoutDialog.MemberCard"));
		JLabel lbPrice = new JLabel();
		lbMemberInfo.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.MemberInfo")));
		if (member != null){
			lbMemberInfo.setText(Messages.getString("CheckoutDialog.MemberInfo.Name")+ member.getName() + ", " 
				+ Messages.getString("CheckoutDialog.MemberInfo.DiscountRate") + member.getDiscountRate() + ", "
				+ Messages.getString("CheckoutDialog.MemberInfo.Score") + CommonTools.transferDouble2Scale(member.getScore()) + ", "
				+ Messages.getString("CheckoutDialog.MemberInfo.Balance") + CommonTools.transferDouble2Scale(member.getBalanceMoney()));
		}
		tfDiscountPrice = new NumberTextField(this, true, false);
		
//		tfGetCash = new NumberTextField(this, true, false);
		
		JPanel pPayway = new JPanel(new GridBagLayout());
		pPayway.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.PayWay"))); //$NON-NLS-1$
		ButtonGroup bgPayway = new ButtonGroup();
//		bgPayway.add(rbPayCash);
//		bgPayway.add(rbPayBankCard);
//		bgPayway.add(rbPayMember);
//		pPayway.add(rbPayCash, 		new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//		pPayway.add(lbGetCash, 		new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 50, 0, 0), 0, 0));
//		pPayway.add(tfGetCash, 		new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
//		pPayway.add(lbChange, 		new GridBagConstraints(3, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
//		pPayway.add(rbPayBankCard, 	new GridBagConstraints(0, 1, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//		pPayway.add(rbPayMember,	new GridBagConstraints(1, 1, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 50, 0, 0), 0, 0));
//		pPayway.add(tfMember, 		new GridBagConstraints(2, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 20, 0, 0), 0, 0));
		if (!mainFrame.getPaywayList().isEmpty()){
			for (int i = 0; i < mainFrame.getPaywayList().size(); i++) {
				PayWay pw = mainFrame.getPaywayList().get(i);
				PaywayPanel pp = new PaywayPanel(pw, sellPrice);
				bgPayway.add(pp.getRadioButton());
				if (i == 0){
					pp.setSelected(true);
				}
				listPaywayPanel.add(pp);
				pPayway.add(pp, new GridBagConstraints(0, i, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
			}
			
		}
		JPanel pDiscountTemplate = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		pDiscountTemplate.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.DiscountTemplateBorderTitle")));
		if (mainFrame.getDiscountTemplateList().isEmpty()){
			rbDiscountTemp.setEnabled(false);
		} else {
			discountTempRadioButtonList.clear();
			ButtonGroup bg = new ButtonGroup();
			for (int i = 0; i < mainFrame.getDiscountTemplateList().size(); i++) {
				DiscountTemplateRadioButton rb = new DiscountTemplateRadioButton(false, mainFrame.getDiscountTemplateList().get(i));
				rb.addItemListener(new ItemListener(){
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							rbDiscountTemp.setSelected(true);
							calculatePaidPrice();
					    } 
					}
				});
				discountTempRadioButtonList.add(rb);
				bg.add(rb);
				pDiscountTemplate.add(rb);
			}
		}
		
		Dimension dDiscountPrice = tfDiscountPrice.getPreferredSize();
		dDiscountPrice.width = 150;
		tfDiscountPrice.setPreferredSize(dDiscountPrice);
		
		JPanel pDiscount = new JPanel(new GridBagLayout());
//		pDiscount.setBackground(bgDiscountColor);
		pDiscount.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.BorderDiscount"))); //$NON-NLS-1$
		ButtonGroup bgDiscount = new ButtonGroup();
		bgDiscount.add(rbDiscountNon);
		bgDiscount.add(rbDiscountTemp);
		bgDiscount.add(rbDiscountDirect);
		pDiscount.add(rbDiscountNon, 	new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pDiscount.add(rbDiscountDirect, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 50, 0, 0), 0, 0));
		pDiscount.add(tfDiscountPrice, 	new GridBagConstraints(2, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
		pDiscount.add(rbDiscountTemp, 	new GridBagConstraints(3, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 50, 0, 0), 0, 0));
		pDiscount.add(pDiscountTemplate,new GridBagConstraints(0, 2, 4, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		
		JPanel pButton = new JPanel(new GridBagLayout());
		btnPay.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnCancelOrder.setPreferredSize(new Dimension(150, 50));
		pButton.add(btnPay,			new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		pButton.add(btnClose,		new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		pButton.add(btnCancelOrder,	new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		Dimension dPButton = pButton.getPreferredSize();
		dPButton.height = 60;
		pButton.setPreferredSize(dPButton);
		
		lbMember.setFont(ConstantValue.FONT_25BOLD);
		lbPrice.setFont(ConstantValue.FONT_25BOLD);
		lbDiscountPrice.setFont(ConstantValue.FONT_25BOLD);
		lbPrice.setText(Messages.getString("CheckoutDialog.Price") + CommonTools.transferNumberByPM(sellPrice, "")); //$NON-NLS-1$
		lbDiscountPrice.setText(Messages.getString("CheckoutDialog.DiscountPrice") + CommonTools.transferNumberByPM(discountPrice, "")); //$NON-NLS-1$
		
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbPrice, 			new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		c.add(lbMemberInfo,		new GridBagConstraints(0, 2, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pPayway, 			new GridBagConstraints(0, 3, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pDiscount, 		new GridBagConstraints(0, 4, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(lbDiscountPrice, 	new GridBagConstraints(0, 5, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pButton, 			new GridBagConstraints(0, 6, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		this.setSize(new Dimension(750, 750));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				CheckoutDialog.this.setVisible(false);
			}});
		
		btnPay.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doPay();
			}});
		
		btnCancelOrder.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				CheckoutDialog.this.setVisible(false);
				mainFrame.clearTable();
			}});
		
//		tfGetCash.getDocument().addDocumentListener(new DocumentListener(){
//			
//			@Override
//			public void insertUpdate(DocumentEvent e) {
//				rbPayCash.setSelected(true);
//				showChargeText();
//			}
//
//			@Override
//			public void removeUpdate(DocumentEvent e) {
//				rbPayCash.setSelected(true);
//				showChargeText();
//			}
//
//			@Override
//			public void changedUpdate(DocumentEvent e) {
//				rbPayCash.setSelected(true);
//				showChargeText();
//			}});
		tfDiscountPrice.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
				rbDiscountDirect.setSelected(true);
				calculatePaidPrice();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				rbDiscountDirect.setSelected(true);
				calculatePaidPrice();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				rbDiscountDirect.setSelected(true);
				calculatePaidPrice();
			}});
		
		
		
		rbDiscountNon.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					calculatePaidPrice();
			    } 
			}
		});
		
		rbDiscountTemp.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					calculatePaidPrice();
			    } 
			}
		});
		
		rbDiscountDirect.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					calculatePaidPrice();
			    } 
			}
		});
	}
	
	private void showChargeText(){
//		if (!rbPayCash.isSelected())
//			return;
//		if (tfGetCash.getText() == null || tfGetCash.getText().length() == 0){
//			lbChange.setText("");
//			return;
//		}
//		double value = Double.parseDouble(tfGetCash.getText());
//		if (value < discountPrice)
//			return;
//		lbChange.setText(Messages.getString("CheckoutDialog.Charge")+" $" + String.format("%.2f", value - discountPrice));
	}
	
	private DiscountTemplateRadioButton getSelectedDiscountTemplateRadioButton(){
		for (DiscountTemplateRadioButton rb : discountTempRadioButtonList) {
			if (rb.isSelected())
				return rb;
		}
		return null;
	}
	
	private void calculatePaidPrice(){
		if (rbDiscountNon.isSelected()) {
			discountPrice = sellPrice;
		} else if(rbDiscountTemp.isSelected()) {
			DiscountTemplateRadioButton rbTemplate = getSelectedDiscountTemplateRadioButton();
			if (rbTemplate == null){
				discountTempRadioButtonList.get(0).setSelected(true);
				rbTemplate = discountTempRadioButtonList.get(0);
			}
			discountPrice = sellPrice * rbTemplate.getDiscountTemplate().getRate();
		} else if (rbDiscountDirect.isSelected()) {
			double dp = 0;
			try{
				dp = Double.parseDouble(tfDiscountPrice.getText());
			}catch(Exception e){}
			discountPrice = sellPrice - dp;
		}
		
		lbDiscountPrice.setText(Messages.getString("CheckoutDialog.DiscountPrice") + new DecimalFormat("0.00").format(discountPrice)); //$NON-NLS-1$
		showChargeText();
	}
	
	private PayWay getChoosedPayWay(){
		for(PaywayPanel pp : listPaywayPanel){
			if (pp.getRadioButton().isSelected())
				return pp.payway;
		}
		return null;
	}
	
	private PaywayPanel getChoosedPayWayPanel(){
		for(PaywayPanel pp : listPaywayPanel){
			if (pp.getRadioButton().isSelected())
				return pp;
		}
		return null;
	}
	
	/**
	 * seperate the list by amount; if amount > 0, think it as order, otherwise think it as refund
	 */
	public void doPay(){
		boolean existingOrder = false;
		boolean existingRefund = false;
		JSONArray ja_order = new JSONArray();//订单
		JSONArray ja_refund = new JSONArray();//退单
		for (int i = 0; i< choosedGoods.size(); i++) {
			JSONObject jo = new JSONObject();
			ChoosedGoods cg = choosedGoods.get(i);
			jo.put("id", cg.goods.getId());
			jo.put("amount", cg.amount);
			if (cg.modifiedPrice >= 0)
				jo.put("soldPrice", cg.modifiedPrice);
			else if (member == null)
				jo.put("soldPrice", cg.goods.getSellPrice());
			else if (member != null)
				jo.put("soldPrice", String.format(ConstantValue.FORMAT_DOUBLE, cg.goods.getSellPrice() * member.getDiscountRate()));
			if (cg.amount > 0){
				ja_order.put(jo);
				existingOrder = true;
			} else if (cg.amount < 0){
				ja_refund.put(jo);
				existingRefund = true;
			}
		}
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		
		if (member !=null)
			params.put("member", member.getMemberCard());
		else
			params.put("member", "");
		params.put("paidPrice", discountPrice + "");
		PayWay payway = getChoosedPayWay();
		if (payway == null){
			JOptionPane.showMessageDialog(this, "Must choose a way for payment.");
			return;
		} else {
			params.put("payWay", payway.getName());
		}
		Indent indent = null;//use this indent to collect all of the indent.items
		if (existingOrder){
			String url = "indent/makeindent";
			params.put("indents", ja_order.toString());
			String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
			if (response == null){
				logger.error("get null from server for doing pay. URL = " + url);
				JOptionPane.showMessageDialog(this, "get null from server for doing pay. URL = " + url);
				return;
			}
			Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();
			HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
			if (!result.success){
				logger.error("return false while doing pay. URL = " + url + ", response = "+response);
				JOptionPane.showMessageDialog(this, "return false while doing pay. URL = " + url + ", response = "+response);
				return;
			}
			if (indent == null){
				indent = result.data;
			} else {
				indent.getItems().addAll(result.data.getItems());
			}
		}
		if (existingRefund){
			String url = "indent/refundindent";
			params.put("indents", ja_refund.toString());
			params.put("returnToStorage", String.valueOf(true));
			params.put("refundPrice", "");
			String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
			if (response == null){
				logger.error("get null from server for doing refund. URL = " + url);
				JOptionPane.showMessageDialog(this, "get null from server for doing refund. URL = " + url);
				return;
			}
			Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();
			HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
			if (!result.success){
				logger.error("return false while doing refund. URL = " + url + ", response = "+response);
				JOptionPane.showMessageDialog(this, "return false while doing refund. URL = " + url + ", response = "+response);
				return;
			}
			if (indent == null){
				indent = result.data;
			} else {
				indent.getItems().addAll(result.data.getItems());
			}
		}
		
		//print ticket
		if (indent != null)
			doPrintTicket(indent);
		
		//clean table data
		mainFrame.clearTable();
		CheckoutDialog.this.setVisible(false);
//		if (rbPayCash.isSelected()){
//			double getcash = 0;
//			if (tfGetCash.getText() != null && tfGetCash.getText().length() !=0){
//				getcash = Double.parseDouble(tfGetCash.getText());
//			}
//			JOptionPane.showMessageDialog(mainFrame, Messages.getString("CheckoutDialog.GetCash") + tfGetCash.getText()
//			+ "\n" + Messages.getString("CheckoutDialog.ShouldPayAmount") + CommonTools.transferNumberByPM(discountPrice, "")
//			+ "\n" + Messages.getString("CheckoutDialog.Charge") + CommonTools.transferNumberByPM(getcash - discountPrice, ""));
//		}
	}
	
	private void doPrintTicket(Indent indent){
		Map<String,String> keyMap = new HashMap<String, String>();
		if (member != null){
			//reload member data from server
			member = HttpUtil.doLoadMember(CheckoutDialog.this, mainFrame.getOnDutyUser(), member.getMemberCard());
			//store into local memory
			mainFrame.getMapMember().put(member.getMemberCard(), member);
			keyMap.put("member", member.getMemberCard() + ", points: " + CommonTools.transferDouble2Scale(member.getScore()) 
				+ ", discount: " + (member.getDiscountRate() * 100) + "%");
		}else {
			keyMap.put("member", "");
		}
		keyMap.put("cashier", indent.getOperator());
		keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getCreateTime()));
		keyMap.put("totalPrice", CommonTools.transferNumberByPM(indent.getPaidPrice(), ""));
		keyMap.put("gst", CommonTools.transferNumberByPM(indent.getPaidPrice()/11, ""));
		keyMap.put("payWay", indent.getPayWay());
		keyMap.put("orderNo", indent.getIndentCode());
		PaywayPanel paywayPanel = getChoosedPayWayPanel();
		keyMap.put("getcash", paywayPanel.getMoneyAmount()+"");
		keyMap.put("change", CommonTools.transferDouble2Scale(paywayPanel.getChangeAmount()));
		double originPrice = 0;
		List<Map<String, String>> goods = new ArrayList<>();
		for (int i = 0; i< indent.getItems().size(); i++) {
			IndentDetail detail = indent.getItems().get(i);
			Map<String, String> mg = new HashMap<String, String>();
			mg.put("name", detail.getGoodsName());
			mg.put("price", CommonTools.transferNumberByPM(detail.getGoodsPrice(), ""));
			mg.put("amount", detail.getAmount() + "");
			mg.put("subTotal", CommonTools.transferNumberByPM(detail.getSoldPrice() * detail.getAmount(), ""));
			originPrice += detail.getGoodsPrice() * detail.getAmount();
			goods.add(mg);
		}
		keyMap.put("originPrice", CommonTools.transferNumberByPM(originPrice, ""));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keyMap);
		params.put("goods", goods);
		PrintJob job = new PrintJob(ConstantValue.TICKET_TEMPLATE_PURCHASE, params, mainFrame.printerName);
		PrintQueue.add(job);
	}
	
	public JButton getBtnCancelOrder() {
		return btnCancelOrder;
	}

	class DiscountTemplateRadioButton extends JRadioButton{
		private DiscountTemplate temp;
		public DiscountTemplateRadioButton (boolean selected, DiscountTemplate temp) {
	        super(temp.getName(), selected);
	        this.temp = temp;
	    }
		public DiscountTemplate getDiscountTemplate() {
			return temp;
		}
		public void setDiscountTemplate(DiscountTemplate temp) {
			this.temp = temp;
		}
	}
	
	class PaywayPanel extends JPanel{
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
}
