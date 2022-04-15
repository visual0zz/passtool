package com.zz.passtool.infrastructure;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    void 对齐与取消对齐() {
        Assertions.assertFalse(zeaData.isAligned());
        Assertions.assertTrue(zeaData.align(10).isAligned());
        ZeaData alignedZeaData = zeaData.align(3);
        List<Integer> data = getData(alignedZeaData);
        Assertions.assertNotNull(data);
        System.out.println(data);
        Assertions.assertEquals(0, data.get(data.size() - 1));
        Assertions.assertEquals(5, data.get(data.size() - 2));
        Assertions.assertEquals(3, data.get(data.size() - 3));
        Assertions.assertEquals(0, data.get(data.size() - 4));
        Assertions.assertEquals(getData(zeaData), getData(zeaData.align(13).unalign()));
        Assertions.assertThrows(ParamCheckException.class, () -> getData(zeaData.align(0).unalign()));
    }

    @Test
    void merge() {
        String a = "123", b = "yyy";
        Assertions.assertEquals(ZeaData.from(a + b), ZeaData.merge(ZeaData.from(a), ZeaData.from(b)));
    }

    @Test
    void fromIntList() {
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
    }

    @Test
    void toIntList() {
        Assertions.assertEquals(zeaData.transferToString(), zeaData.align(123).unalign().transferToString());
        Assertions.assertEquals(zeaData.transferToList(Integer.class),
            zeaData.align(123).unalign().transferToList(Integer.class));
        Assertions.assertEquals("天王盖地虎", zeaData.transferToString());
        List<Integer> data = new ArrayList<>();
        data.add(22825);
        data.add(22825);
        ZeaData zeaData1 = ZeaData.from(data);
        Assertions.assertEquals(data, zeaData1.transferToList(Integer.class));
    }

    @Test
    void 测试不同类型的兼容性() {
        Random random = new Random(123145995324011L);
        for (int index = 0; index <= 7; index++) {
            List<Byte> data = new ArrayList<>();
            data.add((byte)random.nextInt());
            data.add((byte)random.nextInt());
            data.add((byte)random.nextInt());
            data.add((byte)random.nextInt());
            data.add((byte)random.nextInt());
            data.add((byte)random.nextInt());
            data.add((byte)random.nextInt());
            data.add((byte)random.nextInt());
            data.add((byte)random.nextInt());
            data.add((byte)random.nextInt());
            ZeaData zeaData = ZeaData.from(data);
            ZeaData zeaData1 = zeaData.align(13).unalign();
            Assertions.assertNotEquals(data, getData(zeaData1));
            Assertions.assertEquals(data.stream().map(e -> e & 0xff).collect(Collectors.toList()), getData(zeaData1));
            Assertions.assertEquals(data.stream().map(e -> (byte)(e & 0xff)).collect(Collectors.toList()),
                zeaData1.transferToList(Byte.class));
        }
        for (int index = 0; index <= 7; index++) {
            List<Character> data = new ArrayList<>();
            data.add((char)random.nextInt());
            data.add((char)random.nextInt());
            data.add((char)random.nextInt());
            data.add((char)random.nextInt());
            data.add((char)random.nextInt());
            data.add((char)random.nextInt());
            data.add((char)random.nextInt());
            data.add((char)random.nextInt());
            data.add((char)random.nextInt());
            data.add((char)random.nextInt());
            ZeaData zeaData = ZeaData.from(data);
            ZeaData zeaData1 = zeaData.align(18).unalign();
            Assertions.assertNotEquals(data, getData(zeaData1));
            Assertions.assertEquals(data.stream().map(e -> e & 0xffff).collect(Collectors.toList()), getData(zeaData1));
            Assertions.assertEquals(data.stream().map(e -> (char)(e & 0xffff)).collect(Collectors.toList()),
                zeaData1.transferToList(Character.class));
        }
        for (int index = 0; index <= 7; index++) {
            List<Short> data = new ArrayList<>();
            data.add((short)random.nextInt());
            data.add((short)random.nextInt());
            data.add((short)random.nextInt());
            data.add((short)random.nextInt());
            data.add((short)random.nextInt());
            data.add((short)random.nextInt());
            data.add((short)random.nextInt());
            data.add((short)random.nextInt());
            data.add((short)random.nextInt());
            data.add((short)random.nextInt());
            ZeaData zeaData = ZeaData.from(data);
            ZeaData zeaData1 = zeaData.align(3).unalign();
            Assertions.assertNotEquals(data, getData(zeaData1));
            Assertions.assertEquals(data.stream().map(e -> e & 0xffff).collect(Collectors.toList()), getData(zeaData1));
            Assertions.assertEquals(data.stream().map(e -> (short)(e & 0xffff)).collect(Collectors.toList()),
                zeaData1.transferToList(Short.class));
        }
        for (int index = 0; index <= 7; index++) {
            List<Integer> data = new ArrayList<>();
            data.add(random.nextInt());
            data.add(random.nextInt());
            data.add(random.nextInt());
            data.add(random.nextInt());
            data.add(random.nextInt());
            data.add(random.nextInt());
            data.add(random.nextInt());
            data.add(random.nextInt());
            data.add(random.nextInt());
            data.add(random.nextInt());
            ZeaData zeaData = ZeaData.from(data);
            ZeaData zeaData1 = zeaData.align(31).unalign();
            Assertions.assertEquals(data, zeaData1.transferToList(Integer.class));
        }
        for (int index = 0; index <= 7; index++) {
            List<Long> data = new ArrayList<>();
            data.add(random.nextLong());
            data.add(random.nextLong());
            data.add(random.nextLong());
            data.add(random.nextLong());
            data.add(random.nextLong());
            data.add(random.nextLong());
            data.add(random.nextLong());
            data.add(random.nextLong());
            data.add(random.nextLong());
            data.add(random.nextLong());
            ZeaData zeaData = ZeaData.from(data);
            ZeaData zeaData1 = zeaData.align(23).unalign();
            System.out.println("data=" + data);
            System.out.println("zeaData=" + zeaData);
            System.out.println("zeaData=" + zeaData1);
            Assertions.assertEquals(data, zeaData1.transferToList(Long.class));
        }
    }

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