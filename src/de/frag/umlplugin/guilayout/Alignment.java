package de.frag.umlplugin.guilayout;

/**
  * Alignment are used to describe the horizontal or vertical alignment
  * of a component within its available space.
  * @author Frank Gerberding
  */
interface Alignment
{
  /**
    * Get the offset of a component with the given preferred size and
    * the specified available space.
    * @param offset start of available space
    * @param availableSpace size of available space
    * @param preferredSpace preferred size of component
   * @return offset
    */
  public int getOffset (int offset, int availableSpace, int preferredSpace);
  /**
    * Get the size of a component with the given preferred size and
    * the specified available space.
    * @param availableSpace size of available space
    * @param preferredSpace preferred size of component
   * @return size
    */
  public int getSize (int availableSpace, int preferredSpace);

  /**
    * Alignment that places the component at the start of the available space
    * and that uses the preferred component size.
    */
  public static class StartAlignment implements Alignment
  {
    public int getOffset (int offset, int availableSpace, int preferredSpace)
    {
      return offset;
    }
    
    public int getSize (int availableSpace, int preferredSpace)
    {
      return preferredSpace;
    }
    
    public String toString ()
    {
      return "StartAlignment";
    }
  }

  /**
    * Alignment that places the component at the end of the available space
    * and that uses the preferred component size.
    */
  public static class EndAlignment implements Alignment
  {
    public int getOffset (int offset, int availableSpace, int preferredSpace)
    {
      return offset + (availableSpace - preferredSpace);
    }
    
    public int getSize (int availableSpace, int preferredSpace)
    {
      return preferredSpace;
    }
    
    public String toString ()
    {
      return "EndAlignment";
    }
  }

  /**
    * Alignment that places the component at the center of the available space
    * and that uses the preferred component size.
    */
  public static class CenterAlignment implements Alignment
  {
    public int getOffset (int offset, int availableSpace, int preferredSpace)
    {
      return offset + (availableSpace - preferredSpace) / 2;
    }
    
    public int getSize (int availableSpace, int preferredSpace)
    {
      return preferredSpace;
    }

    public String toString ()
    {
      return "CenterAlignment";
    }
  }

  /**
    * Alignment that uses as much of the given space as available and that
    * stretches the available space.
    */
  public static class StretchAlignment implements Alignment
  {
    public int getOffset (int offset, int availableSpace, int preferredSpace)
    {
      return offset;
    }
    
    public int getSize (int availableSpace, int preferredSpace)
    {
      return availableSpace;
    }
    
    public String toString ()
    {
      return "StretchAlignment";
    }
  }

  /**
    * Alignment that uses the complete available space.
    *
    */
  public static class FillAlignment implements Alignment
  {
    public int getOffset (int offset, int availableSpace, int preferredSpace)
    {
      return offset;
    }
    
    public int getSize (int availableSpace, int preferredSpace)
    {
      return availableSpace;
    }
    
    public String toString ()
    {
      return "FillAlignment";
    }
  }

  /**
    * Alignment that makes the component as big as the biggest component
    * within the same container.
    */
  public static class MaxAlignment implements Alignment
  {
    public int getOffset (int offset, int availableSpace, int preferredSpace)
    {
      return offset;
    }
    
    public int getSize (int availableSpace, int preferredSpace)
    {
      return availableSpace;
    }
    
    public String toString ()
    {
      return "MaxAlignment";
    }
  }
}
