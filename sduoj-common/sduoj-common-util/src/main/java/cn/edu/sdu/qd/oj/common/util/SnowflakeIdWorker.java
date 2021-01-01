/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * @ClassName SnowflakeIdWorker
 * @Description Twitter 的 Snowflakes 实现，分布式自增ID
 *              64位ID = 1(始终为0符号位) + 42(毫秒级时间戳) + 5(数据中心ID 或 网络IP哈希) + 5(线程PID 或 机器ID) + 12(重复累加))
 * @Date 2020/4/6 14:40
 * @Version V1.0
 **/

public class SnowflakeIdWorker {
    // 时间起始标记点，作为基准，一般取系统的最近时间 （一旦确定不能变动！）
    private final static long TWEPOCH = 1577808000000L; // 2020-01-01 00:00:00
    // 机器标识位数
    private final static long WORKER_ID_BITS = 5L;
    // 数据中心标识位数
    private final static long DATACENTER_ID_BITS = 5L;
    // 机器ID最大值
    private final static long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
    // 数据中心ID最大值
    private final static long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);
    // 毫秒内自增位
    private final static long SEQUENCE_BITS = 12L;
    // 机器ID偏左移12位
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    // 数据中心ID左移17位
    private final static long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    // 时间毫秒左移22位
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    private final static long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

    // 上次生产id时间戳
    private static long lastTimestamp = -1L;

    // 数据中心ID 或 网络IP哈希
    private final long datacenterId;

    // 线程PID 或 机器ID
    private final long workerId;

    // 并发控制，重复累加序列
    private long sequence = 0L;

    public SnowflakeIdWorker() {
        this.datacenterId = getDatacenterId(MAX_DATACENTER_ID);
        this.workerId = getWorkerId(datacenterId, MAX_WORKER_ID);
    }

    /**
     * @param workerId     工作机器ID
     * @param datacenterId 序列号
     */
    public SnowflakeIdWorker(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获取下一个64位雪花ID
     * @return
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        /* 系统并发量不大时，sequence 一直为 0
         * 导致生成的 Id，后 12 位一直为 0，前端体验不友好
        if (lastTimestamp == timestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) { // 当前毫秒内计数满了，则等待下一秒
                timestamp = waitForNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        */

        /* 改成以下代码
         * 好处：前端体验较友好
         * 缺点：每 2^12 次请求会阻塞 1ms
         * TODO: 如果使用雪花 id 的并发量大起来了，就再把下面代码的换回上面的
         */
        sequence = (sequence + 1) & SEQUENCE_MASK;
        if (sequence == 0) { // 当前毫秒内计数满了，则等待下一秒
            timestamp = waitForNextMillis(lastTimestamp);
        }

        lastTimestamp = timestamp;
        // ID偏移组合生成最终的ID，并返回ID
        long nextId = ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
                     | (datacenterId << DATACENTER_ID_SHIFT)
                     | (workerId << WORKER_ID_SHIFT) | sequence;

        return nextId;
    }

    private long waitForNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        // 自旋等待下一毫秒
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获取 workerId
     */
    protected static long getWorkerId(long datacenterId, long maxWorkerId) {
        StringBuffer mpid = new StringBuffer();
        mpid.append(datacenterId);
        // 得到 JVM 线程 PID
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            mpid.append(name.split("@")[0]);
        }
        // MAC + PID 的 hashcode 获取16个低位
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 数据标识id部分
     */
    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                id = id % (maxDatacenterId + 1);
            }
        } catch (Exception e) {
            System.err.println("[getDatacenterId]: " + e.getMessage());
        }
        return id;
    }
}