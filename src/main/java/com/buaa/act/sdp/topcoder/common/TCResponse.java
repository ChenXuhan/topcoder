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
        setCode(Constant.TC_NOT_FOUND);
        setDes(Constant.TC_NOT_FOUND_DES);
    }

    public void setErrorResponse() {
        setCode(Constant.TC_INNER_ERROR);
        setDes(Constant.TC_INNER_ERROR_DES);
    }

    public void setNotSupport() {
        setCode(Constant.TC_NOT_SUPPORT);
        setDes(Constant.TC_NOT_SUPPORT_DES);
    }

    public void setMsgMiss() {
        setCode(Constant.TC_MSG_MISS);
        setDes(Constant.TC_MSG_MISS_DES);
    }

    public void setNotEnoughTrainningSet() {
        setCode(Constant.TC_NOT_ENOUGH_TRAINNING_SET);
        setDes(Constant.TC_NOT_ENOUGH_TRAINNING_SET_DES);
    }
}
