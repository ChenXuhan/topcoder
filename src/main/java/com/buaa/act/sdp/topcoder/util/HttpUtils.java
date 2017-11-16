package com.buaa.act.sdp.topcoder.util;

import com.buaa.act.sdp.topcoder.common.Constant;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by yang on 2016/10/16.
 */
public class HttpUtils {
    /**
     * http get请求
     *
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        String result = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == Constant.HTTP_SUCCESS) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
