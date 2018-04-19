package net.supercraft.scalengine.view.opengl

import java.io.{File, FileNotFoundException}

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._

import scala.io.Source

case class ShaderProgramData(programId:Int,vertexShaderId:Int,fragmentShaderId:Int)
class ShaderLoaderException(msg:String) extends Exception(msg:String)
class ShaderCompilationException(msg:String) extends ShaderLoaderException(msg:String)
class ShaderBindException(msg:String) extends ShaderLoaderException(msg:String)


//In development
//see note at the bottom
//we are just making a layer of abstraction at the wrong place: at the actor place
case class Shader(shaderId:Int,uniforms:Map[String,Int],attributes:Map[String,Int])
case class ShaderProgram(programId:Int,shaders:Map[Int,Shader])

object ShaderLoader{
		def createGLProgram():Int=glCreateProgram()
		def loadCode(f:File)=Source.fromFile(f).mkString
		/**
		  * Removes the vertex and fragment shaders at the lower opengl level from the input shaderProgram
		  * Then deletes the shaderProgram from the lower level
		  *
		  * @param shaderProg
		  */
		def attachShaders(shaderProg:ShaderProgramData): Unit ={
				glAttachShader(shaderProg.programId,shaderProg.vertexShaderId)
				glAttachShader(shaderProg.programId,shaderProg.fragmentShaderId)
		}

		/**
		  *
		  * @param shaderCode The shader program code loaded from loadCode(f:File)
		  * @param mode GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
		  * @return Handle id of the shader program
		  */
		def compileShader(shaderCode:String,mode:Int):Either[ShaderCompilationException,Int]={
				//Create handle for shader type
				val shaderId=glCreateShader(mode)
				//Associate code to handle
				glShaderSource(shaderId,shaderCode)

				//Compile the shader associated to the handle
				glCompileShader(shaderId)
				//Check for compilation error
				if(glGetShaderi(shaderId,GL_COMPILE_STATUS) == GL_FALSE)
						return Left(new ShaderCompilationException(s"Error compiling shader program: ${glGetShaderInfoLog(shaderId,glGetShaderi(shaderId,GL_INFO_LOG_LENGTH))}"))
				Right(shaderId)
		}

		def createShader(programId:Int,file:File,shaderType:Int,attributes:Map[String,Int],uniforms:List[String]):Shader={
				//Create handle for shader type
				val shaderId=glCreateShader(shaderType)
				//Associate code to handle
				glShaderSource(shaderId,Source.fromFile(file).mkString)

				//Compile the shader associated to the handle
				glCompileShader(shaderId)
				//Check for compilation error
				if(glGetShaderi(shaderId,GL_COMPILE_STATUS) == GL_FALSE)
						System.err.println(s"Error compiling shader program: ${glGetShaderInfoLog(shaderId,glGetShaderi(shaderId,GL_INFO_LOG_LENGTH))}")

				attributes.foreach(
						a=>glBindAttribLocation(programId,a._2,a._1)
				)

				val uni = uniforms.foldLeft(Map[String,Int]()){
						(c,u)=>c + (u->glGetUniformLocation(programId,u))
				}
				Shader(shaderId,uni,attributes)
		}

		/**
		  * Binds the shader program to opengl
		  *
		  * @param shaderProgramData
		  * @throws ShaderBindException
		  */
		@throws(classOf[ShaderBindException])
		def bind(shaderProgramData: ShaderProgramData) ={
				glLinkProgram(shaderProgramData.programId)
				println(glGetProgramInfoLog(shaderProgramData.programId))
				if(glGetProgrami(shaderProgramData.programId,GL_LINK_STATUS)==GL_FALSE)
						throw new ShaderBindException("Unable to bind shader program")
				glUseProgram(shaderProgramData.programId)
		}

		/**
		  * Unbinds the shader program from opengl
		  */
		def unbind=glUseProgram(0)

		/**
		  * Deletes the shader program from the gpu.
		  * The ShaderProgramData passed in parameter will become useless.
		  *
		  * @param shaderProgramData
		  */
		def dispose(shaderProgramData:ShaderProgramData): Unit ={
				glDetachShader(shaderProgramData.programId,shaderProgramData.vertexShaderId)
				glDetachShader(shaderProgramData.programId,shaderProgramData.fragmentShaderId)

				glDeleteShader(shaderProgramData.vertexShaderId)
				glDeleteShader(shaderProgramData.fragmentShaderId)

				glDeleteProgram(shaderProgramData.programId)
		}

		/**
		  * Does all the required operations to load,compile and attach the shaders at the lower opengl level.
		  * Note that you will have to call ShaderLoader.bind(shaderProgramData) to make opengl
		  * use the newly created shader.
		  *
		  * @param vertexProgramPath
		  * @param fragmentProgramPath
		  * @return ShaderProgramData containing the handles for the program and both shaders
		  */
		def createShaderProgram(vertexProgramPath:String,fragmentProgramPath:String):Either[Exception,ShaderProgramData]={
				//Create the program in opengl
				val programId=createGLProgram()

				//Load both codes from files
				var vertexShaderFile:File=null
				var fragmentShaderFile:File=null
				try {
						vertexShaderFile = new File(vertexProgramPath)
						fragmentShaderFile = new File(fragmentProgramPath)
				}catch{
						case e:Exception=>return Left(e)
				}
				if(!vertexShaderFile.exists()){
						return Left(new FileNotFoundException())
				}

				//Compiles both shader programs
				val vertexShaderId=compileShader(loadCode(vertexShaderFile),GL_VERTEX_SHADER)
				val fragmentShaderId=compileShader(loadCode(fragmentShaderFile),GL_FRAGMENT_SHADER)

				//Check for any errors during compilation
				vertexShaderId match{
						case Left(e)=>return Left(e)
						case Right(h)=>
				}
				fragmentShaderId match{
						case Left(e)=>return Left(e)
						case Right(h)=>
				}

				val spd=new ShaderProgramData(programId,vertexShaderId.right.get,fragmentShaderId.right.get)
				ShaderLoader.attachShaders(spd)
				Right(spd)
		}
		//that's useless, we are duplicating the variable data, and we are losing the types
		/*def createShaderProgram2(shaders:Map[Int,File]){
				val programId = createGLProgram
				val loadedShaders = shaders.foldLeft(List[Shader]())(
						(c,v)=>c :+ ShaderLoader.createShader(programId,v._2,v._1,)
				)
				glLinkProgram(shaderProgramData.programId)
		}*/
}