@Grab(group='org.imgscalr', module='imgscalr-lib', version='4.2', initClass=false)
​
import org.craftercms.engine.service.context.SiteContext
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import org.imgscalr.Scalr
​
try {
    def host = request.getHeader("X-FORWARD") ? request.getHeader("X-FORWARD") : "${request.getScheme()}://${request.getServerName()}:${request.getServerPort()}"
    def site = params.crafterSite ? params.crafterSite : SiteContext.getCurrent().siteName
    def size = (params.s) ? Integer.parseInt(params.s) : null
    def path = params.path
    def imgExt = path.substring(path.lastIndexOf(".")+1)
​
    if(size && path) {
        def assetUrl = "${host}${path}?crafterSite=${site}"
        logger.info("Creaing dynamic asset for ${assetUrl}")
        def url = new URL(assetUrl)
        def connection = url.openConnection()
​
        connection.setRequestMethod("GET")
        connection.connect()
​
        if (connection.responseCode == 200 || connection.responseCode == 201) {
            def imageStream = connection.getInputStream()
            def imgSrc = ImageIO.read(imageStream)
​
​
            def rendition = Scalr.resize(imgSrc, size)
​
            response.setHeader("Content-Type", "image/${imgExt}")
            ImageIO.write(rendition, imgExt, response.getOutputStream())
            return
        }
        else {
            response.setStatus(404)
            return "Resource not found"
        }
    }
    else {
        response.setStatus(404)
        return "Resource not found"
    }
}
catch(err) {
    println err
    logger.error("Unable to rendition asset with input ${params}")
    response.setStatus(500)
}
