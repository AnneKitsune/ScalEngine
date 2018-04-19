package net.supercraft.scalengine.core

import java.io.File

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import net.supercraft.scalengine._
import net.supercraft.scalengine.core.manager.{Init, _}
import net.supercraft.scalengine.core.state.{Component, Event, GameFrame, GameObject}
import net.supercraft.scalengine.util.math.TimeUtils
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.github.jpbetz.subspace.{Quaternion, Vector3}
import net.supercraft.scalengine.CManagerMessageLogging.TestLoggingCollect
import net.supercraft.scalengine.assetloading.{ImageLoader, Model, ModelLoader}
import net.supercraft.scalengine.core.manager.ManagerChildSync.AllChildsDone
import net.supercraft.scalengine.view.{DestroyDisplay, SetFullScreen, ToggleDisplayMode}
import net.supercraft.scalengine.view.glfw.{EMove, KeyBinds, KeyState}
import net.supercraft.scalengine.view.opengl.GLHelper2
import org.lwjgl.glfw.GLFW._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent._
import scala.concurrent.duration._
import scala.reflect.ClassTag

case object Start
case object Loop
case object Replay
case class GameSettings(fpsLock:Int=10000) extends Component
class CoreTest extends Manager with ManagerChildInit{
		var logicManager:ActorRef = null
		var ioManager:ActorRef = null
		var loggingManager:ActorRef = null



		/*var logicManagerInitDone = false
		var ioManagerInitDone = false
		override def init()={
				logicManager ! Init
				ioManager ! Init
		}*/

		var startTime = TimeUtils.nanoToSecond(System.nanoTime)
		//var gameFrame = GameFrame(startTime,Vector[GameObject](),Vector[Event]())

		var gameFrame = CoreTest.mockGO

		var targetTime = 0.0


		def calculateGameFrame(currentGameFrame:GameFrame,targetTime:Double):GameFrame={
				implicit val timeout = Timeout(10 seconds)
				val nextFrameFuture = logicManager ? CalculateFrame(currentGameFrame,targetTime)
				val newStates = Await.result(nextFrameFuture,timeout.duration).asInstanceOf[DoneCalculatingFrame].state
				gameFrame.copy(targetTime,newStates,Vector[Event]())
		}

		def doLogic={

				//remove fps lock from the game states...
				val fpsLock = CoreTest.findFirstComponentUnsafe[GameSettings](gameFrame.states).fpsLock
				do{
						targetTime = TimeUtils.nanoToSecond(System.nanoTime) - startTime
				}while(targetTime == gameFrame.time || targetTime-gameFrame.time < 1.0/fpsLock)
				//calculateGameFrame(gameFrame,targetTime)

				if(replaying){
						val evs = events.map(_._2).flatten
						gameFrame = gameFrame.copy(events = evs.filter(e=>e.time >= gameFrame.time && e.time < targetTime))
				}
				logicManager ! CalculateFrame(gameFrame,targetTime)
				//doIO
		}
		def doIO={
				targetTime = TimeUtils.nanoToSecond(System.nanoTime) - startTime
				ioManager ! ExecuteSideEffects(gameFrame,targetTime)
		}


		var replaying = false


		override def getMessage: Receive = {
				case DoneCalculatingFrame(f)=>{
						gameFrame = gameFrame.copy(time = targetTime,states=f,events = Vector[Event]())
						doIO
				}
				case IODone(f)=>{
						if(!replaying)gameFrame = gameFrame.copy(events = f)
						if(!replaying)events = events :+ (gameFrame.time,gameFrame.events)
						doLogic
				}
				case AllChildsDone=> if(initStep == InitDone)doIO//Should only happen when the init is done
				case TestLoggingCollect=>loggingManager ! TestLoggingCollect
				case Replay=>replay
		}
		/*def initAnswer(sender:ActorRef)={
				if(sender == logicManager){
						logicManagerInitDone = true
				}else if(sender == ioManager){
						ioManagerInitDone = true
				}
				if(logicManagerInitDone && ioManagerInitDone){
						doLogic
				}
		}*/


		var initialState = gameFrame.copy()
		var events = Vector[(Double,Vector[Event])]()
		def replay={
				gameFrame = initialState
				startTime = TimeUtils.nanoToSecond(System.nanoTime)
				replaying = true
				println(events.filter(_._2.size != 0))
		}

