/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.checkpoint.util;

import java.io.ByteArrayOutputStream;

/**
 * link break conversion utils with only a subset of all the features in dos2unix.
 * @link https://sourceforge.net/p/dos2unix/dos2unix/ci/master/tree/dos2unix/dos2unix.c
 * @author zhangt2333
 */
public class Dos2UnixUtils {

    public static final byte CR = 0x0d;
    public static final byte LF = 0x0a;

    public static boolean isDos2Unix(String mode) {
        return "dos2unix".equalsIgnoreCase(mode);
    }

    public static boolean isUnix2Dos(String mode) {
        return "unix2dos".equalsIgnoreCase(mode);
    }

    public static String handle(String bytes, String mode) {
        return new String(handle(bytes.getBytes(), mode));
    }

    public static byte[] handle(byte[] bytes, String mode) {
        if (isDos2Unix(mode)) {
            return dos2unix(bytes);
        }
        if (isUnix2Dos(mode)) {
            return unix2dos(bytes);
        }
        return bytes;
    }

    /**
     * CR-LF -> LF
     * other -> other
     */
    public static byte[] dos2unix(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        for (int i = 0, length = bytes.length; i < length; i++) {
            byte b = bytes[i];
            if (i + 1 < length && CR == b && LF == bytes[i + 1]) {
                baos.write(LF);
                i++;
            } else {
                baos.write(b);
            }
        }
        return baos.toByteArray();
    }

    /**
     * CR-LF -> CR-LF
     * LF    -> CR-LF
     */
    public static byte[] unix2dos(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        for (int i = 0, length = bytes.length; i < length; i++) {
            byte b = bytes[i];
            if (i + 1 < length && CR == b && LF == bytes[i + 1]) {
                baos.write(CR);
                baos.write(LF);
                i++;
            } else if (LF == b) {
                baos.write(CR);
                baos.write(LF);
            } else {
                baos.write(b);
            }
        }
        return baos.toByteArray();
    }
}
