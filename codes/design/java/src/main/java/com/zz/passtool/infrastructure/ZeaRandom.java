package com.zz.passtool.infrastructure;

import static com.zz.passtool.infrastructure.Constants.ALGORITHM_BLOCK_SIZE;

/**
 * @author 书台
 * @since 2022/4/15 11:26 上午
 */
public class ZeaRandom { 
    ZeaData                 data;

    public ZeaRandom(ZeaData data) {
        this.data = data.align(ALGORITHM_BLOCK_SIZE);
    }
}
