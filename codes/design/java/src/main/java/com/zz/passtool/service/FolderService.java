package com.zz.passtool.service;

import com.zz.passtool.infrastructure.filesystem.Bash;

import java.io.*;
import java.util.Comparator;
import java.util.List;

import static com.zz.passtool.Main.DATA_FOLDER;

/**
 * @author 书台
 * @since 2022/5/20 11:31 上午
 */
public final class FolderService {
    private static final Comparator<File> DATA_ITEM_ORDER =(a, b)->{//排序考虑路径的层数，使得层数相近的路径排在一起
        String as=a.getPath();
        String bs=b.getPath();
        int al=as.split("[/\\\\]").length;
        int bl=bs.split("[/\\\\]").length;
        if(al==bl){
            return as.compareTo(bs);
        }else {
            return al-bl;
        }
    };
    public static File currentFolder=DATA_FOLDER;
    public static String ls(){
        List<File> dataFiles= Bash.find(currentFolder,".*\\.json",false);
        dataFiles.sort(DATA_ITEM_ORDER);
        StringBuilder index=new StringBuilder();
        for(File f:dataFiles){
            index.append(Bash.relatedPath(f,currentFolder).replaceAll("[/\\\\]","/")).append('\n');
        }
        return index.toString().replaceAll("\\.json(?=\n|$)","");
    }
    public static void cd(String target){
        if(target.contains("/")||target.contains("\\")){
            System.out.println("不允许跨层级cd");
            return;
        }
        if(target.equals("..")){
            if(currentFolder.compareTo(DATA_FOLDER)==0){
                System.out.println("已到达最上层");
            }else{
                currentFolder=currentFolder.getParentFile();
            }
            return ;
        }
        File targetFile=new File(currentFolder,target);
        if(targetFile.exists() && targetFile.isDirectory()){
            currentFolder=targetFile;
        }else {
            System.out.println("找不到目标文件夹:"+targetFile.getAbsolutePath());
        }
        try {
            currentFolder=currentFolder.getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String pwd(){
        return currentFolder.getAbsolutePath();
    }

    public static List<File> getAllFiles(){
        List<File> dataFiles= Bash.find(currentFolder,".*\\.json",false);
        return dataFiles;
    }
    /**
     * 重建索引文件,index文件是用于前端读取整个文件夹结构用的
     */
    public static void rebuildIndexFile(){
        List<File> dataFiles= Bash.find(DATA_FOLDER,".*\\.json",false);
        dataFiles.sort(DATA_ITEM_ORDER);
        StringBuilder index=new StringBuilder();
        for(File f:dataFiles){
            index.append(Bash.relatedPath(f,DATA_FOLDER).replaceAll("[/\\\\]","/").replaceAll("\\.json(?=\n|$)","")).append('\n');
        }
        if(index.charAt(index.length()-1)=='\n'){
            index.deleteCharAt(index.length()-1);
        }
        try (FileOutputStream indexFile=new FileOutputStream(new File(DATA_FOLDER,"index"))){
            PrintStream p=new PrintStream(indexFile);
            p.print(index);
            p.close();
        } catch (FileNotFoundException e) {
            System.out.println("找不到索引文件："+new File(DATA_FOLDER,"index").getAbsolutePath());
        } catch (IOException e) {
            System.out.println("打开索引文件失败："+new File(DATA_FOLDER,"index").getAbsolutePath());
        }
    }

    public static void purgeAllDeleted(){
        List<File> dataFiles= Bash.find(DATA_FOLDER,".*\\.json.deleted",false);
        for (File dataFile : dataFiles) {
            String result="清理文件<"+dataFile.getAbsolutePath()+">";
            result=result+(dataFile.delete()?"成功":"失败");
            System.out.println(result);
        }
    }
}
