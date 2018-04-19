package net.supercraft.scalengine.network

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.{IO, Udp}

/**
  * Created by jojolepro on 6/16/16.
  */
/*


STATE EVENTS CONTAIN CLID DIRECTLY IN THE EVENT


default packet perm=deny
 */
/*class NetworkSocket(ip:String="::1",port:Int=5555,isServer:Boolean) extends Actor{
		import context.system
		IO(Udp) ! Udp.Bind(self, new InetSocketAddress(ip,port))

		override def receive: Receive = {
				case Udp.Bound(local)=> context.become(bounded(sender()))
		}

		def bounded(socket:ActorRef): Receive = {
				case Udp.Received(data,remote) => //onReceive check auth, broadcast, reply.......
				case Udp.Unbind => socket ! Udp.Unbind
				case Udp.Unbound => context.stop(self)
		}

		//Avoid dependency to World, Entity, PlayerData
		//Output legit event, Send input event on tick when enough accumulated

		//Does not check if this come from a connected client
		def filterLegit(netEvents: List[NetEvent]):List[NetEvent] = {
				/*if(isServer){
						return netEvents.filter(_.isSvRunnable())
				}else{

				}*/
				netEvents
		}
}*/
