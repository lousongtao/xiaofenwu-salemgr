package com.shuishou.salemgr.beans;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/22.
 */

public class Category1 implements Serializable{
	@SerializedName(value = "id", alternate={"objectid"})
    private int id;

    private String name;


    private int sequence;

    @SerializedName(value = "children", alternate = {"category2s"})
    private ArrayList<Category2> category2s;

    public Category1(){

    }

    public Category1(int id, String name, int sequence){
        this.id = id;
        this.name = name;
        this.sequence = sequence;
    }

    public ArrayList<Category2> getCategory2s() {
        return category2s;
    }

    public void setCategory2s(ArrayList<Category2> category2s) {
        this.category2s = category2s;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void addCategory2(Category2 c2){
        if (category2s == null)
            category2s = new ArrayList<Category2>();
        category2s.add(c2);
    }
    
    

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
    public String toString() {
        return name;
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
        Category1 other = (Category1) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
