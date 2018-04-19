package net.supercraft.scalengine.view

/**
  * Created by jojolepro on 4/19/16.
  */
/*class MenuInGame(ctx: JmeCanvasContext,game: B2L) extends Menu {
		protected val guiFont = game.getAssetManager().loadFont("Interface/Fonts/Impact.fnt")
		protected val guiColor = ColorRGBA.Green;

		protected var healthText = new BitmapText(guiFont, false)
		healthText setColor guiColor
		protected val healthTextSize = 3f;

		protected val ammoText = new BitmapText(guiFont, false)
		ammoText setColor guiColor
		protected val ammoTextSize = 3f;

		protected val crosshairNode = new Node();
		protected val crosshairLeftNode = new Node();
		protected val crosshairRightNode = new Node();
		protected val crosshairUpNode = new Node();
		protected val crosshairDownNode = new Node();
		protected val crosshairMaterial = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md")
		protected val crosshairColor = ColorRGBA.Green;
		crosshairMaterial setColor("Color", crosshairColor)

		protected val crosshairLenght = 10f;
		protected val crosshairThickness = 1f;
		protected val crosshairGap = 9f;
		protected val crosshairGapAngleMultiplier = 10f;
		private val crosshairRotation = 0f;
		protected val crosshairBit = new Quad(crosshairThickness, crosshairLenght)
		protected val crosshairBitGeom = new Geometry("CrosshairBit", crosshairBit)
		crosshairBitGeom setMaterial crosshairMaterial
		crosshairBitGeom setLocalTranslation(-crosshairThickness / 2f, -crosshairLenght / 2f, 0)
		private var showCrosshair = true;
		private var showAmmo = true;
		val dynamicCrosshair = true;


		game.setDisplayFps(true)
		game.setDisplayStatView(true)
		game.getGuiNode attachChild healthText
		game.getGuiNode attachChild ammoText

		crosshairLeftNode.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Z));
		crosshairRightNode.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));

		crosshairLeftNode.attachChild(crosshairBitGeom.clone());
		crosshairRightNode.attachChild(crosshairBitGeom.clone());
		crosshairUpNode.attachChild(crosshairBitGeom.clone());
		crosshairDownNode.attachChild(crosshairBitGeom.clone());

		crosshairNode.attachChild(crosshairLeftNode);
		crosshairNode.attachChild(crosshairRightNode);
		crosshairNode.attachChild(crosshairUpNode);
		crosshairNode.attachChild(crosshairDownNode);

		game.getGuiNode attachChild crosshairNode
		//currentPanel setLayout new BorderLayout
		//currentPanel.add(ctx.getCanvas, BorderLayout.CENTER)
		//currentPanel = null
		//currentPanel setBackground Color.GREEN

		//TODOS
		/*public void update(long l) {
				setShowCrosshair(showCrosshair);
				EntityPlayer player;
				if ((player = B2L.getGameInstance().getModuleEntityManager().getMainPlayer()) != null) {
						healthText.setText("Health: " + (int) FastMath.ceil(player.getHealth()));
						if (player.getCurrentGun() != null) {
								setShowAmmo(true);
								if(player.getCurrentGun().crosshairAllowed){
										setShowCrosshair(true);
								}else{
										setShowCrosshair(false);
								}
								ammoText.setText("Ammo: " + player.getCurrentGun().getClipContent() + "/" + player.getCurrentGun().getAmmoLeft());

								crosshairNode.setLocalTranslation(currentPanel.getWidth() / 2f, currentPanel.getHeight() / 2f, 0);
								if(dynamicCrosshair){
										crosshairLeftNode.setLocalTranslation(-crosshairGap - player.getCurrentGun().currentSprayAngle * crosshairGapAngleMultiplier, 0, 0);
										crosshairRightNode.setLocalTranslation(crosshairGap + player.getCurrentGun().currentSprayAngle * crosshairGapAngleMultiplier, 0, 0);

										crosshairUpNode.setLocalTranslation(0, crosshairGap + player.getCurrentGun().currentSprayAngle * crosshairGapAngleMultiplier, 0);
										crosshairDownNode.setLocalTranslation(0, -crosshairGap - player.getCurrentGun().currentSprayAngle * crosshairGapAngleMultiplier, 0);
								}else{
										crosshairLeftNode.setLocalTranslation(-crosshairGap, 0, 0);
										crosshairRightNode.setLocalTranslation(crosshairGap, 0, 0);

										crosshairUpNode.setLocalTranslation(0, crosshairGap, 0);
										crosshairDownNode.setLocalTranslation(0, -crosshairGap, 0);
								}
						}else{
								setShowCrosshair(false);
								setShowAmmo(false);
						}
				}
				healthText.setSize(guiFont.getCharSet().getRenderedSize() * healthTextSize);
				healthText.setLocalTranslation(this.currentPanel.getWidth() - healthText.getLineWidth() - 10, healthText.getLineHeight(), 0);

				ammoText.setSize(guiFont.getCharSet().getRenderedSize() * ammoTextSize);
				ammoText.setLocalTranslation((this.currentPanel.getWidth() / 2) - (ammoText.getLineWidth() / 2), ammoText.getLineHeight(), 0);
		}*/
		override def onScreenSizeChange() {

		}

		/*def setCrosshairRotation(rot:Float){
				this.crosshairRotation = rot;
				crosshairNode.setLocalRotation(new Quaternion().fromAngles(0, 0, FastMath.DEG_TO_RAD*rot));
		}*/
		def setShowCrosshair(show: Boolean) {
				if (showCrosshair != show) {
						if (showCrosshair) {
								//true->false
								game.getGuiNode().detachChild(crosshairNode);
						} else {
								//false->true
								game.getGuiNode().attachChild(crosshairNode);
						}
						showCrosshair = show;
				}
		}

		//TODO should use player as arg and then player.gun.drawCrosshair
		def setShowAmmo(show: Boolean) {
				if (showAmmo != show) {
						if (showAmmo) {
								//true->false
								game.getGuiNode().detachChild(ammoText);
						} else {
								//false->true
								game.getGuiNode().attachChild(ammoText);
						}
						showAmmo = show;
				}
		}
}
*/