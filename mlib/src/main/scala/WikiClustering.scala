import java.io.StringReader

import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode
import com.huaban.analysis.jieba.SegToken
import com.huaban.analysis.jieba.WordDictionary

import scala.collection.mutable.ListBuffer

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._

import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}

import java.nio.file.Path
import java.nio.file.Paths

object WikiClustering {
   
   def tokenize(context: String) : Seq[String] = {
      var jiebaSeg = new JiebaSegmenter()
      var toks = jiebaSeg.process(context, SegMode.INDEX);
      var tok:SegToken = null
      var segItr = toks.iterator()
      var builder = new ListBuffer[String]()
      while(segItr.hasNext()){
          builder += segItr.next().word
      }
      return builder.toList
   }

   def w2v_fit(sc: SparkContext, sourcePath: String, modelPath:String) {
 
      val path:Path = Paths.get("/usr/share/workspace/dict.txt.big")
      var dict = WordDictionary.getInstance()
      dict.init(path)

      val input:RDD[String] = sc.textFile(sourcePath, 6).cache()
      val token:RDD[Seq[String]] = input.map(context => tokenize(context))
      

      val word2vec = new Word2Vec()
      word2vec.setNumPartitions(5)
      val model = word2vec.fit(token)

      //model.save(sc, modelPath)
      sc.parallelize(Seq(model), 1).saveAsObjectFile(modelPath)
   }

   def w2v_predict(sc: SparkContext, modelPath:String) {
 
      //var model = Word2VecModel.load(sc, modelPath)
      val model = sc.objectFile[Word2VecModel](modelPath).first()
      val synonyms = model.findSynonyms("花瓶",10)
      
      println("testing")
      for((synonyms, cosineSim) <- synonyms){
         println(synonyms+":"+cosineSim)
      }

   }

   def main(args: Array[String]) {

      val sc = new SparkContext("spark://69.164.193.25:7077", "WikiClustering", "/usr/share/workspace/spark-1.5.2/",
         List("/usr/share/workspace/app.jar"))

      val sourcePath = "/usr/share/workspace/twwiki.txt"
      val modelPath = "hdfs://69.164.193.25:9000/abc/twwiki.model"
      
      w2v_fit(sc, sourcePath, modelPath)
      //w2v_predict(sc, modelPath)
     
   }
}
  
