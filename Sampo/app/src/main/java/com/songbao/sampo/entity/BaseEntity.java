package com.songbao.sampo.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 所有数据实体类的父类
 */

public class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 2980439304361030908L;

    private int errno; //响应状态
    private String errmsg; //状态描述
    private String others; //其它内容

    private T data; //单个数据
    private List<T> lists; //集合数据

    private int dataTotal; //数据总量

    public BaseEntity() {
        super();
    }

    public BaseEntity(int errno, String errmsg) {
        this.errno = errno;
        this.errmsg = errmsg;
    }

    public String getEntityId() {
        return "";
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
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
                "errno=" + errno +
                ", errmsg='" + errmsg + '\'' +
                ", others='" + others + '\'' +
                ", data=" + data +
                ", lists=" + lists +
                ", dataTotal=" + dataTotal +
                '}';
    }
}
