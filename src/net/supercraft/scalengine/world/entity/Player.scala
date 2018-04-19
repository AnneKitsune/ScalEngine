package net.supercraft.scalengine.world.entity

/*import com.jme3.asset.AssetManager
import com.jme3.material.Material
import com.jme3.math.{ColorRGBA, FastMath, Quaternion, Vector3f}
import com.jme3.renderer.Camera
import com.jme3.scene.shape.Box
import com.jme3.scene.{Geometry, Node}
import net.supercraft.B2LScala.event.{Event, Walk}
import net.supercraft.B2LScala.world.entity.weapon.Weapon

/**
  * Created by jojolepro on 4/19/16.
  */
object PlayerState extends Enumeration {
		val STANDING, WALKING, CROUCHING, RUNNING, AIMING = Value

		def getSpeedMult(pstate: PlayerState.Value): Float = pstate match {
				case PlayerState.STANDING => 1f
				case PlayerState.WALKING => 1f
				case PlayerState.CROUCHING => 0.5f
				case PlayerState.RUNNING => 1.5f
				case PlayerState.AIMING => 0.5f
				case _ => 1
		}
}

object Player {
		val PLAYER_HEIGHT = 2f
		val DEFAULT_CAMERA_HEIGHT = PLAYER_HEIGHT / 1.1f
		val DUCK_HEIGHT_MULTIPLIER = 0.77f
		val MINIMUM_FALL_DAMAGE_HEIGHT = 2.5f
		val LOCK_CAMERA_VERTICAL = true
		val SPEED_CAP = 5f
		val FRICTION = 1.1f
		//0<x<1 acceleration x=1 no friction x>1 real friction (controls both acceleration and deceleration, written description is when no key is pressed)
		val AIR_CONTROL = 0.3f
		//The amount of the movement that we can control when mid-air
		val ACCELERATION = 1f
		//player movement force multiplier (used to make acceleration faster or slower)
		val FALL_DAMAGE = 30f
		val PICKUP_DISTANCE = 1.0f
}

class Player(name: String, id: Int,assetManager: AssetManager) extends Entity(name: String, id: Int) {
		//States
		var playerState = PlayerState.STANDING
		val playerController = new CustomCharacterControl(0.4f, Player.PLAYER_HEIGHT, 77f, new Vector3f(0f, 300f, 0f), Player.DUCK_HEIGHT_MULTIPLIER, 0f)
		var velocity = new Vector3f()
		var left, right, up, down = false
		var handleCamera = false
		var currentGun: Weapon = null
		var currentCameraHeight = Player.DEFAULT_CAMERA_HEIGHT
		var waitingForUnCrouch = false
		var mouseButtonHold = false
		var wasFalling = false
		var fallDistance = 0f
		var isControllable = false
		var health = 100f


		val playerRoot = new Node()
		val playerBody = new Node()
		val playerHead = new Node()
		val camCopyNode = new Node()
		val verticalLock = new Node()


		initPlayer()

		//TODO tmp
		def createCube(assetManager:AssetManager,name:String, start:Vector3f, end:Vector3f):Geometry={
				val cube = new Geometry(name, new Box(start, end))
				cube.setLocalTranslation(0, 0, 0)
				val mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
				mat1.setColor("Color", ColorRGBA.randomColor())
				cube.setMaterial(mat1)
				return cube
		}
		def initPlayer() {
				//Attach a default gun

				//this.attachGun(new EntityGunRifle308());

				//////////////////////Anims  TODO: anim + UV maps
				/*gun = (Node) net.supercraft.B2LScala.B2L.getGameInstance().getAssetManager().loadModel("Models/TestModelBonesUV.j3o");
				 Material mat = net.supercraft.B2LScala.B2L.getGameInstance().getAssetManager().loadMaterial("Materials/TestModelBonesUV.j3m");
				 gun.setMaterial(mat);
				 gun.move(0, 0, 2f);
				 AnimControl con = gun.getChild("Cylinder").getControl(AnimControl.class);
				 AnimChannel cha = con.createChannel();
				 cha.setSpeed(1f);*/
				////////////////////////////

				playerRoot.addControl(playerController)
				playerRoot.setName("PlayerRootNode")
				/*
				 *
				 *
				 *playerRoot manually added in entitymanager
				 *
				 */

				playerHead.attachChild(createCube(assetManager,"head", new Vector3f(-0.1f, -0.1f, -0.1f), new Vector3f(0.1f, 0.1f, 0.1f)))

				playerBody.setLocalTranslation(verticalLock.getWorldTranslation().getX(), verticalLock.getWorldTranslation().getY() - currentCameraHeight, verticalLock.getWorldTranslation().getZ())
				playerBody.attachChild(createCube(assetManager,"foot", new Vector3f(-0.1f, -0.1f, -0.1f), new Vector3f(0.1f, 0.1f, 0.1f)))

				verticalLock.attachChild(playerBody)

				camCopyNode.attachChild(verticalLock)
				camCopyNode.attachChild(playerHead)
				model = camCopyNode

				this.registerBulletPhysics()

				//this.teleport(new Vector3f(0, 10, 0));
		}
		//TODO not fine
		def attachGun(newGun: Weapon) {
				detachGun(false)
				currentGun = newGun
				//TODO hum...
				currentGun.setPosition(Vector3f.ZERO)
				currentGun.setRotation(Quaternion.DIRECTION_Z)

				playerHead.attachChild(newGun.model)
		}
		//TODO not fine
		def detachGun(removeAudio: Boolean) {
				if (currentGun != null) {
						//todo
						//currentGun.unloadGun()//?
						playerHead.detachChild(currentGun.model)//fine
						currentGun = null
				}
		}

