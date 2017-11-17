package com.buaa.act.sdp.topcoder.common;

/**
 * Created by yang on 2017/11/17.
 */
public class TCResponse<T> {

    private int code;
    private String des;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setSuccessResponse(T data) {
        setCode(Constant.TC_SUCCESS);
        setDes(Constant.TC_SUCCESS_DES);
        setData(data);
    }

    public void setNotFoundResponse() {
        setCode(Constant.TC_NOTFOUND);
        setDes(Constant.TC_NOTFOUND_DES);
    }

    public void setErrorResponse() {
        setCode(Constant.TC_INNER_ERROR);
        setDes(Constant.TC_INNER_ERROR_DES);
    }

}
