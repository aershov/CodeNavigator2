package de.frag.umlplugin.history;

import junit.framework.TestCase;

/**
 * Tests the history list.
 */
public class TestHistoryList extends TestCase
{
  public void testHistoryList ()
  {
    HistoryList<String> list = new HistoryList<String> (3);
    assertEquals (3, list.getMaxSize ());
    assertEquals (0, list.size ());
    assertFalse  (list.canStepBack ());
    assertFalse  (list.canStepForward ());

    list.add ("1");
    assertEquals (1, list.size ());
    assertTrue   (list.canStepBack ());
    assertFalse  (list.canStepForward ());

    list.add ("2");
    assertEquals (2, list.size ());
    assertTrue   (list.canStepBack ());
    assertFalse  (list.canStepForward ());

    list.add ("3");
    assertEquals (3, list.size ());
    assertTrue   (list.canStepBack ());
    assertFalse  (list.canStepForward ());

    list.add ("4");
    assertEquals (3, list.size ());
    assertTrue   (list.canStepBack ());
    assertFalse  (list.canStepForward ());

    assertIterator (list, "2", "3", "4");

    assertEquals ("4", list.stepBack ());
    assertTrue   (list.canStepBack ());
    assertTrue   (list.canStepForward ());

    assertEquals ("3", list.stepBack ());
    assertTrue   (list.canStepBack ());
    assertTrue   (list.canStepForward ());

    assertEquals ("2", list.stepBack ());
    assertFalse  (list.canStepBack ());
    assertTrue   (list.canStepForward ());

    assertEquals ("2", list.stepForward ());
    assertTrue   (list.canStepBack ());
    assertTrue   (list.canStepForward ());

    assertEquals ("3", list.stepForward ());
    assertTrue   (list.canStepBack ());
    assertTrue   (list.canStepForward ());

    assertEquals ("4", list.stepForward ());
    assertTrue   (list.canStepBack ());
    assertFalse  (list.canStepForward ());

    assertEquals ("4", list.stepBack ());
    list.add     ("4b");
    assertEquals (3, list.size ());
    assertTrue   (list.canStepBack ());
    assertFalse  (list.canStepForward ());

    assertEquals ("4b", list.stepBack ());
    assertEquals ("3",  list.stepBack ());
    list.add     ("3b");
    assertEquals (2, list.size ());
    assertTrue   (list.canStepBack ());
    assertFalse  (list.canStepForward ());

    assertIterator (list, "2", "3b");
    list.rewind ();
    assertIterator (list);
    assertEquals ("2",  list.stepForward ());
    assertEquals ("3b", list.stepForward ());
  }

  private void assertIterator (HistoryList<String> list, String ... expected)
  {
    int index = 0;
    for (String s : list)
    {
      assertEquals (expected [index], s);
      index++;
    }
    assertEquals (expected.length, index);
  }
}
