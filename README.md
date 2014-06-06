XBRLCORE
================

XBRL关键功能的实现范例 包括有XBRL实例文档校验、XBRL实例文档数据导入数据表和XBRL实例文档比较等。

校验功能说明
================
校验功能主要采用公司内部的XBRL-CORE包的校验方法来得到校验结果。

基本流程如下：
1.读取实例文档，转化成二进制流；
2.加载分类标准，先设定分类标准的cache路径，然后通过入口文件路径来引用相应的分类标准并加载；
3.使用validate（）方法进行校验，得到校验结果；
4.对校验结果进行处理，取得可以展示的校验结果；
5.返回完全的校验结果的字符串。

使用net.gbicc.xbrl.ent.util.ValidateUtils类中的validate（）方法：

  List<ValidateObject> validate(byte[] content,String taxonomyBase, String location, String instanceId)一共有4个参数。
    第一个参数是“实例文档的二进制代码”，
    第二个参数是“分类标准的根地址”，
    第三个参数是“分类标准的引用路径”，
    第四个参数是“实例文档名称和id”；
    主要流程是先加载分类标准的根地址和引用路径，得到分类标准的缓存；
    方法内部调用validateInstance（）方法，对分类标准进行校验并得到校验结果，最后对校验结果进行分析，得到校验的可读内容。





