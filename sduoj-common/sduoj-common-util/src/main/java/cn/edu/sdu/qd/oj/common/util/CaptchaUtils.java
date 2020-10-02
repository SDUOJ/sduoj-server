package cn.edu.sdu.qd.oj.common.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

/**
 * @Description 验证码工具类 code from the Internet
 **/
public class CaptchaUtils {

    private static Random random = new Random();
    private static int width = 165;      // 验证码的宽
    private static int height = 45;      // 验证码的高
    private static int lineSize = 30;    // 验证码中夹杂的干扰线数量
    private static int randomStrNum = 4; // 验证码字符个数

    private static String randomString = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWSYZ";

    // 字体的设置
    private static Font getFont() {
        return new Font("Times New Roman", Font.ROMAN_BASELINE, 40);
    }

    // 颜色的设置
    private static Color getRandomColor(int fc, int bc) {
        fc = Math.min(fc, 255);
        bc = Math.min(bc, 255);

        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 12);

        return new Color(r, g, b);
    }

    // 干扰线的绘制
    private static void drawLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(20);
        int yl = random.nextInt(10);
        g.drawLine(x, y, x + xl, y + yl);

    }

    // 随机字符的获取
    private static String getRandomString(int num) {
        num = num > 0 ? num : randomString.length();
        return String.valueOf(randomString.charAt(random.nextInt(num)));
    }

    // 字符串的绘制
    private static String drawString(Graphics g, String randomStr, int i) {
        g.setFont(getFont());
        g.setColor(getRandomColor(108, 190));
        //System.out.println(random.nextInt(randomString.length()));
        String rand = getRandomString(random.nextInt(randomString.length()));
        randomStr += rand;
        g.translate(random.nextInt(3), random.nextInt(6));
        g.drawString(rand, 40 * i + 10, 25);
        return randomStr;
    }

    // 生成随机图片的base64编码字符串
    public static CaptchaEntity getRandomBase64Captcha() {
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, width, height);
        g.setColor(getRandomColor(105, 189));
        g.setFont(getFont());
        // 干扰线
        for (int i = 0; i < lineSize; i++) {
            drawLine(g);
        }
        //随机字符
        String randomStr = "";
        for (int i = 0; i < randomStrNum; i++) {
            randomStr = drawString(g, randomStr, i);
        }
        g.dispose();
        String base64String = "";
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", bos);

            byte[] bytes = bos.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            base64String = "data:image/png;base64," + encoder.encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CaptchaEntity(randomStr, base64String);
    }

    public static class CaptchaEntity {
        private String randomStr;
        private String base64;

        public CaptchaEntity(String randomStr, String base64) {
            this.randomStr = randomStr;
            this.base64 = base64;
        }

        public String getRandomStr() {
            return randomStr;
        }

        public String getBase64() {
            return base64;
        }
    }
}
