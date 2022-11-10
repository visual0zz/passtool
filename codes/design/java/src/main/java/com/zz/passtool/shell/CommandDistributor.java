package com.zz.passtool.shell;

import com.zz.passtool.infrastructure.PackageScanner;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

public final class CommandDistributor {
    public static final HashMap<String,Command> commandMap;
    static {
        commandMap=new HashMap<>();
        try {
            new PackageScanner()
                    .setRecursive(true)
                    .addPackage("com.zz.passtool.shell")
                    .addListener(clazz -> {
                        if(Command.class.isAssignableFrom(clazz)
                                &&!Modifier.isAbstract(clazz.getModifiers())
                                &&!Modifier.isInterface(clazz.getModifiers())){
                            Command command=(Command) clazz.getDeclaredConstructor().newInstance();
                            commandMap.put(clazz.getSimpleName().toLowerCase(),command);
                            for(String alias: command.alias()){
                                alias=alias.toLowerCase();
                                if(!commandMap.containsKey(alias)){
                                    commandMap.put(alias,command);
                                }
                            }
                        }
                    }).scan();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用某个指令，args[0]会被视为指令 后续字符串会被视为参数
     * @param args 指令和参数
     */
    public static Object run(String...args) throws Exception{
        if(args.length>0&&!args[0].isEmpty())
        {
            Command command=commandMap.get(args[0].toLowerCase());
            if(command==null){
                System.out.println("command \""+args[0]+"\" not found.");
                return null;
            }
            command.run(Arrays.copyOfRange(args,1,args.length));
        }
        return null;
    }
}
