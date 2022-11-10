package com.zz.passtool.shell.cmds;

import com.zz.passtool.shell.Command;
import com.zz.passtool.shell.CommandDistributor;
import com.zz.passtool.utils.ParamCheckUtil;

public class Help extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==0||args.length==1,"help有0或1个参数");
        if(args.length==0){
            System.out.println("系统中的指令有下列这些:");
            for(Command command: CommandDistributor.commandMap.values()){
                System.out.println(command.shortHelp());
            }
        }else {
            Command cmd=CommandDistributor.commandMap.get(args[0]);
            System.out.println(cmd==null?"找不到指令 "+args[0]:cmd.longHelp());
        }
        return null;
    }
    @Override
    public String shortHelp() {
        return "help [command] : 查询某个指令";
    }

}
