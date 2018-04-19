package net.supercraft.scalengine.view

import java.awt.Color
import java.io.{File, FileInputStream, PrintStream}
import java.nio.{ByteBuffer, ByteOrder, FloatBuffer}

import akka.actor.{Actor, Props}
import com.github.jpbetz.subspace._
import net.supercraft.scalengine.assetloading.{ImageLoader, Model, ModelLoader, Texture}
import net.supercraft.scalengine.event.GameEvent
import net.supercraft.scalengine.util.math._
import net.supercraft.scalengine.view.glfw._
import net.supercraft.scalengine.view.opengl._
import net.supercraft.scalengine._
import net.supercraft.scalengine.core.manager.Manager.SubSystemBroadcast
import net.supercraft.scalengine.core.manager.ManagerChildSync.DoneProcessing
import net.supercraft.scalengine.core.manager.{Manager, ManagerMessageLog, StartSubSystemExecution}
import net.supercraft.scalengine.core.state.{Event, GameFrame, GameObject}
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import net.supercraft.scalengine.util.math.TimeUtils._
import net.supercraft.scalengine.view.OpenGLDisplay.DrawableObject
import net.supercraft.scalengine.view.console.{Log, LogLevel}
import org.lwjgl.BufferUtils

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.io.Source
import scala.util.Try
/**
  * Created by jojolepro on 8/5/16.
  */

//We need to separate opengl from the window and the controls (mainly to have multiple control system ex: mouse,gamepad,vr,...)


case class CreateDisplay(defaultWidth:Float,defaultHeight:Float) extends GameEvent
case class DestroyDisplay(override val time:Double) extends Event(time)

case class VSync(enabled:Boolean)
case class WindowChangedSize(size:Vector2)
case class SetFullScreen(override val time:Double,enable:Boolean) extends Event(time)
case class ToggleDisplayMode(override val time:Double) extends Event(time)
case class RotateCameraRelative(override val time:Double,dist:Vector2) extends Event(time)
case class DoDisplay(gameFrame: GameFrame)


object OpenGLDisplay{
		case class DrawableObject(modelMatrix:BufferableData[Matrix4x4],handle:GlHandleObject)
}

class OpenGLDisplay extends Manager with ManagerMessageLog{
		var window:Long=0
		val keyPollingActor=context.actorOf(Props[KeyPollingActor],name="key-polling-actor")
		val windowSizeCallback = new WindowSizeCallback(self)
		//var camera=new CameraControllable(BufferableData(new Vector3(0,0,4f)),new Vector2(Math.PI.toFloat,0),Math.toRadians(60).toFloat,3f,0.010f,0.1f,100f)
		var shaderProgram:Option[ShaderProgramData]=None
		var models = Map[String,Model]()
		var windowSize = Vector2(0,0)
		var stableDeltaAccum = BufferHelper.emptyCircularBuffer[Double]

		//var gameObjects = Vector[GameObject]() //Data race condition occurs while using this kind of optimisation

		/*complete rewrite of ChildManager, Initiation,LifeCycle, EndCheck,FrameSubSystemModifier
		spider pattern
		todo queue overflow detection(send msg to self, calculate receive delay)*/

		override def getMessage: Receive={
				//case CreateDisplay(w,h)=>createDisplay(w,h)
				//case e:Any=>println(e)
				case e:DestroyDisplay=>destroyDisplay()
				case RegisterKeyListener(keyboardMouseListener)=>glfwSetKeyCallback(window, keyboardMouseListener)
				case WindowChangedSize(size)=>windowSize=size
				case StartSubSystemExecution=>context.parent ! GetIOGameState
				case f:IOReadState=>updateDisplay(f)
		}
		override def init={
				createDisplay(Vector2(800,600))
				initGL()
				super.init
		}
		override def postInit={
				context.parent ! SubSystemBroadcast(KeyPollingStart)
				super.postInit
		}
		def createDisplay(size:Vector2): Unit ={

				window = GLFWHelper.createWindow("ScalEngine",size,cursorInvisible=false,glfwErrorCallbackTarget=System.err)
				self ! WindowChangedSize(size)

				glfwSetWindowSizeCallback(window,windowSizeCallback)
				keyPollingActor ! KeyPollingStart
		}

