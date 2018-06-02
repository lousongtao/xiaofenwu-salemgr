package com.shuishou.salemgr.ui;

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
import com.shuishou.salemgr.ui.components.CommonDialog;
import com.shuishou.salemgr.ui.components.JBlockedButton;
import com.shuishou.salemgr.ui.components.NumberTextField;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

public class RefundDialog extends CommonDialog{
	private final Logger logger = Logger.getLogger(RefundDialog.class.getName());
	private MainFrame mainFrame;
	
	private JRadioButton rbReturnStorage = new JRadioButton(Messages.getString("RefundDialog.ReturnStorage"), true);
	private JRadioButton rbNotReturnStorage = new JRadioButton(Messages.getString("RefundDialog.NotReturnStorage"));
	private NumberTextField tfRefundPrice = new NumberTextField(this, true, false);
	private JButton btnRefund = new JButton(Messages.getString("RefundDialog.Refund")); //$NON-NLS-1$
	private JButton btnClose = new JButton(Messages.getString("CloseDialog")); //$NON-NLS-1$
	private JLabel lbMemberInfo = new JLabel();
	private double refundPrice = 0;
	private double sellPrice = 0;
	private ArrayList<ChoosedGoods> choosedGoods;
	private Member member;
	
	public RefundDialog(MainFrame mainFrame,String title, boolean modal, ArrayList<ChoosedGoods> choosedGoods, Member m){
		super(mainFrame, title, modal);
		this.mainFrame = mainFrame;
		this.member = m;
		this.choosedGoods = choosedGoods;
		for(ChoosedGoods cg : choosedGoods){
			sellPrice += cg.amount * cg.goods.getSellPrice(); 
		}
		if (member != null)
			sellPrice *= member.getDiscountRate();
		refundPrice = sellPrice;
		initUI();
	}
	
	private void initUI(){
		tfRefundPrice.setText(new DecimalFormat("0.00").format(refundPrice)); //$NON-NLS-1$
		JLabel lbPrice = new JLabel();
		JLabel lbRefundPrice = new JLabel(Messages.getString("RefundDialog.RefundPrice"));
		lbMemberInfo.setBorder(BorderFactory.createTitledBorder(Messages.getString("RefundDialog.MemberInfo")));
		if (member != null){
			lbMemberInfo.setText(Messages.getString("CheckoutDialog.MemberInfo.Name")+ member.getName() + ", " 
				+ Messages.getString("CheckoutDialog.MemberInfo.DiscountRate") + member.getDiscountRate() + ", "
				+ Messages.getString("CheckoutDialog.MemberInfo.Score") + CommonTools.transferDouble2Scale(member.getScore()) + ", "
				+ Messages.getString("CheckoutDialog.MemberInfo.Balance") + CommonTools.transferDouble2Scale(member.getBalanceMoney()));
		}
		
		JPanel pButton = new JPanel(new GridBagLayout());
		btnRefund.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		pButton.add(btnRefund,		new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		pButton.add(btnClose,		new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		Dimension dPButton = pButton.getPreferredSize();
		dPButton.height = 60;
		pButton.setPreferredSize(dPButton);
		
		lbPrice.setFont(ConstantValue.FONT_25BOLD);
		lbPrice.setText(Messages.getString("RefundDialog.Price") + sellPrice); //$NON-NLS-1$
		
		ButtonGroup bgReturnStorage = new ButtonGroup();
		bgReturnStorage.add(rbNotReturnStorage);
		bgReturnStorage.add(rbReturnStorage);
		JPanel pReturnStorage = new JPanel();
		pReturnStorage.add(rbReturnStorage);
		pReturnStorage.add(rbNotReturnStorage);
		pReturnStorage.setBorder(BorderFactory.createTitledBorder(Messages.getString("RefundDialog.ReturnStorage")));
		
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbPrice, 			new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		c.add(lbMemberInfo,		new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pReturnStorage,	new GridBagConstraints(0, 3, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(lbRefundPrice,	new GridBagConstraints(0, 4, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		c.add(tfRefundPrice,	new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pButton, 			new GridBagConstraints(0, 5, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		this.setSize(new Dimension(550, 500));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				RefundDialog.this.setVisible(false);
			}});
		
		btnRefund.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doRefund();
			}});
		
	}
	
	public void doEnterClick(){
		doRefund();
	}
	
	public void doRefund(){
		JSONArray ja = new JSONArray();
		for (int i = 0; i< choosedGoods.size(); i++) {
			JSONObject jo = new JSONObject();
			ChoosedGoods cg = choosedGoods.get(i);
			jo.put("id", cg.goods.getId());
			jo.put("amount", cg.amount);
			if (member == null)
				jo.put("soldPrice", cg.goods.getSellPrice());
			else 
				jo.put("soldPrice", CommonTools.transferDouble2Scale(cg.goods.getSellPrice() * member.getDiscountRate()));
			ja.put(jo);
		}
		String url = "indent/refundindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		params.put("indents", ja.toString());
		if (member != null){
			params.put("member", member.getMemberCard());
		}else {
			params.put("member", "");
		}
		params.put("returnToStorage", String.valueOf(rbReturnStorage.isSelected()));
		params.put("refundPrice", refundPrice + "");

		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for doing refund. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for doing refund. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
		if (!result.success){
			logger.error("return false while doing refund. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		doPrintTicket(result.data);
		//clean table
		mainFrame.clearTable();
		RefundDialog.this.setVisible(false);
	}
	
	private void doPrintTicket(Indent indent){
		Map<String,String> keyMap = new HashMap<String, String>();
		if (member != null){
			//reload member data from server
			member = HttpUtil.doLoadMember(RefundDialog.this, mainFrame.getOnDutyUser(), member.getMemberCard());
			//store into local memory
			mainFrame.getMapMember().put(member.getMemberCard(), member);
			keyMap.put("member", member.getMemberCard() + ", "+ member.getName() + String.format(ConstantValue.FORMAT_DOUBLE, member.getScore()) 
			+ ", " + (member.getDiscountRate() * 100) + "%");
		}else {
			keyMap.put("member", "");
		}
		keyMap.put("cashier", indent.getOperator());
		keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getCreateTime()));
		keyMap.put("totalPrice", CommonTools.transferNumberByPM(indent.getPaidPrice(), ""));
		keyMap.put("gst", CommonTools.transferNumberByPM(indent.getPaidPrice()/11, ""));
		keyMap.put("orderNo", indent.getIndentCode());
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
		PrintJob job = new PrintJob(ConstantValue.TICKET_TEMPLATE_REFUND, params, mainFrame.printerName);
		PrintQueue.add(job);
	}
}
