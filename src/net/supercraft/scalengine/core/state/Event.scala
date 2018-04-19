package net.supercraft.scalengine.core.state

abstract class Event(val time:Double,val id:String=java.util.UUID.randomUUID.toString){
		//lazy val uuid = java.util.UUID.randomUUID.toString
}
