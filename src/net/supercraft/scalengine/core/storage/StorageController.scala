package net.supercraft.scalengine.core.storage

import java.util

import net.supercraft.scalengine.Transform
import net.supercraft.scalengine.core.state.{Component, GameObject}

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

trait StorageRepository{
		def addEntity:Int
		def addEntity(comp:List[Component]):Int={
				val id = addEntity
				comp.foreach(addComponent(id,_))
				id
		}
		def removeEntity(id:Int):Boolean
		def addComponent(id:Int,component:Component)
		def updateComponent[C <: Component : ClassTag](id:Int, updated:Component):Boolean
		def removeComponent[C <: Component : ClassTag](id:Int):Boolean
		def getComponentOfType[C <: Component : ClassTag](id:Int):Option[C]
		def hasComponentOfType[C <: Component : ClassTag](id:Int):Boolean
		def getComponentsOfType[C <: Component : ClassTag]:List[Component]
		def getComponents(id:Int):List[Component]
}

//Goal: Make the different components independent from each other in the exposed methods, it doesn't matters
//how the data is really layed out in the repo.
class DefaultStorageRepository() extends StorageRepository{
		import scala.reflect.ClassTag
		import scala.reflect._
		case class GO(id:Int,comp:List[Component]=List())
		val data = new ArrayBuffer[GO]()

		var maxId = -1
		override def addEntity: Int = {
				maxId += 1
				data.append(new GO(maxId))
				maxId
		}

		override def hasComponentOfType[C <: Component : ClassTag](id: Int): Boolean = data.find(g=>g.id == id).get.comp.exists(c=>c.getClass == classTag[C].runtimeClass)

		override def addComponent(id: Int, component: Component): Unit = {
				val go = data.find(_.id == id).get
				val idx = data.indexOf(go)
				val ngo = go.copy(comp = go.comp :+ component)
				data.update(idx,ngo)
		}

		override def removeComponent[C <: Component : ClassTag](id: Int): Boolean = {
				val go = data.find(_.id == id).get
				val idx = data.indexOf(go)
				val ngo = go.copy(comp = go.comp.filterNot(_.getClass == classTag[C].runtimeClass))
				data.update(idx,ngo)
				true
		}

		override def updateComponent[C <: Component : ClassTag](id: Int, updated: Component): Boolean = {
				val go = data.find(_.id == id).get
				val idx = data.indexOf(go)
				val ngo = go.copy(comp = go.comp.filterNot(_.getClass == classTag[C].runtimeClass) :+ updated)
				data.update(idx,ngo)
				true
		}

		override def removeEntity(id: Int): Boolean = {
				val go = data.find(_.id == id)
				data.remove(data.indexOf(go))
				true
		}

		override def getComponentsOfType[C <: Component : ClassTag]: List[Component] = data.flatMap{go=>go.comp.find(_.getClass == classTag[C].runtimeClass)}.toList

		override def getComponents(id: Int): List[Component] = data.find(_.id == id).get.comp

		override def getComponentOfType[C <: Component : ClassTag](id: Int): Option[C] = data.find(_.id == id).get.comp.find(_.getClass == classTag[C].runtimeClass).asInstanceOf[Option[C]]
}
/*class DOStorageRepository(componentClasses:List[Class[_]]) extends StorageRepository{
		//Map[ID,BitSet[Component]]
		//Map[ComponentType,Map[ID,Component]]
		val entityMetadata = new ArrayBuffer[util.BitSet]

		//Implicit mapping between componentsTypes and the storages
		//val components = componentClasspaths.foreach(t=>new ArrayBuffer[Component])
		val components = Array.fill[ArrayBuffer[Component]](componentClasses.size)()


		def compArrayIndex(comp:Component)=componentClasses.indexOf(comp.getClass)
		def compArrayIndex[C <: Component : ClassTag]=componentClasses.indexOf(classOf[C])

		override def addEntity: Int = {
				entityMetadata += new util.BitSet()
				components.foreach(_ += null)
				entityMetadata.size-1
		}

		override def hasComponentOfType[C <: Component : ClassTag](id: Int): Boolean = entityMetadata(id).get(componentClasses.indexOf(classOf[C]))

		override def addComponent(id: Int, component: Component): Unit = {
				val cid = compArrayIndex(component)
				entityMetadata(id).set(cid)
				val comps = components(cid)
				comps(id) = component
		}

		override def removeComponent[C <: Component : ClassTag](id: Int): Boolean = {
				val cid = compArrayIndex[C]
				entityMetadata(id).clear(cid)
				val comps = components(cid)
				comps(id) = null
				true
		}

		override def updateComponent[C <: Component : ClassTag](id: Int, updated: Component): Boolean = {
				val cid = compArrayIndex[C]
				val comps = components(cid)
				comps(id) = updated
				true
		}

		override def removeEntity(id: Int): Boolean = {
				entityMetadata(id).clear()
				components.foreach(c=>c(id) = null)
				true
		}

		override def getComponentsOfType[C <: Component : ClassTag]: List[Component] = {
				val cid = compArrayIndex[C]
				components(cid).toList
		}

		override def getComponents(id: Int): List[Component] = {
				components.fold(List[Component]())((c,comp)=>if(c(id) != null)c :+ c(id) else c)
		}

		override def getComponentOfType[C <: Component : ClassTag](id: Int): Option[C] = ???
}*/


//System
// Read -> List[ComponentType]
// Write -> List[ComponentType]


//Default = easy to make
//No concurrency
//Slow read/write speed
//Dynamic sized lists


//DO = ez read write locking of specific component type lists
//++speed
//requires heavy maps, and index based ID
//no ez possibility of dynamic sized lists

//Controller manages concurrency of read/write of repo


//Required: Deterministic ID allocation
//NetworkIdentityComponent

//network sync: if(entity.Contains(_.isInstanceOf[NetworkIdentityComponent) find all components .asInstanceOf[NetworkSync])
//go.addComponent(Transform with NetworkSync)
//tagging of components