package net.supercraft.scalengine.view

import java.awt.Color
import javax.swing.JPanel

/**
  * Created by jojolepro on 4/19/16.
  */
abstract class Menu {
		var currentPanel = new JPanel();
		def onScreenSizeChange()
		currentPanel setBackground Color.BLUE
}
