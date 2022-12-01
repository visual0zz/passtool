import * as zeadata from "./zeadata.js"
import * as tool from "./tool.js"


function readIndex(access){
    tool.accessIndexList(access);
}
const PASSWORD_FORMATTER_FORMAT=/<[0-9a-zA-Z#@%!*$]+>/g;
function readItem(path,keys,seed,access){
    tool.accessEncryptedDataFile(path,keys,function(data){
        //在解密出来的明文中将密码格式符号替换为生成的密码
        if(seed==null){
            access(data);
        }
        var result=[];
        for(var i=0;i<data.length;i++){
            var row=data[i];
            var newRow=row;
            var words=row.split(/((?=<)|(?<=>))/);
            for(var j=0;j<words.length;j++){
                var word=words[j];
                if(word.match(PASSWORD_FORMATTER_FORMAT)){
                    var password=zeadata.generatePassword(seed,path,word.slice(1,-1));
                    newRow=newRow.replace(word,password);
                    console.log("word match:",word,"password=",password);
                }
            }
            result.push(newRow);
        }
        access(result);
    });
}

readItem("t","1","jsp",function(d){
    console.log("data=",d);
})