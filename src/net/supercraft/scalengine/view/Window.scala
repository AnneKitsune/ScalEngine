package net.supercraft.scalengine.view

import java.awt.Dimension

import net.supercraft.scalengine.event._
//import net.supercraft.scalengine.{B2L}
import org.lwjgl.opengl.{GL11}


/*case class Window() extends GameModule{

		def update(tpf:Double,ev:List[GameEvent]):(GameModule,List[GameEvent])={
				checkEvents(ev)
				var sideEffects:List[GameEvent]=List()
				if(Display.isCloseRequested){
						sideEffects = sideEffects:+new EventCloseGame
				}
				//Display.update()
				return (this,sideEffects)
		}

		def checkEvents(ev:List[GameEvent]): Unit ={
				ev.foreach{
						x=>x match{
								case e:EventModAdd=>if(e.module.equals(this)){init()}
								case e:EventUpdateDisplay=>updateDisplay()
								case e:EventModDel=>if(e.module.equals(this)){destroy()}
								case e=>
						}
				}
		}

		def init(): Unit ={
				createDisplay(B2L.GAMETITLE,new Dimension(800,600))
		}
		def updateDisplay(): Unit ={
				draw()
				Display.update(false)
		}
		def destroy(): Unit ={
				Display.destroy()
		}
		/*def createFrame(title:String,dimension:Dimension):Window={
				val nframe=new JFrame(title)
				nframe.setSize(dimension)
				nframe.setVisible(true)
				copy(frame = nframe)
		}*/
		def createDisplay(title:String,dimension:Dimension)={
				try {
						Display.setTitle(title)
						Display.setDisplayMode(new DisplayMode(dimension.width,dimension.height))
						Display.setResizable(true)
						Display.create()
				} catch  {
						case e:LWJGLException=>
							e.printStackTrace()
							Display.destroy()
							System.exit(1)
				}
				this
		}
		def draw(): Unit ={


				GL11.glMatrixMode(GL11.GL_PROJECTION)
				GL11.glLoadIdentity()
				GL11.glOrtho(0,Display.getDisplayMode.getWidth,Display.getDisplayMode.getHeight,0,1,-1)

				GL11.glMatrixMode(GL11.GL_MODELVIEW)

				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


				GL11.glColor4f(0f,1f,0f,0.8f)
				GL11.glBegin(GL11.GL_TRIANGLES)
				GL11.glVertex2f(100,500)
				GL11.glVertex2f(300,500)
				GL11.glVertex2f(200,100)
				GL11.glEnd()



				//GL11.glDisable(GL11.GL_BLEND);
				GL11.glColor4f(1f,0f,0f,1f)
				GL11.glBegin(GL11.GL_TRIANGLES)
				GL11.glVertex2f(600,800)
				GL11.glVertex2f(900,800)
				GL11.glVertex2f(100,500)
				GL11.glEnd()

		}
}

/**
  * Created by jojolepro on 4/19/16.
  *
  *
  * Canvas always on screen, but menu on top if present
  *
  *
  *
  */
/*class Window(title: String, dimension: PointXY,private val game: B2L) {
		protected val frame = new JFrame()
		//protected var content = new JPanel()
		val cleanScreen = true

		protected var currentMenu:Menu = null

		game.createCanvas(); // create canvas!
		val ctx = game.getContext.asInstanceOf[JmeCanvasContext]

		initWindow()
		def initWindow() {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

				frame.setTitle(title);
				frame.setVisible(true);
				frame.setResizable(true);
				frame.setFocusable(true);
				frame.setLocationRelativeTo(null);
				frame.setSize(dimension.getX.asInstanceOf[Int], dimension.getY.asInstanceOf[Int])

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				this.createScreenSettings()

				//TODO
				//game.createCanvas(); // create canvas!
				//val ctx = game.getContext.asInstanceOf[JmeCanvasContext]
				ctx.setSystemListener(game)
				ctx.setSettings(game.getSettings())
				val dim = new Dimension(game.getSettings().getWidth(), game.getSettings().getHeight())
				ctx.getCanvas().setPreferredSize(dim)

				frame.add(ctx.getCanvas)
		}
		def createScreenSettings() {
				game.setShowSettings(false)
				val cfg = new AppSettings(true)
				cfg.setFrameRate(0) // set to less than or equal screen refresh rate
				cfg.setVSync(true)// prevents page tearing
				cfg.setFrequency(60) // set to screen refresh rate
				//cfg.setResolution(4, 3);//If we change this to a higher value, the screen won't resize properly
				cfg.setResolution(800, 600)
				cfg.setFullscreen(false)
				cfg.setBitsPerPixel(24)
				cfg.setSamples(16) // anti-aliasing
				cfg.setTitle(B2L.GAMETITLE) // branding: window name
				game.setSettings(cfg)
				game.setPauseOnLostFocus(false)
		}
		def changeMenu(newMenu:Menu) {
				//frame.setContentPane(currentMenu.currentPanel)
				if (currentMenu != null) {
						frame.remove(currentMenu.currentPanel)
						ctx.getCanvas.setVisible(true)
				}

				this.currentMenu = newMenu
				if(newMenu!=null && newMenu.currentPanel!=null) {
						frame.add(newMenu.currentPanel)
						ctx.getCanvas.setVisible(false)
				}
		}
}*/
*/