package com.shuishou.salemgr.beans;

public class IndentDetail {

	private int id;
	
	private Indent indent;
	
	private int goodsId;
	
	private int amount;
	
	private double goodsPrice;//单个goods价格, 不考虑amount
	
	private String goodsName;
	
	private double soldPrice;
	
	public Indent getIndent() {
		return indent;
	}

	public void setIndent(Indent indent) {
		this.indent = indent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}


	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public double getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(double goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public double getSoldPrice() {
		return soldPrice;
	}

	public void setSoldPrice(double soldPrice) {
		this.soldPrice = soldPrice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndentDetail other = (IndentDetail) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IndentDetail [goodsName=" + goodsName + ", amount=" + amount + "]";
	}
}
