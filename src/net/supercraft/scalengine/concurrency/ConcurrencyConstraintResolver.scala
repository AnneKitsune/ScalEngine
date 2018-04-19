package net.supercraft.scalengine.concurrency

import com.github.jpbetz.subspace.Vector3
import net.supercraft.scalengine.core.state.Component
import net.supercraft.scalengine.{ScalEngine, Transform}

import scala.collection.mutable.ListBuffer
import scala.reflect.{ClassTag, classTag}

//What should it return?

//Executer needs to know which module it needs to execute in parallel, and which ones needs to execute in serial and in which order
abstract sealed class SystemAction(val sys:LSystem)
sealed trait WithComp{
		val comp:Class[_]
}
case class Read(override val sys:LSystem,comp:Class[_]) extends SystemAction(sys) with WithComp
case class Exec(override val sys:LSystem) extends SystemAction(sys)
case class Write(override val sys:LSystem,comp:Class[_]) extends SystemAction(sys) with WithComp
//case class Write(override val sys:LSystem) extends SystemAction(sys)

//~100 lines of code
//time to make from idea to final testing: ~2 weeks part time
//32 loops
object ConcurrencyConstraintResolver {
		def resolve(sys:List[LSystem])={
				//Find all that don't have any conflict
				//Sys(Write(1,2,3)),Sys(Write(4,1,5))

				val actions = ListBuffer[SystemAction]()

				val prioritySort = sys.sortBy(_.priority).reverse

				def readRelative={
						prioritySort.foreach{
								s=>
										if(s.dataExactitude == DataExactitudePolicy.linear)s.read.filterNot(c=>containsSysCompActions[Read](actions.toList,s,c)).foreach{r=>
												//si t linéaire, read la comp quand tout les writes de + haut priorité on été fait sur ce comp

												//systems that have higher priority and write to a read slot of this linear comp
												val depSys = prioritySort.filter(_.priority > s.priority).find(_.write.contains(r))
												if(depSys.forall(d=>containsSysCompActions[Write](actions.toList,d,r))){
														actions.append(new Read(s,r))
												}
										}
						}
				}

				def write(doneExec:List[LSystem],notWritten:List[LSystem])={
						doneExec.intersect(notWritten).sortBy(s=>s.priority).reverse.foreach{s=>
								//Are all sys in priority sort before this one already written?
								if(prioritySort.filter(_.priority > s.priority).forall(s2=>containsSysAllWrites(actions.toList,s2))){
										actions.appendAll(s.write.map(w=>new Write(s,w)))
								}
						}
				}
				/*
				Read All Speed by priority

				foreach
					Execute systems that have all read
					Write Data according to priority
					Read Linear data that can now be read after the last write
				 */

				//List[LSystem] => List[Read[LSystem,Component]]
				prioritySort.foreach(s=>if(s.dataExactitude == DataExactitudePolicy.speed)s.read.foreach{c=>actions.append(Read(s,c))})
				readRelative
				println("MARKER: "+actions.toList.mkString("\n")+"DONE")
				do{
						//actions.append(sys.flatMap{s=>if(actions.flatMap{a=>a.sys==sys && a.isInstanceOf[Read] && a.asInstanceOf[Read].comp})})
						val doneRead = sys.partition(s=>containsSysAllReads(actions.toList,s))


						//--------------------------EXECUTE SYSTEM---------------
						//Exec systems that have all read and not already read in actions
						var doneExec = doneRead._1.partition(s=>containsSysAction[Exec](actions.toList,s))
						actions.appendAll(doneRead._1.intersect(doneExec._2).map(s=>new Exec(s)))
						doneExec = doneRead._1.partition(s=>containsSysAction[Exec](actions.toList,s))

						var doneWrite = doneExec._1.partition(s=>containsSysAction[Write](actions.toList,s))


						//-----------------------------WRITE-----------------
						//Write Data using descending priority until missing read
						write(doneExec._1,doneWrite._2)


						//--------------------READ RELATIVE----------------
						//Read relative
						readRelative
						doneExec._1.partition(s=>containsSysAction[Write](actions.toList,s))

						//Write if its now possible, because of a upper priority linear sys done reading
						//write(doneExec._1,doneWrite._2)
						//issue: writes are only calculated in batch

						println("---------------AFTER READ RELATIVE LOOP---------------------")
						ScalEngine.printConcurrent(actions.toList)

				}while(!done(actions.toList,sys))


				/*
				in manager, batch exec consecutive systemaction of same type
				 */
				actions.toList
		}
		def done(actions:List[SystemAction],sys:List[LSystem]):Boolean={
				for(s<-sys){
						if(!containsSysAllWrites(actions,s))
								return false
				}
				true
		}
		def containsSysAction[A <: SystemAction : ClassTag](actions:List[SystemAction], sys:LSystem):Boolean={
				for(a<-actions){
						if(a.sys == sys && a.getClass == classTag[A].runtimeClass){
								return true
						}
				}
				false
		}
		def containsSysAllWrites(actions:List[SystemAction],sys:LSystem):Boolean={
				for(c<-sys.write){
						if(!containsSysCompActions[Write](actions,sys,c))
								return false
				}
				true
		}
		def containsSysAllReads(actions:List[SystemAction],sys:LSystem):Boolean={
				for(c<-sys.read){
						if(!containsSysCompActions[Read](actions,sys,c))
								return false
				}
				true
		}
		def containsSysCompActions[A <: (SystemAction with WithComp) : ClassTag](actions:List[SystemAction],sys:LSystem,comp:Class[_]):Boolean={
				for(a<-actions){
						if(a.sys == sys && a.getClass == classTag[A].runtimeClass && a.asInstanceOf[A].comp == comp){
								return true
						}
				}
				false
		}
}
object ConcurrencyConstraintResolverTest{
		case class A()
		case class B()
		case class C()
		class T6 extends LSystem(List[Class[_]](),List[Class[_]](classOf[A]), List[Class[_]](classOf[A],classOf[C]),6,dataExactitude = DataExactitudePolicy.linear) with SR2SW{
				override def exec(id: Int, read: List[Component]): List[Component] = {
						read
				}
		}
		class T5 extends LSystem(List[Class[_]](),List[Class[_]](classOf[C]), List[Class[_]](classOf[A]),5,dataExactitude = DataExactitudePolicy.linear) with SR2SW{
				override def exec(id: Int, read: List[Component]): List[Component] = {
						read
				}
		}
		class T4 extends LSystem(List[Class[_]](),List[Class[_]](), List[Class[_]](classOf[B]),4,dataExactitude = DataExactitudePolicy.speed) with SR2SW{
				override def exec(id: Int, read: List[Component]): List[Component] = {
						read
				}
		}
		class T3 extends LSystem(List[Class[_]](),List[Class[_]](classOf[B]), List[Class[_]](classOf[C]),3,dataExactitude = DataExactitudePolicy.speed) with SR2SW{
				override def exec(id: Int, read: List[Component]): List[Component] = {
						read
				}
		}
		class T2 extends LSystem(List[Class[_]](),List[Class[_]](classOf[C]), List[Class[_]](classOf[A]),2,dataExactitude = DataExactitudePolicy.linear) with SR2SW{
				override def exec(id: Int, read: List[Component]): List[Component] = {
						read
				}
		}
		class T1 extends LSystem(List[Class[_]](),List[Class[_]](classOf[B]), List[Class[_]](classOf[B]),1,dataExactitude = DataExactitudePolicy.linear) with SR2SW{
				override def exec(id: Int, read: List[Component]): List[Component] = {
						read
				}
		}
}