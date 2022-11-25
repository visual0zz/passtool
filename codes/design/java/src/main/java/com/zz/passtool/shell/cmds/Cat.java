package com.zz.passtool.shell.cmds;

import com.zz.passtool.Main;
import com.zz.passtool.constants.ShellColor;
import com.zz.passtool.infrastructure.ZeaData;
import com.zz.passtool.infrastructure.filesystem.Bash;
import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;

public class Cat extends Command {
    private static final String PASSWORD_FORMATTER_FORMAT="<[0-9a-zA-Z#@%!*$]+>";
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==1,"只能输入一个参数");
        ParamCheckUtil.assertTrue(!(args[0].contains("..")||args[0].contains("~")),"文件路径不能出现.. 和 ~");
        ParamCheckUtil.assertTrue(!args[0].matches("^/.*"),"此处只接受相对路径");
        File file=new File(FolderService.currentFolder,args[0]+".json");
        List<String> data=FileCacheService.read(file);
        NumberFormat format=NumberFormat.getNumberInstance();
        format.setMinimumIntegerDigits(data.size()<5?1:((int) Math.log10(data.size()-1)+1));
        format.setGroupingUsed(false);
        for(int i=0;i<data.size();i++){
            System.out.println(ShellColor.green+ShellColor.hightlight
                    +format.format(i)+"|"+ShellColor.clear
                    +replacePassword(file,data.get(i)));
        }
        return null;
    }
    @Override
    public String shortHelp() {
        return "cat <file> : 显示文件内容";
    }

    /**
     * 将字符串中的密码格式字符串替换为根据格式生成的密码
     * @param row 原文件的行
     * @return 替换后的字符串
     */
    private static String replacePassword(File file,String row){
        if(Main.seed==null||!row.matches("^.*"+PASSWORD_FORMATTER_FORMAT+".*$")){
            return row;
        }
        String result=row;
        String fileRelatedPath= Bash.relatedPath(file,Main.DATA_FOLDER);
        for(String word:row.split("((?=<)|(?<=>))")){
            if(word.matches(PASSWORD_FORMATTER_FORMAT)){
                result=result.replace(word,buildPassword(Main.seed,fileRelatedPath,word.substring(1,word.length()-1)));
            }
        }
        return result;
    }
    private static String buildPassword(String systemSeed,String filePath,String formatter){
//        System.out.println(systemSeed+"#"+filePath+"#"+formatter);
        ZeaData data=ZeaData.from(systemSeed+"#"+filePath+"#"+formatter);
        data=data.encrypt(data.align(20).zeaHash(20));
        data=data.zeaHash(formatter.length());
        return data.generatePassword(formatter);
    }
}
