package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

import java.io.File;

public class Delete extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==1,"只能输入一个参数");
        ParamCheckUtil.assertTrue(args[0].matches("[^\\.!@#$%^&*(){};:'\"?<>,`~]*"),"文件名不能出现特殊符号");
        File file=new File(FolderService.currentFolder,args[0]+".json");
        ParamCheckUtil.assertTrue(file.exists(),"目标文件不存在");
        FileCacheService.deleteFile(file);
        return null;
    }
    @Override
    public String shortHelp() {
        return "delete <file> : 删除文件(软删除)";
    }

}
