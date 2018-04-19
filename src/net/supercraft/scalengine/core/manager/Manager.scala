package net.supercraft.scalengine.core.manager

import akka.actor.Actor
import net.supercraft.scalengine.core.manager.Manager.{ReceiveAction, SubSystemBroadcast}


/**
  * Do not use context
  */
object Manager{
		case class SubSystemBroadcast(msg:Any)
		type ReceiveAction=PartialFunction[Any,Unit]
}
abstract class Manager extends Actor{
		protected val receiveActions=createReceiveActions
		protected val receiveFunctions=createReceiveFunctions
		//println(s"${self.toString()}: $receiveFunctions")
		/**
		  * Use to load resources, create classes, create workers
		  * No interaction is allowed between managers
		  */
		def preInit={
				sender ! InitStepDone
		}
		/**
		  * Use to bind with other actors, register callbacks
		 */
		def init={
				sender ! InitStepDone
		}

		/**
		  * Use to start the actor main function
		  */
		def postInit={
				sender ! InitStepDone
		}

		/**
		  * Called when the game engine closes normally
		  */
		def destroy()={

		}

		override def receive:Receive ={
				/*receiveActions.foreach{
						a=>a(_)
								println("ran")
				}*/
				actionPassthrough andThen receiveFunctions.reduce[Receive]{case (f,s)=>f orElse s}
		}
		/*override final def receive: Receive = {
				case PreInit=>preInit
				case Init if(this.isInstanceOf[ChildInitiation])=>context become this.asInstanceOf[ChildInitiation].initPhase;self ! Init
				case Init=>init
				case PostInit=>postInit
				case Destroy=>destroy;context stop self
				case default=>getMessage(default)
		}*/
		final def actionPassthrough:PartialFunction[Any,Any]={
				case msg=>{
						receiveActions.foreach{
								a=>if(a.isDefinedAt(msg))a(msg)
						}
				};msg
		}
		def baseReceive:Receive={
				case PreInit=>preInit
				case Init=>init
				case PostInit=>postInit
				case b:SubSystemBroadcast=>broadcastChilds(b.msg)
				case Destroy=>broadcastChilds(Destroy);destroy;context stop self
		}
		def createReceiveActions:Vector[ReceiveAction]={
				//if(isInstanceOf[ManagerMessageLog])ac = ac :+ asInstanceOf[ManagerMessageLog].log
				//if(isInstanceOf[ManagerChildInit])ac = ac :+ asInstanceOf[ManagerChildInit].initReceive //because we don't want to interrupt the Init call going into the baseReceive, we put it as action (Correct since its a reuse of the Init object)
				Vector[ReceiveAction]()
		}

		/**
		  * override and use super to add to receive functions vector (added using orElse, executed after the actions,uses ordering)
		  * Always add using  Vector[Receive](mystuff) :+ super.createReceiveFunctions
		  * @return
		  */
		def createReceiveFunctions:Vector[Receive]={
				Vector[Receive](getMessage,baseReceive,voidMessage)
				//if(isInstanceOf[ManagerMessageLog])fc = fc :+ asInstanceOf[ManagerMessageLog].loggerReceive
		}

		def voidMessage:Receive={
				case _=>
		}

		def getMessage:Receive

		def broadcastChilds(msg:Any)={
				context.children.foreach{c=>c ! msg}
		}
}
