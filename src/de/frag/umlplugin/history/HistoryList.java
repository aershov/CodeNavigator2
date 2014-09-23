package de.frag.umlplugin.history;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Bounded list for history storage.
 */
public class HistoryList<T> implements Iterable<T>
{
  private final int maxSize;
  private final List<T> elements = new ArrayList<T> ();
  private int nextIndex = 0;

  /**
   * Creates a new bounded list with given maximum size.
   * @param maxSize maximum size
   */
  public HistoryList (int maxSize)
  {
    this.maxSize = maxSize;
  }

  /**
   * Gets iterator that iterates over all active elements in this history list.
   * @return iterator for iterating over all elements in history "past".
   */
  public @NotNull Iterator<T> iterator ()
  {
    return new Iterator<T> ()
    {
      private int index = 0;

      public boolean hasNext ()
      {
        return index < nextIndex;
      }

      public @NotNull T next ()
      {
        return elements.get (index++);
      }

      public void remove ()
      {
        throw new UnsupportedOperationException ();
      }
    };
  }

  /**
   * Clears this history.
   */
  public void clear ()
  {
    elements.clear ();
    nextIndex = 0;
  }

  /**
   * Adds the given element to this list.
   * If the list size exceeds the maximum size after adding the element, the list start will be truncated.
   * if the element is inserted somewhere in the middle of the list because of calls to {@link #stepBack()},
   * then alle elements after the added element will be discarded.
   * @param element element to add
   */
  public void add (@NotNull T element)
  {
    // insert somewhere in the middle? then remove all elements with index >= next index
    while (nextIndex < elements.size ())
    {
      elements.remove (elements.size () - 1);
    }
    elements.add (element);
    if (elements.size () > maxSize)
    {
      elements.remove (0);
    }
    else
    {
      nextIndex++;
    }
  }

  /**
   * Steps back in the history and returns the corresponing element. Without calls to {@link #stepBack()}, the
   * result will be the element that was added just before the last added element.
   * @return current element
   */
  public @NotNull T stepBack ()
  {
    nextIndex--;
    return elements.get (nextIndex);
  }

  /**
   * Steps forward in the history and returns the corresponing element.
   * @return current element
   */
  public @NotNull T stepForward ()
  {
    T result = elements.get (nextIndex);
    nextIndex++;
    return result;
  }

  /**
   * Rewinds history to beginning.
   */
  public void rewind ()
  {
    nextIndex = 0;
  }

  /**
   * Checks whether a step back in the history is possible.
   * @return true, if a step back is possible; false, if the current position is already the first list position.
   */
  public boolean canStepBack ()
  {
    return nextIndex > 0;
  }

  /**
   * Checks whether a step forward in the history is possible.
   * @return true, if a step forward is possible; false, if the current position is already the last list position.
   */
  public boolean canStepForward ()
  {
    return nextIndex < size ();
  }

  /**
   * Gets the current size of this list.
   * @return current size
   */
  public int size ()
  {
    return elements.size ();
  }

  /**
   * Gets the maximum size of this bounded list.
   * @return maximum size
   */
  public int getMaxSize ()
  {
    return maxSize;
  }

  /**
   * Converts this history list to a list.
   * @param includeForwardableEntries true, if entries that can be enabled by calls to {@link #stepForward()}
   *        should be included; false otherwise
   * @return converted list
   */
  public @NotNull List<T> toList (boolean includeForwardableEntries)
  {
    return new ArrayList<T> (elements.subList (0, includeForwardableEntries ? elements.size () : nextIndex));
  }

  /**
   * Returns a string representation of the list.
   * @return a string representation of the list.
   */
  public @NotNull String toString ()
  {
    return elements.toString () + ", next index = " + nextIndex;
  }
}
