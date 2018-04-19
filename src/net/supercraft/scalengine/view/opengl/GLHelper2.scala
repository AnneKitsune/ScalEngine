package net.supercraft.scalengine.view.opengl

import java.awt.Color

import net.supercraft.scalengine.assetloading.{Model, Texture}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL14._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._

/**
  * Created by jojolepro on 10/30/16.
  */
/*



this should only react to the standard Model defined in assetloading
it should not try to load the model



current idea:

model(general data like coords and uv and textures...) > opengl data(vao,vbos,etc...) + real physical object properties(position,rotation,scale)

 */
object GLHelper2 {
		def init(clearColor:Array[Float]):Unit={

		}
		def create(model:Model):GlHandleObject={
				val vao = createVao()
				glBindVertexArray(vao)
				val vertexVbo=createVbo(vao,model.vertice.toArray,3,0)
				val uvVbo=createVbo(vao,model.uv.toArray,2,1)
				val normalVbo=createVbo(vao,model.normal.toArray,3,2)
				val textureVbo=model.texture match{
						case Left(e)=>e._2(e._1)
						case Right(e)=>generateTexture(e)
				}
				glBindVertexArray(0)
				new GlHandleObject(vao,Array(vertexVbo,uvVbo,normalVbo),textureVbo)
		}
		def createVao()=glGenVertexArrays()

		/**
		  * vao needs to be binded before using this procedure
 *
		  * @param vao
		  * @param data
		  * @param batchSize
		  * @return
		  */
		def createVbo(vao:Int,data:Array[Float],batchSize:Int,index:Int)={
				//Create the vbo buffer
				val buffer=BufferUtils.createFloatBuffer(data.length)
				//Put the data into the buffer
				buffer.put(data).flip
				//Create the gpu memory buffer and put the data inside
				val handle = glGenBuffers()
				glBindBuffer(GL_ARRAY_BUFFER,handle)
				glBufferData(GL_ARRAY_BUFFER,buffer,GL_STATIC_DRAW)

				//Create the vertex attrib pointer for the vbo
				glEnableVertexAttribArray(index)
				glVertexAttribPointer(index, batchSize, GL_FLOAT, false, 0, 0)

				new VboData(handle,data.length,batchSize)
		}

		//not done
		def createIndiceVbo(vao:Int,data:Array[Int])={
				glBindVertexArray(vao)
				val buffer=BufferUtils.createIntBuffer(data.length)
				buffer.put(data).flip
				val handle = glGenBuffers()

				glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,handle)
				glBufferData(GL_ELEMENT_ARRAY_BUFFER,buffer,GL_STATIC_DRAW)

				new VboData(handle,data.length,1)

		}
		def bufferTextureNearest(texture:Texture)={
				val texId=glGenTextures()
				glBindTexture(GL_TEXTURE_2D,texId)
				glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,texture.width,texture.height,0,GL_RGBA,GL_UNSIGNED_BYTE,texture.texture)
				glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST)
				glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST)
				texId
		}
		def bindTexture(target:Int,texHandle:Int)={
				glActiveTexture(target)//target usually GL_TEXTUREx
				glBindTexture(GL_TEXTURE_2D,texHandle)
		}
		def bufferTextureMipMap(texture:Texture)={
				val texId=glGenTextures()
				glBindTexture(GL_TEXTURE_2D,texId)
				glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,texture.width,texture.height,0,GL_RGBA,GL_UNSIGNED_BYTE,texture.texture)
				glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR)
				glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR)
				glGenerateMipmap(GL_TEXTURE_2D)
				texId
		}
		def generateTexture(color:Color)={
				0//todo
		}
		def clear():Unit={

		}
		def draw(obj:GlHandleObject):Unit={
				glBindVertexArray(obj.vaoHandle)
				bindTexture(GL_TEXTURE0,obj.textureHandle)
				glDrawArrays(GL_TRIANGLES, 0, obj.vboHandles(0).dataLength/3)
		}
		def destroy(obj:GlHandleObject):Unit={

		}

}
