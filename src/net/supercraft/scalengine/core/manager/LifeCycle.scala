package net.supercraft.scalengine.core.manager

trait InitState
case object PreInit extends InitState
case object Init extends InitState
case object PostInit extends InitState
case object InitDone extends InitState
case object InitStepDone
case object Destroy
