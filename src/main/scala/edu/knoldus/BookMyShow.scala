package edu.knoldus

import akka.actor.{Props, ActorSystem, Actor}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask

class BookMyShow extends Actor {

  var status: Boolean = true

  override def receive = {
    case list => sender() ! checkBooked
  }

  def checkBooked = {
    if(status) {
      status = false
      "Your seat has been booked!!"
    } else {
      "Sorry! Seat has been taken"
    }
  }

}

object BookMyShow extends App {

  val list = List.range(1, 10)
  val system = ActorSystem("BookMyShow")
  val ref1 = system.actorOf(Props[BookMyShow])

  implicit val timeout = Timeout(1000 seconds)

  val person1 = ref1 ? list(4)
  val person2 = ref1 ? list(4)
  val person3 = ref1 ? list(4)
  val person4 = ref1 ? list(4)

  person1 foreach println
  person2 foreach println
  person3 foreach println
  person4 foreach println

}
