package net.supercraft.scalengine

import akka.actor.Props
import net.supercraft.scalengine.GameIOManager.IOOperation
import net.supercraft.scalengine.core.manager._
import net.supercraft.scalengine.core.manager.Manager.SubSystemBroadcast
import net.supercraft.scalengine.core.manager.ManagerChildSync.{AllChildsDone, DoneProcessing}
import net.supercraft.scalengine.core.state.{Component, Event, GameFrame, GameObject}
import net.supercraft.scalengine.view.OpenGLDisplay
import net.supercraft.scalengine.view.glfw.{KeyPollingActor, KeyboardMouseController}

case class ExecuteSideEffects(gameFrame:GameFrame,targetTime:Double)
case class IODone(events:Vector[Event])
case class AddEvent(ev:Event)
case class EventModification(f:IOOperation)
case class IOReadState(gameFrame: GameFrame,targetTime:Double)
case object GetIOGameState

object GameIOManager{
		type IOOperation = (GameFrame,Double)=>Vector[Event]
}

class GameIOManager extends Manager with ManagerChildSync with GameFrameSubSystemModifier with ManagerChildInit with ManagerMessageLog{
		/*val display = context.actorOf(Props[OpenGLDisplay].withDispatcher("single-thread-dispatcher"),name="opengl-display")
		val keyManager = context.actorOf(Props[KeyboardMouseController],"keyboard-mouse-controller")*/
		//val keyPollingActor = context.actorOf(Props[KeyPollingActor],"keyboard-polling-actor")

		var processing = false
		var targetTime = 0.0
		override def getMessage: Receive = {
				case c:ExecuteSideEffects=>processing = true;gameFrame = c.gameFrame;targetTime=c.targetTime;resetChildsDone;broadcastChilds(StartSubSystemExecution)//;println("IO Cur time: "+c.gameFrame.time)
				case SubSystemBroadcast(msg)=>broadcastChilds(msg)
				case DoneProcessing=>doneProcessing(sender)
				case GetIOGameState=>sender ! IOReadState(gameFrame,targetTime)
				case AddEvent(e)=>gameFrame = gameFrame.copy(events = gameFrame.events :+ e)
				case EventModification(f)=>gameFrame = gameFrame.copy(events = f(gameFrame,targetTime))
				case AllChildsDone=>if(processing){context.parent ! IODone(gameFrame.events);processing=false}
		}

		/** Invoked on module init, will create childs
		  * Override to specify childs
		  */
		override def createChilds: Unit = {
				context.actorOf(Props[OpenGLDisplay].withDispatcher("single-thread-dispatcher"),name="opengl-display")
				context.actorOf(Props[KeyboardMouseController],"keyboard-mouse-controller")
		}
}
