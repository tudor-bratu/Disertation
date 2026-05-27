package stud.bratutudor.disertationbe.mercury

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/test")
class MercuryTestController {

    @GetMapping("/edit")
    fun editTest() {
        //TODO
    }
}