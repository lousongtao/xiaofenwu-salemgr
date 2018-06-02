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
import com.shuishou.salemgr.beans.HttpResult;
import com.shuishou.salemgr.beans.Indent;
import com.shuishou.salemgr.beans.IndentDetail;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.beans.PayWay;
import com.shuishou.salemgr.http.HttpUtil;
import com.shuishou.salemgr.printertool.PrintJob;
import com.shuishou.salemgr.printertool.PrintQueue;
import com.shuishou.salemgr.ui.CheckoutDialog.DiscountTemplateRadioButton;
import com.shuishou.salemgr.ui.components.CommonDialog;
import com.shuishou.salemgr.ui.components.JBlockedButton;
import com.shuishou.salemgr.ui.components.NumberTextField;
import com.shuishou.salemgr.ui.components.WaitDialog;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

public class PreOrderCheckoutDialog extends CommonDialog{
	private final Logger logger = Logger.getLogger(PreOrderCheckoutDialog.class.getName());
	private MainFrame mainFrame;
	
	private JLabel lbDiscountPrice = new JLabel();
	private JRadioButton rbDiscountNon = new JRadioButton(Messages.getString("CheckoutDialog.NoDiscount"), true); //$NON-NLS-1$
	private JRadioButton rbDiscountTemp = new JRadioButton(Messages.getString("CheckoutDialog.TempDiscount"), false); //$NON-NLS-1$
	private JRadioButton rbDiscountDirect = new JRadioButton(Messages.getString("CheckoutDialog.DirectDiscount"), false); //$NON-NLS-1$
	private ArrayList<JRadioButton> listRBOtherPayway = new ArrayList<>();
	private ArrayList<PaywayPanel> listPaywayPanel = new ArrayList<>();
	private NumberTextField tfDiscountAmount = null;
	private JBlockedButton btnPay = new JBlockedButton(Messages.getString("CheckoutDialog.PayButton"), "/resource/checkout.png"); //$NON-NLS-1$
	private JButton btnClose = new JButton(Messages.getString("CloseDialog")); //$NON-NLS-1$
	private JButton btnUnpay = new JButton(Messages.getString("PreOrderCheckoutDialog.UnpayButton")); //$NON-NLS-1$
	private NumberTextField tfGetCash;
	private JLabel lbChange;
	private JLabel lbMemberInfo = new JLabel();
	private double discountPrice = 0;
	private double sellPrice = 0;
	private ArrayList<ChoosedGoods> choosedGoods;
	private Member member;
	
	private List<DiscountTemplateRadioButton> discountTempRadioButtonList = new ArrayList<DiscountTemplateRadioButton>();
	
