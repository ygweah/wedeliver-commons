package us.wedeliver.commons.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class HTTPUtil {

  public static final HTTPResponse get(String url) throws IOException {
    HttpGet httpGet = new HttpGet(url);
    try (CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpGet)) {
      return new HTTPResponse(response);
    }
  }

  public static final HTTPResponse post(String url, Map<String, ?> paramMap) throws IOException {
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    for (Map.Entry<String, ?> mapEntry : paramMap.entrySet()) {
      if (mapEntry.getValue() != null)
        nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
    }

    HttpPost httpPost = new HttpPost(url);
    httpPost.setEntity(new UrlEncodedFormEntity(nvps));

    try (CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpPost)) {
      return new HTTPResponse(response);
    }
  }

}
