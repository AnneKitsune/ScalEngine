package net.supercraft.scalengine.util.math

import com.github.jpbetz.subspace.{Vector3, Vector4}

/**
  * Created by jojolepro on 12/3/16.
  */
object VectorHelper {
		def vector4ToArray(vec:Vector4)={
				Array[Float](vec.x,vec.y,vec.z,vec.w)
		}
		def vector3ToArray(vec:Vector3)={
				Array[Float](vec.x,vec.y,vec.z)
		}
		def replaceAll[A](vector:Vector[A],condition:(A)=>Boolean,replaceBy:(A)=>A):Vector[A]={
				vector.map{e=>if(condition(e))replaceBy(e) else e}

				/*val allgo = vector.partition(g=>condition(g))
				val not = allgo._2
				val ok = allgo._1.map(g=>replaceBy(g))
				not ++ ok*/
		}

		/*def containsAll[A](container:List[A],contains:List[A],f:(A,A)=>Boolean = (a1,a2)=>a1.equals(a2)):Boolean={
				container.foldLeft(false)((c,e)=>c && container.contains(e))
		}*/
}
