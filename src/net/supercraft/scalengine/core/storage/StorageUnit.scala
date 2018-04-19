package net.supercraft.scalengine.core.storage

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

/**
  * Created by jojolepro on 4/3/17.
  */
trait StorageUnit[A]{
		def insert(a:A)
		def update(original:A,a:A):Boolean
		def remove(a:A):Boolean
		def getAll():List[A] //using list because of sequential access
}
/*final class FixedMemoryStorageUnit[A : ClassTag](maximumSize:Int=65536) extends StorageUnit[A]{
		private val arr = Array.ofDim[A](maximumSize)
		private var elemCount = 0
		def getSize=elemCount
		override def insert(a: A): Unit = {
				if(elemCount < maximumSize){
						arr.update(elemCount,a)
						elemCount += 1
						true
				}else{
						throw new ArrayIndexOutOfBoundsException("FixedMemoryStorageUnit: Storage is full")
						false
				}
		}

		def update(idx: Int, a: A): Boolean = {
				arr.update(idx,a)
				true
		}

		override def remove(a: A): Boolean = {
				val idx = arr.indexOf(a)
				idx match{
						case -1 => false
						case _ => arr.update(idx,null);true
				}
		}

		override def getAll(): List[A]= arr.take(elemCount).toList
}*/
final class DynamicMemoryStorageUnit[A]() extends StorageUnit[A]{
		private val arr = ArrayBuffer[A]()
		def getSize=arr.size
		override def insert(a: A) = arr.append(a)

		override def update(original: A, a: A): Boolean = {
				val idx = arr.indexOf(original)
				idx match{
						case -1 => false
						case _ => arr.update(idx,a);true
				}
		}

		override def remove(a: A) = {
				val idx = arr.indexOf(a)
				idx match{
						case -1 => false
						case _ => arr.remove(idx);true
				}
		}

		override def getAll(): List[A] = arr.toList
}