package net.supercraft.scalengine.view.glfw

import akka.actor.ActorRef
//import net.supercraft.scalengine.B2L
import net.supercraft.scalengine.core.manager.Manager
import org.lwjgl.glfw.{GLFWKeyCallback, GLFWKeyCallbackI}
import org.lwjgl.glfw.GLFW._
/**
  * Created by jojolepro on 10/11/16.
  */

class KeyboardMouseListener(owner:ActorRef) extends GLFWKeyCallbackI{
		override def invoke(window: Long, key: Int, scanCode: Int, action: Int, mods: Int): Unit = {
				if(action != GLFW_REPEAT){
						owner ! KeyStateChange(window,key,scanCode,action,mods)
						/*if(action == GLFW_PRESS)
								println(s"KeyPress: $window $key $scanCode $action $mods")
						else if(action == GLFW_RELEASE)
								println(s"KeyRelease: $window $key $scanCode $action $mods")*/
				}
		}
}