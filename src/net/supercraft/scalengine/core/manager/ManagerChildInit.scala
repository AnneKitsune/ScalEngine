package net.supercraft.scalengine.core.manager

import net.supercraft.scalengine.core.manager.Manager.ReceiveAction
import net.supercraft.scalengine.core.manager.ManagerChildSync.AllChildsDone

//todo fix that when we call context.parent ! InitStepDone, we do it only when all childs are init AND this is Init
trait ManagerChildInit extends Manager with ManagerChildSync{
		var initStep:InitState = PreInit
		def initReceive:ReceiveAction={
						case Init=>init;createChilds;resetChildsDone;broadcastChilds(initStep)
						case InitStepDone=>doneProcessing(sender)
						case AllChildsDone=>
								if(initStep != PostInit && initStep != InitDone){
										nextStep
										resetChildsDone
										broadcastChilds(initStep)
								}else{
										nextStep
								}
		}
		/**Invoked on module init, will create childs
		  *Override to specify childs
		 */
		def createChilds
		override def init={
				//removes the context.parent ! InitStepDone call
				//override init to run the initReceive?
		}
		override def createReceiveActions:Vector[ReceiveAction]={
				Vector[ReceiveAction](initReceive) ++ super.createReceiveActions
		}
		def nextStep={
				initStep match{
						case PreInit=>initStep = Init
						case Init=>initStep = PostInit
						case PostInit=>context.parent ! InitStepDone;initStep = InitDone
						case _=>
				}
		}
}
