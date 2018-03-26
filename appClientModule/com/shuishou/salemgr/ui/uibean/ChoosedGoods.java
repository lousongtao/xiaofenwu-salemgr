package com.shuishou.salemgr.ui.uibean;

import com.shuishou.salemgr.beans.Goods;

public class ChoosedGoods {

	/**
	 * 判断该项是否已经在促销组合中, 默认值false
	 */
	public boolean inPromotion = false;
	public Goods goods;
	public int amount;
	/**
	 * 用户可以手动更改商品售价
	 * 该值默认-1, 如果收银时该值小于0, 不考虑该值, 否则以该值为第一优先选项
	 */
	public double modifiedPrice = -1;
}