		def changeGun(newGun: Weapon) {
				//Animator
				//wait for animator done
				this.attachGun(newGun)
		}
		//TODO WTF?
		private def registerBulletPhysics() {
				/*net.supercraft.B2LScala.B2L.getGameInstance().getBulletAppState().getPhysicsSpace().add(playerController)
				net.supercraft.B2LScala.B2L.getGameInstance().getBulletAppState().getPhysicsSpace().addAll(playerRoot)*/
		}

		def move(tpf: Float):Vector3f={
				val deltaVelocity = new Vector3f(0, 0, 0)

				//Getting camera direction
				val camDir = camCopyNode.getWorldRotation().getRotationColumn(2).clone().normalizeLocal()
				val camLeft = camCopyNode.getWorldRotation().getRotationColumn(0).clone().normalizeLocal()

				if (left) {
						deltaVelocity.addLocal(camLeft)
				}
				if (right) {
						deltaVelocity.addLocal(camLeft.negate())
				}
				if (up) {
						deltaVelocity.addLocal(camDir.x, 0, camDir.z)
				}
				if (down) {
						deltaVelocity.addLocal(-camDir.x, 0, -camDir.z)
				}
				deltaVelocity.normalizeLocal()
				deltaVelocity.multLocal(Player.ACCELERATION)

				//We apply the amount that we can actually control(if in mid-air)
				if (!playerController.isOnGround()) {
						//[0,1]-> biggest = more air control
						deltaVelocity.multLocal(Player.AIR_CONTROL)
				}

				velocity.addLocal(deltaVelocity)

				if (Player.FRICTION > 0f) {
						//Apply slowdown
						if (playerController.isOnGround()) {
								velocity.divideLocal(Player.FRICTION); //speed cap (friction too)
						}
				}

				//Limit the velocity to the current max speed(which itself depends on the state of the player)
				if (velocity.length() > getCurrentSpeedCap()) {
						velocity.normalizeLocal().multLocal(getCurrentSpeedCap())
				}

				//this.updateCameraPosition()
			velocity
		}

		private def getCurrentSpeedCap(): Float = Player.SPEED_CAP * PlayerState.getSpeedMult(playerState)

		//TODO: Move in a view module that manages the pos/rot of cam according to player data
		def updateCameraPosition() {
				/*//playerRoot.lookAt(Vector3f.ZERO, playerController.getViewDirection());
				if (isControllable) {
						net.supercraft.B2LScala.B2L.getGameInstance().getCamera().setLocation(new Vector3f(playerRoot.getLocalTranslation().getX(), playerRoot.getLocalTranslation().getY() + currentCameraHeight, playerRoot.getLocalTranslation().getZ()))
						camCopyNode.setLocalRotation(net.supercraft.B2LScala.B2L.getGameInstance().getCamera().getRotation())
				}

				camCopyNode.setLocalTranslation(new Vector3f(playerRoot.getLocalTranslation().getX(), playerRoot.getLocalTranslation().getY() + currentCameraHeight, playerRoot.getLocalTranslation().getZ()))

				//Need to ajust the player height if we are crouching
				playerBody.setLocalTranslation(0, -currentCameraHeight, 0)

				//Vertical body loc
				float[] angles = new float[ 3]
				camCopyNode.getLocalRotation().toAngles(angles)

				verticalLock.setLocalRotation(new Quaternion().fromAngles(-angles[ 0], 0, 0) )*/
		}

