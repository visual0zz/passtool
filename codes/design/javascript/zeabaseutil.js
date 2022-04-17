
ALGORITHM_BLOCK_SIZE=32;

function string2data(str){
    //将字符串转化为统一数据格式，输入str 输出数组
    if(str.constructor!=String){
        throw "input must be string"
    }
    var result=[];
    for(i=0;i<str.length;i++){
        result.push(str.charCodeAt(i));
    }
    return result;
}

function data2string(data){
    //将统一格式转化为字符串
    if(!Array.isArray(data)){
        throw "input must be array"
    }
    var result="";
    for(i=0;i<data.length;i++){
        result+=String.fromCharCode(data[i]);
    }
    return result;
}

function align(data,alignment){
    if(!Array.isArray(data) || isNaN(alignment) || alignment<=0){
        throw "input must be an array and a positive number."
    }
    var tail=[data.length,alignment];
    while((data.length+tail.length)%alignment!=0){
        tail.push(0);
    }
    var result=data.concat(tail.reverse());
    return result;
}

function unalign(data){
    if(!Array.isArray(data)){
        throw "input must be array"
    }
    console.info("aaa");
}

function isaligned(data){
    if(!Array.isArray(data)){
        throw "input must be array"
    }
    console.info("aaa");
}
d=string2data("66766");
console.info(d);
console.info(align(d,3));