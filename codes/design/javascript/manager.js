import * as zeadata from "./zeadata.js"

function testEncrypt(){
    var data = zeadata.string2data("昆仑#@13abc赑箜琳亵渎琅篌屃");
    var key = zeadata.string2data("注意注意");
    console.info("data=", data);
    var encrypted=zeadata.encrypt(data,key);
    console.info("encrypted=",encrypted);
    var decrypted=zeadata.decrypt(encrypted,key);
    console.info("decrypted=",decrypted);
    console.info("decrypted string=",zeadata.data2string(decrypted));
}
testEncrypt();