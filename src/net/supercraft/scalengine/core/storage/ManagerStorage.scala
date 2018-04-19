package net.supercraft.scalengine.core.storage

import akka.actor.Actor
import akka.actor.Actor.Receive
import net.supercraft.scalengine.{FPSCamera, Light, Transform}
import net.supercraft.scalengine.core.storage.ManagerStorage.{ById, Insert}

/**
  * Created by jojolepro on 4/3/17.
  */
object ManagerStorage{
		case class Insert()//instance
		case class Delete(id:Int)//id or instance
		case class Update(id:Int)//instance
		case class ById(id:Int)//id
}
class ManagerStorage(repo:StorageRepository) extends Actor{
		override def receive: Receive = {
				case Insert=>repo.addEntity(List(Transform(),FPSCamera()))
				case ById(id)=>println(repo.getComponents(id));println(repo.hasComponentOfType[Transform](id))
		}
}
