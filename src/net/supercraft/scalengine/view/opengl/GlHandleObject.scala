package net.supercraft.scalengine.view.opengl

/**
  * Created by jojolepro on 9/29/16.
  */
//vao,vbo(handle,length,batch)
case class GlHandleObject(vaoHandle:Int,vboHandles:Array[VboData],textureHandle:Int)
case class VboData(vboHandle:Int,dataLength:Int,batchSize:Int)
