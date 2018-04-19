package net.supercraft.scalengine.audio

import java.nio.ByteBuffer

import akka.actor.Actor
import org.lwjgl.openal.ALC10

/**
  * Created by jojolepro on 10/31/16.
  */

case object InitSoundSystem
case object SoundSystemFailedToStart
class SoundSystem extends Actor{
		val device= ALC10.alcOpenDevice(null.asInstanceOf[ByteBuffer])

		override def receive: Receive = {
				case InitSoundSystem=>init
		}
		def init()={

		}
}
