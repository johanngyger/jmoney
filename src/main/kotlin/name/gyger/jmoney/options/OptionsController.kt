package name.gyger.jmoney.options

import name.gyger.jmoney.session.SessionService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("/api/options")
class OptionsController(private val optionsService: OptionsService, private val sessionService: SessionService) {

    @PutMapping("/init")
    @ResponseBody
    fun init() {
        sessionService.initSession()
    }

    @PostMapping("/import")
    @ResponseBody
    fun importFile(@RequestParam("file") file: MultipartFile) {
        optionsService.importFile(file.inputStream)
    }

}
