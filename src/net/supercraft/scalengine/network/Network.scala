package net.supercraft.scalengine.network

import java.net._

import akka.actor.Actor
import akka.io.{IO, Udp}
import net.supercraft.scalengine.event._
import spray.json._

import scala.io.Source

/**
  * Created by jojolepro on 5/29/16.
  */

object Network{
		val separatorId='$'
		val separatorData='_'
		val separatorIdData='='
}


/*
Packet

Event=object
id=int

receive   id->check perms+event class
send  event instance->id send


Event <----> id
Event contains id, read perm from file using id
perms=id->List[Boolean]
 */

/*class Network(ip:String="::1",port:Int=5555) extends Actor{
		//val socket = createSocket(port)
		val idToEvent:Map[Int,NetEvent] = loadNetworkEvents()
		import context.system
		IO(Udp) ! Udp.Bind(self, new InetSocketAddress(ip,port))
		def checkEvents(ev:List[GameEvent]): Unit ={
				ev.foreach{
						x=> x match{
								case e:EventModAdd=>if(e.module.equals(this)){}
								case e:EventModDel=>if(e.module.equals(this)){}
								case e=>
						}
				}
		}

		def createSocket(port:Int):Option[DatagramSocket]={
				try{
						return Option(new DatagramSocket(port))
				}catch{
						case se:SocketException=>return Option(null)
						case uhe:UnknownHostException=>return Option(null)
				}
		}
		def loadNetworkEvents():Map[Int,NetEvent]= {
				val str=Source.fromFile("assets/Data/PacketType.json").getLines().mkString
				val json=str.parseJson
				println(json.compactPrint)
				val out=Map(1->new EventPosition(0))
				return out
		}
		def sendPacket(dest:InetAddress,port:Int,evs:List[NetEvent]): Unit ={

		}

		override def toString:String="Network[selfIP="+ip.toString+", port="+port+"]"

		override def receive: Receive ={
				case Udp.Received(data,remote) => //onReceive check auth, broadcast, reply.......
				case Udp.Unbind => socket
		}
}*/
