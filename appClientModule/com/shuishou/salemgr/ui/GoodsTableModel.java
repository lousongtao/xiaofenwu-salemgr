package com.shuishou.salemgr.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import com.shuishou.salemgr.CommonTools;
import com.shuishou.salemgr.ConstantValue;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.Goods;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.beans.Promotion;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

public class GoodsTableModel extends DefaultTableModel{
	private MainFrame mainFrame;
	private ArrayList<ChoosedGoods> items = new ArrayList<>();
	private Member member;
	private String[] header = new String[]{
			Messages.getString("GoodsTableModel.Header.Name"),
			Messages.getString("GoodsTableModel.Header.Barcode"),
			Messages.getString("GoodsTableModel.Header.Amount"),
			Messages.getString("GoodsTableModel.Header.SellPrice"),
			Messages.getString("GoodsTableModel.Header.MemberDiscountPrice"),
			Messages.getString("GoodsTableModel.Header.ModifiedPrice")
	};

	public GoodsTableModel(MainFrame mainFrame){
		this.mainFrame = mainFrame;
	}
	
//	public GoodsTableModel(ArrayList<ChoosedGoods> items){
//		this.items = items;
//	}
	@Override
	public int getRowCount() {
		if (items == null)
			return 0;
		return items.size();
	}

	@Override
	public int getColumnCount() {
		return header.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ChoosedGoods cg = getObjectAt(rowIndex);
		switch(columnIndex){
		case 0:
			return cg.goods.getName();
		case 1:
			return cg.goods.getBarcode();
		case 2:
			return cg.amount;
		case 3:
			return cg.goods.getSellPrice();
		case 4:
			if (member != null){
				return CommonTools.transferNumberByPM(member.getDiscountRate() * cg.goods.getSellPrice(), "");
			}
			return "";
		case 5:
			if (cg.modifiedPrice < 0) return "";
			return cg.modifiedPrice;
		}
		return "";
	}
	
	@Override
	public String getColumnName(int column) {
		return header[column];
    }
	
	public void setData(ArrayList<ChoosedGoods> items){
		this.items = items;
	}
	
	public ArrayList<ChoosedGoods> getData(){
		return items;
	}
	
	/**
	 * 添加一个商品项进入列表
	 * 1. 检查是否有促销项设置, 如果有, 判断是否触发促销
	 * 2. 新加入项 + 已存在项, 触发了促销, 将触发促销的项目单独拉出, 并标记他们为"促销已用"状态
	 * 3. 促销状态的ChoosedGoods, 不再参与后续的促销判断.
	 * @param item
	 */
	public void addItem(ChoosedGoods item){
		Promotion promotion = validatePromotion(item.goods);
		if (promotion == null){
			boolean isExist = false;
			for(ChoosedGoods cg : items){
				if (cg.goods.getId() == item.goods.getId()){
					isExist = true;
					cg.amount++;
					return;
				}
			}
			if (!isExist){
				items.add(0, item);
			}
		} else {
			//TODO: 目前只处理直接折扣这种方式的, 不考虑ObjectAQuantity>0的情形
			if (promotion.getObjectAQuantity() == 0){
				if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNDISCOUNT){
					item.inPromotion = true;
					item.modifiedPrice = item.goods.getSellPrice() * promotion.getRewardValue();
					items.add(0, item);
				} else if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNREDUCEPRICE){
					item.inPromotion = true;
					item.modifiedPrice = item.goods.getSellPrice() - promotion.getRewardValue();
					items.add(0, item);
				}
			}
		}
		
	}
	
	/**
	 * 验证添加goods进入列表后, 是否会触发促销
	 * @param goods
	 * @return 碰到第一个满足条件的促销模式, 返回; 没有适配的促销模式, 返回空
	 */
	private Promotion validatePromotion(Goods goods){
		ArrayList<Promotion> promotions = mainFrame.getPromotionList();
		for (int i = 0; i< promotions.size(); i++) {
			if (promotions.get(i).getObjectAType() == ConstantValue.PROMOTION_GOODS){
				int amount = 0;
				for (ChoosedGoods cg : items){
					if (cg.inPromotion)
						continue;
					if (cg.goods.getId() == goods.getId())
						amount += cg.amount;
				}
				if (amount == promotions.get(i).getObjectAQuantity()){
					return promotions.get(i);
				}
			} else if (promotions.get(i).getObjectAType() == ConstantValue.PROMOTION_CATEGORY2){
				int amount = 0;
				for(ChoosedGoods cg : items){
					if (cg.inPromotion)
						continue;
					if (cg.goods.getCategory2().getId() == goods.getCategory2().getId())
						amount += cg.amount;
				}
				if (amount == promotions.get(i).getObjectAQuantity() 
						&& goods.getCategory2().getId() == promotions.get(i).getObjectAId())
					return promotions.get(i);
			} else if (promotions.get(i).getObjectAType() == ConstantValue.PROMOTION_CATEGORY1){
				int amount = 0;
				for(ChoosedGoods cg : items){
					if (cg.inPromotion)
						continue;
					if (cg.goods.getCategory2().getCategory1().getId() == goods.getCategory2().getCategory1().getId())
						amount += cg.amount;
				}
				if (amount == promotions.get(i).getObjectAQuantity() 
						&& goods.getCategory2().getCategory1().getId() == promotions.get(i).getObjectAId())
					return promotions.get(i);
			}
		}
		return null;
	}
	
	public void deleteItem(ChoosedGoods item){
		items.remove(item);
	}
	
	public void deleteItem(int i){
		items.remove(i);
	}
	
	public void deleteAllItem(){
		items.clear();
	}
	
	public ChoosedGoods getObjectAt(int index){
		return items.get(index);
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
		this.fireTableDataChanged();
	}

	public boolean isCellEditable(int row, int column) {
        return false;
    }
	
	/**
	 * 目前列表中的物品, 满足促销的条件, 将满足条件的物品放入一个集合中, 与promotion一起组装成该对象
	 * @author Administrator
	 *
	 */
//	class PromotionCombo{
//		public Promotion promotion;
//		public 
//	}
}
