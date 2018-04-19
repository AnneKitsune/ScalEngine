package net.supercraft.scalengine.assetloading

import java.io.{File, FileInputStream}
import java.nio.ByteBuffer

import de.matthiasmann.twl.utils.PNGDecoder
import de.matthiasmann.twl.utils.PNGDecoder.Format

/**
  * Created by jojolepro on 11/9/16.
  */
object ImageLoader {
		def loadPNG(file:File)={
				val inputStream=new FileInputStream(file)
				val decoder = new PNGDecoder(inputStream)
				val buffer = ByteBuffer.allocateDirect(4*decoder.getWidth*decoder.getHeight)
				decoder.decode(buffer,decoder.getWidth*4,Format.RGBA)
				buffer.flip()
				new Texture(buffer,decoder.getWidth,decoder.getHeight)
		}
}
