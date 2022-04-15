package com.zz.passtool.infrastructure;

import java.util.Collections;

/**
 * @author 书台
 * @since 2022/4/15 11:26 上午
 */
public class ZeaRandom {
    public ZeaRandom() {

    }

    static class Seed {
        ZeaData data;

        public Seed(ZeaData data){
            this.data=data.align(Constants.ALGORITHM_BLOCK_SIZE);
        }

        public static Seed of(Integer integer) {
            return new ZeaRandom.Seed(ZeaData.from(Collections.singletonList(integer)));
        }
    }
}
