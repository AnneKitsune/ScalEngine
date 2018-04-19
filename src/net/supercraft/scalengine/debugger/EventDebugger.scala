package net.supercraft.scalengine.debugger

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive

/**
  * Created by jojolepro on 12/1/16.
  */
class EventDebugger extends Actor{
		val bufferSize = 1024
		override def receive: Receive = {
				case e:Any =>
		}
		def trace={
				//trace a event route from its creation to its destination
				//will probably use the event type
}
def watch(actorRef: ActorRef)={
		//log all events from the actor
}
		def dump={
		//dump stored events
}
}
