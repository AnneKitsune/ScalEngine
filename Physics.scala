package net.supercraft.B2LScala

import akka.actor.Actor

/**
  * Created by jojolepro on 6/26/16.
  */
class Physics() extends Actor{
		//private var physicsSettings:PhysicsSettings = new PhysicsSettings()//determined by the map being loaded (map.phys)
		override def receive: Receive = {
				case _=>
		}


		//avoid side effects if possible, may be able to return new entities
}

trait Physical[A <: Physical]{
		def compute(time:Float,events:List[(PhysicalEvent,Float)]):A
}
trait PhysicalEvent{
		val targets:List[Physical]
		def evaluate(fromTime:Float,toTime:Float)
}

/**
  * Properties[Type] are evaluated using PhysicalEvents
  */
trait Property[A]{
		def evaluate()
}
case class SetAcceleration(targets:List[Physical],calculation:(Float)=>Float) extends PhysicalEvent

case class TimeLine(objects:List[Physical],events:List[PhysicalEvent],evaluationTime:Float) extends Physical[TimeLine]{
		/**
		  * "Polls" the timeline to find out what is the current state of the objects
		  *
		  * @param time
		  * @return
		  */
		override def compute(time: Float,events:List[(PhysicalEvent,Float)]): TimeLine = {
				//on all objects computed them using the time and the events that are targeting that object
				copy(objects.map(obj=>obj.compute(time,events.filter(e=>e._1.targets.contains(obj)))),evaluationTime = time)
		}

		def addEvent(event: PhysicalEvent): TimeLine = {
				copy(objects,events:+event)
		}
		def addObject(obj: Physical): TimeLine = {
				copy(objects:+obj)
		}

}
class DynamicObject(position:Float){
		//position = vitesseTotale(aire)
		//vitesse = accelerationTotal(aire)
		//acceleration = fonction
		val acceleration:PartialFunction[Float,Float]={
				case x if(x>=1)=>5
				case x if(x>=0)=>1
		}
		//not the real calculation
		val speed=(fromTime:Float,toTime:Float)=>{
				if(fromTime>=1){
						(toTime-fromTime)*acceleration.apply(fromTime)
				}else{
						(1-fromTime)*acceleration.apply(fromTime) + (toTime-1) * acceleration.apply(toTime)
				}
		}

		val position = (fromTime:Float,toTime:Float)
}



//Testing calculations capabilities...
object Physics{
		def test={
				val obj = new DynamicObject(0)

				val accelFunc:PartialFunction[Float,Float]={
						case x if(x>=0)=>0
				}
				val accelFunc2:PartialFunction[Float,Float]={
						case x if(x>=1)=>5
				}
				val accelFuncFull=accelFunc2.orElse(accelFunc)
				def getSpeedFromAccel(from:Float,to:Float,func:PartialFunction[Float,Float])={
						func.
				}
				val accelEvent = new SetAcceleration(1,List(obj),)
				val timeLine = new TimeLine(List(obj),List(accelEvent))
		}
		/*def test: Unit ={
				val acceleration:PartialFunction[Double,Double]={
						case x:Double if(x >=1 && x<=2)=>x*x+6
				}

				def speed(iniSpeed:Double,iniTime:Double,endTime:Double,accelerationFunc:PartialFunction[Double,Double]):Option[Double]={
						//My attempt to integrate

						val f=f1(endTime)-fi(iniTime)
						speed + f
				}

				val finalSpeed = speed(0,1,2,acceleration)//t 1 to 2
				val finalSpeed2 = speed(0,1,1.5,acceleration)//t 1 to 1.5

				println(finalSpeed.get)
				println(finalSpeed2.get)

		}*/
		def compute(x:Int,func:PartialFunction[Double,Double]):Double= if(func.isDefinedAt(x)) func.apply(x) else 0
		//store from left to right, and read from right to left to enable
}


//overflow check: if(Int.max - currentvalue + addedValue < 0)not overflow if > then overflow will happen

//gravity=9.80665

/*
Collision will be like the one in EndlessWorlds, but with 3d and colliding triangles
Precision = 1 -> Look for collisions at end point
              = 2 -> Look for collisions at half distance between starting and end point; and at end point
              = ... -> look between start and end more times

and/or do a raycast at center point towards direction and check for collisions
and/or do a raycast from closest to direction point towards end of pos change and check for collisions

ex: 0------|-g  where 0 is object, | is end point and g id another object
there you would see that the end point is before the object(nothing intersects raycast before g)
you would then be able to watch for other collisions(with triangles) close to the end point | with less precision(skip triangles)




should not calculate the physic each time a change is made, should only calculate when the whole physic world is requested
accumulate an event queue with timestamps

should probably use a buffer system on a switch(ex: enable for game/disable for simulation or other stuff) to avoid calculating the physics 4000 times per seconds

collisions could be calculated as seen at http://www.gamasutra.com/view/feature/131790/simple_intersection_tests_for_games.php?print=1

properties, like acceleration and speed could be computed as a composition of function determined on an interval
ex: [0,1[ -> 5, [1,2[ -> x^2+5, etc....

 */
