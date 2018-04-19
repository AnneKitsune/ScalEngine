package net.supercraft.scalengine.assetloading

import java.io.File

import scala.io.Source
import scala.util.Try

/**
  * Created by jojolepro on 11/9/16.
  */
object FileReaderUtil {
		def readFloatArray(file:File)=Source.fromFile(file).mkString.replaceAll("\t","").split(",").flatMap(maybeFloat=>Try(maybeFloat.toFloat).toOption)
}
