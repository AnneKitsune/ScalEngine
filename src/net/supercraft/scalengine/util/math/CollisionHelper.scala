package net.supercraft.scalengine.util.math

import com.github.jpbetz.subspace.{Quaternion, Vector3}

import scala.collection.mutable.ListBuffer

/**
  * Created by jojolepro on 12/8/16.
  */
//tmp
case class AABBBox(min:Vector3,max:Vector3)
case class InfinitePlane(normal:Vector3,distance:Float)
case class TestModel(mesh:Array[Float]){
		lazy val aabb = CollisionHelper.AABBFromVertices(mesh)
}
object CollisionHelper {
		def AABBFromVertices(vert:Array[Float])={
				//list duplication :(
				val g = vert.grouped(3).toList
				def extractaxis(grouped:List[Array[Float]],index:Int)={
						val gr = ListBuffer[Float]()
						grouped.foreach{
								l=>gr +=l(index)
						}
						gr
						//grouped.foldLeft(List[Float]())((c,v)=>c :+ v(index))
				}
				//list duplication :(
				val x = extractaxis(g,0)
				//list duplication :(
				val y = extractaxis(g,1)
				//list duplication :(
				val z = extractaxis(g,2)
				val xmin = x.min
				val xmax = x.max
				val ymin = y.min
				val ymax = y.max
				val zmin = z.min
				val zmax = z.max
				AABBBox(Vector3(xmin,ymin,zmin),Vector3(xmax,ymax,zmax))
		}

		//distance=eye-center-(distance(center,size))
		def AABBSphericalDistance(from:Vector3,aabb: AABBBox)={
				val hsize = (aabb.max - aabb.min) / 2
				val position = (aabb.max + aabb.min) / 2

				//Currently makes it like if the aabb was a sphere
				Math.abs(from.distanceTo(position)-hsize.magnitude)
		}
}
