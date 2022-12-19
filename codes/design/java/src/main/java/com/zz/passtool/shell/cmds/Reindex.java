package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

public class Reindex extends Command {
    @Override
    public Object run(String... args) throws Exception{
        ParamCheckUtil.assertTrue(args.length==0,"reindex没有参数");
        FolderService.rebuildIndexFile();
        return null;
    }

    @Override
    public String shortHelp() {
        return "reindex : 重建index文件（文件写）";
    }
}
