package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

public class Cd extends Command {
    @Override
    public Object run(String... args) throws Exception{
        ParamCheckUtil.assertTrue(args.length==1,"只能输入一个参数");
        FolderService.cd(args[0]);
        return null;
    }
    @Override
    public String shortHelp() {
        return "cd <folder> : 移动到某个文件夹";
    }

}
