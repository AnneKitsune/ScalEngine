package net.supercraft.scalengine

import net.supercraft.scalengine.CManagerMessageLogging._
import net.supercraft.scalengine.core.manager.{Manager, StartSubSystemExecution}
import net.supercraft.scalengine.core.manager.ManagerMessageLog.{GetLoggedMessages, LoggedMessage, LoggedMessages}
import net.supercraft.scalengine.view.glfw.EMove

import scala.collection.immutable.Range

object CManagerMessageLogging{
		case object TestLoggingCollect
		case object TestLoggingAnalysis
}
class CManagerMessageLogging extends Manager{
		var loggedMessages = LoggedMessages(Vector[LoggedMessage]())
		override def getMessage: Receive = {
				case e:LoggedMessages=>loggedMessages = loggedMessages.copy(messages = loggedMessages.messages ++ e.messages);testAnalysis
				case TestLoggingCollect=> Range(0,4).foreach(broadcast(_,GetLoggedMessages))
				case TestLoggingAnalysis=>
		}
		def testWrite(str:String)={
				import java.io._
				val pw = new PrintWriter(new File("logtest.txt" ))
				pw.write(str)
				pw.close
		}
		def testAnalysis={
				val t = loggedMessages.messages.filter(m=>m.msgClass.equals(classOf[EMove])).map(_.msgClass)
				val t2 = loggedMessages.messages.filter(m=>m.msgClass == EMove.getClass).map(_.msgClass)
				val t3 = loggedMessages.messages.filter(_.msgClass match{
						case c if c == StartSubSystemExecution.getClass=>true
						case _=>false
				})
				//println(t3)
		}
		def broadcast(depth:Int,msg:Any)={
				val str = new StringBuilder("/user")
				for(i<- 0 until depth){
						str.append("/*")
				}
				context.system.actorSelection(str.toString) ! msg
		}
}