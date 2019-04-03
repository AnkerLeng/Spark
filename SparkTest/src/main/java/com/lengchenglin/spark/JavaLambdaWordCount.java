package com.lengchenglin.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;


public class JavaLambdaWordCount {

    public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("JavaWordCount").setMaster("local[2]");
        //不设置master则要在submit时指定
        //SparkConf conf = new SparkConf().setAppName("JavaWordCount");
        //创建sparkContext
        JavaSparkContext jsc = new JavaSparkContext(conf);
        //指定以后从哪里读取数据
        JavaRDD<String> lines = jsc.textFile("hdfs://hadoop000:8020/hdfsapi/test/c.txt");
        //参数形式
        //JavaRDD<String> lines = jsc.textFile(args[0]);
        //切分压平
        JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
        //将单词和一组合
        JavaPairRDD<String, Integer> wordAndOne = words.mapToPair(w -> new Tuple2<>(w, 1));
        //聚合
        JavaPairRDD<String, Integer> reduced = wordAndOne.reduceByKey((m, n) -> m + n);
        //调整顺序
        JavaPairRDD<Integer, String> swaped = reduced.mapToPair(tp -> tp.swap());
        //排序
        JavaPairRDD<Integer, String> sorted = swaped.sortByKey(false);
        //调整顺序
        JavaPairRDD<String, Integer> result = sorted.mapToPair(tp -> tp.swap());
        //将结果保存到hdfs
        result.saveAsTextFile("hdfs://hadoop000:8020/hdfsapi/test/out");
        //result.saveAsTextFile(args[1]);
        //释放资源
        jsc.stop();


    }
}