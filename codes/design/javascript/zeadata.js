
var HASH_INDEX_JUMP = [1, 3, 5, 7, 11, 13, 19, 23, 29, 31, 37, 67, 79, 131, 257, 331];
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
    for (var i = 0; i < str.length; i++) {
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
    for (var i = 0; i < data.length; i++) {
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
        var raw_length_H = data[data.length - 1];
        var raw_length_L = data[data.length - 2];
        var raw_length = raw_length_H * 65536 + raw_length_L;
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
    var raw_length_H = data[data.length - 1];
    var raw_length_L = data[data.length - 2];
    var raw_length = raw_length_H * 65536 + raw_length_L;
    var alignment_H = data[data.length - 3];
    var alignment_L = data[data.length - 4];
    var alignment=alignment_H*65536+alignment_L;
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
    var HASH_MULTIPLIER_A = 12347;
    var HASH_MULTIPLIER_B = 54323;
    var HASH_MULTIPLIER_C = 17783;
    var HASH_MULTIPLIER_D = 33347;
    var targetData = data.slice(0, hashLength);

    for (var indexJump of HASH_INDEX_JUMP) {
        for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
            var sourceIndex = (targetIndex + indexJump) % data.length;
            targetData[targetIndex] = (targetData[targetIndex] * HASH_MULTIPLIER_A + data[sourceIndex] * HASH_MULTIPLIER_B) & 0xffff;
        }
    }
    for (var targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
        targetData[targetIndex] = 0xffff & (targetData[targetIndex] ^ targetData[(targetIndex + 17) % targetData.length]);
    }
    for (var sourceIndex = 0; sourceIndex < data.length; sourceIndex++) {
        var targetIndex = sourceIndex % targetData.length;
        targetData[targetIndex] = targetData[targetIndex] ^ data[sourceIndex];
    }
    for (var indexJump of HASH_INDEX_JUMP) {
        for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
            var sourceIndex = (targetIndex + 31) * indexJump % data.length;
            targetData[targetIndex] = 0xffff & (targetData[targetIndex] * HASH_MULTIPLIER_C + data[sourceIndex] * HASH_MULTIPLIER_D);
        }
    }
    for (var targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
        targetData[targetIndex] = 0xffff & (targetData[targetIndex] ^ targetData[(targetIndex + 3) % targetData.length]);
    }
    for (var indexJump of HASH_INDEX_JUMP) {
        for (targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
            var sourceIndex = (targetIndex + 137) * indexJump % data.length;
            targetData[targetIndex] = 0xffff & (targetData[targetIndex] * HASH_MULTIPLIER_A + data[sourceIndex] * HASH_MULTIPLIER_D);
        }
    }
    for (var targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
        targetData[targetIndex]=
            targetData[targetIndex] ^ targetData[targetData[(targetIndex+1)%targetData.length] % targetData.length];
    }
    for(var sourceIndex=0;sourceIndex<data.length;sourceIndex++){
        var targetIndex=(sourceIndex*113+71)% targetData.length;
        targetData[targetIndex]=targetData[targetIndex]^data[sourceIndex];
        targetIndex=(sourceIndex+1)%targetData.length;
        targetData[targetIndex]=targetData[targetIndex]^data[sourceIndex];
    }
    for (var indexJump of HASH_INDEX_JUMP) {
        for (var targetIndex = 0; targetIndex < targetData.length; targetIndex++) {
            var sourceIndex = (targetIndex + 1 + indexJump) % targetData.length;
            targetData[targetIndex]= 0xffff & (targetData[targetIndex] * HASH_MULTIPLIER_A + targetData[sourceIndex] * HASH_MULTIPLIER_B);
        }
    }
    for(var sourceIndex=0;sourceIndex<data.length;sourceIndex++){
        var targetIndex=(sourceIndex+1)%targetData.length;
        targetData[targetIndex]=targetData[targetIndex]^data[sourceIndex];
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
    var TURNS=32;
    var salt = salt == null ? string2data("1234") : salt;
    var targetData = align(data,4);
    var hashes = [];
    var hash = align(key.concat(salt),targetData.length);
    for (var turn = 0; turn < TURNS; turn++) {
        hash =zeahash(hash.concat(key,salt),targetData.length);
        hashes[turn] = hash;
        // 构造轮密钥
    }
    for (var turn = 0; turn < TURNS; turn++) {
        for (var indexStart = 0; indexStart < targetData.length; indexStart += 4) {
            // 四个一组，组内互相异或
            var tmp=[];
            tmp[0] =targetData[indexStart]^targetData[indexStart+3]^targetData[indexStart+1];
            tmp[1] =targetData[indexStart+1]^targetData[indexStart]^targetData[indexStart+2];
            tmp[2] =targetData[indexStart+2]^targetData[indexStart+1]^targetData[indexStart+3];
            tmp[3] =targetData[indexStart+3]^targetData[indexStart+2]^targetData[indexStart];
            targetData[indexStart]=tmp[0];
            targetData[indexStart+1]=tmp[1];
            targetData[indexStart+2]=tmp[2];
            targetData[indexStart+3]=tmp[3];
        }

        for (var index = 0; index < targetData.length; index++) {
            targetData[index]=shift(targetData[index],index);
            // 全体数据进行比特循环移位
        }
        var start = turn % targetData.length, indexJump = HASH_INDEX_JUMP[turn % HASH_INDEX_JUMP.length]%targetData.length + 2;
        var index=start, temp = targetData[start];
        for (index = start; index + indexJump < targetData.length; index += indexJump) {
            // 进行错位
            targetData[index]=targetData[index+indexJump];
        }
        targetData[index]=temp;

        for (var i = 0; i < targetData.length; i++) {
            targetData[i]=targetData[i]^hashes[turn][i];
            // 全体数据和轮密钥异或
        }
    }
    var head=[];
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
    var TURNS=32;
    var saltLength = data[0]&0xffff|((0xffff&data[1])<<16);
    var salt=data.slice(2,2+saltLength);
    var targetData = data.slice(2 + saltLength, data.length);
    var hashes = [];
    var hash = align(key.concat(salt),targetData.length);
    for (var turn = 0; turn < TURNS; turn++) {
        hash =zeahash(hash.concat(key,salt),targetData.length);
        hashes[turn] = hash;
        // 构造轮密钥
    }
    for (var turn = TURNS - 1; turn >= 0; turn--) {
        for (var i = 0; i < targetData.length; i++) {
            targetData[i]=targetData[i]^hashes[turn][i];
            // 全体数据和轮密钥异或
        }

        var start = turn % targetData.length;
        var indexJump = HASH_INDEX_JUMP[turn % HASH_INDEX_JUMP.length]%targetData.length + 2;
        for (var index = start; index + indexJump < targetData.length; index += indexJump) {
            // 进行错位
            tmp = targetData[index + indexJump];
            targetData[index+indexJump]=targetData[start];
            targetData[start] = tmp;
        }
    
        for (var index = 0; index < targetData.length; index++) {
            targetData[index]=shift(targetData[index],-index);
            // 全体数据进行比特循环移位
        }

        for (var indexStart = 0; indexStart < targetData.length; indexStart += 4) {
            // 四个一组，组内互相异或
            var tmp=[];
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

const SPECIAL_CHARS = "~`!@#$%^&*()-_=+[]{}\\|'\";:?/<>.,";//特殊字符列表
const EASY_CHARS = "0123456789ADEFGdefHhLNnQRTrtYy~!@#%^&*()+=[]{}\\<>?/";//易辨识字符列表
const NUMBER_CHARS = "0123456789";//数字字符
const LOWER_CHARS = "abcdefghijklmnopqrstuvwxyz";//小写字母
const UPPER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";//大写字母列表
function generatePassword(systemSeed,filePath,formatter){
    var data=string2data(systemSeed+"#"+filePath+"#"+formatter);
    // console.info("passwd seed=",systemSeed+"#"+filePath+"#"+formatter);
    data=encrypt(data,zeahash(align(data,20),20));
    data=zeahash(data,formatter.length);
    formatter=formatter.replace(/[0-9]/g, "0");
    formatter=formatter.replace(/[a-z]/g, "a");
    formatter=formatter.replace(/[A-Z]/g, "A");
    // console.info(formatter);
    var charsetmap={
        '0':NUMBER_CHARS,
        'a':LOWER_CHARS,
        'A':UPPER_CHARS,
        '#':SPECIAL_CHARS,
        '!':EASY_CHARS,
        '@':SPECIAL_CHARS+LOWER_CHARS+UPPER_CHARS,
        '%':SPECIAL_CHARS+NUMBER_CHARS,
        '*':SPECIAL_CHARS+LOWER_CHARS+UPPER_CHARS+NUMBER_CHARS,
        '$':LOWER_CHARS+UPPER_CHARS+NUMBER_CHARS,
    };
    var result="";
    for(var i=0;i<formatter.length;i++){
        var charset=charsetmap[formatter[i]];
        if(charset != null){
            result+=charset[data[i]%charset.length];
        }
    }
    return result;
}
/**
 * 用于和java代码核对两者行为是否一致
 */
function 跨平台对测(){
    var data = string2data("昆仑#@13abc赑箜琳亵渎琅篌屃");
    var key = string2data("注意注意");
    console.info("data=", data);
    var encrypted=encrypt(data,key);
    console.info("encrypted=",encrypted);
    var decrypted=decrypt(encrypted,key);
    console.info("decrypted=",decrypted);
    console.info("decrypted string=",data2string(decrypted));
}

function 测试密码生成(){
    var pass=generatePassword("123","2/tmp/ii","123###!!!@@@%%%***$$$aaaBBB");
    console.assert(pass==="229.#?FQG:Lk<{1_YEs0mhlmHSK","生成密码行为发生变动,pass="+pass);
}
测试密码生成();
export {string2data,data2string,zeahash,encrypt,decrypt,generatePassword}