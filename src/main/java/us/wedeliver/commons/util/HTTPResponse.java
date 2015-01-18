package us.wedeliver.commons.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

public class HTTPResponse {
  private int code;
  private String status;
  private String content;

  protected HTTPResponse(CloseableHttpResponse response) throws IOException {
    StatusLine statusLine = response.getStatusLine();
    this.code = statusLine.getStatusCode();
    this.status = statusLine.getReasonPhrase();

    HttpEntity entity = response.getEntity();
    this.content = IOUtils.toString(entity.getContent());
    EntityUtils.consume(entity);
  }

  public boolean isOK() {
    return (code == HttpServletResponse.SC_OK);
  }

  public int getCode() {
    return code;
  }

  public String getStatus() {
    return status;
  }

  public String getContent() {
    return content;
  }

}