	public PreOrderCheckoutDialog(MainFrame mainFrame,String title, boolean modal, ArrayList<ChoosedGoods> choosedGoods, Member m){
		super(mainFrame, title, modal);
		this.mainFrame = mainFrame;
		this.member = m;
		this.choosedGoods = choosedGoods;
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
		JLabel lbPrice = new JLabel();
		lbMemberInfo.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.MemberInfo")));
		if (member != null){
			lbMemberInfo.setText(Messages.getString("CheckoutDialog.MemberInfo.Name")+ member.getName() + ", " 
				+ Messages.getString("CheckoutDialog.MemberInfo.DiscountRate") + member.getDiscountRate() + ", "
				+ Messages.getString("CheckoutDialog.MemberInfo.Score") + CommonTools.transferDouble2Scale(member.getScore()) + ", "
				+ Messages.getString("CheckoutDialog.MemberInfo.Balance") + CommonTools.transferDouble2Scale(member.getBalanceMoney()));
		}
		tfDiscountAmount = new NumberTextField(this, true, false);
		
		tfGetCash = new NumberTextField(this, true, false);
		JLabel lbGetCash = new JLabel(Messages.getString("CheckoutDialog.GetCash"));
		lbChange = new JLabel();
		
		JPanel pPayway = new JPanel(new GridBagLayout());
		pPayway.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.PayWay"))); //$NON-NLS-1$
		ButtonGroup bgPayway = new ButtonGroup();
		if (!mainFrame.getPaywayList().isEmpty()){
			for (int i = 0; i < mainFrame.getPaywayList().size(); i++) {
				PayWay pw = mainFrame.getPaywayList().get(i);
				PaywayPanel pp = new PaywayPanel(this, pw, sellPrice);
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
		
		Dimension dDiscountPrice = tfDiscountAmount.getPreferredSize();
		dDiscountPrice.width = 150;
		tfDiscountAmount.setPreferredSize(dDiscountPrice);
		
		JPanel pDiscount = new JPanel(new GridBagLayout());
//		pDiscount.setBackground(bgDiscountColor);
		pDiscount.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.BorderDiscount"))); //$NON-NLS-1$
		ButtonGroup bgDiscount = new ButtonGroup();
		bgDiscount.add(rbDiscountNon);
		bgDiscount.add(rbDiscountTemp);
		bgDiscount.add(rbDiscountDirect);
		pDiscount.add(rbDiscountNon, 	new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pDiscount.add(rbDiscountDirect, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 50, 0, 0), 0, 0));
		pDiscount.add(tfDiscountAmount, new GridBagConstraints(2, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
		pDiscount.add(rbDiscountTemp, 	new GridBagConstraints(3, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 50, 0, 0), 0, 0));
		pDiscount.add(pDiscountTemplate,new GridBagConstraints(0, 2, 4, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		
		JPanel pButton = new JPanel(new GridBagLayout());
		btnPay.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnUnpay.setPreferredSize(new Dimension(150, 50));
		pButton.add(btnPay,			new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		pButton.add(btnClose,		new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		pButton.add(btnUnpay,		new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		Dimension dPButton = pButton.getPreferredSize();
		dPButton.height = 60;
		pButton.setPreferredSize(dPButton);
		
		lbPrice.setFont(ConstantValue.FONT_25BOLD);
		lbDiscountPrice.setFont(ConstantValue.FONT_25BOLD);
		lbPrice.setText(Messages.getString("CheckoutDialog.Price") + CommonTools.transferNumberByPM(sellPrice, "")); //$NON-NLS-1$
		lbDiscountPrice.setText(Messages.getString("CheckoutDialog.DiscountPrice") + CommonTools.transferNumberByPM(discountPrice, "")); //$NON-NLS-1$
		
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbPrice, 			new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//		c.add(tfMember, 		new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
		c.add(lbMemberInfo,		new GridBagConstraints(0, 2, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pPayway, 			new GridBagConstraints(0, 3, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pDiscount, 		new GridBagConstraints(0, 4, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(lbDiscountPrice, 	new GridBagConstraints(0, 5, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pButton, 			new GridBagConstraints(0, 6, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		this.setSize(new Dimension(750, 700));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				PreOrderCheckoutDialog.this.setVisible(false);
			}});
		
		btnPay.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doMakePreOrder(true);
			}});
		
		btnUnpay.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doMakePreOrder(false);
			}});
		
		tfDiscountAmount.getDocument().addDocumentListener(new DocumentListener(){

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
			if (rbTemplate.getDiscountTemplate().getType() == ConstantValue.DISCOUNTTYPE_RATE)
				discountPrice = sellPrice * rbTemplate.getDiscountTemplate().getValue();
			else if (rbTemplate.getDiscountTemplate().getType() == ConstantValue.DISCOUNTTYPE_QUANTITY)
				discountPrice = sellPrice + rbTemplate.getDiscountTemplate().getValue();
		} else if (rbDiscountDirect.isSelected()) {
			double dp = 0;
			try{
				dp = Double.parseDouble(tfDiscountAmount.getText());
			}catch(Exception e){}
			discountPrice = sellPrice - dp;
		}
		
		lbDiscountPrice.setText(Messages.getString("CheckoutDialog.DiscountPrice") + new DecimalFormat("0.00").format(discountPrice)); //$NON-NLS-1$
	}
	
	private PayWay getChoosedPayWay(){
		for(PaywayPanel pp : listPaywayPanel){
			if (pp.getRadioButton().isSelected())
				return pp.getPayway();
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
	
	public void doMakePreOrder(boolean paid){
		JSONArray ja = new JSONArray();
		for (int i = 0; i< choosedGoods.size(); i++) {
			JSONObject jo = new JSONObject();
			ChoosedGoods cg = choosedGoods.get(i);
			jo.put("id", cg.goods.getId());
			jo.put("amount", cg.amount);
			if (cg.modifiedPrice >= 0){
				jo.put("soldPrice", cg.modifiedPrice);
			} else if (member == null)
				jo.put("soldPrice", cg.goods.getSellPrice());
			else 
				jo.put("soldPrice", String.format(ConstantValue.FORMAT_DOUBLE, cg.goods.getSellPrice() * member.getDiscountRate()));
			ja.put(jo);
		}
		final String url = "indent/prebuyindent";
		final Map<String, String> params = new HashMap<>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		params.put("indents", ja.toString());
		if (member == null){
			params.put("member", "");
		} else {
			params.put("member", member.getMemberCard());
		}
		params.put("paidPrice", discountPrice + "");
		PayWay payway = getChoosedPayWay();
		if (payway == null){
			JOptionPane.showMessageDialog(this, "Must choose a way for payment.");
			return;
		} else {
			params.put("payWay", payway.getName());
		}
		if (!paid)
			params.put("payWay", "");
		if (rbDiscountNon.isSelected()){
			params.put("discountTemplate", "");
		} else if (rbDiscountDirect.isSelected()){
			params.put("discountTemplate", tfDiscountAmount.getText());
		} else {
			DiscountTemplateRadioButton rbTemplate = getSelectedDiscountTemplateRadioButton();
			if (rbTemplate == null){
				discountTempRadioButtonList.get(0).setSelected(true);
				rbTemplate = discountTempRadioButtonList.get(0);
			}
			params.put("discountTemplate", String.valueOf(rbTemplate.getDiscountTemplate().getName()));
		}
		params.put("paid", String.valueOf(paid));
		WaitDialog wdlg = new WaitDialog(this, "Posting data..."){
			public Object work() {
				return HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
			}
		};
		String response = (String)wdlg.getReturnResult();
		if (response == null){
			logger.error("get null from server for doing pay. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for doing pay. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
		if (!result.success){
			logger.error("return false while doing pay. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		//print ticket
		doPrintTicket(paid, result.data);
		//clean table data
		mainFrame.clearTable();
		PreOrderCheckoutDialog.this.setVisible(false);
		mainFrame.doOpenCashdrawer(false);
	}
	
	public void doEnterClick(){
		doMakePreOrder(true);
	}
	
	private void doPrintTicket(boolean paid, Indent indent){
		Map<String,String> keyMap = new HashMap<String, String>();
		if (member != null){
			//reload member data from server
			member = HttpUtil.doLoadMember(PreOrderCheckoutDialog.this, mainFrame.getOnDutyUser(), member.getMemberCard());
			//store into local memory
			mainFrame.getMapMember().put(member.getMemberCard(), member);
			keyMap.put("member", member.getMemberCard() + ", "+ member.getName() + String.format(ConstantValue.FORMAT_DOUBLE, member.getScore()) 
					+ ", " + (member.getDiscountRate() * 100) + "%");
		}else {
			keyMap.put("member", "");
			keyMap.put("discount", "");
		}
		keyMap.put("cashier", indent.getOperator());
		keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getCreateTime()));
		keyMap.put("totalPrice", CommonTools.transferNumberByPM(indent.getPaidPrice(), ""));
		keyMap.put("gst", CommonTools.transferNumberByPM(indent.getPaidPrice()/11, ""));
		keyMap.put("orderNo", indent.getIndentCode());
		keyMap.put("adjustPrice", "");
		keyMap.put("discountTemp", "");
		if (paid){
			keyMap.put("payWay", indent.getPayWay());
		} else {
			keyMap.put("payWay", "Unpaid");
		}
		PaywayPanel paywayPanel = getChoosedPayWayPanel();
		String symbol = paywayPanel.getPayway().getSymbol().replace("$", "\\$");
		keyMap.put("paid", symbol + paywayPanel.getPaidMoney());
		if (paywayPanel.getChange() <= 0)
			keyMap.put("change", "");
		else 
			keyMap.put("change", symbol + CommonTools.transferDouble2Scale(paywayPanel.getChange()));
		List<Map<String, String>> goods = new ArrayList<>();
		for (int i = 0; i< indent.getItems().size(); i++) {
			IndentDetail detail = indent.getItems().get(i);
			Map<String, String> mg = new HashMap<String, String>();
			mg.put("name", detail.getGoodsName());
			mg.put("price", CommonTools.transferNumberByPM(detail.getGoodsPrice(), ""));
			mg.put("amount", detail.getAmount() + "");
			mg.put("subTotal", CommonTools.transferNumberByPM(detail.getSoldPrice() * detail.getAmount(), ""));
			goods.add(mg);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keyMap);
		params.put("goods", goods);
		PrintJob job = new PrintJob(ConstantValue.TICKET_TEMPLATE_PREBUY, params, mainFrame.printerName);
		PrintQueue.add(job);
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
}
