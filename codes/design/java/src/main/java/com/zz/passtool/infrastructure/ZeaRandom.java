package com.zz.passtool.infrastructure;

/**
 * @author 书台
 * @since 2022/4/15 11:26 上午
 */
public class ZeaRandom {
    public static final int ALGORITHM_BLOCK_SIZE = 32;
    ZeaData                 data;

    public ZeaRandom(ZeaData data) {
        this.data = data.align(ALGORITHM_BLOCK_SIZE);
    }
}
