package net.supercraft.scalengine.util.math

import com.github.jpbetz.subspace.{Matrix4x4, Vector3}

/**
  * Created by jojolepro on 9/29/16.
  */
object MatrixHelper {
		def lookAt(from:Vector3,to:Vector3,upAxis:Vector3):Matrix4x4={
				val front=(from-to).normalize
				//val xaxis=(upAxis.crossProduct(front)).normalize
				val side=upAxis.crossProduct(front).normalize
				val up=front.crossProduct(side)

				Matrix4x4(
						side.x,                        up.x,                        front.x,0,
						side.y,                        up.y,                        front.y,0,
						side.z,                        up.z,                        front.z,0,
						-side.dotProduct(from),-up.dotProduct(from),-front.dotProduct(from),1
				)
		}
		def matrix4ToArray(mat: Matrix4x4)={
				Array[Float](
						mat.c0r0,mat.c0r1,mat.c0r2,mat.c0r3,
						mat.c1r0,mat.c1r1,mat.c1r2,mat.c1r3,
						mat.c2r0,mat.c2r1,mat.c2r2,mat.c2r3,
						mat.c3r0,mat.c3r1,mat.c3r2,mat.c3r3
				)
		}
}
