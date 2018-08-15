package com.shuishou.salemgr.ui.uibean;

import com.shuishou.salemgr.beans.Goods;
import com.shuishou.salemgr.beans.Promotion;

public class ChoosedGoods {

	/**
	 * 判断该项是否已经在促销组合中
	 */
	public Promotion promotion;
	public Goods goods;
	public int amount;
	/**
	 * 用户可以手动更改商品售价
	 * 该值默认-1, 如果收银时该值小于0, 不考虑该值, 否则以该值为第一优先选项
	 */
	public double modifiedPrice = -1;
	
	public ChoosedGoods(){
		
	}
	
	public ChoosedGoods(int amount, Goods goods){
		this.amount = amount;
		this.goods = goods;
	}
	
	public ChoosedGoods(int amount, Goods goods, Promotion promotion){
		this.amount = amount;
		this.goods = goods;
		this.promotion = promotion;
	}
	
	public ChoosedGoods(int amount, Goods goods, Promotion promotion, double modifiedPrice){
		this.amount = amount;
		this.goods = goods;
		this.promotion = promotion;
		this.modifiedPrice = modifiedPrice;
	}
}
