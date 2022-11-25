package com.zz.passtool;
import com.zz.passtool.infrastructure.filesystem.Bash;
import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.CommandDistributor;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static File DATA_FOLDER;
    static{
        String url=Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File buildFolder;
        if(url.endsWith(".jar")){
            buildFolder=new File(url).getParentFile().getParentFile();
        }else{
            buildFolder=new File(url).getParentFile().getParentFile().getParentFile();
        }
        DATA_FOLDER =new File(buildFolder.getParentFile().getParentFile().getParentFile().getParentFile(),"datastorage");
        System.out.println("数据储存在:"+ DATA_FOLDER);
    }
    public static void main(String[] args) throws Exception {
        if(args.length>0&&!args[0].isBlank()){
            CommandDistributor.run(args);
        }else {
            Scanner in=new Scanner(System.in);
            while(true){
                System.out.print(Bash.relatedPath(FolderService.currentFolder,DATA_FOLDER)+">");
                try {
                    String[]commandString=in.nextLine().trim().split(" +");
                    if(commandString.length==0){
                        continue;
                    }
                    if(Arrays.stream(commandString).allMatch(s->s.matches("^.*[!@#$%^&*~()<>,./?'\";:\\[\\]{}+=`].*$"))){
                        System.out.println("输入字符串存在非法字符");
                    }else{
                        CommandDistributor.run(commandString);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("");
                }
            }
        }
    }
}
