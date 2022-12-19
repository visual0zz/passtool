package com.zz.passtool.service;

import com.zz.passtool.infrastructure.EncryptedFileEditor;
import com.zz.passtool.infrastructure.filesystem.Bash;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FileCacheService {
    public static final EncryptedFileEditor SOURCE =new EncryptedFileEditor();
    private static HashMap<String, List<String>> readCache=new HashMap<>();
    private static HashMap<String, List<String>> writeCache=new HashMap<>();

    public static void newFile(File file) throws IOException {
        if(!file.exists()){
            SOURCE.write(file,new ArrayList<>());
        }
    }
    public static List<String> read(File file){
        String key=getFullPath(file);
        if(writeCache.containsKey(key)){
            return writeCache.get(key);
        }
        List<String> result;
        readCache.put(key, result=SOURCE.read(file));
        return result;
    }
    public static void add(File file,String newRow){
        String key=getFullPath(file);
        if(!writeCache.containsKey(key)){
            writeCache.put(key,new ArrayList<>(read(file)));
        }
        writeCache.get(key).add(newRow);
    }
    public static void remove(File file,int rowNum){
        String key=getFullPath(file);
        if(!writeCache.containsKey(key)){
            writeCache.put(key,new ArrayList<>(read(file)));
        }
        writeCache.get(key).remove(rowNum);
    }
    public static void edit(File file,int rowNum,String rowContent){
        String key=getFullPath(file);
        if(!writeCache.containsKey(key)){
            writeCache.put(key,new ArrayList<>(read(file)));
        }
        writeCache.get(key).set(rowNum,rowContent);
    }

    public static void flush(){
        for(String key:writeCache.keySet()){
            SOURCE.write(new File(key),writeCache.get(key));
            readCache.put(key,writeCache.get(key));
        }
        writeCache=new HashMap<>();
    }
    public static void deleteFile(File file){
        String key=getFullPath(file);
        if(writeCache.containsKey(key))
        {
            SOURCE.write(new File(key),writeCache.get(key));
        }
        readCache.remove(key);
        writeCache.remove(key);
        if(file.renameTo(new File(key+".deleted"))){
            System.out.println("删除成功");
        }else {
            System.out.println("删除失败");
        }
    }
    public static List<List<String>> diff(File file){
        String key=getFullPath(file);
        if(readCache.containsKey(key) && writeCache.containsKey(key)){
            List<List<String>> result=new ArrayList<>();
            result.add(readCache.get(key));
            result.add(writeCache.get(key));
            return result;
        }else {
            return null;
        }
    }
    public static List<String> status(){
        return new ArrayList<>(writeCache.keySet());
    }
    private static String getFullPath(File file){
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
