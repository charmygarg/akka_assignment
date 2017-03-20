package edu.knoldus

import java.io.File

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.FromConfig
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import edu.knoldus.WordCount.State
import scala.io.Source
import akka.pattern.ask
import scala.concurrent.duration._

class WordCount extends Actor {

  override def receive = {

    case file: File =>
      val lines = getLines(file)
      for(line <- lines) {
        println(line)
        self ! line
      }

    case line: String =>
      WordCount.counter += getWordCount(line)

    case State => sender() ! WordCount.counter
  }

  private def getLines(file: File) = {
    val fileSource = Source.fromFile(file)
    fileSource.getLines
  }


  private def getWordCount(line: String) = {
    line.split("\\s+").length
  }

}


object WordCount extends App {

  case object State

  var counter = 0

  val config = ConfigFactory.parseString(
    """
      |akka.actor.deployment {
      | /poolRouter {
      |   router = balancing-pool
      |   nr-of-instances = 5
      | }
      |}
    """.stripMargin
  )

  val system = ActorSystem("RouterSystem", config)
  val router = system.actorOf(FromConfig.props(Props[WordCount]), "poolRouter")

  implicit val timeout = Timeout(1000 seconds)
  import scala.concurrent.ExecutionContext.Implicits.global

  val file = new File("/home/knodus/Downloads/WordCount/src/main/resources/file")
  router ! file

  Thread.sleep(3000)

  val count = router ? State
  println("Count of Words in a file: ")
  count foreach println

}

