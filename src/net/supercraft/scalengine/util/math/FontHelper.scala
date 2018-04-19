package net.supercraft.scalengine.util.math

import java.io.File
import java.nio.ByteBuffer

import net.supercraft.scalengine.assetloading.{ImageLoader, Texture}
import org.lwjgl.BufferUtils

/**
  * Created by jojolepro on 12/15/16.
  */
object FontHelper {
		def charUVFromBitmap(bitmapTex:Texture,char:Char,xGridCount:Int,yGridCount:Int):Array[Float]={
				//val xGridSize = bitmapTex.width/xGridCount
				//val yGridSize = bitmapTex.height/yGridCount
				//val xs = ascii % xGridCount * xGridSize / bitmapTex.width //old unoptimized function

				val ascii = char.toInt
				val relXGridSize = 1 / xGridCount.toFloat
				val relYGridSize = 1 / yGridCount.toFloat
				val xs = ascii % xGridCount / xGridCount.toFloat
				//yes, I'm in fact trying to int(ascii/yGridCount)
				val ys = ascii / yGridCount / yGridCount.toFloat
				val xe = xs+relXGridSize
				val ye = ys+relYGridSize

				Array[Float](
						xs,ye,
						xe,ye,
						xe,ys,
						xs,ye,
						xe,ys,
						xs,ys
				)
		}
}
