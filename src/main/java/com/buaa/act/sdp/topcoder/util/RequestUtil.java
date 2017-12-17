package com.buaa.act.sdp.topcoder.util;

import com.buaa.act.sdp.topcoder.common.Constant;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by yang on 2016/10/15.
 */
public class RequestUtil {

    public static String request(String url) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(url).request(MediaType.TEXT_PLAIN_TYPE).get();
        for (int i = 0; i < Constant.RETRY; i++) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(String.class);
            }
            response = client.target(url).request(MediaType.TEXT_PLAIN_TYPE).get();
        }
        return null;
    }

}
