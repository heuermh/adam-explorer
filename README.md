# adam-explorer
Interactive explorer for ADAM genomics data models.  Apache 2 licensed.

### Hacking adam-explorer

Install

 * JDK 1.8 or later, http://openjdk.java.net
 * Apache Maven 3.3.9 or later, http://maven.apache.org
 * Apache Spark 2.3.2 or later, http://spark.apache.org
 * ADAM: Genomic Data System 0.26.0-SNAPSHOT or later, https://github.com/bigdatagenomics/adam


To build

    $ mvn install


### Running adam-examples using ```spark-shell```

```
$ spark-shell \
    --conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
    --conf spark.kryo.registrator=org.bdgenomics.adam.serialization.ADAMKryoRegistrator \
    --jars target/adam-explorer_2.11-0.26.0-SNAPSHOT.jar,adam-assembly-spark2_2.11-0.26.0-SNAPSHOT.jar

...
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 2.4.0
      /_/

Using Scala version 2.11.12 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_191)
Type in expressions to have them evaluated.
Type :help for more information.

scala> import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.adam.rdd.ADAMContext._

scala> import com.github.heuermh.adam.explorer.ADAMExplorer._
import com.github.heuermh.adam.explorer.ADAMExplorer._

scala> val alignments = sc.loadAlignments("sample.bam")
alignments: org.bdgenomics.adam.rdd.read.AlignmentRecordRDD = RDDBoundAlignmentRecordRDD
with 85 reference sequences, 3 read groups, and 0 processing steps

scala> explore(alignments)
res0: Int = 0

scala>
```

![adam-explorer screenshot](https://github.com/heuermh/adam-explorer/raw/master/images/screen-shot.png)
