package com.shuishou.salemgr.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Indent {

private int id;
	
	private Date createTime;
	
	private List<IndentDetail> items;
	
	private double totalPrice;
	
	private double paidPrice;//实际付款金额
	
	private String payWay;//付款方式
	
	private String memberCard;
	
	private int indentType;
	
	private String indentCode;
	
	private String operator;//操作人
	
	private double adjustPrice;
	
	
	public double getAdjustPrice() {
		return adjustPrice;
	}

	public void setAdjustPrice(double adjustPrice) {
		this.adjustPrice = adjustPrice;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getIndentCode() {
		return indentCode;
	}

	public void setIndentCode(String indentCode) {
		this.indentCode = indentCode;
	}

	public int getIndentType() {
		return indentType;
	}

	public void setIndentType(int indentType) {
		this.indentType = indentType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public String getMemberCard() {
		return memberCard;
	}

	public void setMemberCard(String memberCard) {
		this.memberCard = memberCard;
	}


	public double getPaidPrice() {
		return paidPrice;
	}

	public String getFormatPaidPrice(){
		return String.format("%.2f", paidPrice);
	}
	
	public void setPaidPrice(double paidPrice) {
		this.paidPrice = paidPrice;
	}

	public List<IndentDetail> getItems() {
		return items;
	}

	public void setItems(List<IndentDetail> items) {
		this.items = items;
	}
	
	public void addItem(IndentDetail detail){
		if (items == null)
			items = new ArrayList<IndentDetail>();
		items.add(detail);
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public String getFormatTotalPrice(){
		return String.format("%.2f", totalPrice);
	}
	
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	
	@Override
	public String toString() {
		return "Order [totalPrice=" + totalPrice + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Indent other = (Indent) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
