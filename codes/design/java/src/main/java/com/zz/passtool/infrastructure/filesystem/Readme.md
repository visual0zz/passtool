#文件操作界面
* Bash 
>提供一个模仿linux bash指令的文件操作接口，用于
基本的建立文件夹，删除，列出，清理空文件夹，递归删除文件夹
等等功能
* FileTreeTravelerManager
>提供遍历某个文件夹下某些文件和某些子文件夹的功能，
通过定义一个FileTreeTraveler对象 来定义对于每个被遍历到的
文件或文件夹的操作。

>当调用 `new FileTreeTravelerManager manager(folder).travel(traveler)`
的时候会遍历所有folder的子文件夹，对于每一个子文件夹都会
通过 `FileTreeTraveler.shouldTravelIntoFolder`函数来
询问traveler时候遍历进入此子文件夹，并且会提供这个文件夹的
File对象和其在文件树上的深度作为参数。
对于遍历到的每一个文件和文件夹，都会调用traveler的函数进行操作。

* FileTreeTraveler
```
public interface FileTreeTraveler {
    /**
     * @param layer_level 当前文件夹在迭代树上的层数 根文件夹为0
     * @param folder 当前文件夹
     * @return 如果返回true会继续深入此文件夹进行迭代，返回false不会继续迭代这个文件夹下面的内容。\
     * 不管返回true还是false，都会对当前这个文件夹本身调用travelFolder。
     */
    boolean shouldTravelIntoFolder(int layer_level, File folder)throws Exception;
    /**
     * @param layer_level 当前文件夹在迭代树上的层数 根文件夹为0
     * @param folder 当前文件夹
     * @param result 用于用户自定义功能的实现，迭代完成后会返回这个值，可以在travel代码中对其进行更改。
     */
    void travelFolder(int layer_level, File folder, List<String> result)throws Exception;

    /**
     * @param layer_level 当前文件在迭代树上的层数 根文件夹为0
     * @param file 当前文件
     * @param result 用于用户自定义功能的实现，迭代完成后会返回这个值，可以在travel代码中对其进行更改。
     */
    void travelFile(int layer_level,File file,List<String> result) throws Exception;
}
```

定义一个实现这个接口的类的对象(也就是traveler)，然后在其中自定义自己的操作，
最后使用FileTreeTravelerManager来应用这个traveler。
每迭代到一个文件或文件夹，traveler都会被提供以下参数
    
>1. int layer_level 当前文件在整个迭代树上的深度，根文件夹
被定义为0,根文件夹的子文件和子文件夹被定义为1,这些子文件夹的
子文件的子文件夹被定义为2,以此类推
>2. File folder或者File file 目标文件或者文件夹的File对象
>3. List<String> result 一个一开始是空的字符串数组，用于
帮助traveler在不同的迭代分支间传递信息，没有预定义功能，用于实现
traveler的自定义功能，这个对象最后会作为FileTreeTravelerManager.travel
函数的返回值。