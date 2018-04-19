package net.supercraft.scalengine

import com.github.jpbetz.subspace.{Vector2, Vector3}
import net.supercraft.scalengine.Direction.Direction
import net.supercraft.scalengine.core.manager.ManagerChildSync.DoneProcessing
import net.supercraft.scalengine.core.manager.{Manager, ManagerMessageLog, StartSubSystemExecution}
import net.supercraft.scalengine.core.state.{Component, Event, GameFrame, GameObject}
import net.supercraft.scalengine.util.math.VectorHelper
import net.supercraft.scalengine.view._
import net.supercraft.scalengine.view.glfw.EMove

/**
  * Created by jojolepro on 2/14/17.
  */
class LInputHandler extends Manager with ManagerMessageLog{
		override def getMessage: Receive = {
				case StartSubSystemExecution=>context.parent ! GetGameFrameState
				case gf:GameFrame=>runIOEvents(gf.events);context.parent ! DoneProcessing
		}

		def runIOEvents(events:Vector[Any])={
				events.foreach(e=>e match{
						case RotateCameraRelative(t,d)=>rotateCamera(d)
						//case e:WPressed=>moveCamera(e)
						//case e:APressed=>moveCamera(e)
						//case e:DPressed=>moveCamera(e)
						//case e:SPressed=>moveCamera(e)
						case e:EMove=>moveCamera(e.deltaTime,e.direction)
						case _=>
				})
		}

		def moveCamera(deltaTime:Double,direction: Direction.Value):Unit={
				def action(frame:GameFrame,target:Double):Vector[GameObject]={
						//val dt = target-frame.time
						VectorHelper.replaceAll[GameObject](frame.states,_.containsComponent[FPSCamera],g=>{
								val settings = g.findComponent[FlyCameraController].get
								val cam = g.findComponent[FPSCamera].get
								val speed = settings.speed
								g.copy(components = VectorHelper.replaceAll[Component](g.components,_.isInstanceOf[Transform],c=>{
										val tr = c.asInstanceOf[Transform]
										direction match {
												case Direction.front=>tr.copy(moveCamera(tr.position,cam.direction,deltaTime.toFloat*settings.speed))
												case Direction.back=>tr.copy(position = tr.position - cam.direction*deltaTime.toFloat*settings.speed)
												case Direction.left=>tr.copy(position = tr.position - cam.right*deltaTime.toFloat*settings.speed)
												case Direction.right=>tr.copy(position = tr.position + cam.right*deltaTime.toFloat*settings.speed)
										}
								}))
						})
				}
				context.parent ! Operate(action)
		}

		def moveCamera(pos:Vector3,direction: Vector3,distance:Float):Vector3={
				pos + direction * distance
		}

		def rotateCamera(dist:Vector2)={
				def action(frame:GameFrame,target:Double):Vector[GameObject]={
						val dt = target-frame.time
						VectorHelper.replaceAll[GameObject](frame.states,_.containsComponent[FPSCamera],g=>{
								val settings = g.findComponent[FlyCameraController].get
								g.copy(components = VectorHelper.replaceAll[Component](g.components,_.isInstanceOf[FPSCamera],c=>{
										val cam = c.asInstanceOf[FPSCamera]
										println("dt: "+dt)
										cam.copy(rotateC(dist,cam.angles,Vector2(settings.sensitivityX,settings.sensitivityY),dt.toFloat))
								}))
						})
				}
				context.parent ! Operate(action)
		}
		def rotateC(dist:Vector2,angles:Vector2,mouseSpeed:Vector2,deltaTime:Float)={
				val deltaPos=dist
				//delta time too unstable to do any kind of stable movement :(
				val newHAngle=angles.x+(mouseSpeed.x * 0.01f * -deltaPos.x)
				val newVAngle=limitAngle(angles.y+(mouseSpeed.y * 0.01f * -deltaPos.y))
				new Vector2(newHAngle,newVAngle)
		}
		def limitAngle(v:Float)=if(v>Math.PI/2)Math.PI.toFloat/2 else if(v< -Math.PI/2)-Math.PI.toFloat/2 else v
}
