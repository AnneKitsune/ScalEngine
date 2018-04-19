package net.supercraft.scalengine.view.glfw

import java.io.PrintStream

import com.github.jpbetz.subspace.Vector2
//import net.supercraft.scalengine.view.DisplayTest
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback

/**
  * Created by jojolepro on 10/18/16.
  */
object GLFWHelper {
		def createWindow(name:String,
		                 size:Vector2=new Vector2(800,600),
		                 resizable:Boolean=true,
		                 antialiasing:Int=4,
		                 glVersionMaj:Int=4,
		                 glVersionMin:Int=3,
		                 cursorInvisible:Boolean=false,
		                 vsync:Boolean=false,
		                 glfwErrorCallbackTarget:PrintStream=System.err):Long={

				GLFWErrorCallback.createPrint(glfwErrorCallbackTarget).set()

				if ( !glfwInit() )
						throw new IllegalStateException("Unable to initialize GLFW")

				glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

				if(resizable) glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

				glfwWindowHint(GLFW_SAMPLES, antialiasing)
				glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, glVersionMaj)
				glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, glVersionMin)
				glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
				glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

				val window = glfwCreateWindow(size.x.toInt, size.y.toInt, name, 0,0)
				if ( window == 0 )
						throw new RuntimeException("Failed to create the GLFW window")

				GLFWHelper.centerWindow(window)

				glfwMakeContextCurrent(window)
				GLFWHelper.setVSyncEnabled(vsync)
				glfwShowWindow(window)
				if(cursorInvisible) glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
				window
		}
		def centerWindow(window:Long): Unit ={
				val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
				glfwSetWindowPos(window, (vidmode.width() - 800) / 2, (vidmode.height() - 600) / 2)
		}
		def getWindowSize(window:Long)={
				val w = BufferUtils.createIntBuffer(1)
				val h = BufferUtils.createIntBuffer(1)
				glfwGetWindowSize(window, w, h)
				new Vector2(w.get(0),h.get(0))
		}
		def getCursorPos(windowId:Long)={
				val x = BufferUtils.createDoubleBuffer(1)
				val y = BufferUtils.createDoubleBuffer(1)
				glfwGetCursorPos(windowId,x,y)
				new Vector2(x.get(0).toFloat,y.get(0).toFloat)
		}

		def setVSyncEnabled(enabled:Boolean)=if(enabled) glfwSwapInterval(1) else glfwSwapInterval(0)

		/**
		  * Remember to use GLHelper.setViewportSize if you have a GL canvas inside the window
 *
		  * @param window
		  * @param size
		  */
		def setScreenSize(window:Long,size:Vector2)=glfwSetWindowSize(window,size.x.toInt,size.y.toInt)
		def destroyDisplay(window:Long): Unit ={
				glfwSetWindowShouldClose(window,true)
				glfwFreeCallbacks(window)
				glfwDestroyWindow(window)
				glfwTerminate()
				glfwSetErrorCallback(null).free()
		}
}
