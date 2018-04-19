package net.supercraft.scalengine.world.entity


/**
  * Created by jojolepro on 4/19/16.
  */
/*case class Entity(val name:String,val id:Int){
		var model = new Node
		def update(tpf:Float):Seq[Event]={
				/*if (net.supercraft.B2LScala.B2L.getGameInstance().isServer()) {
						broadcastData(tpf);
				}
				setLastData();*/
				Seq.empty[Event]
		}

		def setPosition(pos:Vector3f){
				model.setLocalTranslation(pos)
		}

		def setRotation(quat:Quaternion) {
				model.setLocalRotation(quat)
		}
		def setDirection(dir:Vector3f) {
				model.setLocalRotation(new Quaternion().fromAngles(dir.x, dir.y, dir.z))
		}
		def getPosition():Vector3f={
				return model.getLocalTranslation()
		}

		def getRotation():Quaternion={
				return model.getLocalRotation()
		}

		def getDirection():Vector3f={
				return model.getLocalRotation().getRotationColumn(2).normalize();
		}
}
*/