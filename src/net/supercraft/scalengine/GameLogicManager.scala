package net.supercraft.scalengine

import akka.actor.{ActorRef, Props}
import net.supercraft.scalengine.GameLogicManager.LogicOperation
import net.supercraft.scalengine.core.manager.Manager.SubSystemBroadcast
import net.supercraft.scalengine.core.manager.ManagerChildSync.{AllChildsDone, DoneProcessing}
import net.supercraft.scalengine.core.manager._
import net.supercraft.scalengine.core.state.{GameFrame, GameObject}

case class CalculateFrame(oldFrame:GameFrame, targetTime:Double)
case class DoneCalculatingFrame(state:Vector[GameObject])
case object GetGameFrameState
case class Operate(func:LogicOperation)

object GameLogicManager{
		type LogicOperation = (GameFrame,Double)=>Vector[GameObject]
}
//GameFrame + targetTime -> GameFrame
class GameLogicManager extends Manager with ManagerChildSync with GameFrameSubSystemModifier with ManagerChildInit with ManagerMessageLog{


		var targetTime = 0.0
		var processing = false
		override def getMessage: Receive = {
				case c:CalculateFrame=>processing = true;gameFrame = c.oldFrame;targetTime = c.targetTime;resetChildsDone;broadcastChilds(StartSubSystemExecution)
				case SubSystemBroadcast(msg)=>broadcastChilds(msg)
				case DoneProcessing=>doneProcessing(sender)
				case GetGameFrameState=>returnGameFrameState
				case Operate(f)=>operate(f)
				case AllChildsDone=>if(processing){context.parent ! DoneCalculatingFrame(gameFrame.states);processing = false}
		}

		def operate(op:LogicOperation)={
				gameFrame = gameFrame.copy(states = op(gameFrame,targetTime))
		}

		/** Invoked on module init, will create childs
		  * Override to specify childs
		  */
		override def createChilds: Unit = {
				context.actorOf(Props[LInputHandler],"manager-logic-input-handler")
		}
}
