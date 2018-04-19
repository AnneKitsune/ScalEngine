package net.supercraft.scalengine.concurrency

//import net.supercraft.scalengine.concurrency.AccessMode.AccessMode
import net.supercraft.scalengine.concurrency.ConcurrencyMode.ConcurrencyMode
import net.supercraft.scalengine.concurrency.DataExactitudePolicy.DataExactitudePolicy
import net.supercraft.scalengine.core.state.Component

/*object AccessMode extends Enumeration{
		type AccessMode = AccessMode.Value
		val SR2SW,GR2SW,GR2GW= Value
}*/
object ConcurrencyMode extends Enumeration{
		type ConcurrencyMode = ConcurrencyMode.Value
		val linear,random = Value
}
object DataExactitudePolicy extends Enumeration{
		type DataExactitudePolicy = DataExactitudePolicy.Value
		val linear,speed = Value
}

/**
  *
  * @param read
  * @param write
  * @param priority  Highest number = executes first
  */
case class LSystem(events:List[Class[_]],read:List[Class[_]], write:List[Class[_]], priority:Int,runOnEventOnly:Boolean=false,dataExactitude:DataExactitudePolicy=DataExactitudePolicy.linear)

sealed trait AccessModePolicy extends LSystem
trait SR2SW extends AccessModePolicy{
		def exec(id:Int,read:List[Component]):List[Component]
}
trait GR2SW extends AccessModePolicy{
		def exec(id:Int,read:List[(Int,List[Component])]):List[Component]
}
trait GR2GW extends AccessModePolicy{
		def exec(read:List[(Int,List[Component])]):List[(Int,List[Component])]
}