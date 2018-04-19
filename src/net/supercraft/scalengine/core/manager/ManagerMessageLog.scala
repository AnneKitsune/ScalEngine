package net.supercraft.scalengine.core.manager

import akka.actor.ActorRef
import net.supercraft.scalengine.core.manager.Manager.ReceiveAction
import net.supercraft.scalengine.core.manager.ManagerMessageLog._


object ManagerMessageLog{
		case object GetLoggedMessages
		case object ClearLoggedMessages
		case class LoggedMessage(actorref:ActorRef, msgClass:Class[_], time:Long)
		case class LoggedMessages(messages:Vector[LoggedMessage])
}
trait ManagerMessageLog extends Manager{
		private var inputMessages = LoggedMessages(Vector[LoggedMessage]())
		def recordInput(actorRef: ActorRef,msg:Any)={
				inputMessages = inputMessages.copy(messages = inputMessages.messages :+ LoggedMessage(actorRef,msg.getClass,System.nanoTime))//not all messages are events
		}
		def log:ReceiveAction={
				//first sigsev and second stackoverflow
						//case msg if(receiveFunctions.dropRight(1).find(_.isDefinedAt(msg)).isDefined || receiveActions.filterNot(a=>a == log).find(b=>b.isDefinedAt(msg)).isDefined) => recordInput(sender,msg)
				case msg =>if(receiveFunctions.dropRight(1).find(_.isDefinedAt(msg)).isDefined || receiveActions.filterNot(a=>a == log).find(b=>b.isDefinedAt(msg)).isDefined)recordInput(sender,msg)
						//much complex line :)
						//remove voidMessage, check if the message is defined in one of the functions, remove self action, check if message used by one of the actions
						//we are including every message, even the ones broadcasted and unused, we need to be able to remove the ones ending up in the voidMessage
		}
		override def createReceiveActions:Vector[ReceiveAction]={
				Vector[ReceiveAction](log) ++ super.createReceiveActions
		}
		override def createReceiveFunctions:Vector[Receive]={
				Vector[Receive](loggerReceive) ++ super.createReceiveFunctions
		}
		def loggerReceive:Receive={
				case GetLoggedMessages=>sender ! inputMessages;println("Sending logged messages reply")
				case ClearLoggedMessages=>inputMessages = LoggedMessages(Vector[LoggedMessage]())
		}
}
