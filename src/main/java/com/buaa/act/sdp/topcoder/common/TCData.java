package com.buaa.act.sdp.topcoder.common;

/**
 * Created by yang on 2017/12/26.
 */

import java.util.List;

/**
 * 用于任务和项目分页
 *
 * @param <T>
 */
public class TCData<T> {
    private List<T> data;
    private int total;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
