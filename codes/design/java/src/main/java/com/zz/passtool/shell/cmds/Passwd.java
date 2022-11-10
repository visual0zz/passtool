package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

public class Passwd extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==1,"只能输入一个参数");
        FileCacheService.SOURCE.addPassword(args[0]);
        return null;
    }
    @Override
    public String shortHelp() {
        return "passwd <password> : 设定密码，密码用于加解密文件";
    }
}
