package de.frag.umlplugin.graphio;

import java.awt.image.BufferedImage;

/**
 * Scales down an image using an area averaging filter.
 */
public class ImageScaler
{
  private final int targetHeight;

  /**
   * Creates a new image scaler that scales to given target height.
   * @param targetHeight target height for scaled images
   */
  public ImageScaler (int targetHeight)
  {
    this.targetHeight = targetHeight;
  }

  /**
   * Scales down given image and returns new image.
   * @param src source image to scale down
   * @return scaled image
   */
  public BufferedImage filter (BufferedImage src)
  {
    int    srcWidth  = src.getWidth ();
    int    srcHeight = src.getHeight ();
    double scale     = (srcHeight != 0) ? (double) targetHeight / (double) srcHeight : 1.0;
    int    dstWidth  = (int) Math.ceil (srcWidth  * scale);
    int    dstHeight = (int) Math.ceil (srcHeight * scale);
    double stepH     = (double) srcWidth  / (double) dstWidth;
    double stepV     = (double) srcHeight / (double) dstHeight;

    BufferedImage dst = new BufferedImage (dstWidth, dstHeight, src.getType ());
    int [] srcPixels = src.getRGB (0, 0, srcWidth, srcHeight, null, 0, srcWidth);
    int [] dstPixels = new int [dstWidth * dstHeight];
    for (int dstY = 0; dstY < dstHeight; dstY++)
    {
      for (int dstX = 0; dstX < dstWidth; dstX++)
      {
        int srcYStart = (int) (dstY * stepV);
        int srcYEnd   = (int) Math.min ((dstY + 1) * stepV, srcHeight);
        int srcXStart = (int) (dstX * stepH);
        int srcXEnd   = (int) Math.min ((dstX + 1) * stepH, srcWidth);
        int r = 0;
        int g = 0;
        int b = 0;
        int count = 0;
        for (int srcY = srcYStart; srcY < srcYEnd; srcY++)
        {
          for (int srcX = srcXStart; srcX < srcXEnd; srcX++)
          {
            int rgb = srcPixels[srcY * srcWidth + srcX];
            r += (rgb >> 16) & 0xFF;
            g += (rgb >>  8) & 0xFF;
            b +=  rgb        & 0xFF;
            count++;
          }
        }
        if (count > 0)
        {
          dstPixels [dstY * dstWidth + dstX] = (((r / count) & 0xFF) << 16) |
                                               (((g / count) & 0xFF) << 8) |
                                                ((b / count) & 0xFF);
        }
      }
    }
    dst.setRGB (0, 0, dstWidth, dstHeight, dstPixels, 0, dstWidth);
    return dst;
  }
}
