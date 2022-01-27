package com.vivid.biz.flow.config.web;

public class PagerHolder {
    private Integer num;
    private Integer size;

    public PagerHolder(Integer num, Integer size) {
        this.num = num;
        this.size = size;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
