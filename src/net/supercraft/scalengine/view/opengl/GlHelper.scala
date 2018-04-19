package net.supercraft.scalengine.view.opengl

import java.nio.{Buffer, FloatBuffer}

import com.github.jpbetz.subspace.{Vector2, Vector3}
import net.supercraft.scalengine.util.math.VectorHelper
import net.supercraft.scalengine.view.GlLight
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._

import scala.annotation.tailrec

/**
  * Created by jojolepro on 9/30/16.
  */
class BatchedData(val data:Array[Float],val batchSize:Int)
object DisplayMode extends Enumeration{
		val normal,wireframe,point = Value
}
object GlHelper {
		/**
		  *
		  * @param data the array of data
		  * @return The Vbo handle
		  */
		def createAndBufferVbo(data:Array[Float]):Int={
				val buffer=BufferUtils.createFloatBuffer(data.length)
				buffer.put(data).flip
				val handle = glGenBuffers()
				glBindBuffer(GL_ARRAY_BUFFER,handle)
				glBufferData(GL_ARRAY_BUFFER,buffer,GL_STATIC_DRAW)
				handle
		}

		def createVertexAttrib(vboIndex:Int,batchSize:Int,lastVboLength:Int,lastVboBatchSize:Int)={
				if(lastVboBatchSize==0) {
						glVertexAttribPointer(vboIndex, batchSize, GL_FLOAT, false, 0, 0)
						//println(s"${vboIndex} ${batchSize} 0")
				} else {
						glVertexAttribPointer(vboIndex, batchSize, GL_FLOAT, false, 0, 0)//changed the last 0  from lastVboLength / (lastVboBatchSize*lastVboBatchSize) to test
						//println(s"${vboIndex} ${batchSize} ${lastVboLength / (lastVboBatchSize*lastVboBatchSize)}")
				}
				glEnableVertexAttribArray(vboIndex)
		}
		//change to list.map?
		def createGlObject(vboList:Array[BatchedData]):GlHandleObject={
				val vaoHandle = glGenVertexArrays()
				glBindVertexArray(vaoHandle)
				@tailrec def  consumeVboList(vbos:Array[BatchedData],carry:Array[VboData],index:Int,lastLength:Int,lastBatch:Int):Array[VboData]={
						if(index>=vbos.length){
								carry
						}else {
								val vboHandle = createAndBufferVbo(vbos(index).data)
								val handle = new VboData(vboHandle, vbos(index).data.length, vbos(index).batchSize)
								createVertexAttrib(index,vbos(index).batchSize,lastLength,lastBatch)
								consumeVboList(vbos,carry:+handle,index+1,vbos(index).data.length,vbos(index).batchSize)
						}
				}
				val vboHandles=consumeVboList(vboList,Array(),0,0,0)
				glBindVertexArray(0)
				new GlHandleObject(vaoHandle,vboHandles,0)
		}

		def setViewportSize(size:Vector2)=glViewport(0,0,size.x.toInt,size.y.toInt)
		def setDisplayMode(displayMode: DisplayMode.Value)={
				if(displayMode == DisplayMode.normal){
						glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
				}else if(displayMode == DisplayMode.wireframe){
						glPolygonMode(GL_FRONT_AND_BACK,GL_LINE)
				}else{
						glPolygonMode(GL_FRONT_AND_BACK,GL_POINT)
				}
		}
		/*def uploadUniform[A](shaderProgramId:Int,propName:String,glUniformFunc:(Int,A)=>Unit,a:A)={
				val uniform = glGetUniformLocation(shaderProgramId,propName)
				glUniformFunc(uniform,a)
		}*/
		def uploadUniform[A](propName:String,glUniformFunc:(Int,A)=>Unit,a:A)(implicit shaderProgramId:Int)={
				val uniform = glGetUniformLocation(shaderProgramId,propName)
				glUniformFunc(uniform,a)
		}
		def uploadUniformMatrix[A](propName:String,glUniformFunc:(Int,Boolean,A)=>Unit,a:A,transpose:Boolean=false)(implicit shaderProgramId:Int)={
				val uniform = glGetUniformLocation(shaderProgramId,propName)
				glUniformFunc(uniform,transpose,a)
		}
		type uniformFuncArray = (Int,Array[Float])=>Unit
		/**
		  * uses predefined names in the glsl
		  */
		def uploadUniformLight(implicit shaderProgramId:Int,propName:String,light:GlLight)={
				val w = if(light.directional) 0f else 1f
				uploadUniform[Array[Float]](propName+".position",glUniform4fv,VectorHelper.vector3ToArray(light.position) :+ w)
				uploadUniform[Array[Float]](propName+".intensities",glUniform3fv,VectorHelper.vector3ToArray(light.colors))
				uploadUniform(propName+".ambientCoefficient",glUniform1f,light.ambientCoefficient)
				uploadUniform(propName+".attenuation",glUniform1f,light.attenuation)
				uploadUniform(propName+".coneAngle",glUniform1f,light.coneAngle)
				uploadUniform[Array[Float]](propName+".coneDirection",glUniform3fv,VectorHelper.vector3ToArray(light.coneDirection))
		}
}
