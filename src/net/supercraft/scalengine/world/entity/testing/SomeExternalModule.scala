package net.supercraft.scalengine.world.entity.testing

import akka.actor.Actor


case class TestUpdateState(v:Float)
class SomeExternalModule extends Actor{
	var innerstate = 0.0f
	var innerstatetime = 0l
	var lastinnerstate = 0.0f
	var lastinnerstatetime = 0l
	override def receive: Receive = {
		case "init"=> innerstate
		case "loop"=>dostuff()
		case e:TestUpdateState=>updateState(e.v);
	}
	def dostuff()={
		println(s"$lastinnerstate -> $innerstate @ ${System.nanoTime()}")
		self ! "loop"
	}
	def updateState(v: Float)={
		lastinnerstate = innerstate
		innerstate=v
		lastinnerstatetime = innerstatetime
		innerstatetime = System.nanoTime
	}
	//(laststate @ lastTime -> curstate @ t)  @ nanoTime
	//def lerp(v:Float,t:Long,last:Float,lastTime:Long)=((v-last)/(System.nanoTime/(t-lastTime)))+last  //fucked up something xD
}
