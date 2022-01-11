/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

/**
 * Utils for generating captcha
 * @author Internet
 * @author zhangt2333
 */
public class CaptchaUtils {

    private static final Random random = new Random();

    // 验证码的宽
    private static final int width = 200;

    // 验证码的高
    private static final int height = 50;

    // 验证码中夹杂的干扰线数量
    private static final int lineSize = 30;

    // 验证码中夹杂的干扰点数量
    private static final int pointSize = 150;

    // 随机串, 去掉了一些肉眼难以识别的字母数字
    private static final String randomString = "3456789ABCDEFGHJKMNPQRSTUVWXYabcdefghjkmnpqrstuvwxy";

    private static final Color BLANK = new Color(0, 0, 0);

    // 获取验证码字符个数
    private static int getRandomStrNum() {
        return 4 + random.nextInt(3);
    }

    // 字体的设置
    private static Font getFont() {
        return new Font(Font.DIALOG, Font.HANGING_BASELINE, 40);
    }

    // 颜色的设置, 已改为了二值验证码
    private static Color getRandomColor(int fc, int bc) {
        return BLANK;
//        fc = Math.min(fc, 255);
//        bc = Math.min(bc, 255);
//
//        int r = fc + random.nextInt(bc - fc - 16);
//        int g = fc + random.nextInt(bc - fc - 14);
//        int b = fc + random.nextInt(bc - fc - 12);
//
//        return new Color(r, g, b);
    }

    // 干扰线的绘制
    private static void drawLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(30);
        int yl = random.nextInt(10);
        g.drawLine(x, y, x + xl, y + yl);
    }

    // 干扰点的绘制
    private static void drawPoint(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        g.drawOval(x, y, 2, 2);
    }

    // 随机字符的获取
    public static String getRandomString(int num) {
        num = num > 0 ? num : 1;
        if (num == 1) {
            return String.valueOf(randomString.charAt(random.nextInt(randomString.length())));
        }
        StringBuilder sb = new StringBuilder();
        while (num-- > 0) {
            sb.append(randomString.charAt(random.nextInt(randomString.length())));
        }
        return sb.toString();
    }

    // 字符串的绘制
    private static String drawString(Graphics g, String randomStr, int i) {
        g.setFont(getFont());
        g.setColor(getRandomColor(108, 190));
        //System.out.println(random.nextInt(randomString.length()));
        String rand = getRandomString(1);
        randomStr += rand;
        g.translate(random.nextInt(3), random.nextInt(6));
        g.drawString(rand, 30 * i + random.nextInt(15), 25 + random.nextInt(5));
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
        // 干扰线
        for (int i = 0; i < pointSize; i++) {
            drawPoint(g);
        }
        // 随机字符
        String randomStr = "";
        for (int i = 0, length = getRandomStrNum(); i < length; i++) {
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
        private final String randomStr;
        private final String base64;

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