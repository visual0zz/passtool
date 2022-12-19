package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

import java.io.File;

public class New extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==1,"只能输入一个参数");
        ParamCheckUtil.assertTrue(args[0].matches("[^\\.!@#$%^&*(){};:'\"?<>,`~\\\\]*"),"文件名不能出现特殊符号");
        FileCacheService.newFile(new File(FolderService.currentFolder,args[0]+".json"));
        return null;
    }
    @Override
    public String shortHelp() {
        return "new <file> : 新建文件（文件写）";
    }
}
