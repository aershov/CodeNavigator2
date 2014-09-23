package de.frag.umlplugin.psi;

import org.jetbrains.annotations.NotNull;

/**
   * Describes a reason for a dependency.
 */
public class DependencyReason
{
  private final UsageType usageType;
  private final int       offset;

  /**
   * Creates a new dependency reason.
   * @param usageType usage type of dependency
   * @param offset source offset
   */
  public DependencyReason (@NotNull UsageType usageType, int offset)
  {
    this.usageType = usageType;
    this.offset = offset;
  }

  /**
   * Gets usage type.
   * @return usage type
   */
  public @NotNull UsageType getUsageType ()
  {
    return usageType;
  }

  /**
   * Gets source offset.
   * @return source offset
   */
  public int getOffset ()
  {
    return offset;
  }

  /**
   * Creates a string representation of this reason.
   * @return string representation
   */
  public @NotNull String toString ()
  {
    return usageType + " (" + offset + ")";
  }
}
