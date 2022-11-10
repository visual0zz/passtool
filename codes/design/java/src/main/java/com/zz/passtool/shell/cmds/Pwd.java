package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

public class Pwd extends Command {
    @Override
    public Object run(String... args) throws Exception{
        ParamCheckUtil.assertTrue(args.length==0,"pwd没有参数");
        System.out.println(FolderService.pwd());
        return null;
    }
    @Override
    public String shortHelp() {
        return "pwd : 当前所处文件系统路径";
    }
}
