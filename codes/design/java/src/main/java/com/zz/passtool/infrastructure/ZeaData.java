package com.zz.passtool.infrastructure;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.zz.passtool.constants.exception.DataFormatTransferException;
import com.zz.passtool.constants.tag.ReadOnly;
import com.zz.passtool.utils.ParamCheckUtil;

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

    private static final int TURNS = 32;                                                                       // 加密一共进行几轮
    private static final int MIN_ALIGN_LENGTH = 4;                                                                        // 最小填充长度
    private static final int HASH_MULTIPLIER_A = 12347;
    private static final int HASH_MULTIPLIER_B = 54323;
    private static final int HASH_MULTIPLIER_C = 17783;
    private static final int HASH_MULTIPLIER_D = 33347;
    private static final int[] HASH_INDEX_JUMP =
            new int[]{1, 3, 5, 7, 11, 13, 19, 23, 29, 31, 37, 67, 79, 131, 257, 331};
    private static final String SPECIAL_CHARS = "~`!@#$%^&*()-_=+[]{}\\|'\";:?/<>.,";//特殊字符列表
    private static final String EASY_CHARS = "0123456789ADEFGdefHhLNnQRTrtYy~!@#%^&*()+=[]{}\\<>?/";//易辨识字符列表
    private static final String NUMBER_CHARS = "0123456789";//数字字符
    private static final String LOWER_CHARS = "abcdefghijklmnopqrstuvwxyz";//小写字母
    private static final String UPPER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";//大写字母列表
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
        ParamCheckUtil.assertTrue(alignment > 0, "invalid align block size ");
        List<Integer> fillData = new ArrayList<>();
        // 尾部填充数据的核心部分是原始数据的长度信息
        int size = data.size();
        fillData.add((size >> 16) & 0xffff);
        fillData.add(size & 0xffff);
        fillData.add((alignment >> 16) & 0xffff);
        fillData.add(alignment & 0xffff);
        // 剩余部分填充0值,直到长度满足对齐的需要
        while ((fillData.size() + this.data.size()) % alignment != 0) {
            fillData.add(0);
        }
        Collections.reverse(fillData);
        List<Integer> result = new ArrayList<>(this.data);
        result.addAll(fillData);
        return new ZeaData(result);
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
            return fromString((String) obj);
        } else if (obj instanceof Collection) {
            return merge(from(((Collection<?>) obj).toArray()));
        } else if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            return merge(Arrays.stream(array).map(ZeaData::from).toArray(ZeaData[]::new));
        } else if (obj instanceof Byte) {
            List<Integer> data = new ArrayList<>();
            data.add(((int) (byte) obj) & 0xff);
            return new ZeaData(data);
        } else if (obj instanceof Character) {
            List<Integer> data = new ArrayList<>();
            data.add(((int) (char) obj) & 0xffff);
            return new ZeaData(data);
        } else if (obj instanceof Short) {
            List<Integer> data = new ArrayList<>();
            data.add(((int) (short) obj) & 0xffff);
            return new ZeaData(data);
        } else if (obj instanceof Integer) {
            int originData = (int) obj;
            List<Integer> data = new ArrayList<>();
            data.add(originData & 0xffff);
            data.add((originData >> 16) & 0xffff);
            return new ZeaData(data);
        } else if (obj instanceof Long) {
            long originData = (long) obj;
            List<Integer> data = new ArrayList<>();
            data.add((int) (originData & 0xffff));
            data.add((int) ((originData >> 16) & 0xffff));
            data.add((int) ((originData >> 32) & 0xffff));
            data.add((int) ((originData >> 48) & 0xffff));
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
            result.append((char) c);
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
                result.add((byte) s);
            }
        } else if (clazz == Character.class) {
            result = new ArrayList<Character>();
            for (int s : sourceData) {
                result.add((char) s);
            }
        } else if (clazz == Short.class) {
            result = new ArrayList<Short>();
            for (int s : sourceData) {
                result.add((short) s);
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
     */
    public ZeaData zeaHash(int hashLength) {
        ParamCheckUtil.assertTrue(hashLength <= this.data.size(), "hashLength is too long.");
        List<Integer> targetData = new ArrayList<>(this.data.subList(0, hashLength));
        for (int indexJump : HASH_INDEX_JUMP) {
            for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
                int sourceIndex = (targetIndex + indexJump) % this.data.size();
                targetData.set(targetIndex, 0xffff & (targetData.get(targetIndex) * HASH_MULTIPLIER_A
                        + this.data.get(sourceIndex) * HASH_MULTIPLIER_B));
            }
        }
        for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
            targetData.set(targetIndex,
                    0xffff & (targetData.get(targetIndex) ^ targetData.get((targetIndex + 17) % targetData.size())));
        }
        for (int sourceIndex = 0; sourceIndex < this.data.size(); sourceIndex++) {
            int targetIndex = sourceIndex % targetData.size();
            targetData.set(targetIndex, targetData.get(targetIndex) ^ this.data.get(sourceIndex));
        }
        for (int indexJump : HASH_INDEX_JUMP) {
            for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
                int sourceIndex = (targetIndex + 31) * indexJump % this.data.size();
                targetData.set(targetIndex, 0xffff & (targetData.get(targetIndex) * HASH_MULTIPLIER_C
                        + this.data.get(sourceIndex) * HASH_MULTIPLIER_D));
            }
        }
        for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
            targetData.set(targetIndex,
                    0xffff & (targetData.get(targetIndex) ^ targetData.get((targetIndex + 3) % targetData.size())));
        }
        for (int indexJump : HASH_INDEX_JUMP) {
            for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
                int sourceIndex = (targetIndex + 137) * indexJump % this.data.size();
                targetData.set(targetIndex, 0xffff & (targetData.get(targetIndex) * HASH_MULTIPLIER_A
                        + this.data.get(sourceIndex) * HASH_MULTIPLIER_D));
            }
        }
        for (int targetIndex = 0; targetIndex < targetData.size(); targetIndex++) {
            targetData.set(targetIndex, targetData.get(targetIndex)
                    ^ targetData.get(targetData.get((targetIndex + 1) % targetData.size()) % targetData.size()));
        }
        for (int sourceIndex = 0; sourceIndex < this.data.size(); sourceIndex++) {
            int targetIndex = (sourceIndex * 113 + 71) % targetData.size();
            targetData.set(targetIndex, targetData.get(targetIndex) ^ this.data.get(sourceIndex));
            targetIndex = (sourceIndex + 1) % targetData.size();
            targetData.set(targetIndex, targetData.get(targetIndex) ^ this.data.get(sourceIndex));
        }
        for (int indexJump : HASH_INDEX_JUMP) {
            for (int targetIndex1 = 0; targetIndex1 < targetData.size(); targetIndex1++) {
                int targetIndex2 = (targetIndex1 + 1 + indexJump) % targetData.size();
                targetData.set(targetIndex1, 0xffff & (targetData.get(targetIndex1) * HASH_MULTIPLIER_A
                        + targetData.get(targetIndex2) * HASH_MULTIPLIER_B));
            }
        }
        for (int sourceIndex = 0; sourceIndex < this.data.size(); sourceIndex++) {
            int targetIndex = (sourceIndex + 1) % targetData.size();
            targetData.set(targetIndex, targetData.get(targetIndex) ^ this.data.get(sourceIndex));
        }
        return fromRawData(targetData);
    }

    /**
     * 加密
     *
     * @param key  密钥
     * @param salt 盐 可为空
     * @return 加密后的数据
     */
    public ZeaData encrypt(ZeaData key, ZeaData salt) {
        salt = salt == null ? ZeaData.from("1234") : salt;
        List<Integer> targetData = this.align(4).data;
        int[][] hashes = new int[TURNS][];
        ZeaData hash = merge(key, salt).align(targetData.size());
        for (int turn = 0; turn < TURNS; turn++) {
            hash = merge(hash, key, salt).zeaHash(targetData.size());
            hashes[turn] = hash.data.stream().mapToInt(Integer::intValue).toArray();
            // 构造轮密钥
        }
        for (int turn = 0; turn < TURNS; turn++) {
            for (int indexStart = 0; indexStart < targetData.size(); indexStart += 4) {
                // 四个一组，组内互相异或
                int[] tmp = new int[4];
                tmp[0] = targetData.get(indexStart) ^ targetData.get(indexStart + 3) ^ targetData.get(indexStart + 1);
                tmp[1] = targetData.get(indexStart + 1) ^ targetData.get(indexStart) ^ targetData.get(indexStart + 2);
                tmp[2] =
                        targetData.get(indexStart + 2) ^ targetData.get(indexStart + 1) ^ targetData.get(indexStart + 3);
                tmp[3] = targetData.get(indexStart + 3) ^ targetData.get(indexStart + 2) ^ targetData.get(indexStart);
                targetData.set(indexStart, tmp[0]);
                targetData.set(indexStart + 1, tmp[1]);
                targetData.set(indexStart + 2, tmp[2]);
                targetData.set(indexStart + 3, tmp[3]);
            }

            for (int index = 0; index < targetData.size(); index++) {
                targetData.set(index, shift(targetData.get(index), index));
                // 全体数据进行比特循环移位
            }
            int start = turn % targetData.size(),
                    indexJump = HASH_INDEX_JUMP[turn % HASH_INDEX_JUMP.length] % targetData.size() + 2;
            int index, temp = targetData.get(start);
            for (index = start; index + indexJump < targetData.size(); index += indexJump) {
                // 进行错位
                targetData.set(index, targetData.get(index + indexJump));
            }
            targetData.set(index, temp);

            for (int i = 0; i < targetData.size(); i++) {
                targetData.set(i, targetData.get(i) ^ hashes[turn][i]);
                // 全体数据和轮密钥异或
            }
        }
        return merge(ZeaData.from(salt.data.size()), salt, ZeaData.fromRawData(targetData));
    }

    public ZeaData encrypt(ZeaData key) {
        return encrypt(key, null);
    }

    /**
     * 解密
     *
     * @param key 密钥
     * @return 加密后的数据
     */
    public ZeaData decrypt(ZeaData key) {
        int saltLength = ZeaData.fromRawData(this.data.subList(0, 2)).transferToList(Integer.class).get(0);
        ZeaData salt = ZeaData.fromRawData(this.data.subList(2, 2 + saltLength));
        List<Integer> targetData = new ArrayList<>(this.data.subList(2 + saltLength, this.data.size()));
        int[][] hashes = new int[TURNS][];
        ZeaData hash = merge(key, salt).align(targetData.size());
        for (int turn = 0; turn < TURNS; turn++) {
            hash = merge(hash, key, salt).zeaHash(targetData.size());
            hashes[turn] = hash.data.stream().mapToInt(Integer::intValue).toArray();
            // 构造轮密钥
        }
        for (int turn = TURNS - 1; turn >= 0; turn--) {
            for (int i = 0; i < targetData.size(); i++) {
                targetData.set(i, targetData.get(i) ^ hashes[turn][i]);
                // 全体数据和轮密钥异或
            }

            int start = turn % targetData.size(),
                    indexJump = HASH_INDEX_JUMP[turn % HASH_INDEX_JUMP.length] % targetData.size() + 2;
            for (int index = start; index + indexJump < targetData.size(); index += indexJump) {
                // 进行错位
                int tmp = targetData.get(index + indexJump);
                targetData.set(index + indexJump, targetData.get(start));
                targetData.set(start, tmp);
            }

            for (int index = 0; index < targetData.size(); index++) {
                targetData.set(index, shift(targetData.get(index), -index));
                // 全体数据进行比特循环移位
            }

            for (int indexStart = 0; indexStart < targetData.size(); indexStart += 4) {
                // 四个一组，组内互相异或
                int[] tmp = new int[4];
                tmp[0] = targetData.get(indexStart) ^ targetData.get(indexStart + 3) ^ targetData.get(indexStart + 1);
                tmp[1] = targetData.get(indexStart + 1) ^ targetData.get(indexStart) ^ targetData.get(indexStart + 2);
                tmp[2] =
                        targetData.get(indexStart + 2) ^ targetData.get(indexStart + 1) ^ targetData.get(indexStart + 3);
                tmp[3] = targetData.get(indexStart + 3) ^ targetData.get(indexStart + 2) ^ targetData.get(indexStart);
                targetData.set(indexStart, tmp[0]);
                targetData.set(indexStart + 1, tmp[1]);
                targetData.set(indexStart + 2, tmp[2]);
                targetData.set(indexStart + 3, tmp[3]);
            }
        }
        return ZeaData.fromRawData(targetData).unalign();
    }

    ////////////////////////////// private /////////////////////////////////
    private static int shift(int data, int shift) {
        // 将data的低16字节进行循环位移 shift>0表示循环左移 shift<0表示循环右移
        shift %= 16;
        shift = shift > 0 ? shift : shift + 16;
        data = data << shift;
        data = data & 0xffff | (data >> 16) & 0xffff;
        return data;
    }

    private static ZeaData fromString(String string) {
        List<Integer> data = new ArrayList<>();
        for (char c : string.toCharArray()) {
            data.add((int) c);
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
            int alignmentL = data.get(data.size() - 4);
            int alignmentH = data.get(data.size() - 3);
            int originDataLengthL = data.get(data.size() - 2);
            int originDataLengthH = data.get(data.size() - 1);

            int alignment = alignmentH << 16 | alignmentL;
            int originDataLength = originDataLengthH << 16 | originDataLengthL;
            if (alignment > 0 && data.size() % alignment == 0 && data.size() >= originDataLength + MIN_ALIGN_LENGTH) {
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
        return "ZeaData{" + data + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZeaData zeaData = (ZeaData) o;
        return data.equals(zeaData.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    public String toJson() {
        return data.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]"));
    }

    public static ZeaData fromJson(String json) {
        ParamCheckUtil.assertTrue(Pattern.matches("^ *\\[(?: *[0-9]+ *,)* *[0-9]* *\\] *$", json), "json格式错误");
        List<Integer> data = Arrays.stream(json.replaceAll("[\\[\\]]", "").split(",")).map(String::trim).map(Integer::valueOf).collect(Collectors.toList());
        return fromRawData(data);
    }

    /**
     * 使用ZeaData的内部数据按照指定格式装配一个密码出来
     *
     * @param format 密码格式描述 0-9表示数字 a-z表示小写字母 A-Z表示大写字母 #表示特殊字符 @表示特殊字符或者大小写字母
     *               %表示特殊字符或者数字 *表示数字或者大小写字母或者特殊字符 !表示易辨识符号 $表示数字或者大小写字母
     *               除此之外的其他字符会被忽略
     *               例如 "0567b" 表示四位数字后面跟一个小写字母
     * @return 密码
     */
    public String generatePassword(String format) {
        ZeaData source = this;
        if (source.data.size() < format.length()) {
            throw new RuntimeException("没有足够的数据匹配format的长度");
        }
        format = format.replaceAll("[0-9]", "0");
        format = format.replaceAll("[a-z]", "a");
        format = format.replaceAll("[A-Z]", "A");
        StringBuilder builder = new StringBuilder();
        HashMap<Character,String> charsMap=new HashMap<>();
        charsMap.put('0',NUMBER_CHARS);
        charsMap.put('a',LOWER_CHARS);
        charsMap.put('A',UPPER_CHARS);
        charsMap.put('#',SPECIAL_CHARS);
        charsMap.put('!',EASY_CHARS);
        charsMap.put('@',SPECIAL_CHARS+LOWER_CHARS+UPPER_CHARS);
        charsMap.put('%',SPECIAL_CHARS+NUMBER_CHARS);
        charsMap.put('*',SPECIAL_CHARS+LOWER_CHARS+UPPER_CHARS+NUMBER_CHARS);
        charsMap.put('$',LOWER_CHARS+UPPER_CHARS+NUMBER_CHARS);
        for (int i = 0; i < format.length(); i++) {
            String chars=charsMap.get(format.charAt(i));
            if(!(chars == null)) {
                builder.append(chars.charAt(source.data.get(i) % chars.length()));
            }
        }
        return builder.toString();
    }
}
