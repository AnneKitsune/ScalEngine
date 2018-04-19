package net.supercraft.scalengine.physics

import com.github.jpbetz.subspace.{Matrix4x4, Quaternion, Vector3}
import javax.vecmath.{Matrix4f, Quat4f, Vector3f}

/**
  * Created by jojolepro on 1/10/17.
  */
/*
Type system are both a blessing and a curse... pls whelp
 */
object TypeConvert {
		object javax{
				def vec3(vector:Vector3)=new Vector3f(vector.x,vector.y,vector.z)
				def quat(quat:Quaternion)=new Quat4f(quat.x,quat.y,quat.z,quat.w)
				def mat4(m:Matrix4x4)=new Matrix4f(m.c0r0,m.c1r0,m.c2r0,m.c3r0,m.c0r1,m.c1r1,m.c2r1,m.c3r1,m.c0r2,m.c1r2,m.c2r2,m.c3r2,m.c0r3,m.c1r3,m.c2r3,m.c3r3)
		}
		object jpbetz{
				def vec3(vector:Vector3f)=new Vector3(vector.x,vector.y,vector.z)
		}
}
