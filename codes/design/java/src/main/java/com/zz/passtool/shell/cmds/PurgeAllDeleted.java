package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;

public class PurgeAllDeleted extends Command {
    @Override
    public Object run(String... args) throws Exception {
        FolderService.purgeAllDeleted();
        return null;
    }
    @Override
    public String shortHelp() {
        return "purgeAllDeleted : 清理所有已删除的条目";
    }
}
