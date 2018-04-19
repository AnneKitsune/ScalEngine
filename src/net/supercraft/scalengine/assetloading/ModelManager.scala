package net.supercraft.scalengine.assetloading

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
  * Created by jojolepro on 11/6/16.
  */
/**
  * Caches models
  * should it be used? do we need instant access to the model? or only instant access to the opengl vao/vbo data?
  * this will manage the heavy static model data
  * then we need to find a way to make a module derive the animations from those static models
  */
class ModelManager extends Actor {
		override def receive: Receive = {
				case _ =>
		}
}
