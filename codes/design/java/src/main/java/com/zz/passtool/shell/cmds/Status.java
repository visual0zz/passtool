package com.zz.passtool.shell.cmds;

import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

public class Status extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==0,"这个指令无参数");
        return null;
    }

    @Override
    public String shortHelp() {
        return "status : 列出所有被修改的文件";
    }
}
