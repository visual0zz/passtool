package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

import java.io.File;

public class Edit extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==3,"只能输入3个参数");
        FileCacheService.edit(new File(FolderService.currentFolder,args[0]+".json"), Integer.parseInt(args[1]),args[2]);
        return null;
    }
    @Override
    public String shortHelp() {
        return "edit <file> <rowNum> <content> : 修改文件的某一行的内容,行号从0开始计数";
    }

}
