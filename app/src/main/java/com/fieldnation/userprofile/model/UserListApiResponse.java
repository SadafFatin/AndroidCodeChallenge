package com.fieldnation.userprofile.model;

import java.io.Serializable;
import java.util.ArrayList;

public class UserListApiResponse implements Serializable {


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public ArrayList<User> getData() {
        return data;
    }

    public void setData(ArrayList<User> data) {
        this.data = data;
    }

    public Support getSupport() {
        return support;
    }

    public void setSupport(Support support) {
        this.support = support;
    }

    private int total_pages;
    private ArrayList<User> data;
    private Support support;
    private int page;
    private int per_page;
    private int total;

    public UserListApiResponse(int total_pages, ArrayList<User> data, Support support, int page, int per_page, int total) {
        this.total_pages = total_pages;
        this.data = data;
        this.support = support;
        this.page = page;
        this.per_page = per_page;
        this.total = total;
    }


}
