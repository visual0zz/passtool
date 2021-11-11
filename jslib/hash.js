function hash(str){//输入任意长度字符串，输出固定16个字节长度的字符串，满足密码key的需求
    var data=[];
    str=str+"tail";
    for(var i=0;i<=15;i++){//初始化数值缓冲区
        data[i]=global_iv.charCodeAt(i)&255;
    }
    
    for(var i=0;i<str.length;i++){//写入一次
        data[i]=(data[i%16]*str.charCodeAt(i))&255;
    }
    for(var i=0;i<=15;i++){//混乱一次
        data[(i+3)%16]=(data[i]^data[(i+3)%16])&255;
    }

    for(var i=0;i<str.length;i++){//写入一次
        data[i]=(data[i%16]*str.charCodeAt((i+7)%str.length))&255;
    }
    for(var i=15;i>=0;i--){//混乱一次
        data[i]=(data[i]^data[(i+5)%16])&255;
    }
    
    for(var i=0;i<str.length;i++){//写入一次
        data[i]=(data[i%16]*str.charCodeAt((i+31)%str.length))&255;
    }
    for(var i=0;i<=15;i++){//混乱一次
        data[(i+1)%16]=(data[i]^data[(i+1)%16])&255;
    }
    return String.fromCharCode(data[0],data[1],data[2],data[3],data[4],data[5],data[6],data[7],data[8],data[9],data[10],data[11],data[12],data[13],data[14],data[15]);
}
