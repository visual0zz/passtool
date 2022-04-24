
ALGORITHM_BLOCK_SIZE = 32;

function string2data(str) {
    //将字符串转化为统一数据格式，输入str 输出数组
    if (str.constructor != String) {
        throw "input must be string"
    }
    var result = [];
    for (i = 0; i < str.length; i++) {
        result.push(str.charCodeAt(i));
    }
    return result;
}

function data2string(data) {
    //将统一格式转化为字符串
    if (!Array.isArray(data)) {
        throw "input must be array"
    }
    var result = "";
    for (i = 0; i < data.length; i++) {
        result += String.fromCharCode(data[i]);
    }
    return result;
}

function align(data, alignment) {
    //将数据对齐与某个模，尾部填充 0，模，原数据长度
    if (!Array.isArray(data) || isNaN(alignment) || alignment <= 0) {
        throw "input must be an array and a positive number."
    }
    var tail = [Math.floor(data.length / 65536), data.length % 65536, alignment];
    while ((data.length + tail.length) % alignment != 0) {
        tail.push(0);
    }
    var result = data.concat(tail.reverse());
    return result;
}

function unalign(data) {
    //去除填充的尾部
    if (!Array.isArray(data)) {
        throw "input must be array"
    }
    if (isaligned(data)) {
        raw_length_H = data[data.length - 1];
        raw_length_L = data[data.length - 2];
        raw_length = raw_length_H * 65536 + raw_length_L;
        return data.slice(0, raw_length);
    } else {
        return data;
    }
}

function isaligned(data) {
    //判断数据是否有尾部填充
    if (!Array.isArray(data)) {
        throw "input must be array"
    }
    raw_length_H = data[data.length - 1];
    raw_length_L = data[data.length - 2];
    raw_length = raw_length_H * 65536 + raw_length_L;
    alignment = data[data.length - 3];
    if (data.length % alignment == 0 && data.length - 3 >= raw_length) {
        return true;
    } else {
        return false;
    }
}

function zeahash(data, hashLength) {
    HASH_MULTIPLIER_A = 12347;
    HASH_MULTIPLIER_B = 54323;
    HASH_INDEX_JUMP = [1, 3, 5, 7, 11, 13, 19, 23, 29, 31, 37, 67, 79, 131, 257, 331];
    targetData = data.slice(0, hashLength);

    for (indexJump of HASH_INDEX_JUMP) {
        for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
            sourceIndex = (targetIndex + indexJump) % data.length;
            targetData[targetIndex] = (targetData[targetIndex] * HASH_MULTIPLIER_A + data[sourceIndex] * HASH_MULTIPLIER_B) & 0xffff;
        }
    }
    for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
        targetData[targetIndex] = 0xffff & (targetData[targetIndex] ^ targetData[(targetIndex + 17) % targetData.length]);
    }
    for (sourceIndex = 0; sourceIndex < data.length; sourceIndex++) {
        targetIndex = sourceIndex % targetData.length;
        targetData[targetIndex] = targetData[targetIndex] ^ data[sourceIndex];
    }
    for (indexJump of HASH_INDEX_JUMP) {
        for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
            sourceIndex = (targetIndex + 31) * indexJump % data.length;
            targetData[targetIndex] = 0xffff & (targetData[targetIndex] * HASH_MULTIPLIER_A + data[sourceIndex] * HASH_MULTIPLIER_B);
        }
    }
    for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
        targetData[targetIndex] = 0xffff & (targetData[targetIndex] ^ targetData[(targetIndex + 3) % targetData.length]);
    }
    for (indexJump of HASH_INDEX_JUMP) {
        for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
            sourceIndex = (targetIndex + 137) * indexJump % data.length;
            targetData[targetIndex] = 0xffff & (targetData[targetIndex] * HASH_MULTIPLIER_A + data[sourceIndex] * HASH_MULTIPLIER_B);
        }
    }
    for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
        targetData[targetIndex]=
            targetData[targetIndex] ^ targetData[targetData[(targetIndex+1)%targetData.length] % targetData.length];
    }
    for (indexJump of HASH_INDEX_JUMP) {
        for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
            sourceIndex = (targetIndex + 1 + indexJump) % targetData.length;
            targetData[targetIndex]= 0xffff & (targetData[targetIndex] * HASH_MULTIPLIER_A + targetData[sourceIndex] * HASH_MULTIPLIER_B);
        }
    }
}

data = align(string2data("昆仑4@"), 17);
console.info("data=", data);
zeahash(data, 7);
zeahash(data, 12);
zeahash(data, 13);
zeahash(data, 16);