import java.io.StringReader

import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode
import com.huaban.analysis.jieba.SegToken

import scala.collection.mutable.ListBuffer

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._

import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem
import akka.actor.Inbox

import akka.io.IO
import spray.can.Http
import spray.routing._
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout
import spray.json._
import spray.http.MediaTypes._
import spray.http.HttpHeaders.RawHeader

case class NeighborWord(
   word: String,
   sim: Double,
   vector: Array[Double]
)

object NeighborWordProtocol extends DefaultJsonProtocol {
   implicit val neighborWordFormat = jsonFormat3(NeighborWord)
}

import NeighborWordProtocol._

class Word2VecActor extends Actor with HttpService with spray.httpx.SprayJsonSupport {

   var sc:SparkContext = _ 
   var model:Word2VecModel = _

   implicit def actorRefFactory = context

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

   override def preStart() : Unit = {
      
      val conf = new SparkConf()
      conf.setMaster("spark://69.164.193.25:7077")
      conf.setAppName("WikiQuery")
      //conf.setJars(List("/usr/share/hisarack-wikipedia/web/target/scala-2.10/Wikipedia-Model-Query-assembly-1.0.jar"))
      conf.setSparkHome("/usr/share/workspace/spark-1.5.2/")
      conf.set("spark.executor.memory", "12g")
      sc = new SparkContext(conf)

      val modelPath = "hdfs://69.164.193.25:9000/abc/twwiki.model"
      model = sc.objectFile[Word2VecModel](modelPath).first()
   }

   def receive = runRoute(word2VecRoute)
  
   val word2VecRoute: Route =
   {
      path("wiki") {
         get {
            parameters('word, 'topn) { (word, topn) =>

               println("Transform")
               var neighbors:List[NeighborWord] = List()
               var vector = model.transform(word).toArray
               neighbors = NeighborWord(word=word, sim=0.0, vector=vector) :: neighbors
               val synonyms = model.findSynonyms(word, topn.toInt)
               for((synonyms, cosineSim) <- synonyms){
                  println(synonyms+":"+cosineSim)
                  vector = model.transform(synonyms).toArray
                  neighbors = NeighborWord(word=synonyms, sim=cosineSim, vector=vector) :: neighbors
               }

               //respondWithMediaType(`application/json`) {
               respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
                  complete {
                     neighbors
                  }
               }
            }
         }
      }
   }
}

object WikiQuery {
   
   def main(args: Array[String]) {

      implicit val system = ActorSystem("WikiQuery")
      val actor = system.actorOf(Props[Word2VecActor])
      implicit val executionContext = system.dispatcher
      implicit val timeout = Timeout(5 seconds)
      IO(Http).ask(Http.Bind(actor, interface="69.164.193.25", port=5566))
      println("main ended")   
   }
}
  
