package us.wedeliver.commons.util;

import org.apache.commons.codec.digest.DigestUtils;

public class DigestUtil {

  public static final String digest(String username, String password) {
    return DigestUtils.sha1Hex(String.format("%s%s", username, password));
  }

}
