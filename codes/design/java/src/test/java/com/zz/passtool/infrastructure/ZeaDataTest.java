package com.zz.passtool.infrastructure;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.zz.utils.ParamCheckUtil.ParamCheckException;

/**
 * @author 书台
 * @since 2022/4/11 7:32 下午
 */
class ZeaDataTest {
    private static final ZeaData zeaData = ZeaData.from("天王盖地虎");

    @Test
    void isAligned() {
        Assertions.assertFalse(zeaData.isAligned());
        Assertions.assertTrue(zeaData.align(10).isAligned());
    }

    @Test
    void align() {
        ZeaData alignedZeaData = zeaData.align(3);
        List<Integer> data = getData(alignedZeaData);
        Assertions.assertNotNull(data);
        System.out.println(data);
        Assertions.assertEquals(0, data.get(data.size() - 1));
        Assertions.assertEquals(5, data.get(data.size() - 2));
        Assertions.assertEquals(3, data.get(data.size() - 3));
        Assertions.assertEquals(0, data.get(data.size() - 4));
    }

    @Test
    void unalign() {
        Assertions.assertEquals(getData(zeaData), getData(zeaData.align(13).unalign()));
        Assertions.assertThrows(ParamCheckException.class, () -> getData(zeaData.align(0).unalign()));
    }

    @Test
    void merge() {
        String a = "123", b = "yyy";
        Assertions.assertEquals(ZeaData.from(a + b), ZeaData.merge(ZeaData.from(a), ZeaData.from(b)));
    }

    @Test
    void from() {
        List<Integer> data = new ArrayList<>();
        data.add(22825);
        data.add(22825);
        ZeaData zeaData1 = ZeaData.from(data);
        ZeaData zeaData2 = zeaData1.align(2);
        data = getData(zeaData2);
        Assertions.assertEquals(0, data.get(1));
        Assertions.assertEquals(0, data.get(3));
        Assertions.assertEquals(0, data.get(4));
        Assertions.assertEquals(0, data.get(7));
        System.out.println(zeaData2);
    }

    @Test
    void to() {}
    private List<Integer> getData(ZeaData zeaData) {
        try {
            Field data = ZeaData.class.getDeclaredField("data");
            data.setAccessible(true);
            return (List<Integer>)data.get(zeaData);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
}