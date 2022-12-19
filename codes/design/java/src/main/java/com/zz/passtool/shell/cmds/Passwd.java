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
        return "passwd <password> : 设定密码，密码用于加解密文件（内存写）";
    }

    @Override
    public String longHelp() {
        return shortHelp()
                + "\npasswd指令会建立一个待选密码列表，从文件读取数据时会使用所有密码进行解密尝试，写文件时，会使用最新的密码进行加密"
                + "\n多次执行passwd会将多个密码放入这个待选列表中（只写不读）";
    }
}
