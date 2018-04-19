package net.supercraft.scalengine.assetloading

import java.awt.Color
import java.io.File
import java.nio.ByteBuffer

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.Try

/**
  * Created by jojolepro on 10/30/16.
  */


/*

copy the model of blender  material (with props) -> includes texture


 */
case class Model(vertice:List[Float],uv:List[Float],normal:List[Float],texture:Either[(Texture,Texture=>Int),Color])

case class Texture(texture:ByteBuffer,width:Int,height:Int)
object ModelLoader {
		case class OBJContainer(vertices:List[Float]=List[Float](),uvCoords:List[Float]=List(),normals:List[Float]=List())
		def loadOBJ(obj:File)={
				/*
				  Unique
				        Vertex x y z
				        VTvertices u v
				        VNnormals x y z
				  Indices
				        f V/VT/VN V/VT/VN V/VT/VN
				        f V//VN V//VN V//VN
				 */

				val objData=Source.fromFile(obj).getLines().toList
				def extractSpacedLine(line:String)=line.split(" ").drop(1).map(_.toFloat).toList
				def extractFaceLine(line:String)={
						//f v/vt/vn v/vt/vn v/vt/vn
						val majSplit = line.split(" ").drop(1)

						// v,vt,vn,v,vt,vn,v,vt,vn
						val minSplit = majSplit.foldLeft(List[String]()){
								(c,v)=>c ++ v.split("/")
						}.map(v=>Try(v.toInt-1).getOrElse(-1))//note that we substract 1 from the value because .obj indexes starts at 1; -1 id error value
						(List(minSplit(0),minSplit(3),minSplit(6)),List(minSplit(1),minSplit(4),minSplit(7)),List(minSplit(2),minSplit(5),minSplit(8)))
				}

				//v,vt,vn,indicev,indicevt,indicevn
				objData.foldLeft((List[Float](),List[Float](),List[Float](),List[Int](),List[Int](),List[Int]()))(
						(c,ln)=> {
								ln.take(2) match{
										case "v "=>c.copy(_1 = c._1 ++ extractSpacedLine(ln))
										case "vt"=>val uv=extractSpacedLine(ln);c.copy(_2 = c._2 ++ List(uv(0),1f - uv(1)))
										case "vn"=>c.copy(_3 = c._3 ++ extractSpacedLine(ln))
										case "f "=>val lndata=extractFaceLine(ln);c.copy(_4 = c._4 ++ lndata._1,_5 = c._5 ++ lndata._2,_6 = c._6 ++ lndata._3)
										case _=>c
								}
						}
				)
		}

		def indexify(data:List[Float])={
				val unique = data.distinct
				val indexes=data.foldLeft(List[Int]()){
						(c,v)=>c :+ unique.indexOf(v)
				}
				(unique,indexes)
		}
		//we are not checking if the index is -1 (mostly caused by absent uv coordinate)
		def deindexify(uniqueData:List[Float],indexes:List[Int],batchSize:Int)={
				val grouped = uniqueData.grouped(batchSize).toList
				indexes.foldLeft(List[Float]()){
						(c,v)=>c ++ grouped(v)
				}
		}


		/*
			Add .flat and .indexed implicit conversion to help make the transition between the 2 modes
		 */

		/*def loadOBJBkp(objData:Array[String])={
				val data=objData.foldLeft(new OBJContainer())(
						(c,ln)=> {
								if (ln.startsWith("v ")) {
										val splitLine = ln.split(" ")
										val lnData = List[Float](splitLine(1).toFloat, splitLine(2).toFloat, splitLine(3).toFloat)
										c.copy(vertices=c.vertices:::lnData)
								}else if(ln.startsWith("vt")){
										val splitLine = ln.split(" ")
										val lnData = List[Float](splitLine(1).toFloat, splitLine(2).toFloat)
										c.copy(uvCoords=c.uvCoords:::lnData)
								}else if(ln.startsWith("vn")){
										val splitLine = ln.split(" ")
										val lnData = List[Float](splitLine(1).toFloat, splitLine(2).toFloat, splitLine(3).toFloat)
										c.copy(normals=c.normals:::lnData)
								}else{
										c
								}
						}
				)
				def getBatched(str:Int,batch:Int,ls:List[Float]):List[Float]={
						val out=ListBuffer[Float]()
						for(i<-str*batch until str*batch+batch){
								out += ls(i)
						}
						out.toList
				}
				//Ajusted data
				objData.foldLeft(new OBJContainer())((c,ln)=>{
						if(ln.startsWith("f")){
								val splitLineSpace=ln.split(" ")
								val triple=List[String](splitLineSpace(1),splitLineSpace(2),splitLineSpace(3))
								val splitLineSlash=triple.foldLeft(List[Int]())((c,batch)=>{
										val split=batch.split("/")
										c:::List(split(0).toInt-1,Try(split(1).toInt-1).getOrElse(-1),split(2).toInt-1)
								})
								val verts=getBatched(splitLineSlash(0),3,data.vertices):::getBatched(splitLineSlash(3),3,data.vertices):::getBatched(splitLineSlash(6),3,data.vertices)
								//val verts=List(data.vertices(splitLineSlash(0)),data.vertices(splitLineSlash(3)),data.vertices(splitLineSlash(6)))
								val uvs=if(splitLineSlash(1) != -1 && splitLineSlash(4) != -1 && splitLineSlash(7) != -1)
										getBatched(splitLineSlash(1),2,data.uvCoords):::getBatched(splitLineSlash(4),2,data.uvCoords):::getBatched(splitLineSlash(7),2,data.uvCoords)
								else List[Float]()
								val normals=getBatched(splitLineSlash(2),3,data.normals):::getBatched(splitLineSlash(5),3,data.normals):::getBatched(splitLineSlash(8),3,data.normals)
								c.copy(c.vertices:::verts,c.uvCoords:::uvs,normals:::normals)
						}else{
								c
						}
				}
				)
		}*/
}
