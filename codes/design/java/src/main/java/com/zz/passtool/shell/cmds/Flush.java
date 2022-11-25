package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.shell.Command;

public class Flush extends Command {
    @Override
    public Object run(String... args) throws Exception {
        FileCacheService.flush();
        return null;
    }

    @Override
    public String shortHelp() {
        return "flush : 将所有的修改刷入文件";
    }
}
