package com.shuishou.salemgr.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/22.
 */
public class Category2 implements Serializable{
	@SerializedName(value = "id", alternate={"objectid"})
    private int id;

    private String name;


    private int sequence;

    private Category1 category1;
    
    private List<Goods> goods;
    

    public Category2(){

    }

    public Category2(int id, String name, int sequence, Category1 category1){
        this.id = id;
        this.name = name;
        this.sequence = sequence;
        this.category1 = category1;
    }

    public Category1 getCategory1() {
        return category1;
    }

    public void setCategory1(Category1 category1) {
        this.category1 = category1;
    }

    @Override
    public String toString() {
        return "Category2 [name=" + name + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Goods> getGoods() {
		return goods;
	}

	public void setGoods(List<Goods> goods) {
		this.goods = goods;
	}

	public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
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
        Category2 other = (Category2) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
