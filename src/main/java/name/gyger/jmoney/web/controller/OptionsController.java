/*
 * Copyright 2012 Johann Gyger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.gyger.jmoney.web.controller;

import name.gyger.jmoney.service.OptionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/rest/options")
public class OptionsController {

    private static final Logger log = LoggerFactory.getLogger(OptionsController.class);

    @Autowired
    private OptionsService optionsService;

    @RequestMapping(path = "/init", method = RequestMethod.PUT)
    @ResponseBody
    public void init() {
        optionsService.init();
    }

    @RequestMapping(path = "/import", method = RequestMethod.POST)
    public String importFile(@RequestParam("file") MultipartFile file) {
        try {
            optionsService.importFile(file.getInputStream());
            return "redirect:/options.html#/options/import?success";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "redirect:/options.html#/options/import?error";
        }
    }


}
