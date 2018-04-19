package net.supercraft.scalengine.physics

import akka.actor.Actor

/**
  * Created by jojolepro on 6/26/16.
  */
//Just a testing unit to experiment...
class Physics2() extends Actor{
		override def receive: Receive = {
				case _=>
		}


}

case class FragmentedPhysicFunction(funcs:List[RangedPhysicFunction],override val name:String,override val initialValue:Double=0,override val lastEvaluation:Double=0) extends PhysicFunction(name,initialValue = initialValue,lastEvaluation = lastEvaluation){
		override def evaluate(x: Double): Double = {
				val f=funcs.reverse.filter(f=>x>=f.from && x<=f.to).headOption
				f match{
						case Some(e)=>e.func.evaluate(x)
						case None=>initialValue
				}
		}
		override def getVariation(x: Double): Double = {
				val f=funcs.reverse.filter(f=>x>=f.from && x<f.to).headOption
				f match{
						case Some(e)=>e.func.getVariation(x)
								//Is it a good idea?
						case None=>initialValue
				}
		}
		override def getArea(from: Double, to: Double): Double = {
				var lastFunctionStart=Double.MaxValue
				def getAreaOfFunc(curFunc:RangedPhysicFunction):Double={
						val start = Math.max(from,curFunc.from)
						val end = Math.min(Math.min(to,curFunc.to),lastFunctionStart)
						lastFunctionStart = curFunc.from
						curFunc.func.getArea(start,end)
				}
				funcs.reverse.filter(f=>f.from>=from || f.to<=to).foldLeft(0.0)((c,f)=>c+getAreaOfFunc(f))
		}
}
case class RangedPhysicFunction(from:Double,to:Double,func:PhysicFunction)
abstract class PhysicFunction(val name:String,val initialValue:Double=0,val lastEvaluation:Double=0){
		//Evaluation
		def evaluate(x:Double):Double
		//Derivation
		def getVariation(x:Double):Double
		//Integration
		def getArea(from:Double,to:Double):Double
}
//@TODO
/*
  * We have to manually tell each function how to behave, some of them are just 5*otherFunction.evaluate
  * others are x*x*otherFunction.getArea
  *
  * I need to find a way to make this clearer...
  */

case class Car(funcs:List[FragmentedPhysicFunction]){
		/*val acceleration = new PhysicFunction("acceleration"){
				override def evaluate(x: Double): Double = 5
				override def getVariation(x: Double): Double = 0
				override def getArea(from: Double, to: Double): Double = (to-from)*evaluate(from)
		}
		val speed = new PhysicFunction("speed"){

				/*-----------The evaluate part feels weird cause 0 should be a variable, but should no go outside of scope----------*/
				override def evaluate(x: Double): Double = acceleration.getArea(0,x)
				override def getVariation(x: Double): Double = MathUtils.dumbDifferentiation(x,evaluate)
				override def getArea(from: Double, to: Double): Double = MathUtils.simpsonIntegration(32,from,to,evaluate)
		}
		val position = new PhysicFunction("position"){
				override def evaluate(x: Double): Double = speed.getArea(0,x)
				override def getVariation(x: Double): Double = MathUtils.dumbDifferentiation(x,evaluate)
				override def getArea(from: Double, to: Double): Double = MathUtils.simpsonIntegration(32,from,to,evaluate)
		}*/
}