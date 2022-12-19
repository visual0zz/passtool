package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;

public class UpdatePasswd extends Command {
    @Override
    public Object run(String... args) throws Exception {
        FolderService.updatePassword();
        return null;
    }
    @Override
    public String shortHelp() {
        return "updatePasswd : 使用新密码重新加密所有当前目录下的所有文件（内存写）";
    }
}
