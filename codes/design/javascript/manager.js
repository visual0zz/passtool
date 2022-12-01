import * as zeadata from "./zeadata.js"
import * as tool from "./tool.js"


function readIndex(access){
    tool.accessIndexList(access);
}
function readItem(path,keys,seed,access){
    tool.accessEncryptedDataFile(path,keys,function(data){
        //在解密出来的明文中将密码格式符号替换为生成的密码
    });
}

readItem("t","1","jsp",function(d){
    console.log("data=",d);
})