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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPUtil {
  private static final Logger logger = LoggerFactory.getLogger(HTTPUtil.class);

  public static final HTTPResponse get(String url) throws IOException {
    logger.debug("HTTP GET: {}", url);
    HttpGet httpGet = new HttpGet(url);
    try (CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpGet)) {
      HTTPResponse httpResponse = new HTTPResponse(response);
      logger.debug("HTTP GET Response: {}, {}", httpResponse.getCode(), httpResponse.getContent());
      return httpResponse;
    }
  }

  public static final HTTPResponse post(String url, Map<String, ?> paramMap) throws IOException {
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    for (Map.Entry<String, ?> mapEntry : paramMap.entrySet()) {
      if (mapEntry.getValue() != null)
        nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
    }

    if (logger.isDebugEnabled()) {
      StringBuilder sb = new StringBuilder();
      for (NameValuePair nvp : nvps) {
        if (sb.length() != 0)
          sb.append(", ");
        sb.append(nvp.getName()).append('=').append(nvp.getValue());
      }
      logger.debug("HTTP POST: {} - {}", url, sb);
    }

    HttpPost httpPost = new HttpPost(url);
    httpPost.setEntity(new UrlEncodedFormEntity(nvps));

    try (CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpPost)) {
      HTTPResponse httpResponse = new HTTPResponse(response);
      logger.debug("HTTP POST Response: {}, {}", httpResponse.getCode(), httpResponse.getContent());
      return httpResponse;
    }
  }

}
