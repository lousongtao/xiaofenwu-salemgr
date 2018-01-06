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

import com.shuishou.salemgr.ConstantValue;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.DiscountTemplate;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.beans.PayWay;
import com.shuishou.salemgr.http.HttpUtil;
import com.shuishou.salemgr.ui.components.JBlockedButton;
import com.shuishou.salemgr.ui.components.NumberTextField;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

public class RefundDialog extends JDialog{
	private final Logger logger = Logger.getLogger(RefundDialog.class.getName());
	private MainFrame mainFrame;
	
	private JRadioButton rbReturnStorage = new JRadioButton(Messages.getString("RefundDialog.ReturnStorage"), true);
	private JRadioButton rbNotReturnStorage = new JRadioButton(Messages.getString("RefundDialog.NotReturnStorage"));
	private JTextField tfMember = new JTextField();
	private NumberTextField tfRefundPrice = new NumberTextField(this, true, false);
	private JButton btnRefund = new JButton(Messages.getString("RefundDialog.Refund")); //$NON-NLS-1$
	private JButton btnClose = new JButton(Messages.getString("CloseDialog")); //$NON-NLS-1$
	private JLabel lbMemberInfo = new JLabel();
	private double refundPrice = 0;
	private double sellPrice = 0;
	private ArrayList<ChoosedGoods> choosedGoods;
	
	public RefundDialog(MainFrame mainFrame,String title, boolean modal, ArrayList<ChoosedGoods> choosedGoods){
		super(mainFrame, title, modal);
		this.mainFrame = mainFrame;
		this.choosedGoods = choosedGoods;
		for(ChoosedGoods cg : choosedGoods){
			sellPrice += cg.amount * cg.goods.getSellPrice(); 
		}
		refundPrice = sellPrice;
		initUI();
	}
	
	private void initUI(){
		tfRefundPrice.setText(new DecimalFormat("0.00").format(refundPrice)); //$NON-NLS-1$
		JLabel lbMember = new JLabel(Messages.getString("RefundDialog.MemberCard"));
		JLabel lbPrice = new JLabel();
		JLabel lbRefundPrice = new JLabel(Messages.getString("RefundDialog.RefundPrice"));
		lbMemberInfo.setBorder(BorderFactory.createTitledBorder(Messages.getString("RefundDialog.MemberInfo")));
		
		JPanel pButton = new JPanel(new GridBagLayout());
		btnRefund.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		pButton.add(btnRefund,		new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		pButton.add(btnClose,		new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		Dimension dPButton = pButton.getPreferredSize();
		dPButton.height = 60;
		pButton.setPreferredSize(dPButton);
		
		lbMember.setFont(ConstantValue.FONT_25BOLD);
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
		c.add(lbMember, 		new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		c.add(tfMember, 		new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
		c.add(lbMemberInfo,		new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pReturnStorage,	new GridBagConstraints(0, 3, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(lbRefundPrice,	new GridBagConstraints(0, 4, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		c.add(tfRefundPrice,	new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pButton, 			new GridBagConstraints(0, 5, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		this.setSize(new Dimension(450, 400));
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
		
		tfMember.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.getID() != KeyEvent.KEY_PRESSED)
					return;
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					lbMemberInfo.setText("");
					if (tfMember.getText() == null || tfMember.getText().length() == 0)
						return;
					Member m = mainFrame.getMember(tfMember.getText());
					if (m == null){
						JOptionPane.showMessageDialog(RefundDialog.this, Messages.getString("RefundDialog.NofindMember") + tfMember.getText());
						return;
					}
					lbMemberInfo.setText(Messages.getString("RefundDialog.MemberInfo.Name")+ m.getName() + ", " 
							+ Messages.getString("RefundDialog.MemberInfo.DiscountRate") + m.getDiscountRate() + ", "
							+ Messages.getString("RefundDialog.MemberInfo.Score") + m.getScore() + ", "
							+ Messages.getString("RefundDialog.MemberInfo.Balance") + m.getBalanceMoney());
					refundPrice = sellPrice * m.getDiscountRate();
					tfRefundPrice.setText(new DecimalFormat("0.00").format(refundPrice)); //$NON-NLS-1$
				}
			}
		});
	}
	
	
	public void doRefund(){
		JSONArray ja = new JSONArray();
		for (int i = 0; i< choosedGoods.size(); i++) {
			JSONObject jo = new JSONObject();
			ChoosedGoods cg = choosedGoods.get(i);
			jo.put("id", cg.goods.getId());
			jo.put("amount", cg.amount);
			ja.put(jo);
		}
		String url = "indent/refundindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		params.put("indents", ja.toString());
		params.put("member", tfMember.getText());
		params.put("returnToStorage", String.valueOf(rbReturnStorage.isSelected()));
		params.put("refundPrice", refundPrice + "");

		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		JSONObject jsonObj = new JSONObject(response);
		if (!jsonObj.getBoolean("success")){
			logger.error("Do checkout failed. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(mainFrame, Messages.getString("RefundDialog.FailRefundMsg") + jsonObj.getString("result")); //$NON-NLS-1$
		}
		//clean table
		mainFrame.clearTable();
		RefundDialog.this.setVisible(false);
	}
	
	
}
