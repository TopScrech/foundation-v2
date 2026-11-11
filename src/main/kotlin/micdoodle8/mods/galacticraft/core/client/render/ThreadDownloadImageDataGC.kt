package micdoodle8.mods.galacticraft.core.client.render

import net.minecraft.client.renderer.IImageBuffer
import net.minecraft.client.renderer.texture.SimpleTexture
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.resources.IResourceManager
import net.minecraft.resources.Identifier as ResourceLocation
import foundation.logging.FoundationLog as FMLLog
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO

@OnlyIn(Dist.CLIENT)
open class ThreadDownloadImageDataGC constructor(
    private val field_152434_e: File?,
    private val imageUrl: String,
    p_i1049_3_: ResourceLocation,
    private val imageBuffer: IImageBuffer?
) : SimpleTexture(p_i1049_3_) {
    private var bufferedImage: BufferedImage? = null
    private var imageThread: Thread? = null
    private var textureUploaded = false

    private fun checkTextureUploaded() {
        if (!this.textureUploaded) {
            if (this.bufferedImage != null) {
                if (this.textureLocation != null) {
                    this.deleteGlTexture()
                }

                TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage)
                this.textureUploaded = true
            }
        }
    }

    override fun getGlTextureId(): Int {
        this.checkTextureUploaded()
        return super.getGlTextureId()
    }

    open fun setBufferedImage(p_147641_1_: BufferedImage?) {
        this.bufferedImage = p_147641_1_
    }

    override fun loadTexture(p_110551_1_: IResourceManager) {
        try {
            if (this.bufferedImage == null && this.textureLocation != null) {
                super.loadTexture(p_110551_1_)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (this.imageThread == null) {
            if (this.field_152434_e != null && this.field_152434_e.isFile()) {
                FMLLog.fine("Loading http texture from local cache (%s)", this.field_152434_e)

                try {
                    this.bufferedImage = ImageIO.read(this.field_152434_e)

                    if (this.imageBuffer != null) {
                        this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage))
                    }
                } catch (ioexception: IOException) {
                    logger.error("Couldn\'t load skin " + this.field_152434_e, ioexception)
                    this.func_152433_a()
                }
            } else {
                this.func_152433_a()
            }
        }
    }

    protected open fun func_152433_a() {
        this.imageThread = object : Thread("Texture Downloader #" + threadDownloadCounter.incrementAndGet()) {
            override fun run() {
                var httpurlconnection: HttpURLConnection? = null
                FMLLog.fine(
                    "Downloading http texture from %s to %s",
                    this@ThreadDownloadImageDataGC.imageUrl,
                    this@ThreadDownloadImageDataGC.field_152434_e
                )

                try {
                    httpurlconnection =
                        (URL(this@ThreadDownloadImageDataGC.imageUrl)).openConnection() as HttpURLConnection?
                    httpurlconnection!!.setDoInput(true)
                    httpurlconnection.setDoOutput(false)
                    httpurlconnection.connect()

                    if (httpurlconnection.getResponseCode() / 100 == 2) {
                        var bufferedimage: BufferedImage

                        if (this@ThreadDownloadImageDataGC.field_152434_e != null) {
                            FileUtils.copyInputStreamToFile(
                                httpurlconnection.getInputStream(),
                                this@ThreadDownloadImageDataGC.field_152434_e
                            )
                            bufferedimage = ImageIO.read(this@ThreadDownloadImageDataGC.field_152434_e)
                        } else {
                            bufferedimage = ImageIO.read(httpurlconnection.getInputStream())
                        }

                        if (this@ThreadDownloadImageDataGC.imageBuffer != null) {
                            bufferedimage = this@ThreadDownloadImageDataGC.imageBuffer.parseUserSkin(bufferedimage)
                        }

                        this@ThreadDownloadImageDataGC.setBufferedImage(bufferedimage)
                        return
                    }
                } catch (exception: Exception) {
                    logger.error("Couldn\'t download http texture", exception)
                    return
                } finally {
                    if (httpurlconnection != null) {
                        httpurlconnection.disconnect()
                    }
                }
            }
        }
        this.imageThread!!.setDaemon(true)
        this.imageThread!!.start()
    }

    companion object {
        private val logger: Logger = LogManager.getLogger()
        private val threadDownloadCounter = AtomicInteger(0)
    }
}
