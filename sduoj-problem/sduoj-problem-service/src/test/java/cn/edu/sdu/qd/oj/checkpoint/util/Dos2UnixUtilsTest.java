/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.checkpoint.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author zhangt2333
 */
public class Dos2UnixUtilsTest {

    static byte[] dos = new byte[] {
        52, 56, 48, 48, Dos2UnixUtils.CR, Dos2UnixUtils.LF,
        52, 56, 48, 48, Dos2UnixUtils.CR, Dos2UnixUtils.LF,
        52, 56, 48, 48, Dos2UnixUtils.CR
    };

    static byte[] unix = new byte[] {
        52, 56, 48, 48, Dos2UnixUtils.LF,
        52, 56, 48, 48, Dos2UnixUtils.LF,
        52, 56, 48, 48, Dos2UnixUtils.CR
    };

    @Test
    public void dos2unix() {
        assertArrayEquals(unix, Dos2UnixUtils.dos2unix(dos));
    }

    @Test
    public void unix2dos() {
        assertArrayEquals(dos, Dos2UnixUtils.unix2dos(unix));
    }

//    @Test
//    public void largeFile() throws Exception {
//        byte[] dos = org.apache.commons.io.FileUtils.readFileToByteArray(new java.io.File("01.dos"));
//        byte[] unix = org.apache.commons.io.FileUtils.readFileToByteArray(new java.io.File("01.unix"));
//        assertArrayEquals(unix, Dos2UnixUtils.dos2unix(dos));
//        assertArrayEquals(dos, Dos2UnixUtils.unix2dos(unix));
//    }
}