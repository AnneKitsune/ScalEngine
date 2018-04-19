package net.supercraft.scalengine.core.state

final case class GameFrame(time:Double,states:Vector[GameObject],events:Vector[Event])
