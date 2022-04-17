package com.zz.passtool.infrastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.zz.passtool.interfaces.exception.DataFormatTransferException;
import com.zz.passtool.interfaces.tag.ReadOnly;
import com.zz.utils.ParamCheckUtil;

/**
 * 用于加密或者求哈希的数据承载对象，内部的每个整数只使用低两字节
 * 
 * @author 书台
 * @since 2022/4/6 8:28 下午
 */
@ReadOnly
public final class ZeaData {

    /**
     * 在执行对齐填充时的最小填充长度,填充的格式为[原始数据,0...,对齐模，原始长度值低位，原始长度值高位],其中原始长度为整数
     */
    private static final int    MIN_ALIGN_LENGTH = 3;
    private static final int    HASH_MULTIPLIER_A = 12347;
    private static final int    HASH_MULTIPLIER_B = 54323;
    private static final int[]  HASH_INDEX_JUMP   = new int[] {3, 5, 7, 11, 13, 19, 23, 29, 31, 37, 67, 331};
    /**
     * 数据
     */
    private final List<Integer> data;

    ////////////////////////////// 填充 ////////////////////////////////

    /**
     * 数据是否是经过对齐的
     * 
     * @return true-对齐 false-未对齐
     */
    public boolean isAligned() {
        return getAlignmentInfo() != null;
    }

    /**
     * 将数据对齐于某个模，如果已经对齐过则返回原文
     * 
     * @param alignment 要对齐的模
     * @return 对齐之后的数据
     */
    public ZeaData align(int alignment) {
        ParamCheckUtil.assertTrue(alignment < 0xffff, "align block is too large");
        ParamCheckUtil.assertTrue(alignment > 0, "invalid align block size ");
        AlignmentInfo alignmentInfo = getAlignmentInfo();
        if (alignmentInfo == null) {
            List<Integer> fillData = new ArrayList<>();
            // 尾部填充数据的核心部分是原始数据的长度信息
            int size = data.size();
            fillData.add((size >> 16) & 0xffff);
            fillData.add(size & 0xffff);
            fillData.add(alignment);
            // 剩余部分填充0值,直到长度满足对齐的需要
            while ((fillData.size() + this.data.size()) % alignment != 0) {
                fillData.add(0);
            }
            Collections.reverse(fillData);
            List<Integer> result = new ArrayList<>(this.data);
            result.addAll(fillData);
            return new ZeaData(result);
        } else {
            return this;
        }
    }

    /**
     * 删除为了对齐而添加的尾部,如果没有则返回原文
     */
    public ZeaData unalign() {
        AlignmentInfo alignmentInfo = getAlignmentInfo();
        if (alignmentInfo == null) {
            // 数据没有进行过对齐，直接返回原文
            return this;
        } else {
            // 数据进行过对齐，则返回去除了填充尾的原始数据
            return new ZeaData(data.subList(0, alignmentInfo.originDataLength));
        }
    }

    ////////////////////////////// 转换 ////////////////////////////////

    /**
     * 将多个数据合并
     * 
     * @param zeaDatas 多个数据
     * @return 合并的结果，合并就是单纯的把数据头尾连接起来
     */
    public static ZeaData merge(ZeaData... zeaDatas) {
        List<Integer> data = Arrays.stream(zeaDatas)
            .filter(Objects::nonNull)
            .map(e -> e.data)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        return new ZeaData(data);
    }

    /**
     * 将其他数据类型转换为ZeaData类型， 支持数字集合 或者 字符串的集合
     * 
     * @param obj 其他数据类型
     * @return 转化后的ZeaData数据
     * @throws DataFormatTransferException 输入的数据类型不支持转化
     */
    public static ZeaData from(Object obj) {
        ParamCheckUtil.assertTrue(obj != null, new NullPointerException("no data"));
        if (obj instanceof String) {
            return fromString((String)obj);
        } else if (obj instanceof Collection) {
            return merge(from(((Collection<?>)obj).toArray()));
        } else if (obj.getClass().isArray()) {
            Object[] array = (Object[])obj;
            return merge(Arrays.stream(array).map(ZeaData::from).toArray(ZeaData[]::new));
        } else if (obj instanceof Byte) {
            List<Integer> data = new ArrayList<>();
            data.add(((int)(byte)obj) & 0xff);
            return new ZeaData(data);
        } else if (obj instanceof Character) {
            List<Integer> data = new ArrayList<>();
            data.add(((int)(char)obj) & 0xffff);
            return new ZeaData(data);
        } else if (obj instanceof Short) {
            List<Integer> data = new ArrayList<>();
            data.add(((int)(short)obj) & 0xffff);
            return new ZeaData(data);
        } else if (obj instanceof Integer) {
            int originData = (int)obj;
            List<Integer> data = new ArrayList<>();
            data.add(originData & 0xffff);
            data.add((originData >> 16) & 0xffff);
            return new ZeaData(data);
        } else if (obj instanceof Long) {
            long originData = (long)obj;
            List<Integer> data = new ArrayList<>();
            data.add((int)(originData & 0xffff));
            data.add((int)((originData >> 16) & 0xffff));
            data.add((int)((originData >> 32) & 0xffff));
            data.add((int)((originData >> 48) & 0xffff));
            return new ZeaData(data);
        }
        throw new DataFormatTransferException("cant create ZeaData from:" + obj.getClass().getCanonicalName());
    }

    /**
     * 将ZeaData转换为字符串
     * 
     * @return 字符串
     */
    public String transferToString() {
        int[] sourceData = this.data.stream().mapToInt(Integer::valueOf).toArray();
        StringBuilder result = new StringBuilder();
        for (int c : sourceData) {
            result.append((char)c);
        }
        return result.toString();
    }

