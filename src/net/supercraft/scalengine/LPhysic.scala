package net.supercraft.scalengine

import net.supercraft.scalengine.core.manager.Manager

/**
  * Created by jojolepro on 2/18/17.
  */
class LPhysic extends Manager {
		//OnDone->Broadcast ApplyConstraints
		override def getMessage: Receive = {
				case _=>
		}
}
