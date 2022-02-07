# adam-explorer
Interactive explorer for ADAM genomics data models.  Apache 2 licensed.

[![Build Status](https://travis-ci.org/heuermh/adam-explorer.svg?branch=master)](https://travis-ci.org/heuermh/adam-explorer)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.heuermh.adamexplorer/adam-explorer_2.12.svg?maxAge=600)](http://search.maven.org/#search%7Cga%7C1%7Ccom.github.heuermh.adamexplorer)
[![API Documentation](http://javadoc.io/badge/com.github.heuermh.adamexplorer/adam-explorer_2.12.svg?color=brightgreen&label=scaladoc)](http://javadoc.io/doc/com.github.heuermh.adamexplorer/adam-explorer)


### Hacking adam-explorer

Install

 * JDK 1.8 or later, http://openjdk.java.net
 * Apache Maven 3.3.9 or later, http://maven.apache.org
 * Apache Spark 3.2.0 or later, http://spark.apache.org
 * ADAM: Genomic Data System 0.37.0 or later, https://github.com/bigdatagenomics/adam


To build

    $ mvn install


### Running adam-explorer using ```spark-shell```

```
$ spark-shell \
    --conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
    --conf spark.kryo.registrator=org.bdgenomics.adam.serialization.ADAMKryoRegistrator \
    --jars target/adam-explorer_2.12-<version>-SNAPSHOT.jar,$PATH_TO_ADAM_ASSEMBLY_JAR

...
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 3.2.0
      /_/

Using Scala version 2.12.15 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_191)
Type in expressions to have them evaluated.
Type :help for more information.

scala> import org.bdgenomics.adam.ds.ADAMContext._
import org.bdgenomics.adam.ds.ADAMContext._

scala> import com.github.heuermh.adam.explorer.ADAMExplorer._
import com.github.heuermh.adam.explorer.ADAMExplorer._

scala> val alignments = sc.loadAlignments("sample.bam")
alignments: org.bdgenomics.adam.ds.read.AlignmentRecordRDD = RDDBoundAlignmentRecordRDD
with 85 reference sequences, 3 read groups, and 0 processing steps

scala> explore(alignments)
res0: Int = 0

scala>
```

![adam-explorer screenshot](https://github.com/heuermh/adam-explorer/raw/master/images/screen-shot.png)