		var lastTime=0.0
		def fpsCounter(time:Double)={
				val curTime=time
				val deltaTime = elapsedTime(lastTime,curTime)
				lastTime=curTime
				stableDeltaAccum = BufferHelper.addToCircularBuffer(1)(stableDeltaAccum,deltaTime)
				deltaTime
		}
		def updateDisplay(state:IOReadState): Unit ={
				val deltaTime = fpsCounter(state.targetTime)
				if(glfwGetKey(window,GLFW_KEY_1) == GLFW_PRESS){
						glfwSetWindowShouldClose(window,true)
						println("Exit key pressed")
				}
				if(glfwWindowShouldClose(window))self ! DestroyDisplay(state.targetTime)

				updateMatrix(state.gameFrame.states)

				draw(state.gameFrame.states)
				glfwSwapBuffers(window)

				glfwPollEvents()



				//context.parent ! SubSystemBroadcast(GetHoldKeysEvents(deltaTime))

				rotateCamera
				/*

@todo
				temporary, until keys are back

				 */
				context.parent ! DoneProcessing

		}
		def rotateCamera()={
				def mouseDistanceFromCenter(screenSize:Vector2,mousePosition:Vector2)= new Vector2(mousePosition.x-screenSize.x/2,mousePosition.y-screenSize.y/2)
				val dist = mouseDistanceFromCenter(windowSize,GLFWHelper.getCursorPos(window))
				if(dist.magnitude != 0){
						context.parent ! AddEvent(RotateCameraRelative(lastTime,dist))
						glfwSetCursorPos(window,windowSize.x/2,windowSize.y/2)
				}
		}

		def initGL(): Unit = {
				GL.createCapabilities()
				glClearColor(1.0f, 0.0f, 1.0f, 0.0f)
				/////////////////////////////////////////////////////////////////Faire marcher les exceptions
				val shaderProgramData = ShaderLoader.createShaderProgram("assets/Shader/VertShaderUVLighting.shader", "assets/Shader/FragShaderUVLighting.shader")
				shaderProgramData match {
						case Left(e) => println(e);destroyDisplay()
						case Right(e) => shaderProgram = Some(e)
				}
				try ShaderLoader.bind(shaderProgram.get) catch{ case e:ShaderBindException=>e.printStackTrace()}

				glEnable(GL_DEPTH_TEST)
				glEnable (GL_BLEND)
				glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		}
		def createTriangle(): Unit ={

				/*






				use mesh from component
				keep local ref to old mesh
				compare references to see if opengl needs to be updated with the new mesh


				or we can use  hash map for fast comparison





				 */

				//updateMatrix
		}
		def genDrawable2DChar(glHandle:GlHandleObject,position:Vector2 = Vector2(0,0),rotation:Quaternion = Quaternion.identity,scale:Vector2 = Vector2(1,1))={
				DrawableObject(BufferableData(Matrix4x4.forTranslationRotationScale(Vector3(position.x*2-1,-position.y*2+1,0),rotation,Vector3(scale.x,scale.y,1))),glHandle)
		}
		def genDrawable2DString(str:String,glHandles:Map[Char,GlHandleObject],position:Vector2 = Vector2(0,0),rotation:Quaternion = Quaternion.identity,scale:Vector2 = Vector2(1,1))={
				str.toCharArray.zipWithIndex.foldLeft(List[DrawableObject]())(
						(c,ci)=>c :+ genDrawable2DChar(glHandles.get(ci._1).get,position=position+Vector2(scale.x*ci._2,0),scale=scale)
				)
		}

		var projectionMatrix:Matrix4x4=null
		var viewMatrix:Matrix4x4=null
		var modelMatrix = BufferableData(Matrix4x4.identity)
		var vpMatrix = BufferableData(Matrix4x4.identity)
		def updateMatrix(gameObjects:Vector[GameObject])={
				val screenSize=GLFWHelper.getWindowSize(window)

				val camObject =gameObjects.find(o=>o.containsComponent[FPSCamera])
				if(camObject.isEmpty){
						projectionMatrix = Matrix4x4.identity
						viewMatrix = Matrix4x4.identity
				}else{
						val fpsCamera = camObject.get.findComponent[FPSCamera].get
						val trans = camObject.get.findComponent[Transform].get
						projectionMatrix=Matrix4x4.forPerspective(fpsCamera.fov,screenSize.x/screenSize.y,fpsCamera.znear,fpsCamera.zfar) //fov/screen size change
						viewMatrix=MatrixHelper.lookAt(trans.position,trans.position + fpsCamera.direction.normalize,fpsCamera.up)
				}


				//Position
				//modelMatrix=Matrix4x4.identity//1,1,1  //1xmodel
				//modelMatrix=Matrix4x4(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
				vpMatrix = vpMatrix.copy(projectionMatrix * viewMatrix)

				//use vp matrix and at each object mult by model?
		}
		var curMatSpecColor = BufferableData[Vector3](new Vector3(.5f,.5f,.5f))

