package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

public class Finish extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==0,"这个指令没有参数");
        FileCacheService.flush();
        FolderService.rebuildIndexFile();
        System.exit(0);
        return null;
    }
    @Override
    public String shortHelp() {
        return "finish : 保存所有变更并退出";
    }

}
