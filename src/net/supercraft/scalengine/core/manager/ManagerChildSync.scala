package net.supercraft.scalengine.core.manager

import akka.actor.ActorRef
import net.supercraft.scalengine.DoneCalculatingFrame
import net.supercraft.scalengine.core.manager.ManagerChildSync.AllChildsDone

object ManagerChildSync{
		case object AllChildsDone
		case object DoneProcessing
}
trait ManagerChildSync extends Manager{
		var childDone:Map[ActorRef,Boolean] = null
		def resetChildsDone()={
				val m = context.children.map(m=>m->false).toMap
				if(context.children.size == 0){
						self ! AllChildsDone
				}
				childDone = m
		}
		def doneProcessing(logicActor:ActorRef)={
				childDone = childDone.filterNot(t=>t._1 == logicActor) + (logicActor->true)
				if(!childDone.values.exists(b=>b == false)){
						self ! AllChildsDone
				}
		}
}