		def damage(damage: Float) {
				health = health - damage
		}

		//TODO
		def kill() {

		}

		def isMoving(): Boolean = velocity.length() > 0.00001f

		//TODO: REMOVE MODIFICATION OF CAMERA FROM HERE
		private def lockCameraVertical(cam: Camera) {
				val angles = cam.getRotation.toAngles(new Array[Float](3))
				if (angles(0) > FastMath.HALF_PI) {
						angles(0) = FastMath.HALF_PI
						cam.setRotation(new Quaternion().fromAngles(angles))
				} else if (angles(0) < -FastMath.HALF_PI) {
						angles(0) = -FastMath.HALF_PI
						cam.setRotation(new Quaternion().fromAngles(angles))
				}
		}

		def setControllable(controllable: Boolean) {
				isControllable = controllable
				if (isControllable) {
						handleCamera = true
				}
		}
		override def update(tpf:Float): Seq[Event] ={
				/*var events=Seq.empty[Event]
				events = events:+(new Walk(move(tpf),id))
				events*/

				Seq(new Walk(move(tpf),id))
		}
		//cl only
		//TODO: Return action, no side effect
		def dropGun() {
				/*if (currentGun != null && currentGun.isDropable()) {
						net.supercraft.B2LScala.B2L.getGameInstance().getModuleEntityManager().addObject(currentGun, true);

						currentGun.createPhysicState();

						applyDropImpulse(currentGun, this, 60);

						detachGun(false);

						if ((!net.supercraft.B2LScala.B2L.getGameInstance().isServer() && this.id == ((NetworkedClient) net.supercraft.B2LScala.B2L.getGameInstance().getModuleNetwork()).getID()) /* || net.supercraft.B2LScala.B2L.getGameInstance().isServer()*/ )
						{
								((NetworkedClient) net.supercraft.B2LScala.B2L.getGameInstance().getModuleNetwork()).addPacketElement(PacketType.GUN_DROP.newInstance(""));
						}
				}*/
		}

		//TODO: Switch from side effect towards reactive
		def setPlayerState(state: PlayerState.Value) {
				/*if (state.equals(playerState)) {
						return;
				}
				switch(state) {
						case CROUCHING:
						playerController.setDucked(true);
						playerState = PlayerState.CROUCHING;
						currentCameraHeight = DEFAULT_CAMERA_HEIGHT * playerController.getDuckedFactor();
						break;
						case STANDING:
						if (playerController.isDucked()) {
								unduck();
						} else {
								currentCameraHeight = DEFAULT_CAMERA_HEIGHT;
								playerState = PlayerState.STANDING;
						}
						break;
						case WALKING:
						if (playerController.isDucked()) {
								unduck();
						} else {
								playerState = PlayerState.WALKING;
						}
						break;
						case RUNNING:
						if (playerController.isDucked()) {
								unduck();
						} else {
								playerState = PlayerState.RUNNING;
						}
						break;
						case AIMING :// broke
						break;
						default:
						  System.err.println ("Wrong player state!");
						break;
				}
				if (net.supercraft.B2LScala.B2L.getGameInstance().isServer()) {
						PacketElementPlayerState pstate = (PacketElementPlayerState) PacketType.PLAYER_STATE.newInstance(playerState.toString());
						((NetworkedServer) net.supercraft.B2LScala.B2L.getGameInstance().getModuleNetwork()).addPacketElement(id, pstate);
				}*/
		}

		private def unduck() {
				playerController.setDucked(false)
				if (playerController.wantToUnduck()) {
						waitingForUnCrouch = true
				} else {
						//Successful uncrouch
						waitingForUnCrouch = false
						setPlayerState(PlayerState.STANDING)
				}
		}

		override def setPosition(pos: Vector3f): Unit = playerController.warp(pos)

		override def setRotation(quat: Quaternion): Unit = camCopyNode.setLocalRotation(quat)

		override def setDirection(dir: Vector3f): Unit = camCopyNode.setLocalRotation(new Quaternion().fromAngles(dir.x, dir.y, dir.z))

		override def getPosition(): Vector3f = playerRoot.getLocalTranslation()

		override def getRotation(): Quaternion = camCopyNode.getLocalRotation()

		override def getDirection(): Vector3f = camCopyNode.getWorldRotation().getRotationColumn(2)
}
*/