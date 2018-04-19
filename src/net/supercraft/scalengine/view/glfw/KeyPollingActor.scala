package net.supercraft.scalengine.view.glfw

import java.util.UUID

import akka.actor.{Actor, ActorPath, ActorRef, Props}
import akka.actor.Actor.Receive
import net.supercraft.scalengine.ScalEngine.xDDD
import net.supercraft.scalengine.core.manager.{ManagerChildInit, Manager}
import net.supercraft.scalengine.core.state.Event
import net.supercraft.scalengine.core.util.Settings
import org.lwjgl.glfw.GLFW._

import scala.collection.mutable

/**
  * Created by jojolepro on 12/14/16.
  */
case object KeyPollingStart
case object KeyPollingStop
class KeyPollingActor extends Manager{
		var active = false
		override def getMessage: Receive = {
				case "continue"=>if(active){glfwPollEvents;Thread.sleep(5);self ! "continue"}
				case KeyPollingStart=>active = true;self ! "continue"
				case KeyPollingStop=>active = false
				case _=>
		}
}