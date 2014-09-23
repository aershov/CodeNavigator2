package de.frag.umlplugin.classcloud;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Executes tasks with a given delay. If a new task is scheduled for execution, existing scheduled tasks
 * will be discarded, so only the last scheduled task is executed.
 */
public class DelayedExecutor
{
  private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor (1);
  private final int delay;

  /**
   * Creates a new delayed executor.
   * @param delay delay in milliseconds
   */
  public DelayedExecutor (int delay)
  {
    this.delay = delay;
    executor.setExecuteExistingDelayedTasksAfterShutdownPolicy (false);
    executor.setMaximumPoolSize (1);
  }

  /**
   * Schedules given task for execution after delay specified in constructor. If there are any tasks
   * that are scheduled for execution but not started, these tasks will be discarded.
   * @param task task to schedule for execution
   */
  public void execute (@NotNull Runnable task)
  {
    executor.getQueue ().clear ();
    executor.schedule (task, delay, TimeUnit.MILLISECONDS);
  }
}
