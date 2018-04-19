package net.supercraft.scalengine.network

import spray.json._

import scala.io.Source

/**
  * Perms goes as follow:
  * for each event, there is a list of boolean where [isClRunnable,isSvRunnable,isOp,isCmd] mapped to the full classname of the event
  */
/*
we don't need to have that in an actor
 */
/*class EventPermRegister extends Actor{
		private val perms:Map[String,List[Boolean]] = loadNetworkEvents()


		def loadNetworkEvents():Map[String ,List[Boolean]]= {
				val str=Source.fromFile("assets/Data/PacketType.json").getLines().mkString
				val json=str.parseJson
				println(json.compactPrint)
				val t = new EventPosition(1)
				Map(EventPosition.getClass.getName->List(true,true,true,false))
				//TODO
		}

		override def receive: Receive = {
				case LoadDone=>context.become(loaded)
				case _=>sender() ! ErrorNotReady
		}
		def loaded:Receive={
				case q:IsClientRunnable=>sender() ! isClRunnable(q.netEvent)
				case q:IsServerRunnable=>sender() ! isSvRunnable(q.netEvent)
				case q:IsOp=>sender() ! isOp(q.netEvent)
				case q:IsCmd=>sender() ! isCmd(q.netEvent)
		}

		def getEventPerms(ev:NetEvent):Option[List[Boolean]]=perms.get(ev.getClass.getName)
		def checkPerm(ev:NetEvent,permNumber:Int):Either[ErrorEvent,Boolean]={
				getEventPerms(ev) match{
						case Some(p)=>Right(p(permNumber))
						case None=>Left(new PermsNotDefinedError)
				}
		}
		def isClRunnable(ev:NetEvent)=checkPerm(ev,0)
		def isSvRunnable(ev:NetEvent)=checkPerm(ev,1)
		def isOp(ev:NetEvent)=checkPerm(ev,2)
		def isCmd(ev:NetEvent)=checkPerm(ev,3)
}*/
object Test{
		def main(args: Array[String]): Unit = {
				val epr = new EventPermRegister
		}
}
class EventPermRegister{
		private val perms:Map[String,List[Boolean]]=loadNetworkEventPerms()
		def loadNetworkEventPerms():Map[String ,List[Boolean]]= {
				val json=Source.fromFile("assets/Data/PacketType.json").getLines().mkString
				val jsonobj=json.parseJson
				//println(json.toString)
				println(jsonobj.asJsObject.fields.map{case (k,v)=>k->v}.toString())
				//val t = new EventPosition(1)
				Map("test"->List(true,true,true,false))
				//TODO
		}
		//def getEventPerms(ev:NetEvent):Option[List[Boolean]]=perms.get(ev.getClass.getName)
}
/*

Put this back in the actor after, because the actor model offers the error handling system and that will allow us to keep the usage of the actor hierarchy.
The only downside is the complexity of requesting a perm as there will be some delay...(utilisation of Future or something will be needed in using classes)

 */