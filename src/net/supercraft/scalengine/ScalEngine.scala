package net.supercraft.scalengine

import akka.actor.{ActorSystem, Props}
import com.github.jpbetz.subspace.Vector3
import com.typesafe.config.ConfigFactory
import net.supercraft.scalengine.CManagerMessageLogging.TestLoggingCollect
import net.supercraft.scalengine.concurrency.ConcurrencyConstraintResolverTest._
import net.supercraft.scalengine.concurrency.{SR2SW, _}
import net.supercraft.scalengine.core.manager.ManagerMessageLog.GetLoggedMessages
import net.supercraft.scalengine.core.manager.{Init, PostInit, PreInit}
import net.supercraft.scalengine.core.state.{Component, Event}
import net.supercraft.scalengine.core.storage.ManagerStorage.{ById, Insert}
import net.supercraft.scalengine.core.storage.{DefaultStorageRepository, ManagerStorage}
import net.supercraft.scalengine.core.{CoreTest, Replay, Start}

import scala.reflect.{ClassTag, classTag}
/**
  * Created by jojolepro on 2/10/17.
  */
object ScalEngine {
		case class xDDD(str:String)
		def main(args:Array[String]):Unit={


				val conf= ConfigFactory.load()
				lazy val actorSystem = ActorSystem("scalengine-system",conf.getConfig("custom"))



				//storageTest(actorSystem)
				val c = actorSystem.actorOf(Props[CoreTest],"scalengine-core-test")


				//Manually create all actors here and set inheritances here too

				//Separate all calculations in two parts: 1 interacting with the system, and one doing the calculation without any dependency to the system
				// (except base classes like vector or config types like time=Double)

				c ! PreInit
				c ! Init
				c ! PostInit
				

				//Thread.sleep(15000)
				println("Starting TestLogging...")
				c ! TestLoggingCollect

				c ! Replay

				actorSystem.actorSelection("/user/*") ! GetLoggedMessages;println("TestLoggingCollect3")
		}
		def storageTest(system:ActorSystem)={
				val c = system.actorOf(Props(new ManagerStorage(new DefaultStorageRepository)))
				c ! Insert
				c ! ById(0)

				//See if its possible to find out the r/w from a method header, so the access is direct and we don't need asInstanceOf

				val sys1 = new TestSystem
				val exec = ConcurrencyConstraintResolver.resolve(List(new T1,new T2,new T3,new T4,new T5,new T6))
				println("main -------------------")
				printConcurrent(exec)
				println("main end --------------")
		}
		class TestSystem() extends LSystem(List[Class[_]](),List[Class[_]](classOf[Transform]), List[Class[_]](classOf[Transform]),1,dataExactitude = DataExactitudePolicy.speed) with SR2SW{
				override def exec(id: Int, read: List[Component]): List[Component] = {
						val t = read(0).asInstanceOf[Transform]
						List[Component](t.copy(t.position + Vector3(1,1,1)))
				}
		}

		def printConcurrent(ls:List[SystemAction])={
				def test(a:SystemAction):String=if(a.isInstanceOf[WithComp]){"->Comp:"+a.asInstanceOf[WithComp].comp.getSimpleName}else{""}
				val str = ls.map{
						a=>a.getClass.getSimpleName +" "+ a.sys.getClass.getSimpleName + test(a) + "\n"
				}
				println(str)
		}
}
