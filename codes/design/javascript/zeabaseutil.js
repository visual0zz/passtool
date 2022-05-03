
HASH_INDEX_JUMP = [1, 3, 5, 7, 11, 13, 19, 23, 29, 31, 37, 67, 79, 131, 257, 331];
/**
 * 
 * @param {String} str 字符串
 * @returns Array数据
 */
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
/**
 * 
 * @param {Array} data  数据
 * @returns  字符串
 */
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

/**
 * 
 * @param {Array} data      数据
 * @param {int} alignment   填充模长
 * @returns 将原数据进行尾部填充，使长度变成alignment的整数倍
 */
function align(data, alignment) {
    //将数据对齐与某个模，尾部填充 0，模，原数据长度
    if (!Array.isArray(data) || isNaN(alignment) || alignment <= 0) {
        throw "input must be an array and a positive number."
    }
    var tail = [Math.floor(data.length / 65536), data.length % 65536, Math.floor(alignment / 65536), alignment % 65536];
    while ((data.length + tail.length) % alignment != 0) {
        tail.push(0);
    }
    var result = data.concat(tail.reverse());
    return result;
}

/**
 * 
 * @param {Array} data 去除尾部填充
 * @returns 
 */
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
/**
 * 
 * @param {Array} data  数据
 * @returns true-尾部进行过填充 false-尾部不符合填充的格式
 */
function isaligned(data) {
    //判断数据是否有尾部填充
    if (!Array.isArray(data)) {
        throw "input must be array"
    }
    raw_length_H = data[data.length - 1];
    raw_length_L = data[data.length - 2];
    raw_length = raw_length_H * 65536 + raw_length_L;
    alignment_H = data[data.length - 3];
    alignment_L = data[data.length - 4];
    alignment=alignment_H*65536+alignment_L;
    if (data.length % alignment == 0 && data.length - 4 >= raw_length) {
        return true;
    } else {
        return false;
    }
}
/**
 * 
 * @param {Array} data      原数据
 * @param {int} hashLength  哈希长度
 * @returns 哈希值
 */
function zeahash(data, hashLength) {
    HASH_MULTIPLIER_A = 12347;
    HASH_MULTIPLIER_B = 54323;
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
    for(sourceIndex=0;sourceIndex<data.length;sourceIndex++){
        targetIndex=(sourceIndex*113+71)% targetData.length;
        targetData[targetIndex]=targetData[targetIndex]^data[sourceIndex];
        targetIndex=(sourceIndex+1)%targetData.length;
        targetData[targetIndex]=targetData[targetIndex]^data[sourceIndex];
    }
    for (indexJump of HASH_INDEX_JUMP) {
        for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
            sourceIndex = (targetIndex + 1 + indexJump) % targetData.length;
            targetData[targetIndex]= 0xffff & (targetData[targetIndex] * HASH_MULTIPLIER_A + targetData[sourceIndex] * HASH_MULTIPLIER_B);
        }
    }
    return targetData;
}

/**
 * 
 * @param {int} d 原数据
 * @param {int} shift 位移值
 * @returns 
 */
function shift(d,shift) {
    // 将data的低16字节进行循环位移 shift>0表示循环左移 shift<0表示循环右移
    shift %= 16;
    shift = shift > 0 ? shift : shift + 16;
    d = d << shift;
    d = d & 0xffff | (d >> 16) & 0xffff;
    return d;
}

/**
 * 
 * @param {Array} data 原文
 * @param {Array} key  密钥
 * @param {Array} salt 可空
 * @returns 密文
 */
