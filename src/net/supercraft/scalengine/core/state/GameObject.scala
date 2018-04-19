package net.supercraft.scalengine.core.state

import scala.reflect.ClassTag

/**
  * GameObject
  * Contains the components that carry the required data for various operation done by Modules.
  * Direct calls should only be used from Modules.
  * Modifying the components will produce a new GameObject, which will need to replace the existing one in the main GameObject Array.
  * To do this, you will need to call the corresponding module containing those objects so it can replace the existing GameObject with the new one.
  *
  * @param id
  * @param components
  */
final case class GameObject(components:Vector[Component],id:String=java.util.UUID.randomUUID.toString){
		def addComponent(component: Component)={
				this.copy(components = components :+ component)
		}
		def updateComponent[A <: Component : ClassTag](updateFunc: (A)=>A):GameObject={
				this.findComponent[A] match{
						case Some(c)=>this.removeComponent(c).addComponent(updateFunc(c))
						case None=> this
				}
		}
		def removeComponent(component: Component)={
				this.copy(components = components.filterNot(_ == component))
		}
		def findComponents[A <: Component : ClassTag]():Vector[A]={
				components.flatMap{
						case c:A => Some(c)
						case other=>None
				}
		}
		def findComponent[A <: Component : ClassTag]():Option[A]={
				val comps = findComponents[A]()
				comps(0) match{
						case null => None
						case v:A=>Some(v)
				}
		}
		def containsComponent[A <: Component : ClassTag]():Boolean={
				findComponents[A]().size > 0
		}
}