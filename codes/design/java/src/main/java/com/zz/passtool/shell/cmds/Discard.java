package com.zz.passtool.shell.cmds;

import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

public class Discard extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==0,"这个指令没有参数");
        System.exit(0);
        return null;
    }
    @Override
    public String shortHelp() {
        return "discard : 放弃所有编辑并退出";
    }

}
