package de.frag.umlplugin.classcloud;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Tests BidirectionalMap class.
 */
public class TestBidirectionalMap extends TestCase
{
  public void testMap ()
  {
    BidirectionalMultiMap<String,Integer> map = new BidirectionalMultiMap<String,Integer> ();
    assertEquals (0, map.backwardKeyCount ());
    assertEquals (0, map.forwardKeyCount ());

    map.add ("One",   1);
    map.add ("Two",   2);
    map.add ("Three", 3);
    assertEquals (3, map.forwardKeyCount ());
    assertEquals (3, map.backwardKeyCount ());
    assertEquals (new HashSet<String>  (Arrays.asList ("One", "Two", "Three")), map.forwardKeys ());
    assertEquals (new HashSet<Integer> (Arrays.asList (1, 2, 3)),               map.backwardKeys ());
    assertEquals (new HashSet<Integer> (Arrays.asList (1)),       map.getForward  ("One"));
    assertEquals (new HashSet<Integer> (Arrays.asList (2)),       map.getForward  ("Two"));
    assertEquals (new HashSet<Integer> (Arrays.asList (3)),       map.getForward  ("Three"));
    assertEquals (new HashSet<String>  (Arrays.asList ("One")),   map.getBackward (1));
    assertEquals (new HashSet<String>  (Arrays.asList ("Two")),   map.getBackward (2));
    assertEquals (new HashSet<String>  (Arrays.asList ("Three")), map.getBackward (3));

    map.add ("Eins",  1);
    map.add ("Adin",  1);
    map.add ("Uno",   1);
    map.add ("One",   1001);
    map.add ("One",    101);
    map.add ("One",     11);
    assertEquals (6, map.forwardKeyCount ());
    assertEquals (6, map.backwardKeyCount ());
    assertEquals (new HashSet<String>  (Arrays.asList ("One", "Two", "Three", "Eins", "Adin", "Uno")), map.forwardKeys ());
    assertEquals (new HashSet<Integer> (Arrays.asList (1, 2, 3, 11, 101, 1001)),                       map.backwardKeys ());
    assertEquals (new HashSet<Integer> (Arrays.asList (1, 11, 101, 1001)),       map.getForward  ("One"));
    assertEquals (new HashSet<Integer> (Arrays.asList (2)),       map.getForward  ("Two"));
    assertEquals (new HashSet<Integer> (Arrays.asList (3)),       map.getForward  ("Three"));
    assertEquals (new HashSet<String>  (Arrays.asList ("One", "Eins", "Adin", "Uno")),   map.getBackward (1));
    assertEquals (new HashSet<String>  (Arrays.asList ("Two")),   map.getBackward (2));
    assertEquals (new HashSet<String>  (Arrays.asList ("Three")), map.getBackward (3));
  }
}
