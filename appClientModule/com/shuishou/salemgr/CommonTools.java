package com.shuishou.salemgr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.ui.components.CommonDialog;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

public class CommonTools {

	public static String transferDouble2Scale(double d){
		return String.format(ConstantValue.FORMAT_DOUBLE, d);
	}
	
	/**
	 * if d is positive, return d with 2 decimal
	 * if d is negative, return -d with 2 decimal, surrounding the bracket()
	 * @param d
	 * @return
	 */
	public static String transferNumberByPM(double d, String currencyIcon){
		if (d >= 0){
			if (currencyIcon != null)
				return transferDouble2Scale(d);
			else 
				return currencyIcon + transferDouble2Scale(d);
		} else {
			if (currencyIcon != null)
				return "(" + currencyIcon + transferDouble2Scale(d * (-1)) + ")";
			else 
				return "(" + transferDouble2Scale(d * (-1)) + ")";
		}
	}
	
	public static void addEscapeListener(final CommonDialog dialog){
		ActionListener escListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}};
			
		dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		ActionListener enterListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.doEnterClick();
			}};
		dialog.getRootPane().registerKeyboardAction(enterListener, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
	
	public static double getGoodsOriginPrice(ArrayList<ChoosedGoods> goods){
		double originPrice = 0;
		for(ChoosedGoods cg : goods){
			originPrice += cg.amount * cg.goods.getSellPrice();
		}
		return originPrice;
	}
	
	/**
	 * 计算列表中商品的折扣价, 考虑会员折扣和促销折扣
	 * @param goods
	 * @return
	 */
	public static double getGoodsDiscountPrice(ArrayList<ChoosedGoods> goods, Member member){
		double price = 0;
		for(ChoosedGoods cg : goods){
			/**
			 * if modifiedPrice >= 0, then use the modifiedPrice;
			 * else if member != null, then use the member discount price; need to check if this item in promotion
			 * else use the goods.sellPrice.
			 */
			if (cg.modifiedPrice >= 0)
				price += cg.modifiedPrice * cg.amount;
			else if (member != null){
				if (cg.promotion == null)
					price += cg.goods.getSellPrice() * member.getDiscountRate() * cg.amount;
				else {
					if (cg.promotion.isForbidMemberDiscount()){
						if (cg.modifiedPrice >= 0){
							//user cg.modifiedPrice here
							price += cg.modifiedPrice * cg.amount;
						} else {
							price += cg.goods.getSellPrice() * cg.amount;
						}
					} else {
						if (cg.modifiedPrice >= 0){
							price += cg.modifiedPrice * cg.amount * member.getDiscountRate();
						} else {
							price += cg.goods.getSellPrice() * cg.amount * member.getDiscountRate();
						}
					}
				}
			} else 
				price += cg.goods.getSellPrice() * cg.amount;
		}
		return price;
	}
}
