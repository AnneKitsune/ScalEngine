package net.supercraft.scalengine.view.glfw

import akka.actor.{Actor, Props}
import net.supercraft.scalengine.core.manager.Manager.SubSystemBroadcast
import net.supercraft.scalengine.core.manager.ManagerChildSync.DoneProcessing
import net.supercraft.scalengine.core.manager.ManagerMessageLog

import scala.reflect.ClassTag
import scala.reflect.runtime._
//import net.supercraft.jojoleproUtils.module.model.KeyState
import net.supercraft.scalengine.core.manager.{Manager, StartSubSystemExecution}
import net.supercraft.scalengine.core.state.{Component, Event}
import net.supercraft.scalengine._
import net.supercraft.scalengine.view._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWKeyCallbackI
import reflect._
import scala.reflect.runtime.universe._
import scala.reflect.runtime._

/**
  * Created by jojolepro on 10/10/16.
  */


case class KeyStateChange(window: Long, key: Int, scanCode: Int, action: Int, mods: Int)
case object SendRegisterKeyboardMouse
case class RegisterKeyListener(callback: GLFWKeyCallbackI)
case class ImmutableKeyboard(pressedKeys:Vector[Int]=Vector())
case class GetHoldKeysEvents(deltaTime:Double)

object KeyState extends Enumeration{
		val press,hold,release = Value
}
/*
Logic to be made
1 actor by input type?
GLFW need 1 call back for both mouse and keyboard
other libraries might need to make calls to the actor when a key is pressed

can we be more generic than Int for the key identifier? maybe an api uses an object instead of Int to determine which key is pressed
*/
trait WithDeltaTime{
		val deltaTime:Double
}
case class KeyBinds(keyBinds:Map[(Int,KeyState.Value),Vector[Event]]) extends Component
case class EMove(override val time:Double,deltaTime:Double,direction:Direction.Value) extends Event(time) with WithDeltaTime



class KeyboardMouseController extends Manager with ManagerMessageLog{
		private var immutableKeyboard=new ImmutableKeyboard

		val keyboardMouseListener=new KeyboardMouseListener(self)

		//(Key,Action)->Event
		//Action: Press, Release, Hold(custom)
		//(GLFW_KEY_ESCAPE,KeyPress)->DestroyDisplay,(GLFW_KEY_W,KeyHold)->WPressed

		/*can we sample between 2 frames?
		will time be still deterministic?
		if yes then all events get a time value
		if not we remove the dt from the hold events

		should we move the configs into the game state?
		IOCMD->AddKeyBind(key,state->event)
		LOGICCMD (AddKeyBind)->modif state
		+separation between data and glfw dependency


		Hold(sinceStart,sinceLastFrame)

		         1             1           0.25
		Press----->Hold------>Hold-->Release
		Press(0) Hold(1,1) Hold(2,1) Release(2.25,0.25)


		OnPress
		OnHold
		OnRelease -> emit OnHold too

		btw naming convention could be
		IOXXXXX
		LXXXX


		use stream here
		each frame release HoldKey ev
		if key press before draw, start stream early
		if key release after draw, buf stream and release on tick*/

		/*val holdEvents=Map[Int,Vector[Event]](
				GLFW_KEY_W->Vector(WPressed(0)),
				GLFW_KEY_S->Vector(SPressed(0)),
				GLFW_KEY_A->Vector(APressed(0)),
				GLFW_KEY_D->Vector(DPressed(0)),
				GLFW_KEY_Q->Vector(QPressed(0)),
				GLFW_KEY_Z->Vector(ZPressed(0)),
				GLFW_KEY_P->Vector(MoveObjectForward(0)))
		val keyBinds=Map[(Int,KeyState.Value),List[Any]]((GLFW_KEY_ESCAPE,KeyState.press)->List(DestroyDisplay),(GLFW_KEY_SPACE,KeyState.press)->List(SetFullScreen(0,true)),(GLFW_KEY_I,KeyState.press)->List(ToggleDisplayMode))*/
		//val testKeyMap=Map[(Int,KeyState),List[Any]]((GLFW_KEY_ESCAPE,KeyPress)->DestroyDisplay,GLFW_KEY_W->WPressed(1),GLFW_KEY_S->SPressed,GLFW_KEY_A->APressed,GLFW_KEY_D->DPressed,GLFW_KEY_Q->QPressed,GLFW_KEY_Z->ZPressed,GLFW_KEY_SPACE->SetFullScreen)
		var lastUpdateTime = 0.0
		var keyEvents:KeyBinds = null
		override def getMessage: Receive ={
				case KeyStateChange(window,key,scanCode,action,mods)=>onKeyStateChange(key,action)
				//case GetHoldKeysEvents(deltaTime)=>sendHoldEvents(deltaTime);DoneProcessing
				case StartSubSystemExecution=>context.parent ! GetIOGameState
				case f:IOReadState=>keyEvents = f.gameFrame.states.find(g=>g.containsComponent[KeyBinds]).get.findComponent[KeyBinds].get
						sendHoldEvents(f.targetTime,f.targetTime-lastUpdateTime)
						lastUpdateTime = f.targetTime
						context.parent ! DoneProcessing
		}
		override def init={
				context.parent ! SubSystemBroadcast(new RegisterKeyListener(keyboardMouseListener))
				super.init
		}

