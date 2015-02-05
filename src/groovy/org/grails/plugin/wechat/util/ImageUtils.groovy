package org.grails.plugin.wechat.util

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import javax.imageio.ImageIO
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage

/**
 * Created by haihxiao on 2015/2/2.
 */
class ImageUtils {
    private static final Log log = LogFactory.getLog(ImageUtils.class)

    static File bestForWechat(File imageFile) {
        try {
            //read image file
            BufferedImage bufferedImage = ImageIO.read(imageFile)

            int width = bufferedImage.width
            int height = bufferedImage.height

            float ratio1 = 1, ratio2 = 1
            if(width > MAX_WIDTH) {
                ratio1 = MAX_WIDTH / width
            }
            if(height > MAX_HEIGHT) {
                ratio2 = MAX_HEIGHT / height
            }
            float ratio = Math.max(ratio1, ratio2)
            int newWidth = (int)(width * ratio)
            int newHeight = (int)(height * ratio)

            boolean isJpg = imageFile.name.toLowerCase().endsWith('.jpg')
            if(newWidth == width && newHeight == height && isJpg) {
                return imageFile
            } else {
                // create a blank, RGB, same width and height, and a white background
                BufferedImage newBufferedImage = resize(bufferedImage, newWidth, newHeight, true)
                // write to jpeg file
                File jpgFile = new File("C:/Temp", (isJpg ? imageFile.name : imageFile.name + ".jpg"))
                ImageIO.write(newBufferedImage, "jpg", jpgFile);
                return jpgFile
            }
        } catch (IOException e) {
            log.error(e.message, e)
            return imageFile
        }
    }

    static BufferedImage resize(Image originalImage, int scaledWidth, int scaledHeight, boolean preserveAlpha = true) {
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType)
        Graphics2D g = scaledBI.createGraphics()
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src)
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null)
        g.dispose()
        return scaledBI
    }

    private static final int MAX_WIDTH = 360
    private static final int MAX_HEIGHT = 200
}
