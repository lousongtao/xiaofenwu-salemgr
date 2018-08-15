package com.shuishou.salemgr.ui;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import com.shuishou.salemgr.CommonTools;
import com.shuishou.salemgr.ConstantValue;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.beans.Promotion;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

public class GoodsTableModel extends DefaultTableModel{

	private static final long serialVersionUID = 8134284737199240806L;
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
				if (cg.promotion == null || !cg.promotion.isForbidMemberDiscount())
					return CommonTools.transferNumberByPM(member.getDiscountRate() * cg.goods.getSellPrice(), "");
			}
			return "";
		case 5:
			if (cg.modifiedPrice < 0) return "";
			return CommonTools.transferNumberByPM(cg.modifiedPrice, "");
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
	 * 把商品加入购物列表, 如果列表中已存在该商品, 且已存在的商品未触发促销项, 则进行商品合并
	 * @param item
	 */
	public void addItem(ChoosedGoods item){
		boolean isExist = false;
		for(ChoosedGoods cg : items){
			if (cg.promotion != null)
				continue;
			if (cg.goods.getId() == item.goods.getId()){
				isExist = true;
				cg.amount++;
				break;
			}
		}
		if (!isExist){
			items.add(0, item);
		}	
		Promotion promotion = promotionCheck();
		if (promotion != null)
			applyPromotion(promotion);
	}
	
	//循环订单列表, 找到一个符合条件的promotion即返回
	private Promotion promotionCheck(){
		ArrayList<Promotion> promotions = mainFrame.getPromotionList();
		for (int i = 0; i< promotions.size(); i++) {
			Promotion p = promotions.get(i);
			if (!p.isAvailable()){
				continue;
			}
			if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNREDUCEPRICE
					|| p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNDISCOUNT){
				int amount = 0;
				for (ChoosedGoods cg : items){
					if (cg.promotion != null)
						continue;
					if ((p.getObjectAType() == ConstantValue.PROMOTION_GOODS && cg.goods.getId() == p.getObjectAId())
							|| (p.getObjectAType() == ConstantValue.PROMOTION_CATEGORY2 && cg.goods.getCategory2().getId() == p.getObjectAId())
							|| (p.getObjectAType() == ConstantValue.PROMOTION_CATEGORY1 && cg.goods.getCategory2().getCategory1().getId() == p.getObjectAId())){
						amount += cg.amount;
					}
				}
				if (amount == p.getObjectAQuantity()){
					return p;
				}
			} else if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNNEXTDISCOUNT
				|| p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNNEXTREDUCEPRICE){
				int amount = 0;
				for (ChoosedGoods cg : items){
					if (cg.promotion != null)
						continue;
					if ((p.getObjectAType() == ConstantValue.PROMOTION_GOODS && cg.goods.getId() == p.getObjectAId())
							|| (p.getObjectAType() == ConstantValue.PROMOTION_CATEGORY2 && cg.goods.getCategory2().getId() == p.getObjectAId())
							|| (p.getObjectAType() == ConstantValue.PROMOTION_CATEGORY1 && cg.goods.getCategory2().getCategory1().getId() == p.getObjectAId())){
						amount += cg.amount;
					}
				}
				if (amount == p.getObjectAQuantity() + 1){
					return p;
				}
			} else if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_NEXTBREDUCEPRICE
				|| p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_NEXTBDISCOUNT
				|| p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABDISCOUNT
				|| p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABREDUCEPRICE){
				int amounta = 0;
				int amountb = 0;
				for (ChoosedGoods cg : items){
					if (cg.promotion != null)
						continue;
					if ((p.getObjectAType() == ConstantValue.PROMOTION_GOODS && cg.goods.getId() == p.getObjectAId())
							|| (p.getObjectAType() == ConstantValue.PROMOTION_CATEGORY2 && cg.goods.getCategory2().getId() == p.getObjectAId())
							|| (p.getObjectAType() == ConstantValue.PROMOTION_CATEGORY1 && cg.goods.getCategory2().getCategory1().getId() == p.getObjectAId())){
						amounta += cg.amount;
					}
					if ((p.getObjectBType() == ConstantValue.PROMOTION_GOODS && cg.goods.getId() == p.getObjectBId())
							|| (p.getObjectBType() == ConstantValue.PROMOTION_CATEGORY2 && cg.goods.getCategory2().getId() == p.getObjectBId())
							|| (p.getObjectBType() == ConstantValue.PROMOTION_CATEGORY1 && cg.goods.getCategory2().getCategory1().getId() == p.getObjectBId())){
						amountb += cg.amount;
					}
					if (amounta >= p.getObjectAQuantity() && amountb >= p.getObjectBQuantity())
						return p;
				}
			} 
		}
		return null;
	}
	
	/**
	 * 对商品列表进行促销应用
	 * @param item
	 */
	public void applyPromotion(Promotion promotion){
		if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNDISCOUNT
				|| promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNREDUCEPRICE){
			applyPromotionBuyNA(promotion);
		} else if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNNEXTDISCOUNT
				|| promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNNEXTREDUCEPRICE){
			applyPromotionBuyNAPlusOne(promotion);
		} else if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_NEXTBREDUCEPRICE
				|| promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_NEXTBDISCOUNT
				|| promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABDISCOUNT
				|| promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABREDUCEPRICE){
			applyPromotionBuyNAMB(promotion);
		}
	}
	
	/**
	 * 对当前购物清单中的商品应用promotion, 该promotion仅包括买N个A商品后, 对一个B商品进行降价或打折(N>0);
	 * 或者在买入N个A商品和M个B商品, 然后对A和B同时降价.
	 * 由于AB可能分属不同的物品, 所以要循环检查并将符合条件的物品收集到一个集合中, 直到集合满足条件.
	 * 最后对集合中的数据应用promotion规则.
	 * 本方法假定当前的购物清单已符合promotion规则
	 * @param promotion
	 */
	private void applyPromotionBuyNAMB(Promotion promotion){
		ArrayList<ChoosedGoods> kickoffGoodsA = new ArrayList<>();//记录从购物清单中找到的符合执行promotion规则的项.
		ArrayList<ChoosedGoods> kickoffGoodsB = new ArrayList<>();//记录从购物清单中找到的符合执行promotion规则的项.
		for (int i = items.size() - 1; i >= 0; i--) { //这里需要用倒序, 因为中间items可能会删除某些项
			ChoosedGoods cg = items.get(i);
			if (cg.promotion != null)
				continue;
			//把符合条件的采购项加入列表, 如果数目过大, 要拆分一部分加入进去
			if (getKickoffListQuantity(kickoffGoodsA) < promotion.getObjectAQuantity()
					&& promotion.getObjectAType() == ConstantValue.PROMOTION_GOODS
					&& cg.goods.getId() == promotion.getObjectAId()){
				if (cg.amount + getKickoffListQuantity(kickoffGoodsA) <= promotion.getObjectAQuantity()){
					kickoffGoodsA.add(cg);
					items.remove(cg);
				} else {
					cg.amount -= promotion.getObjectAQuantity() - getKickoffListQuantity(kickoffGoodsA);
					ChoosedGoods cg2 = new ChoosedGoods(promotion.getObjectAQuantity() - getKickoffListQuantity(kickoffGoodsA), cg.goods);
					kickoffGoodsA.add(cg2);
				}
			}
			if (getKickoffListQuantity(kickoffGoodsA) < promotion.getObjectAQuantity()
					&& promotion.getObjectAType() == ConstantValue.PROMOTION_CATEGORY2
					&& cg.goods.getCategory2().getId() == promotion.getObjectAId()){
				if (cg.amount + getKickoffListQuantity(kickoffGoodsA) <= promotion.getObjectAQuantity()){
					kickoffGoodsA.add(cg);
					items.remove(cg);
				} else {
					cg.amount -= promotion.getObjectAQuantity() - getKickoffListQuantity(kickoffGoodsA);
					ChoosedGoods cg2 = new ChoosedGoods(promotion.getObjectAQuantity() - getKickoffListQuantity(kickoffGoodsA), cg.goods);
					kickoffGoodsA.add(cg2);
				}
			}
			if (getKickoffListQuantity(kickoffGoodsA) < promotion.getObjectAQuantity()
					&& promotion.getObjectAType() == ConstantValue.PROMOTION_CATEGORY1
					&& cg.goods.getCategory2().getCategory1().getId() == promotion.getObjectAId()){
				if (cg.amount + getKickoffListQuantity(kickoffGoodsA) <= promotion.getObjectAQuantity()){
					kickoffGoodsA.add(cg);
					items.remove(cg);
				} else {
					cg.amount -= promotion.getObjectAQuantity() - getKickoffListQuantity(kickoffGoodsA);
					ChoosedGoods cg2 = new ChoosedGoods(promotion.getObjectAQuantity() - getKickoffListQuantity(kickoffGoodsA), cg.goods);
					kickoffGoodsA.add(cg2);
				}
			}
			if (getKickoffListQuantity(kickoffGoodsB) < promotion.getObjectBQuantity()
					&& promotion.getObjectBType() == ConstantValue.PROMOTION_GOODS
					&& cg.goods.getId() == promotion.getObjectBId()){
				if (cg.amount + getKickoffListQuantity(kickoffGoodsB) <= promotion.getObjectBQuantity()){
					kickoffGoodsB.add(cg);
					items.remove(cg);
				} else {
					cg.amount -= promotion.getObjectBQuantity() - getKickoffListQuantity(kickoffGoodsB);
					ChoosedGoods cg2 = new ChoosedGoods(promotion.getObjectBQuantity() - getKickoffListQuantity(kickoffGoodsB), cg.goods);
					kickoffGoodsB.add(cg2);
				}
			} 
			if (getKickoffListQuantity(kickoffGoodsB) < promotion.getObjectBQuantity()
					&& promotion.getObjectBType() == ConstantValue.PROMOTION_CATEGORY2
					&& cg.goods.getCategory2().getId() == promotion.getObjectBId()){
				if (cg.amount + getKickoffListQuantity(kickoffGoodsB) <= promotion.getObjectBQuantity()){
					kickoffGoodsB.add(cg);
					items.remove(cg);
				} else {
					cg.amount -= promotion.getObjectBQuantity() - getKickoffListQuantity(kickoffGoodsB);
					ChoosedGoods cg2 = new ChoosedGoods(promotion.getObjectBQuantity() - getKickoffListQuantity(kickoffGoodsB), cg.goods);
					kickoffGoodsB.add(cg2);
				}
			} 
			if (getKickoffListQuantity(kickoffGoodsB) < promotion.getObjectBQuantity()
					&& promotion.getObjectBType() == ConstantValue.PROMOTION_CATEGORY1
					&& cg.goods.getCategory2().getCategory1().getId() == promotion.getObjectBId()){
				if (cg.amount + getKickoffListQuantity(kickoffGoodsB) <= promotion.getObjectBQuantity()){
					kickoffGoodsB.add(cg);
					items.remove(cg);
				} else {
					cg.amount -= promotion.getObjectBQuantity() - getKickoffListQuantity(kickoffGoodsB);
					ChoosedGoods cg2 = new ChoosedGoods(promotion.getObjectBQuantity() - getKickoffListQuantity(kickoffGoodsB), cg.goods);
					kickoffGoodsB.add(cg2);
				}
			}
		}
		//检查数量是否符合规则
		if (promotion.getObjectAQuantity() == getKickoffListQuantity(kickoffGoodsA)
				&& promotion.getObjectBQuantity() == getKickoffListQuantity(kickoffGoodsB)){
			for (ChoosedGoods cg : kickoffGoodsA) {
				cg.promotion = promotion;
				if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABDISCOUNT)
					cg.modifiedPrice = cg.goods.getSellPrice() * promotion.getRewardValue();
				else if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABREDUCEPRICE)
					cg.modifiedPrice = cg.goods.getSellPrice() - promotion.getRewardValue();
			}
			for (ChoosedGoods cg : kickoffGoodsB) {
				cg.promotion = promotion;
				if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_NEXTBREDUCEPRICE)
					cg.modifiedPrice =  cg.goods.getSellPrice() - promotion.getRewardValue();
				else if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_NEXTBDISCOUNT)
					cg.modifiedPrice =  cg.goods.getSellPrice() * promotion.getRewardValue();
				else if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABDISCOUNT)
					cg.modifiedPrice = cg.goods.getSellPrice() * promotion.getRewardValue();
				else if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABREDUCEPRICE)
					cg.modifiedPrice = cg.goods.getSellPrice() - promotion.getRewardValue();
			}
			items.addAll(kickoffGoodsA);
			items.addAll(kickoffGoodsB);
		}
	}
	
	/**
	 * 对当前购物清单中的商品应用promotion, 该promotion仅包括买N个A商品后, 对第N+1个A商品进行打折或降价(N>0)
	 * 由于A可能分属不同的物品, 所以要循环检查并收集到一个集合中, 直到该集合满足数量等于N+1
	 * 然后将集合内商品分成两组, 其中第一个商品抽取出来作为第一组, 并按照促销规则进行降价.
	 * 其余商品归为第二组, 不做降价, 但是要标记promotion, 以防这些商品触发后续的促销模板.
	 * @param promotion
	 */
	private void applyPromotionBuyNAPlusOne(Promotion promotion){
		ArrayList<ChoosedGoods> kickoffGoods = new ArrayList<>();//记录从购物清单中找到的符合执行promotion规则的项.
		for (int i = items.size() - 1; i >= 0; i--) { //这里需要用倒序, 因为中间items可能会删除某些项
			ChoosedGoods cg = items.get(i);
			if (cg.promotion != null)
				continue;
			//跳过跟promotion无关的商品
			if (promotion.getObjectAType() == ConstantValue.PROMOTION_GOODS){
				if (cg.goods.getId() != promotion.getObjectAId()){
					continue;
				}
			} else if (promotion.getObjectAType() == ConstantValue.PROMOTION_CATEGORY2){
				if (cg.goods.getCategory2().getId() != promotion.getObjectAId()){
					continue;
				}
			} else if (promotion.getObjectAType() == ConstantValue.PROMOTION_CATEGORY1){
				if (cg.goods.getCategory2().getCategory1().getId() != promotion.getObjectAId()){
					continue;
				}
			}
			items.remove(cg);
			kickoffGoods.add(cg);
			if (getKickoffListQuantity(kickoffGoods) == promotion.getObjectAQuantity() + 1)
				break;//循环中止
		}
		if (!kickoffGoods.isEmpty()){
			ChoosedGoods cg0 = kickoffGoods.get(0);
			//根据cg0创造一个新的ChoosedGoods对象, 设置Amount=1, 同时对于cg0的Amount减1, 如果cg0的Amount变位0, 则从kickoff列表剔除
			ChoosedGoods cgt = new ChoosedGoods(1, cg0.goods, promotion);
			if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNNEXTDISCOUNT){
				cgt.modifiedPrice = cgt.goods.getSellPrice() * promotion.getRewardValue();
			} else if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNNEXTREDUCEPRICE){
				cgt.modifiedPrice = cgt.goods.getSellPrice() - promotion.getRewardValue();
			}
			kickoffGoods.add(cgt);
			cg0.amount -= 1;
			if (cg0.amount == 0){
				kickoffGoods.remove(cg0);
			}
			for (ChoosedGoods cg : kickoffGoods){
				cg.promotion = promotion;
			}
			items.addAll(kickoffGoods);
		}
	}
	
	/**
	 * 对当前购物清单中的商品应用promotion, 该promotion仅包括买N个A商品, 统一进行打折或降价(N>0)
	 * 买n个A, 对这n个A同时进行降价, 由于A可能分属不同的物品, 所以要循环检查全部购物清单.
	 * 此方法假定购物清单items肯定能触发promotion, 所以该方法内不进行promotion校验, 需要在调用该方法之前进行校验promotion是否适用.
	 * 由此, 该方法内不进行商品A数量的检查, 只要是未标记promotion的A商品, 都认为是触发当下promotion的商品.
	 * 根据promotion的ObjectA类型及数量, 从当前的购物列表中剔除掉对应数量的item, 标记Promotion, 并将其作为一个新记录插入集合中;
	 * @param 
	 */
	private void applyPromotionBuyNA(Promotion promotion){
		for (int i = items.size() - 1; i >= 0; i--) { //这里需要用倒序, 因为中间items可能会删除某些项
			ChoosedGoods cg = items.get(i);
			if (cg.promotion != null)
				continue;
			//跳过跟promotion无关的商品
			if (promotion.getObjectAType() == ConstantValue.PROMOTION_GOODS){
				if (cg.goods.getId() != promotion.getObjectAId()){
					continue;
				}
			} else if (promotion.getObjectAType() == ConstantValue.PROMOTION_CATEGORY2){
				if (cg.goods.getCategory2().getId() != promotion.getObjectAId()){
					continue;
				}
			} else if (promotion.getObjectAType() == ConstantValue.PROMOTION_CATEGORY1){
				if (cg.goods.getCategory2().getCategory1().getId() != promotion.getObjectAId()){
					continue;
				}
			}
			cg.promotion = promotion;
			if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNDISCOUNT){
				cg.modifiedPrice = cg.goods.getSellPrice() * promotion.getRewardValue();
			} else if (promotion.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNREDUCEPRICE){
				cg.modifiedPrice = cg.goods.getSellPrice() - promotion.getRewardValue();
			}
		}
	}
	
	
	private int getKickoffListQuantity(ArrayList<ChoosedGoods> kickoffGoods){
		int n = 0;
		for (ChoosedGoods cg : kickoffGoods) {
			n += cg.amount;
		}
		return n;
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
}
