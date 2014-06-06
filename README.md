XBRL技术core功能
================

XBRL关键功能的实现范例 包括有XBRL实例文档校验、XBRL实例文档数据导入数据表和XBRL实例文档比较等。

文档校验功能说明
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
    
调用流程如下图所示：
![image](https://raw.githubusercontent.com/joephoenix/xbrlcore/master/validateFlow.jpg)

文档比较功能说明
=================
比较功能主要采用InstanceUtils类中的方法，使用readTupleAndItem（）和readContext（）两个方法，读取元素列表和上下文列表。

基本流程如下：
1.读取实例文档1，转化成二进制流；
2.读取实例文档2，转化成二进制流；
3.调用compare（）方法，传入两个文档的二进制流，得到比较的结果的map；
4.对结果的map包进行处理和展示。

使用net.gbicc.xbrl.ent.util.CompareUtils类中的compare方法：
    
    Map<String, Integer> compare(byte[] content1, byte[] content2)方法一共有2个参数。
    分别是两份实例文档的二进制文件流。
    返回比较结果，即新加入元素的个数、弃用元素的个数和元素值发生改变的个数。

流程图如下：
![image](https://raw.githubusercontent.com/joephoenix/xbrlcore/master/comparisonFlow.jpg)

数据导入功能说明
========================
实例文档数据入库功能采用三个主要的类。
1.调用InstanceUtils类的readTupleAndItem（）和readContext（）两个方法，读取元素列表和上下文列表。
2.调用InsToDataUtils类的dealAllElements（）方法，把实例文档内容转化成一个个“数据导入因子（ GeneImport ）”；
3.数据的导入采用insert语句的方式，所以使用GeneImport类的对象，存放生成insert语句的内容，如字段字符串、值字符串、上下文字段和上下文信息等，由于拼接SQL语句的限制，所以只能支持非clob和blob类型的数据。

基本流程如下：
1.读取实例文档，得到文档的文件名，然后解析文件名得到文档的信息，如公司代码、报告日期、报告版本和报告类型等；
2.在得到实例文档路径的情况下、把实例文档转化成二进制流；
3.声明ImptodbUtils类的对象，然后调用mainImpartFun()方法，传入方法需要的参数，如实例文档二进制流、文件类型信息等；
4.通过执行putInstanceToGenes（）方法，得到所有元素入库的“数据导入因子（ GeneImport ）”；
5.遍历GeneImport的列表，解析GeneImport并组成插入数据的SQL语句；
6. 执行SQL语句，插入数据,先执行清空语句，再执行数据插入语句，并返回执行情况。

net.gbicc.xbrl.ent.util.ImptodbUtils类中的
      String mainImpartFun(byte[] instance, String reportScope,String reportType)方法。

流程图如下：
![image](https://raw.githubusercontent.com/joephoenix/xbrlcore/master/importdataFlow.jpg)
