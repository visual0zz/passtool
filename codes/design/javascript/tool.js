import * as zeadata from "./zeadata.js"

function GET(url,processFunction,errorFunction){
    var httpRequest = new XMLHttpRequest();
        httpRequest.open('GET', url, true);
        httpRequest.send();
        httpRequest.onreadystatechange = function () {
            if (httpRequest.readyState == 4 && httpRequest.status == 200) {
                var json = httpRequest.responseText;
                processFunction(json);
            }else if(httpRequest.status != 200 && errorFunction!=null){
                errorFunction();
            }
        }
}
/**
 * @param {function} access 
 */
function accessIndexList(access){
    GET("datastorage/index",function(data){
        access(data.split("\n"));
    },function(){
        console.error("读取index文件失败。");
    })
}
/**
 * 
 * @param {string} file 文件路径
 * @param {string} keys  密码列表 空格分割
 * @param {function} access 接受结果数据的函数
 */
function accessEncryptedDataFile(file,keys,access){
    GET("datastorage/"+file+".json",function(response){
        var data=JSON.parse(response);
        var keyset=keys.split(" ");
        for(var i=0;i<keyset.length;i++){
            var key=keyset[i];
            // console.log("key=",key);
            var potentialRaw=zeadata.data2string(zeadata.decrypt(data,zeadata.string2data(key))).split("\n");
            // console.log("potentialRaw=",potentialRaw);
            if(potentialRaw[potentialRaw.length-1].match(/0+/g)!=null){
                access(potentialRaw.slice(0,-1));
                return;
            }
        }
        access(zeadata.data2string(zeadata.decrypt(data,zeadata.string2data(keyset[0]))))
    },function(){
        console.error("读取文件失败:"+file);
    }) 
}

export{GET,accessIndexList,accessEncryptedDataFile}