function encrypt(data,key,salt) {
    TURNS=16;
    var salt = salt == null ? string2data("1234") : salt;
    var targetData = align(data,4);
    var hashes = [];
    hash = align(key.concat(salt),targetData.length);
    for (turn = 0; turn < TURNS; turn++) {
        hash =zeahash(hash,targetData.length);
        hashes[turn] = hash;
        // 构造轮密钥
    }
    for (turn = 0; turn < TURNS; turn++) {
        for (indexStart = 0; indexStart < targetData.length; indexStart += 4) {
            // 四个一组，组内互相异或
            tmp=[];
            tmp[0] =targetData[indexStart]^targetData[indexStart+3]^targetData[indexStart+1];
            tmp[1] =targetData[indexStart+1]^targetData[indexStart]^targetData[indexStart+2];
            tmp[2] =targetData[indexStart+2]^targetData[indexStart+1]^targetData[indexStart+3];
            tmp[3] =targetData[indexStart+3]^targetData[indexStart+2]^targetData[indexStart];
            targetData[indexStart]=tmp[0];
            targetData[indexStart+1]=tmp[1];
            targetData[indexStart+2]=tmp[2];
            targetData[indexStart+3]=tmp[3];
        }

        for (index = 0; index < targetData.length; index++) {
            targetData[index]=shift(targetData[index],index);
            // 全体数据进行比特循环移位
        }
        start = turn % targetData.length, indexJump = HASH_INDEX_JUMP[turn % HASH_INDEX_JUMP.length] + 2;
        index=start, temp = targetData[start];
        for (index = start; index + indexJump < targetData.length; index += indexJump) {
            // 进行错位
            targetData[index]=targetData[index+indexJump]
        }
        targetData[index]=temp;

        for (i = 0; i < targetData.length; i++) {
            targetData[i]=targetData[i]^hashes[turn][i]
            // 全体数据和轮密钥异或
        }
    }
    head=[];
    head[0]=salt.length&0xffff;
    head[1]=(salt.length>>16)&0xffff;
    return head.concat(salt,targetData);
}

/**
 * 
 * @param {Array} data  数据
 * @param {Array} key   密钥
 * @returns 解密后的原文
 */
function decrypt(data,key) {
    TURNS=16;
    saltLength = data[0]&0xffff|((0xffff&data[1])<<16);
    salt=data.slice(2,2+saltLength);
    var targetData = data.slice(2 + saltLength, data.length);
    var hashes = [];
    hash = align(key.concat(salt),targetData.length);
    for (turn = 0; turn < TURNS; turn++) {
        hash =zeahash(hash,targetData.length);
        hashes[turn] = hash;
        // 构造轮密钥
    }
    for (turn = TURNS - 1; turn >= 0; turn--) {
        for (i = 0; i < targetData.length; i++) {
            targetData[i]=targetData[i]^hashes[turn][i];
            // 全体数据和轮密钥异或
        }

        start = turn % targetData.length, indexJump = HASH_INDEX_JUMP[turn % HASH_INDEX_JUMP.length] + 1;
        cache = targetData[start];
        for (index = start; index + indexJump < targetData.length; index += indexJump) {
            // 进行错位
            tmp = targetData[index + indexJump];
            targetData[index+indexJump]=cache;
            cache = tmp;
        }
        targetData[start]=cache;
    
        for (index = 0; index < targetData.length; index++) {
            targetData[index]=shift(targetData[index],-index);
            // 全体数据进行比特循环移位
        }

        for (indexStart = 0; indexStart < targetData.length; indexStart += 4) {
            // 四个一组，组内互相异或
            tmp=[];
            tmp[0] =targetData[indexStart]^targetData[indexStart+3]^targetData[indexStart+1];
            tmp[1] =targetData[indexStart+1]^targetData[indexStart]^targetData[indexStart+2];
            tmp[2] =targetData[indexStart+2]^targetData[indexStart+1]^targetData[indexStart+3];
            tmp[3] =targetData[indexStart+3]^targetData[indexStart+2]^targetData[indexStart];
            targetData[indexStart]=tmp[0];
            targetData[indexStart+1]=tmp[1];
            targetData[indexStart+2]=tmp[2];
            targetData[indexStart+3]=tmp[3];
        }
    }
    return unalign(targetData);
}
data = string2data("昆仑#@13abc赑箜琳亵渎琅篌屃");
console.info("data=", data);
data=encrypt(data,data);
console.info("encrypted=",data);
data=decrypt(data,data);
console.info("decrypted=",data);