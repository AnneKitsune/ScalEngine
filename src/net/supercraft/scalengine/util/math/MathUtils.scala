package net.supercraft.scalengine.util.math

/**
  * Created by jojolepro on 10/27/16.
  */
case class PartFunction(from:Double,to:Double,func:(Double)=>Double)
case class FullFunction(funcs:List[PartFunction])
object MathUtils {
		//Change distance for type:Numeric = Float
		type Distance = Float


		//initialized like  def pos=integration(lastCheck,now,speedFunction)
		//evaluated like   class.pos
		//pos could use either one of the integrationMethods or it could directly use a definite integration function(so its more precise than my estimation)
		//Use http://www.integral-calculator.com/  to try to find an integration formula for your formula

		def stepIntegration(steps:Int,from:Double,to:Double,integrationFunc:(Double,Double,Double=>Double)=>Double,func:Double=>Double):Double={
				val stepSize=(to-from)/steps
				stepSize*(from+stepSize until to by stepSize).foldLeft(0.0)((c,v)=>c+integrationFunc(v,v+stepSize,func))
		}
		def midRectangleIntegration(steps:Int,from:Double,to:Double,func:Double=>Double):Double={
				def integrationFunc(from:Double,to:Double,func:Double=>Double):Double=func((from+to)/2)
				stepIntegration(steps,from,to,integrationFunc,func)
		}
		def trapezoidalIntegration(steps:Int,from:Double,to:Double,func:Double=>Double):Double={
				def integrationFunc(from:Double,to:Double,func:Double=>Double):Double=((func(from)+func(to))/2)
				stepIntegration(steps,from,to,integrationFunc,func)
		}

		/**
		  * Currently has the best precision to determine the area under a function.
		  *
		  * Please keep in mind that using a precomputed integration function will always be more efficient and more precise than using an estimation method(but not always possible).
		  * Exemple: You could go use a website like http://www.integral-calculator.com/# to calculate the integration formula of your function, or you could calculate it yourself.
		  *
		  * @param steps
		  * @param from
		  * @param to
		  * @param func
		  * @return The area under the function
		  */
		def simpsonIntegration(steps:Int,from:Double,to:Double,func:Double=>Double):Double={
				def integrationFunc(from:Double,to:Double,func:Double=>Double):Double=(func(from)+4*func((from+to)/2)+func(to))/6;
				stepIntegration(steps,from,to,integrationFunc,func)
		}
		//def genSimpsonIntegration(steps:Int,)


		//Not sure if its working...
		def dumbDifferentiation(x:Double,func:Double=>Double):Double={
				val h = 0.000000001
				(func(x)+func(x+h))/h
		}
}
