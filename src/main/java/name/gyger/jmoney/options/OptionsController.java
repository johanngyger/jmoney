package name.gyger.jmoney.options;

import name.gyger.jmoney.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/rest/options")
public class OptionsController {

    private static final Logger log = LoggerFactory.getLogger(OptionsController.class);

    private final OptionsService optionsService;

    private final SessionService sessionService;

    public OptionsController(OptionsService optionsService, SessionService sessionService) {
        this.optionsService = optionsService;
        this.sessionService = sessionService;
    }

    @RequestMapping(path = "/init", method = RequestMethod.PUT)
    @ResponseBody
    public void init() {
        sessionService.initSession();
    }

    @RequestMapping(path = "/import", method = RequestMethod.POST)
    @ResponseBody
    public void importFile(@RequestParam("file") MultipartFile file) throws IOException {
        optionsService.importFile(file.getInputStream());
    }

}
