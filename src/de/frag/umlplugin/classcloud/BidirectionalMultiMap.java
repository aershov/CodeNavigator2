package de.frag.umlplugin.classcloud;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Bidirectional map that provides access to pairs of values that each can be used as key or value.
 */
public class BidirectionalMultiMap<E1,E2>
{
  private final Map<E1, Set<E2>> forwardMap  = new HashMap<E1, Set<E2>> ();
  private final Map<E2, Set<E1>> backwardMap = new HashMap<E2, Set<E1>> ();

  /**
   * Adds an element pair.
   * @param e1 first element of pair
   * @param e2 second element of pair
   */
  public void add (@NotNull E1 e1, @NotNull E2 e2)
  {
    Set<E2> forwardSet = forwardMap.get (e1);
    if (forwardSet == null)
    {
      forwardSet = new HashSet<E2> ();
      forwardMap.put (e1, forwardSet);
    }
    forwardSet.add (e2);

    Set<E1> backwardSet = backwardMap.get (e2);
    if (backwardSet == null)
    {
      backwardSet = new HashSet<E1> ();
      backwardMap.put (e2, backwardSet);
    }
    backwardSet.add (e1);
  }

  /**
   * Gets other element of element pair for given first element.
   * @param key first element of pair that acts as key.
   * @return second element or null, if no pair exists where given element is first element
   */
  public @Nullable Set<E2> getForward (@NotNull E1 key)
  {
    return forwardMap.get (key);
  }

  /**
   * Gets other element of element pair for given second element.
   * @param key second element of pair that acts as key.
   * @return first element or null, if no pair exists where given element is second element
   */
  public @Nullable Set<E1> getBackward (@NotNull E2 key)
  {
    return backwardMap.get (key);
  }

  /**
   * Gets collection of all elements that are first element of element pairs.
   * @return collection of first elements in all pairs
   */
  public @NotNull Collection<E1> forwardKeys ()
  {
    return forwardMap.keySet ();
  }

  /**
   * Gets collection of all elements that are second element of element pairs.
   * @return collection of second elements in all pairs
   */
  public @NotNull Collection<E2> backwardKeys ()
  {
    return backwardMap.keySet ();
  }

  /**
   * Gets number of different elements that act as first element in element pairs.
   * @return number of different first elements
   */
  public int forwardKeyCount ()
  {
    return forwardMap.size ();
  }

  /**
   * Gets number of different elements that act as second element in element pairs.
   * @return number of different second elements
   */
  public int backwardKeyCount ()
  {
    return backwardMap.size ();
  }

  /**
   * Clears this map.
   */
  public void clear ()
  {
    forwardMap.clear ();
    backwardMap.clear ();
  }
}
