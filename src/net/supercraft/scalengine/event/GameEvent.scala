package net.supercraft.scalengine.event

/**
  * Created by jojolepro on 4/20/16.
  */


//In game modules add map Event->Function


object GameEvent{

}

/*
SV send all
chain state group by entity
chain cmd (ex:TIME_SET, which is current server time for clock ajustment)

CL send sv
chain action+cur time

SV receive
get cl ip->entity id

 */
/*
sv->cl cmd+state
TIME_SET 1
ENTITY_ID
POSITION 2
ROTATION 3
PLAYER_STATE 4
HEALTH(<0=destroy entity?) 5
GUN 6
CONNECTED 7

cl->sv cmd only   some are also sv->cl
FORWARD 8
BACKWARD 9
LEFT 10
RIGHT 11
JUMP 12
GUN_SET 13
GUN_DROP(managed by POSITION on cl) 14
KILL(cmd) 15
KICK 16
ROTATE 17
CONNECT 18
DISCONNECT 19

sv<--->cl
SET_LEVEL 20
SET_SCORE 21

connection sequence
cl connect
sv connected+map data(includes TIME_SET)
 */

class GameEvent(){

}
class GameCommand(){

}
class ErrorEvent() extends GameEvent
/*class NetEvent() extends GameEvent{Replaced by NetEvent in NetworkSocket
		val data:String=""
		def getListFromData(data:String):List[String]=data.split(Network.separatorData).asInstanceOf[List[String]]

		def getEventPerms:List[Boolean]=Nil//@TODO

		def isClRunnable(perms:List[Boolean]):Boolean=true//@TODO
		def isSvRunnable(perms:List[Boolean]):Boolean=true
		def isOp(perms:List[Boolean]):Boolean=true

		//def isCmd(perms:List[Boolean]):Boolean=true defined as gameEvent extends StateEvent?
}*/

//First data in data string is entityId
class StateEvent(clId:Int) extends GameEvent()

case class ErrorNotReady() extends ErrorEvent
//case class ErrorUnknownEvent() extends ErrorEvent //AVOID DOING THAT, we are going to broadcast events to avoid most direct dependencies between actors. Having this kind of error will break everything later on

//-------------------------------------------Commands-------------------------------------------------//
/*abstract class EventMovement(mvTime:Float) extends NetEvent(){
		override val data=mvTime
}
case class EventForward(mvTime:Float) extends EventMovement(mvTime:Float){
}

case class EventBackward(mvTime:Float) extends EventMovement(mvTime:Float){
}
case class EventLeft(mvTime:Float) extends EventMovement(mvTime:Float){
}
case class EventRight(mvTime:Float) extends EventMovement(mvTime:Float){
}
case class EventJump() extends NetEvent(){
}
case class EventGunChange() extends NetEvent(){
}
case class EventGunDrop() extends NetEvent(){
}
case class EventKill() extends NetEvent(){
}
case class EventKick() extends NetEvent(){
}
case class EventRotate() extends NetEvent(){
}
case class EventConnect() extends NetEvent(){
}
case class EventDisconnect() extends NetEvent(){
}
case class EventScoreSet() extends NetEvent(){
}
case class EventLevelSet() extends NetEvent(){
}
//---------------------------------------------------States-------------------------------------------------------------//
case class EventPosition(entityId:Int) extends StateEvent(entityId:Int){
		override val data=entityId
}
case class EventRotation(entityId:Int) extends StateEvent(entityId:Int){
		override val data=entityId
}
case class EventPlayerState(entityId:Int) extends StateEvent(entityId:Int){
		override val data=entityId
}
case class EventHealth(entityId:Int,health:Float) extends StateEvent(entityId:Int){
		override val data=entityId+Network.separatorData+health
}*/





//---------------------------------------------------Internal-------------------------------------------------------------//
case class EventOpenMenu() extends GameEvent
case class EventKeyBindAdd(keyCode:Int,events:List[GameEvent]) extends GameEvent


/*case class EventKeyPressed(keyCode:Int) extends GameEvent
case class EventKeyMaintained(keyCode:Int) extends GameEvent
case class EventKeyReleased(keyCode:Int) extends GameEvent*/

//case class EventWalk(relPos:Vector3f,entityId:Int) extends GameEvent

//case class EventRotate(newRot:Quaternion,entityId:Int) extends GameEvent

case class EventUpdateDisplay() extends GameEvent
case class EventCloseGame() extends GameEvent

case class ErrorNetworkSocketClosed() extends ErrorEvent
case class ErrorPacketEventIncorrectParam() extends ErrorEvent