		//On press send event
		//On release send event hold + send event release

		//On press or release send immediatly; on receive event GetHoldKeyEvents send events for hold keys using hold time(based on either creation or last function call time)
		def onKeyStateChange(key:Int,action:Int)={
				val ac=getAction(action)
				if(ac==KeyState.press){
						sendEvents(key,ac)
						//println("KeyPressed!")
				}else if(ac==KeyState.release){
						//sendKeyHold(key) //find a way to pass the delta time
						sendEvents(key,ac)
						//println("KeyReleased")
				}else{
						println("Invalid type of key state")
				}
				updateKeyStore(key,ac)
		}
		def getAction(action:Int):KeyState.Value=
				if(action==GLFW_PRESS)KeyState.press else KeyState.release

		def updateKeyStore(key:Int,action:KeyState.Value)={
				if(action==KeyState.press){
						if(!immutableKeyboard.pressedKeys.contains(key))
							immutableKeyboard=immutableKeyboard.copy(pressedKeys = immutableKeyboard.pressedKeys:+key)
				}else if(action==KeyState.release){
						if(immutableKeyboard.pressedKeys.contains(key))
							immutableKeyboard=immutableKeyboard.copy(pressedKeys = immutableKeyboard.pressedKeys.filter(_!=key))
				}
				//println(immutableKeyboard.pressedKeys)
		}
		def sendEvents(key:Int,action:KeyState.Value)={
				val events=keyEvents.keyBinds.get((key,action))
				events match{
						case Some(evs)=>evs.foreach{e=>context.parent ! SubSystemBroadcast(e);context.parent ! AddEvent(e)};
						case None=> //No events linked to the key and action combo
				}
		}


		//Generate event case classes using input deltaTime
		def sendHoldEvents(curTime:Double,deltaTime:Double)={
				def instantiate(clazz: java.lang.Class[_])(args:AnyRef*): AnyRef =
						clazz.getConstructors()(0).newInstance(args:_*).asInstanceOf[AnyRef]
				val ev3 = EMove(0,0,Direction.front)
				immutableKeyboard.pressedKeys.foreach{
						k=>keyEvents.keyBinds.getOrElse((k,KeyState.hold),Vector[Event]()).foreach{
								ev=>
								        //see if its possible to case class Event.copy(time = x) and WithDelta copy also using delta
										//context.parent ! AddEvent(instantiate(ev.getClass)(new java.lang.Float(deltaTime)))
								        /*if(ev3.isInstanceOf[WithDeltaTime]){
										        val ev2 = ev3.asInstanceOf[WithDeltaTime]
										        val method = ev2.getClass.getDeclaredMethods.find(m=>m.getName() == "copy")
										        if(!method.isEmpty){
												        missing args
												        val constr = ev2.un
												        method.get.invoke(ev2,new java.lang.Double(0),new java.lang.Double(0)).asInstanceOf[ev.type]
										        }else{
												        println("No copy method, please use case class!")
										        }
								        }else{

								        }*/

										//val classSymbol = mirror.classSymbol(getClass)
										//val classMirror = mirror.reflectClass(classSymbol)
										//val companion = classMirror.symbol.companion


										//val companionMirror = mirror.reflectModule(universe.typeOf[ev3.type].typeSymbol.companion.asModule)
										//companionMirror.instance.
										//context.parent ! AddEvent()

										val ev2 = updateParameter(ev,"time",curTime)
										if(ev.isInstanceOf[WithDeltaTime]){
												context.parent ! AddEvent(updateParameter(ev2,"deltaTime",deltaTime))
										}else{
												context.parent ! AddEvent(ev2)
										}
						}
				}
		}
		lazy val mirror = universe.runtimeMirror(getClass.getClassLoader)
		class Empty
		def updateParameter[A:ClassTag](inst:A,parameterName:String,newValue:Any):A={
				val mirrorInstance = mirror.reflect(inst)
				val declaration = mirrorInstance.symbol.asType.toType
				val members = declaration.members.map(method=>transformMethod(method,parameterName,newValue,mirrorInstance)).filter{
						case _: Empty=>false
						case _ =>true
				}.toArray.reverse
				val copyMethod = declaration.decl(TermName("copy")).asMethod
				val copyMethodInstance = mirrorInstance.reflectMethod(copyMethod)
				copyMethodInstance(members: _*).asInstanceOf[A]
		}
		def transformMethod(method:Symbol,parameterName:String,newValue:Any,instanceMirror:InstanceMirror)={
				val term = method.asTerm
				if(term.isAccessor){
						if(term.name.toString == parameterName){
								newValue
						}else instanceMirror.reflectField(term).get
				}else new Empty
		}
}
