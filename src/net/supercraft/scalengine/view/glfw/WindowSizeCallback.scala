package net.supercraft.scalengine.view.glfw

import akka.actor.ActorRef
import com.github.jpbetz.subspace.Vector2
//import net.supercraft.scalengine.B2L
import net.supercraft.scalengine.view.WindowChangedSize
import org.lwjgl.glfw.GLFWWindowSizeCallbackI

/**
  * Created by jojolepro on 12/14/16.
  */
class WindowSizeCallback(parent:ActorRef) extends GLFWWindowSizeCallbackI{
		override def invoke(window: Long, x: Int, y: Int): Unit = {
				parent ! WindowChangedSize(Vector2(x,y))
		}
}
