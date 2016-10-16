package com.buaa.act.sdp.util;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by yang on 2016/10/15.
 */
public class RequestUtil {

    public static Object request(String url,Class clazz){
        Object object=null;
        Client client = ClientBuilder.newClient();
        Response response = client.target(url).request(MediaType.TEXT_PLAIN_TYPE).get();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            object= JsonUtil.fromJson(response.readEntity(String.class), clazz);
        }
        return object;
    }

}
