package net.supercraft.scalengine.view

import com.github.jpbetz.subspace.{Vector3, Vector4}

/**
  * Created by jojolepro on 12/1/16.
  */
/*object LightMode extends Enumeration{
		val directional,spot,point = Value
}*/
case class GlLight(position:Vector3,directional:Boolean,colors:Vector3,ambientCoefficient:Float,attenuation:Float,coneAngle:Float,coneDirection:Vector3)