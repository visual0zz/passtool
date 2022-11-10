package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

import java.io.File;

public class Drop extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==2,"只能输入2个参数");
        FileCacheService.remove(new File(FolderService.currentFolder,args[0]+".json"), Integer.parseInt(args[1]));
        return null;
    }
    @Override
    public String shortHelp() {
        return "drop <file> <rowNum> : 删除文件的某一行，行号从0开始计数";
    }

}
