package net.supercraft.scalengine

import com.github.jpbetz.subspace.{Matrix4x4, Quaternion, Vector2, Vector3}
import net.supercraft.scalengine.assetloading.Texture
import net.supercraft.scalengine.core.state.Component
import net.supercraft.scalengine.util.math.CollisionHelper
import net.supercraft.scalengine.view.opengl.Shader

import scala.reflect.ClassTag

/**
  * Created by jojolepro on 1/24/17.
  */
trait OpenGLBuffered{
		val bufferId:Int
}
case class WorldObject(id:Int,graphicalObject:GraphicalObject,physicalObject:PhysicalObject)
case class GraphicalObject(id:Int,transform:Transform,model:Model,animations:List[Animation])
case class Model(mesh:Mesh,texture:Texture,shader:Shader)
class Animation{

}
//case class KeyFrame(id:Int,atTime:Float)
case class KeyFramedAnimation(name:String,length:Float,keyFrames:Map[Float,Model]) extends Animation
//case class Mesh(vertices:Vector[Float],uv:Vector[Float],normals:Vector[Float])
case class PhysicalObject(id:Int,transform:Transform,mesh:Mesh){
		//TODO change aabb from list to vector
		lazy val aabb = CollisionHelper.AABBFromVertices(mesh.vertices.toArray)
}

//No ref to attached gameobject because we want to reuse the component as much as possible
//Not sure about the related complexity of adding this enabled:Boolean=true
//class Component()
//Having no parenting allows us to do optimisation + no deep copy when modifying a component
case class Transform(position:Vector3=Vector3(0,0,0),rotation:Quaternion=Quaternion.identity,scale:Vector3=Vector3(1,1,1)) extends Component{
		lazy val matrix = Matrix4x4.forTranslationRotationScale(position,rotation,scale)
}
//overrides attached transform using referenced transform
//can lock or limit current transform if the is none referenced
case class TransformRefConstraint(ref:Transform,positionOffset:Vector3=Vector3(0,0,0),rotationOffset:Quaternion=Quaternion.identity,scaleOffset:Vector3=Vector3(1,1,1)) extends Component
case class TransformConstraint()
case class FPSCamera(angles:Vector2=Vector2(0,0),fov:Float=45f,znear:Float=0.1f,zfar:Float=1000f) extends Component{
		lazy val direction=Vector3(
				Math.cos(angles.y).toFloat * Math.sin(angles.x).toFloat,
				Math.sin(angles.y).toFloat,
				Math.cos(angles.y).toFloat * Math.cos(angles.x).toFloat
		)
		lazy val right=Vector3(
				Math.sin(angles.x - Math.PI/2f).toFloat,
				0f,
				Math.cos(angles.x - Math.PI/2f).toFloat
		)
		lazy val up=right.crossProduct(direction)
}
//Should sensitivity be here?

case class FlyCameraController(speed:Float,sensitivityX:Float,sensitivityY:Float) extends Component


trait LightAmbient extends Light{
		val ambientCoefficient:Float
}
trait LightAttenuation extends Light{
		val attenuation:Float
}
trait LightSpot extends Light{
		val coneAngle:Float
}
trait LightDirectional extends Light
case class Light(colors:Vector3) extends Component
//case class LightAmbiant(colors:Vector3,ambientCoefficient:Float=0) extends LightComponent(colors)
//case class LightDirectional(colors:Vector3,attenuation:Float=0) extends LightComponent(colors)
//case class LightSpot(colors:Vector3,attenuation:Float=0,coneAngle:Float) extends LightComponent(colors)
