package de.frag.umlplugin;

import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;

import java.awt.*;

/**
 * Error report submitter to support bug reports.
 */
public class ErrorHandler extends ErrorReportSubmitter
{
  /**
   * @return "Report to vendor" action text to be used in Error Reporter user interface. For example: "Report to
   *         JetBrains".
   */
  public String getReportActionText ()
  {
    return null;
  }

  /**
   * This method is called whenever fatal error (aka exception) in plugin code had happened and user decided to report
   * this problem to plugin vendor.
   * @param events          sequence of the fatal error descriptors. Fatal errors that happened immediately one after
   *                        another most probably caused by first one that happened so it's a common practice to submit
   *                        only first one. Array passed is guaranteed to have at least one element.
   * @param parentComponent one usually wants to show up a dialog asking user for additional details and probably
   *                        authentication info. parentComponent parameter is passed so dialog that would come up would
   *                        be properly aligned with its parent dialog (IDE Fatal Errors).
   * @return submission result status.
   */
  public SubmittedReportInfo submit (IdeaLoggingEvent[] events, Component parentComponent)
  {
    return null;
  }
}
