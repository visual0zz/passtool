package com.zz.passtool.infrastructure;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 对于jar包中或者文件夹中的class文件进行扫描，获得指定包名下的所有类
 */
public class PackageScanner
{

    boolean recursive = true;//是否递归搜索
    private List<String> packageNames = new ArrayList<>();
    private List<Listener> listeners = new ArrayList<>();//监听器

    public PackageScanner setRecursive(boolean recursive) {
        this.recursive = recursive;
        return this;
    }
    public PackageScanner addListener(Listener listener) {
        this.listeners.add(listener);
        return this;
    }

    public PackageScanner addPackage(String packageName)
    {
        if (packageName == null || !packageName.matches("^[\\w]+(\\.[\\w]+)*$"))
        {
            throw new IllegalArgumentException("非法包名.");
        }
        this.packageNames.add(packageName);
        return this;
    }


    public void scan() throws Exception {
        for (String packageName : packageNames)
        {
            ScanAPackage(packageName);
        }
    }

    private void tigerListener(Class<?> clazz) throws Exception {
        for(Listener listener:listeners){
            listener.accessClass(clazz);
        }
    }

    //扫描jar格式的类
    private void ScanAPackage(String packageName) throws Exception {
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs = null;
        try
        {
            dirs = Thread.currentThread().getContextClassLoader()
                    .getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements())
            {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol))
                {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    scanPackageByFile(packageName, filePath);
                }
                else if ("jar".equals(protocol))
                {
                    JarFile jar = null;
                    try
                    {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements())
                        {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/')
                            {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName))
                            {
                                int idx = name.lastIndexOf('/');
                                if (idx != -1)
                                {
                                    packageName = name.substring(0, idx).replace('/',
                                            '.');
                                }
                                if ((idx != -1) || recursive)
                                {
                                    if (name.endsWith(".class") && !entry.isDirectory())
                                    {
                                        String className = name.substring(
                                                packageName.length() + 1,
                                                name.length() - 6);
                                        Class<?> clazz = Thread.currentThread()
                                                .getContextClassLoader().loadClass(
                                                        packageName + '.' + className);
                                        tigerListener(clazz);
                                    }
                                }
                            }
                        }
                    }
                    catch (IOException e) { throw new IOException("从jar包获取类信息出错",e); }
                }
            }
        }
        catch (IOException e) { throw new IOException("扫描包出错",e); }
    }

//扫描class文件格式的类
    private void scanPackageByFile(String packageName,
                                   String packagePath) throws Exception {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory())
        {
            System.err.println("包 " + packageName + " 是空的");
            return;
        }
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory())
                || (file.getName().endsWith(".class")));
        for (File file : dirfiles)
        {
            if (file.isDirectory())
            {
                scanPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath());
            }
            else
            {
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                Class<?> clazz = Thread.currentThread().getContextClassLoader()
                        .loadClass(packageName + '.' + className);
                tigerListener(clazz);
            }
        }
    }
    public static interface Listener {
        void accessClass(Class<?> clazz)throws Exception;
    }

}