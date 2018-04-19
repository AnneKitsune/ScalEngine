package net.supercraft.scalengine.core.manager

import net.supercraft.scalengine.core.state.GameFrame

case object StartSubSystemExecution
trait GameFrameSubSystemModifier extends Manager{
		var gameFrame:GameFrame = null
		def operateSerial(f:(GameFrame)=>GameFrame)={
				gameFrame = f(gameFrame)
		}
		def returnGameFrameState={
				if(gameFrame==null){
						println("gameFrame null, can't return")
				}else{
						sender ! gameFrame
				}
		}
}
