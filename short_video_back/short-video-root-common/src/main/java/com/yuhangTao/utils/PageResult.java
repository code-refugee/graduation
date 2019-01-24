package com.yuhangTao.utils;

import java.util.List;

public class PageResult {

    private int page; //当前是第几页

    private int allPages; //总的页数

    private long total; //总的记录数（即数据库中的记录数量）

    private List<?> content; //每页显示的内容

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getAllPages() {
        return allPages;
    }

    public void setAllPages(int allPages) {
        this.allPages = allPages;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getContent() {
        return content;
    }

    public void setContent(List<?> content) {
        this.content = content;
    }
}
