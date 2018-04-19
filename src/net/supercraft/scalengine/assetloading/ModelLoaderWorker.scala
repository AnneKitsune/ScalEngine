package net.supercraft.scalengine.assetloading

import java.io.File

import akka.actor.Actor

/**
  * Created by jojolepro on 11/6/16.
  */
case class LoadModel(modelData:File,loader:File=>Model)
case class ModelLoaded(model:Model)

class ModelLoaderWorker extends Actor{
		override def receive: Receive = {
				case e:LoadModel=>sender ! ModelLoaded(e.loader(e.modelData))
		}
}
