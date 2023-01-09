package com.zz.passtool.infrastructure;

import com.zz.passtool.infrastructure.filesystem.Bash;
import com.zz.passtool.utils.ParamCheckUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;


public class EncryptedFileEditor {
    /**
     * 用来存储所有的密码，读取时会尝试所有的密码，写入时会最后一个密码
     */
    private List<String> passwords;
    public EncryptedFileEditor(String...passwords){
        this.passwords= new LinkedList<>();
        for(String pass:passwords){
            this.passwords.add(pass);
        }
    }
    public void addPassword(String password){
        if(password!=null){
            passwords.add(password);
        }
    }
    public List<String> listPasswords(){
        return new ArrayList<>(passwords);
    }
    public List<String> read(File file){
        ParamCheckUtil.assertTrue(passwords.size()>0,"必须有至少一个密码才能进行加解密");
        List<String>result= new ArrayList<>();
        try (FileInputStream fileInputStream=new FileInputStream(file)){
            byte[] dataRaw=new byte[fileInputStream.available()];
            fileInputStream.read(dataRaw);
            ZeaData encrypted=ZeaData.fromJson(new String(dataRaw,StandardCharsets.UTF_8));
            Optional<String[]> data=passwords.stream()
                    .map(ZeaData::from)
                    .map(encrypted::decrypt)
                    .map(ZeaData::transferToString)
                    .map(s->s.split("\n"))
                    .reduce((data1, data2)->{
                String tail1=data1[data1.length-1];
                String tail2=data2[data2.length-1];
                int zeroLength1=tail1.replaceAll("0","").length();
                int zeroLength2=tail2.replaceAll("0","").length();
                return zeroLength1<zeroLength2?data1:data2;
            });
            if(data.isPresent()){
                for(int i=0;i<data.get().length;i++){
                    if(i==data.get().length-1 && data.get()[i].matches("0+")){
                        break;
                    }
                    result.add(data.get()[i]);
                }
            }else {
                System.out.println("读不到数据");
            }
        }catch (FileNotFoundException e){
            System.out.println("找不到文件："+file.getAbsolutePath());
        }catch (IOException e){
            System.out.println("打开文件失败："+file.getAbsolutePath());
        }
        return result;
    }
    public void write(File file,List<String> content){
        ParamCheckUtil.assertTrue(passwords.size()>0,"必须有至少一个密码才能进行加解密");
        try {
            Bash.touch(file);
        } catch (IOException e) {
            System.out.println("新建文件失败："+file.getAbsolutePath());
            return;
        }
        content=new ArrayList<>(content);
        content.add("0000000");//用于解密时判定密码是否正确的魔数
        ZeaData data= ZeaData.from(content==null?"":String.join("\n",content));
        ZeaData salt=ZeaData.from(passwords.size()+ Instant.now().toString()+passwords.hashCode()).zeaHash(20);
        ZeaData encrypted=data.encrypt(ZeaData.from(passwords.get(passwords.size()-1)),salt);
        try (FileOutputStream fileOutputStream=new FileOutputStream(file)){
            fileOutputStream.write(encrypted.toJson().getBytes(StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            System.out.println("找不到文件："+file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("打开文件失败："+file.getAbsolutePath());
        }
    }
}
