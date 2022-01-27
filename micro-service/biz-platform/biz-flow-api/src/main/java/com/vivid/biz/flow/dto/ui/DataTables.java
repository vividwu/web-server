package com.vivid.biz.flow.dto.ui;

import java.util.List;

/**
 * Created by wuwei2_m on 2019/5/24.
 */
public class DataTables {

    /**
     * 获取请求次数
     */
    private int draw = 0;

    /**
     * 数据起始位置
     */
    private long start;
    /**
     * 数据长度
     */
    private long length;

    /**
     * 总记录数
     */
    private long recordsTotal = 0;

    /**
     * 过滤后记录数
     */
    private long recordsFiltered;
    private List<?> Data;


    /**
     * 获取用户过滤框中的字符
     */
    private String searchValue;

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<?> getData() {
        return Data;
    }

    public void setData(List<?> data) {
        Data = data;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

}
