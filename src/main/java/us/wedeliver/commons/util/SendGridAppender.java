/*
 * Copyright 1999-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wedeliver.commons.util;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;

/**
 * Send an e-mail when a specific logging event occurs, typically on errors or
 * fatal errors.
 *
 * <p>
 * The number of logging events delivered in this e-mail depend on the value of
 * <b>BufferSize</b> option. The <code>SMTPAppender</code> keeps only the last
 * <code>BufferSize</code> logging events in its cyclic buffer. This keeps
 * memory requirements at a reasonable level while still delivering useful
 * application context.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.0
 */
public class SendGridAppender extends AppenderSkeleton {
  private String to;
  private String cc;
  private String from;
  private String subject;
  private String username;
  private String password;
  private int bufferSize = 512;
  private boolean locationInfo = false;

  protected CyclicBuffer cb = new CyclicBuffer(bufferSize);
  // protected Message msg;

  protected TriggeringEventEvaluator evaluator;

  /**
   * The default constructor will instantiate the appender with a
   * {@link TriggeringEventEvaluator} that will trigger on events with level
   * ERROR or higher.
   */
  public SendGridAppender() {
    this(new TriggeringEventEvaluator() {
      /**
       * Is this <code>event</code> the e-mail triggering event?
       *
       * <p>
       * This method returns <code>true</code>, if the event level has ERROR
       * level or higher. Otherwise it returns <code>false</code>.
       */
      @Override
      public boolean isTriggeringEvent(LoggingEvent event) {
        return true;
      }
    });
  }

  /**
   * Use <code>evaluator</code> passed as parameter as the
   * {@link TriggeringEventEvaluator} for this SMTPAppender.
   */
  public SendGridAppender(TriggeringEventEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  /**
   * Activate the specified options, such as the smtp host, the recipient, from,
   * etc.
   */
  @Override
  public void activateOptions() {
  }

  /**
   * Perform SMTPAppender specific appending actions, mainly adding the event to
   * a cyclic buffer and checking if the event triggers an e-mail to be sent.
   */
  @Override
  public void append(LoggingEvent event) {
    if (!checkEntryConditions()) {
      return;
    }

    event.getThreadName();
    event.getNDC();
    if (locationInfo) {
      event.getLocationInformation();
    }
    cb.add(event);
    if (evaluator.isTriggeringEvent(event)) {
      sendBuffer();
    }
  }

  /**
   * This method determines if there is a sense in attempting to append.
   *
   * <p>
   * It checks whether there is a set output target and also if there is a set
   * layout. If these checks fail, then the boolean value <code>false</code> is
   * returned.
   */
  protected boolean checkEntryConditions() {
    if (this.username == null || this.password == null) {
      errorHandler.error("No SendGrid username/password set for appender named [" + name + "].");
      return false;
    }

    if (this.evaluator == null) {
      errorHandler.error("No TriggeringEventEvaluator is set for appender [" + name + "].");
      return false;
    }

    if (this.layout == null) {
      errorHandler.error("No layout set for appender named [" + name + "].");
      return false;
    }
    return true;
  }

  @Override
  synchronized public void close() {
    this.closed = true;
  }

  /**
   * The <code>SMTPAppender</code> requires a {@link org.apache.log4j.Layout
   * layout}.
   */
  @Override
  public boolean requiresLayout() {
    return true;
  }

  /**
   * Send the contents of the cyclic buffer as an e-mail message.
   */
  protected void sendBuffer() {

    // Note: this code already owns the monitor for this
    // appender. This frees us from needing to synchronize on 'cb'.
    try {
      Email email = new Email();

      email.setFrom(from);
      email.addTo(to);
      if (cc != null && !cc.isEmpty())
        email.addCc(cc);

      if (subject != null)
        email.setSubject(subject);

      email.addFilter("subscriptiontrack", "enable", "0");

      StringBuffer sbuf = new StringBuffer();
      String t = layout.getHeader();
      if (t != null)
        sbuf.append(t);
      int len = cb.length();
      for (int i = 0; i < len; i++) {
        // sbuf.append(MimeUtility.encodeText(layout.format(cb.get())));
        LoggingEvent event = cb.get();
        sbuf.append(layout.format(event));
        if (layout.ignoresThrowable()) {
          String[] s = event.getThrowableStrRep();
          if (s != null) {
            for (int j = 0; j < s.length; j++) {
              sbuf.append(s[j]).append('\n');
            }
          }
        }
      }
      t = layout.getFooter();
      if (t != null)
        sbuf.append(t);

      email.setText(sbuf.toString());

      Response response = getSendGrid().send(email);
      if (!response.getStatus())
        System.err.println(response.getMessage());
    } catch (Exception e) {
      System.err.println("Error occured while sending e-mail notification.");
      e.printStackTrace(System.err);
    }
  }

  private SendGrid sendGrid;

  private synchronized SendGrid getSendGrid() {
    if (sendGrid == null)
      sendGrid = new SendGrid(username, password);
    return sendGrid;
  }

  /**
   * Returns value of the <b>EvaluatorClass</b> option.
   */
  public String getEvaluatorClass() {
    return evaluator == null ? null : evaluator.getClass().getName();
  }

  /**
   * Returns value of the <b>From</b> option.
   */
  public String getFrom() {
    return from;
  }

  /**
   * Returns value of the <b>Subject</b> option.
   */
  public String getSubject() {
    return subject;
  }

  /**
   * The <b>From</b> option takes a string value which should be a e-mail
   * address of the sender.
   */
  public void setFrom(String from) {
    this.from = from;
  }

  /**
   * The <b>Subject</b> option takes a string value which should be a the
   * subject of the e-mail message.
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * The <b>BufferSize</b> option takes a positive integer representing the
   * maximum number of logging events to collect in a cyclic buffer. When the
   * <code>BufferSize</code> is reached, oldest events are deleted as new events
   * are added to the buffer. By default the size of the cyclic buffer is 512
   * events.
   */
  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
    cb.resize(bufferSize);
  }

  /**
   * Returns value of the <b>To</b> option.
   */
  public String getTo() {
    return to;
  }

  /**
   * The <b>To</b> option takes a string value which should be a comma separated
   * list of e-mail address of the recipients.
   */
  public void setTo(String to) {
    this.to = to;
  }

  public void setCc(String cc) {
    this.cc = cc;
  }

  public String getCc() {
    return cc;
  }

  /**
   * Returns value of the <b>BufferSize</b> option.
   */
  public int getBufferSize() {
    return bufferSize;
  }

  /**
   * The <b>EvaluatorClass</b> option takes a string value representing the name
   * of the class implementing the {@link TriggeringEventEvaluator} interface. A
   * corresponding object will be instantiated and assigned as the triggering
   * event evaluator for the SMTPAppender.
   */
  public void setEvaluatorClass(String value) {
    evaluator = (TriggeringEventEvaluator) OptionConverter.instantiateByClassName(value,
                                                                                  TriggeringEventEvaluator.class,
                                                                                  evaluator);
  }

  /**
   * The <b>LocationInfo</b> option takes a boolean value. By default, it is set
   * to false which means there will be no effort to extract the location
   * information related to the event. As a result, the layout that formats the
   * events as they are sent out in an e-mail is likely to place the wrong
   * location information (if present in the format).
   *
   * <p>
   * Location information extraction is comparatively very slow and should be
   * avoided unless performance is not a concern.
   */
  public void setLocationInfo(boolean locationInfo) {
    this.locationInfo = locationInfo;
  }

  /**
   * Returns value of the <b>LocationInfo</b> option.
   */
  public boolean getLocationInfo() {
    return locationInfo;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
