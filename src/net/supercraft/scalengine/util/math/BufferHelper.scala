package net.supercraft.scalengine.util.math

import java.nio.FloatBuffer

import com.github.jpbetz.subspace.Bufferable

import scala.collection.parallel.immutable

/**
  * Created by jojolepro on 12/8/16.
  */
/**
  *
  * @param data
  * @param _buf usually nothing, its the buffer cache that is automatically created/updated whenever a new instance of BufferableData is created
  * @tparam A
  */
case class BufferableData[A <: Bufferable](data:A,private var _buf:FloatBuffer = null){
		if(_buf==null){
				_buf = data.allocateBuffer
		}else{
				data.updateBuffer(_buf)
		}

		def buffer = _buf
		def copy(data:A = data)=new BufferableData(data,_buf)
}

object BufferHelper {
		type CircularBuffer[A] = Vector[A]
		def emptyCircularBuffer[A]:CircularBuffer[A] = Vector.empty[A]
		def addToCircularBuffer[A](maxSize:Int)(buffer:CircularBuffer[A],element:A)=buffer.drop(buffer.size - maxSize + 1) :+ element
}