		//GameObjectId->
		var bufferedMeshes = Map[String,GlHandleObject]()
		def draw(gameObjects:Vector[GameObject]): Unit ={
				implicit val shaderProgramId = shaderProgram.get.programId

				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
				glEnable(GL_DEPTH_TEST)
				GlHelper.uploadUniformMatrix[FloatBuffer]("vpMatrix",glUniformMatrix4fv,vpMatrix.buffer)

				val camObject = gameObjects.find(g=>g.containsComponent[FPSCamera]())
				camObject match{
						case Some(cam)=>val transform = cam.findComponents[Transform]()
								GlHelper.uploadUniform[FloatBuffer]("cameraPos",glUniform3fv,transform(0).position.allocateBuffer)
								//println("CamPos: "+transform(0).position)
						case None=>println("No camera found in gameObjects from DisplayTest")
				}


				GlHelper.uploadUniform("materialShininess",glUniform1f,80f)
				GlHelper.uploadUniform[FloatBuffer]("materialSpecularColor",glUniform3fv,curMatSpecColor.buffer)





				val lights = gameObjects.filter(g=>g.containsComponent[Light]())
				val lightCount = lights.size
				GlHelper.uploadUniform("numLights",glUniform1i,lightCount)
				for(i<-0 until lightCount){
						val light = lights(i)
						val transform = light.findComponent[Transform]
						if(transform.isEmpty){
								//B2L.broadcast(Log("No transform component in light "+light,LogLevel.error))
								println("No transform component in light")
						}else{
								val tr = transform.get
								val lightComponent = light.findComponent[Light].get
								val isDirectional = lightComponent.isInstanceOf[LightDirectional]
								val isAmbient = lightComponent.isInstanceOf[LightAmbient]
								val isAttenuated = lightComponent.isInstanceOf[LightAttenuation]
								val isCone = lightComponent.isInstanceOf[LightSpot]
								val l = GlLight(tr.position,
										isDirectional,
										lightComponent.colors,
										if(isAmbient)lightComponent.asInstanceOf[LightAmbient].ambientCoefficient else 0f,
										if(isAttenuated)lightComponent.asInstanceOf[LightAttenuation].attenuation else 0f,
										if(isCone)lightComponent.asInstanceOf[LightSpot].coneAngle else 360f,
										tr.rotation * Vector3(1,1,1)
								)
								GlHelper.uploadUniformLight(shaderProgramId,s"lights[$i]",l)
						}
				}

				val meshes = gameObjects.filter(_.containsComponent[Mesh]())
				meshes foreach{
						m=>if(!bufferedMeshes.contains(m.id)){
									bufferedMeshes = bufferedMeshes + (m.id->bufferMesh(m.findComponent[Mesh]().get))
								}
								drawDrawableObject(DrawableObject(BufferableData(m.findComponent[Transform].get.matrix),bufferedMeshes.get(m.id).get))
				}


				//obj.foreach(drawDrawableObject(_))


				//todo sorting
				//glDisable(GL_DEPTH_TEST)
				//GlHelper.uploadUniformMatrix[FloatBuffer]("vpMatrix",glUniformMatrix4fv,DisplayTest.orthoVPMatrix.buffer)
				//gui.foreach(drawDrawableObject(_))
		}
		def bufferMesh(mesh:Mesh):GlHandleObject={
				val tex = ImageLoader.loadPNG(new File("assets/Textures/testmodeluv.png"))
				GLHelper2.create(Model(mesh.vertices.toList,mesh.uv.toList,mesh.normals.toList,Left((tex,GLHelper2.bufferTextureMipMap))))
		}
		def drawDrawableObject(obj:DrawableObject)={
				implicit val shaderProgramId = shaderProgram.get.programId
				GlHelper.uploadUniformMatrix[FloatBuffer]("mMatrix",glUniformMatrix4fv,obj.modelMatrix.buffer)
				GLHelper2.draw(obj.handle)
		}
		/*def optimizedDraw(obj:DrawableObject,camera: CameraControllable = camera)={
				val modelMat = obj.modelMatrix.data
				val modelVec = new Vector3(modelMat.c3r1,modelMat.c3r2,modelMat.c3r3)
				if(modelVec.distanceTo(camera.position.data) < 2000)
					drawDrawableObject(obj)
		}*/

		/*
		  *
		  * Window stuff
		  *
		  */


		def createWindow=GLFWHelper.createWindow("Title",cursorInvisible = true)

		def destroyDisplay(): Unit ={
				//Exception testing required
				shaderProgram match{
						case Some(e)=>ShaderLoader.unbind;ShaderLoader.dispose(e)
						case None=>
				}
				//keyPollingActor ! KeyPollingStop
				GLFWHelper destroyDisplay window

				System.exit(1)

		}
		var curDisplayMode = DisplayMode.normal
		def toggleDisplayMode={
				curDisplayMode match{
						case DisplayMode.normal=>curDisplayMode = DisplayMode.wireframe;GlHelper.setDisplayMode(curDisplayMode)
						case DisplayMode.wireframe=>curDisplayMode = DisplayMode.point;GlHelper.setDisplayMode(curDisplayMode)
						case DisplayMode.point=>curDisplayMode = DisplayMode.normal;GlHelper.setDisplayMode(curDisplayMode)
				}

		}
		/*

		text drawing
		-pos
		-font
		-size
		-spacing x
		-spacing y
		-escape character support (\n)
		-fontweight
		-italic (could be using font instead)
		-rotation

		 */
		/*
		(vp matrix is managed by the actor by having 2 permanent matrices: A 3D perspective and a 2D orthographic , and possible 3d orthographic too)
		 DrawableObject
		 modelMatrix,GLHandleObject
		 */
}