		/** Invoked on module init, will create childs
		  * Override to specify childs
		  */
		override def createChilds: Unit = {
				logicManager = context.actorOf(Props[GameLogicManager],"game-logic-manager")
				ioManager = context.actorOf(Props[GameIOManager],"game-io-manager")
				loggingManager = context.actorOf(Props[CManagerMessageLogging],"game-logging-manager")
		}
}


object CoreTest {
		def mockGO = {
				val trans = Transform()
				val camController = FPSCamera()
				val camInput = FlyCameraController(1, 1, 1)
				val camObj = GameObject(Vector[Component](trans, camController, camInput))


				val ctrans = Transform(Vector3(0, 0, 5))

				val tex = ImageLoader.loadPNG(new File("assets/Textures/testmodeluv.png"))
				val verts = Vector[Double](
						-1.0, -1.0, -1.0,
						-1.0, -1.0, 1.0,
						-1.0, 1.0, 1.0,
						1.0, 1.0, -1.0,
						-1.0, -1.0, -1.0,
						-1.0, 1.0, -1.0,
						1.0, -1.0, 1.0,
						-1.0, -1.0, -1.0,
						1.0, -1.0, -1.0,
						1.0, 1.0, -1.0,
						1.0, -1.0, -1.0,
						-1.0, -1.0, -1.0,
						-1.0, -1.0, -1.0,
						-1.0, 1.0, 1.0,
						-1.0, 1.0, -1.0,
						1.0, -1.0, 1.0,
						-1.0, -1.0, 1.0,
						-1.0, -1.0, -1.0,
						-1.0, 1.0, 1.0,
						-1.0, -1.0, 1.0,
						1.0, -1.0, 1.0,
						1.0, 1.0, 1.0,
						1.0, -1.0, -1.0,
						1.0, 1.0, -1.0,
						1.0, -1.0, -1.0,
						1.0, 1.0, 1.0,
						1.0, -1.0, 1.0,
						1.0, 1.0, 1.0,
						1.0, 1.0, -1.0,
						-1.0, 1.0, -1.0,
						1.0, 1.0, 1.0,
						-1.0, 1.0, -1.0,
						-1.0, 1.0, 1.0,
						1.0, 1.0, 1.0,
						-1.0, 1.0, 1.0,
						1.0, -1.0, 1.0
				).map(_.toFloat)
				val uv = Vector[Double](
						0.333333, 0.666667,
						0.333333, 1.000000,
						0.000000, 1.000000,
						0.666667, 0.666667,
						0.333333, 0.333333,
						0.333333, 0.000000,
						0.666667, 0.000000,
						0.000000, 0.000000,
						1.000000, 0.333333,
						0.666667, 0.333333,
						0.000000, 0.666667,
						0.000000, 0.333333,
						1.000000, 0.000000
				).map(_.toFloat)
				val mesh = Mesh(verts, uv, verts)
				val testCube = GameObject(Vector[Component](ctrans, mesh))

				val gameSettingsComp = GameSettings(9999)
				val gameSettings = GameObject(Vector[Component](gameSettingsComp))

				val keyBinds = KeyBinds(Map[(Int, KeyState.Value), Vector[Event]](
						(GLFW_KEY_ESCAPE, KeyState.press) -> Vector(DestroyDisplay(0)),
						(GLFW_KEY_SPACE, KeyState.press) -> Vector(SetFullScreen(0, true)),
						(GLFW_KEY_I, KeyState.press) -> Vector(ToggleDisplayMode(0)),
						(GLFW_KEY_W, KeyState.hold) -> Vector(EMove(0, 0, Direction.front)),
						(GLFW_KEY_A, KeyState.hold) -> Vector(EMove(0, 0, Direction.left)),
						(GLFW_KEY_S, KeyState.hold) -> Vector(EMove(0, 0, Direction.back)),
						(GLFW_KEY_D, KeyState.hold) -> Vector(EMove(0, 0, Direction.right)),
						(GLFW_KEY_Q, KeyState.hold) -> Vector(EMove(0, 0, Direction.top)),
						(GLFW_KEY_Z, KeyState.hold) -> Vector(EMove(0, 0, Direction.bottom))
				))
				val keyConfig = GameObject(Vector[Component](keyBinds))

				GameFrame(0, Vector[GameObject](camObj, testCube, gameSettings, keyConfig), Vector[Event]())

		}

		/** for testing stuff fast*/
		def findFirstComponentUnsafe[A <: Component : ClassTag](states:Vector[GameObject]):A={
				states.filter(g=>g.containsComponent[A]).headOption.get.findComponent[A].get
		}
}