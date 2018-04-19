package net.supercraft.scalengine.view

import com.github.jpbetz.subspace.{Vector2, Vector3}
import net.supercraft.scalengine.util.math.{BufferableData, MatrixHelper}

/**
  * Created by jojolepro on 9/30/16.
  */
trait FlyCamera{
		val speed:Float
		val mouseSpeed:Float
}
abstract class Camera(position:BufferableData[Vector3],angles:Vector2,fov:Float,znear:Float,zfar:Float){
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
		lazy val lookAtMatrix=MatrixHelper.lookAt(position.data,position.data+direction.normalize,up)
}
case class CameraStatic(position:BufferableData[Vector3],angles:Vector2,fov:Float,znear:Float,zfar:Float) extends Camera(position,angles,fov,znear,zfar){

}
//Mouse speed should not be here
case class CameraControllable(position:BufferableData[Vector3],angles:Vector2,fov:Float,speed:Float,mouseSpeed:Float,znear:Float,zfar:Float) extends Camera(position,angles,fov,znear,zfar){
		//should not be here
		def mouseDistanceFromCenter(screenSize:Vector2,mousePosition:Vector2)=
				new Vector2(mousePosition.x-screenSize.x/2,mousePosition.y-screenSize.y/2)
		def rotate(screenSize:Vector2,mousePosition:Vector2,deltaTime:Float)={
				val deltaPos=mouseDistanceFromCenter(screenSize,mousePosition)
				val newHAngle=angles.x+(mouseSpeed * deltaTime * deltaPos.x)
				val newVAngle=angles.y+(mouseSpeed * deltaTime * deltaPos.y)
				copy(angles=new Vector2(newHAngle,newVAngle))
		}
		def rotateC(screenSize:Vector2,mousePosition:Vector2,deltaTime:Float)={
				val deltaPos=mouseDistanceFromCenter(screenSize,mousePosition)
				val newHAngle=angles.x+(mouseSpeed * deltaTime * -deltaPos.x)
				val newVAngle=limitAngle(angles.y+(mouseSpeed * deltaTime * -deltaPos.y))
				new Vector2(newHAngle,newVAngle)
		}
		private def limitAngle(v:Float)= if(v>Math.PI/2)Math.PI.toFloat/2 else if(v< -Math.PI/2)-Math.PI.toFloat/2 else v
		def translateAbsolute(absTrans:Vector3)={
				//Not how it is supposed to be obviously
				copy(position=position.copy(position.data+absTrans))
		}
}