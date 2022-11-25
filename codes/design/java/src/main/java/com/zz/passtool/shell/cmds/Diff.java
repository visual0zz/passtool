package com.zz.passtool.shell.cmds;

import com.zz.passtool.constants.ShellColor;
import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;

public class Diff extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==1,"只能输入一个参数");
        ParamCheckUtil.assertTrue(!(args[0].contains("..")||args[0].contains("~")),"文件路径不能出现.. 和 ~");
        ParamCheckUtil.assertTrue(!args[0].matches("^/.*"),"此处只接受相对路径");
        List<List<String>> diff= FileCacheService.diff(new File(FolderService.currentFolder,args[0]+".json"));
        if(diff==null){
            System.out.println(ShellColor.green +"文件无修改:"+args[0]+ShellColor.clear);
        }else {
            System.out.println(ShellColor.green+ShellColor.hightlight +"原文件内容为:"+ShellColor.clear);
            List<String> data=diff.get(0);
            NumberFormat format=NumberFormat.getNumberInstance();
            format.setMinimumIntegerDigits(data.size()<5?1:((int) Math.log10(data.size()-1)+1));
            format.setGroupingUsed(false);
            for(int i=0;i<data.size();i++){
                System.out.println(ShellColor.green+ShellColor.hightlight+format.format(i)+"|"+ShellColor.clear+data.get(i));
            }
            System.out.println(ShellColor.green+ShellColor.hightlight +"\n修改后内容为:"+ShellColor.clear);
            data=diff.get(1);
            format.setMinimumIntegerDigits(data.size()<5?1:((int) Math.log10(data.size()-1)+1));
            for(int i=0;i<data.size();i++){
                System.out.println(ShellColor.green+ShellColor.hightlight+format.format(i)+"|"+ShellColor.clear+data.get(i));
            }
        }
        return null;
    }

    @Override
    public String shortHelp() {
        return "diff <file> : 显示某个文件的修改";
    }
}
