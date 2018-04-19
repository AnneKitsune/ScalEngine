package net.supercraft.scalengine.view.console

import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.Calendar

import akka.actor.Actor
import net.supercraft.scalengine.event.GameEvent

class GameLog extends Actor{
		var gameLogSettings: GameLogSettings=new GameLogSettings()
		override def receive: Receive = {
				case e:Log=>log(e)
		}
		def log(log:Log): Unit ={
				gameLogSettings.printStream.println(formatLog(log))
		}

		override def toString():String=s"GameModuleLogger[settings=${gameLogSettings.toString}]"
		def formatLog(log:Log):String=s"[${addLevel(log.level)}]${addName}:${addDate} ${log.msg}"

		def addIfTrue(cond: =>Boolean)(str: String):String=cond match{
				case true=>return str
				case false=>return ""
		}
		def addName=addIfTrue(gameLogSettings.printName)(gameLogSettings.name)
		def addDate=addIfTrue(gameLogSettings.printDate)(getCurrentFormattedTime)
		def addLevel(level:LogLevel.Value)=addIfTrue(gameLogSettings.printLevel)(level.toString)

		def getCurrentFormattedTime():String=return getCurrentFormattedTime("yyyy-MM-dd HH:mm:ss")
		def getCurrentFormattedTime(format:String):String={
				val cal=Calendar.getInstance().getTime()
				val format=new SimpleDateFormat()
				return format.format(cal)
		}
}
class GameLogSettings(val name:String="DefaultLogger",val printName:Boolean=true,val printDate:Boolean=false,val printLevel:Boolean=false,val printStream: PrintStream=System.out){
		override def toString:String=s"GameLogSettings[name=$name, printName=$printName, printDate=$printDate, printLevel=$printLevel, printStream=${printStream.toString}]"
}
case class Log(msg:String,level: LogLevel.Value) extends GameEvent
object LogLevel extends Enumeration{
		val info,warning,error,fatal = Value
}