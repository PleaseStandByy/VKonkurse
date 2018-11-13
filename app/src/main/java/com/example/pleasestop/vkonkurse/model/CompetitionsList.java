package com.example.pleasestop.vkonkurse.model;

import java.util.List;

public class CompetitionsList<T> extends  DelayItems {


    private String status;

    private List<T> items;

    private Integer count;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }



}
