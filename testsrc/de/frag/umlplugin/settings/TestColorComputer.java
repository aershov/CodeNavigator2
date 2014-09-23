package de.frag.umlplugin.settings;

import de.frag.umlplugin.classcloud.ColorComputer;
import junit.framework.TestCase;

public class TestColorComputer extends TestCase
{
  public void testFade ()
  {
    Settings settings = new Settings ();
    assertEquals (settings.getNormalCloudColor (),   ColorComputer.computeColor (10, 5, 20, 10, settings));
    assertEquals (settings.getUsedCloudColor (),     ColorComputer.computeColor (20, 5, 20, 10, settings));
    assertEquals (settings.getExtendedCloudColor (), ColorComputer.computeColor (10, 0, 20, 10, settings));
  }
}
