package com.songbao.sampo_b.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 所有数据实体类的父类
 */

public class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 2980439304361030908L;

    private int errNo; //响应状态
    private String errMsg; //状态描述
    private String others; //其它内容

    private T data; //单个数据
    private List<T> lists; //集合数据

    private int dataTotal; //数据总量

    public BaseEntity() {
        super();
    }

    public BaseEntity(int errNo, String errMsg) {
        this.errNo = errNo;
        this.errMsg = errMsg;
    }

    public String getEntityId() {
        return "";
    }

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getLists() {
        return lists;
    }

    public void setLists(List<T> lists) {
        this.lists = lists;
    }

    public int getDataTotal() {
        return dataTotal;
    }

    public void setDataTotal(int dataTotal) {
        this.dataTotal = dataTotal;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "errNo=" + errNo +
                ", errMsg='" + errMsg + '\'' +
                ", others='" + others + '\'' +
                ", data=" + data +
                ", lists=" + lists +
                ", dataTotal=" + dataTotal +
                '}';
    }
}
