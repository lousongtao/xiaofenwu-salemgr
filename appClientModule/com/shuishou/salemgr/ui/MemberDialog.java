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
import com.shuishou.salemgr.beans.HttpResult;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.http.HttpUtil;
import com.shuishou.salemgr.ui.components.JDatePicker;
import com.shuishou.salemgr.ui.components.NumberTextField;

public class MemberDialog extends JDialog{

	private final Logger logger = Logger.getLogger(MemberDialog.class.getName());
	private JTextField tfName= new JTextField(155);
	private JTextField tfMemberCard= new JTextField(155);
	private JTextField tfTelephone= new JTextField(155);
	private JTextField tfAddress= new JTextField(155);
	private NumberTextField tfPostcode= new NumberTextField(this, false, false);
	private NumberTextField tfDiscountRate= new NumberTextField(this, true, false);
	private JButton btnSave = new JButton("Save");
	private JButton btnCancel = new JButton("Cancel");
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
		JLabel lbName = new JLabel("Name");
		JLabel lbMemberCard = new JLabel("Member Card");
		JLabel lbTelephone = new JLabel("Telephone");
		JLabel lbAddress = new JLabel("Address");
		JLabel lbPostcode = new JLabel("Postcode");
		JLabel lbBirthday = new JLabel("Birthday");
		JLabel lbDiscountRate = new JLabel("Discount Rate");
		JPanel pProperty = new JPanel(new GridBagLayout());
		pProperty.setLayout(new GridBagLayout());
		pProperty.add(lbName, 		new GridBagConstraints(0, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfName, 		new GridBagConstraints(1, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbMemberCard, 	new GridBagConstraints(0, 1, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfMemberCard, 	new GridBagConstraints(1, 1, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbTelephone, 	new GridBagConstraints(0, 2, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfTelephone, 	new GridBagConstraints(1, 2, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbAddress, 		new GridBagConstraints(0, 3, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfAddress, 		new GridBagConstraints(1, 3, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbPostcode, 	new GridBagConstraints(0, 4, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfPostcode, 	new GridBagConstraints(1, 4, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbBirthday, 	new GridBagConstraints(0, 5, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(dpBirthday, 	new GridBagConstraints(1, 5, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pProperty.add(lbDiscountRate, new GridBagConstraints(0, 6, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pProperty.add(tfDiscountRate, new GridBagConstraints(1, 6, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
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
		tfDiscountRate.setText("1.00");
		
		this.setSize(new Dimension(350, 500));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	private boolean  doSave() {
		if (!doCheckInput())
			return false;
		
		Map<String, String> params = new HashMap<>();
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
		
		String url = "member/addmember";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "utf-8");
		if (response == null){
			logger.error("get null from server for add/update member. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for add/update member. URL = " + url);
			return false;
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();
		HttpResult<Member> result = gson.fromJson(response, new TypeToken<HttpResult<Member>>(){}.getType());
		if (!result.success){
			logger.error("return false while add/update member. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, "return false while add/update member. URL = " + url + ", response = "+response);
			return false;
		}
		member = result.data;
		return true;
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