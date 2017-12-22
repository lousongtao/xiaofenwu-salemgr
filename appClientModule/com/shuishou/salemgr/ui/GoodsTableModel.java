package com.shuishou.salemgr.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

public class GoodsTableModel extends AbstractTableModel{
	private ArrayList<ChoosedGoods> items = new ArrayList<>();
	private String[] header = new String[]{
			Messages.getString("GoodsTableModel.Header.Name"),
			Messages.getString("GoodsTableModel.Header.Barcode"),
			Messages.getString("GoodsTableModel.Header.Amount"),
			Messages.getString("GoodsTableModel.Header.SellPrice"),
	};

	public GoodsTableModel(){
	}
	
	public GoodsTableModel(ArrayList<ChoosedGoods> items){
		this.items = items;
	}
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
	public void addItem(ChoosedGoods item){
		boolean isExist = false;
		for(ChoosedGoods cg : items){
			if (cg.goods.getId() == item.goods.getId()){
				isExist = true;
				cg.amount++;
				return;
			}
		}
		if (!isExist){
			items.add(item);
		}
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

}
