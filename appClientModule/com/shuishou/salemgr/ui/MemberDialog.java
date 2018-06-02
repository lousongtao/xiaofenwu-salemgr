package com.shuishou.salemgr.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.salemgr.ConstantValue;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.HttpResult;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.http.HttpUtil;
import com.shuishou.salemgr.ui.components.CommonDialog;
import com.shuishou.salemgr.ui.components.JDatePicker;
import com.shuishou.salemgr.ui.components.NumberTextField;
import com.shuishou.salemgr.ui.components.WaitDialog;

public class MemberDialog extends CommonDialog{

	private final Logger logger = Logger.getLogger(MemberDialog.class.getName());
	private JTextField tfName= new JTextField(155);
	private JTextField tfMemberCard= new JTextField(155);
	private JTextField tfTelephone= new JTextField(155);
	private JTextField tfAddress= new JTextField(155);
	private NumberTextField tfPostcode= new NumberTextField(this, false, false);
	private NumberTextField tfDiscountRate= new NumberTextField(this, true, false);
	private JButton btnSave = new JButton("Save");
	private JButton btnCancel = new JButton("Cancel");
	private JButton btnGenerateCard = new JButton("...");	
	private JDatePicker dpBirthday = new JDatePicker();
	private MainFrame mainFrame;
	private Member member;
	
	public MemberDialog(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		this.setModal(true);
		this.setTitle("Add Member");
		initUI();
	}
	
	private void initUI(){
		JLabel lbName = new JLabel(Messages.getString("MemberDialog.Name"));
		JLabel lbMemberCard = new JLabel(Messages.getString("MemberDialog.MemberCard"));
		JLabel lbTelephone = new JLabel(Messages.getString("MemberDialog.Telephone"));
		JLabel lbAddress = new JLabel(Messages.getString("MemberDialog.Address"));
		JLabel lbPostcode = new JLabel(Messages.getString("MemberDialog.Postcode"));
		JLabel lbBirthday = new JLabel(Messages.getString("MemberDialog.Birthday"));
		JLabel lbDiscountRate = new JLabel(Messages.getString("MemberDialog.DiscountRate"));
		JPanel pProperty = new JPanel(new GridBagLayout());
		pProperty.setLayout(new GridBagLayout());
		pProperty.add(lbName, 			new GridBagConstraints(0, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfName, 			new GridBagConstraints(1, 0, 2, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbMemberCard, 	new GridBagConstraints(0, 1, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfMemberCard, 	new GridBagConstraints(1, 1, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(btnGenerateCard, 	new GridBagConstraints(2, 1, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbTelephone, 		new GridBagConstraints(0, 2, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfTelephone, 		new GridBagConstraints(1, 2, 2, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbAddress, 		new GridBagConstraints(0, 3, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfAddress, 		new GridBagConstraints(1, 3, 2, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbPostcode, 		new GridBagConstraints(0, 4, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfPostcode, 		new GridBagConstraints(1, 4, 2, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbBirthday, 		new GridBagConstraints(0, 5, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(dpBirthday, 		new GridBagConstraints(1, 5, 2, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbDiscountRate, 	new GridBagConstraints(0, 6, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfDiscountRate, 	new GridBagConstraints(1, 6, 2, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		tfName.setMinimumSize(new Dimension(180,25));
		
		JPanel pButton = new JPanel();
		pButton.add(btnSave);
		pButton.add(btnCancel);
		this.setLayout(new BorderLayout());
		this.add(pProperty, BorderLayout.CENTER);
		this.add(pButton, BorderLayout.SOUTH);
		
		btnSave.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (doSave()){
					MemberDialog.this.setVisible(false);
				}
			}});
		
		btnCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MemberDialog.this.setVisible(false);
			}});
		btnGenerateCard.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				generateMemberCard();
			}
			
		});
		tfDiscountRate.setText("1.00");
		
		this.setSize(new Dimension(350, 500));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	/**
	 * 根据小粉屋的需求, 从160000开始向上编号, 找到一个空余的编号, 作为新会员的会员卡.
	 * 先遍历本地数据, 查找到一个空闲的编号, 再用此编号去服务端查找, 看是否已经存在该编号的会员(因为存在分店已占用该编号的可能)
	 */
	private void generateMemberCard(){
		int start = 160000;
		for (int i = 0; i < 100000; i++) {
			int temp = start + i;
			Member m = mainFrame.getMapMember().get(String.valueOf(temp));
			if (m == null){
				//do double check to server
				m = HttpUtil.doLoadMember(mainFrame, mainFrame.getOnDutyUser(), String.valueOf(temp));
				if (m == null){
					tfMemberCard.setText(String.valueOf(temp));
					break;
				}
			}
		}
	}
	
	private boolean  doSave() {
		if (!doCheckInput())
			return false;
		
		final Map<String, String> params = new HashMap<>();
		params.put("userId", mainFrame.getOnDutyUser().getId()+"");
		params.put("name", tfName.getText());
		params.put("memberCard", tfMemberCard.getText());
		params.put("discountRate", tfDiscountRate.getText());
		if (tfTelephone.getText() != null && tfTelephone.getText().length() > 0){
			params.put("telephone", tfTelephone.getText());
		}
		if (tfAddress.getText() != null && tfAddress.getText().length() > 0){
			params.put("address", tfAddress.getText());
		}
		if (tfPostcode.getText() != null && tfPostcode.getText().length() > 0){
			params.put("postCode", tfPostcode.getText());
		}
		if (dpBirthday.getModel() != null && dpBirthday.getModel().getValue() != null){
			Calendar c = (Calendar)dpBirthday.getModel().getValue();
			params.put("birth", ConstantValue.DFYMDHMS.format(c.getTime()));
		}
		
		final String url = "member/addmember";
		WaitDialog wdlg = new WaitDialog(this, "Posting data..."){
			public Object work() {
				return HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "utf-8");
			}
		};
		String response = (String)wdlg.getReturnResult();
		
		if (response == null){
			logger.error("get null from server for add/update member. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for add/update member. URL = " + url);
			return false;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<Member> result = gson.fromJson(response, new TypeToken<HttpResult<Member>>(){}.getType());
		if (!result.success){
			logger.error("return false while add/update member. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return false;
		}
		member = result.data;
		return true;
	}
	
	public void doEnterClick(){
		doSave();
	}
	

	public Member getMember(){
		return member;
	}
	private boolean doCheckInput(){
		if (tfName.getText() == null || tfName.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Name");
			return false;
		}
		if (tfMemberCard.getText() == null || tfMemberCard.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Member card");
			return false;
		}
		if (tfDiscountRate.getText() == null || tfDiscountRate.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Discount Rate");
			return false;
		}
		return true;
	}
}
