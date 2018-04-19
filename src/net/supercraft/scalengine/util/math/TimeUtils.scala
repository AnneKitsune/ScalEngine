package net.supercraft.scalengine.util.math

import math.Fractional.Implicits._
/**
  * Created by jojolepro on 11/9/16.
  */
object TimeUtils {
		type Time = Double
		def secondToNano(second:Double)=(second*1000000000.0).toLong
		def nanoToSecond(nano:Long)=nano/1000000000.0
		def elapsedTime(before:Long,now:Long)=now-before
		def elapsedTime[A : Fractional](before:A,now:A)=now - before
}