    /**
     * 将ZeaData转换为别的什么类型的列表
     *
     * @param clazz 要转换的目标数组的内容类型 比如整数，字节，
     * @return 转换后的对象
     */
    public <T> List<T> transferToList(Class<T> clazz) {
        List result = null;
        int[] sourceData = this.data.stream().mapToInt(Integer::valueOf).toArray();
        if (clazz == Byte.class) {
            result = new ArrayList<Byte>();
            for (int s : sourceData) {
                result.add((byte)s);
            }
        } else if (clazz == Character.class) {
            result = new ArrayList<Character>();
            for (int s : sourceData) {
                result.add((char)s);
            }
        } else if (clazz == Short.class) {
            result = new ArrayList<Short>();
            for (int s : sourceData) {
                result.add((short)s);
            }
        } else if (clazz == Integer.class) {
            result = new ArrayList<Integer>();
            for (int index = 0; index + 1 < sourceData.length; index += 2) {
                int dataL = sourceData[index] & 0xffff;
                int dataH = sourceData[index + 1] & 0xffff;
                result.add(dataH << 16 | dataL);
            }
        } else if (clazz == Long.class) {
            result = new ArrayList<Long>();
            for (int index = 0; index + 3 < sourceData.length; index += 4) {
                long dataLL = sourceData[index] & 0xffff;
                long dataLH = sourceData[index + 1] & 0xffff;
                long dataHL = sourceData[index + 2] & 0xffff;
                long dataHH = sourceData[index + 3] & 0xffff;
                result.add(dataHH << 48 | dataHL << 32 | dataLH << 16 | dataLL);
            }
        }
        ParamCheckUtil.assertTrue(result != null, "unsupported type:" + clazz.getCanonicalName());
        return result;
    }

    ////////////////////////////// 内部数据 ////////////////////////////////

    /**
     * 由内部表示直接构造ZeaData
     * 
     * @param rawData 数据的内部表示
     * @return ZeaData对象
     */
    public static ZeaData fromRawData(Collection<Integer> rawData) {
        return new ZeaData(rawData.stream().map(e -> e & 0xffff).collect(Collectors.toList()));
    }

    /**
     * 获得内部的数据表示
     * 
     * @return 内部数据的复制
     */
    public List<Integer> getRawData() {
        return new ArrayList<>(data);
    }

    ////////////////////////////// 计算 ////////////////////////////////

    /**
     * 计算哈希
     * 
     * @param hashLength 目标哈希的长度
     * @return 目标哈希数据
     * 
     */
    public ZeaData zeaHash(int hashLength) {
        ParamCheckUtil.assertTrue(hashLength <= this.data.size(), "hashLength is too long.");
        List<Integer> targetData = new ArrayList<>(this.data.subList(0, hashLength));
        for (int indexJump : HASH_INDEX_JUMP) {
            for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
                int sourceIndex = (targetIndex + 1) * indexJump % this.data.size();
                targetData.set(targetIndex, 0xffff & (targetData.get(targetIndex) * HASH_MULTIPLIER_A
                    + this.data.get(sourceIndex) * HASH_MULTIPLIER_B));
            }
        }
        for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
            targetData.set(targetIndex,
                0xffff & (targetData.get(targetIndex) ^ targetData.get((targetIndex + 2) % targetData.size())));
        }
        for (int indexJump : HASH_INDEX_JUMP) {
            for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
                int sourceIndex = (targetIndex + 1) * indexJump % this.data.size();
                targetData.set(targetIndex, 0xffff & (targetData.get(targetIndex) * HASH_MULTIPLIER_A
                    + this.data.get(sourceIndex) * HASH_MULTIPLIER_B));
            }
        }
        for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
            targetData.set(targetIndex,
                0xffff & (targetData.get(targetIndex) ^ targetData.get((targetIndex + 1) % targetData.size())));
        }
        for (int indexJump : HASH_INDEX_JUMP) {
            for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
                int sourceIndex = (targetIndex + 1) * indexJump % this.data.size();
                targetData.set(targetIndex, 0xffff & (targetData.get(targetIndex) * HASH_MULTIPLIER_A
                    + this.data.get(sourceIndex) * HASH_MULTIPLIER_B));
            }
        }
        return fromRawData(targetData);
    }
    ////////////////////////////// private ////////////////////////////////

    private static ZeaData fromString(String string) {
        List<Integer> data = new ArrayList<>();
        for (char c : string.toCharArray()) {
            data.add((int)c);
        }
        return new ZeaData(data);
    }

    private ZeaData(List<Integer> data) {
        this.data = data;
    }

    /**
     * 获得对齐信息，包含 对齐模 和 原始数据长度
     * 
     * @return 对齐信息 null表示数据不是经过对齐的
     */
    private AlignmentInfo getAlignmentInfo() {
        if (this.data.size() >= MIN_ALIGN_LENGTH) {
            int alignment = data.get(data.size() - 3);
            int originDataLengthL = data.get(data.size() - 2);
            int originDataLengthH = data.get(data.size() - 1);
            int originDataLength = originDataLengthH << 16 | originDataLengthL;
            if (alignment != 0 && data.size() % alignment == 0 && data.size() >= originDataLength + MIN_ALIGN_LENGTH) {
                return new AlignmentInfo(alignment, originDataLength);
            }
        }
        return null;
    }

    private static final class AlignmentInfo {
        public int alignment;
        public int originDataLength;

        public AlignmentInfo(int alignment, int originDataLength) {
            this.alignment = alignment;
            this.originDataLength = originDataLength;
        }
    }

    @Override
    public String toString() {
        return "ZeaData{" + "data=" + data + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZeaData zeaData = (ZeaData)o;
        return data.equals(zeaData.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

}
