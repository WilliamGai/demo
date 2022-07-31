package com.sonic.website.app.lunbo;
                                                        
import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sonic.website.core.spring.interfaces.AccessSupport;
@RestController
@Order(value = 5)
public class MediaController implements AccessSupport {

    @RequestMapping("/get_lunbo")
    public Object get_lunbo(@RequestParam Optional<String> lunbo_group_no) {
        if(lunbo_group_no.isPresent()){
            return MediaManager.inst().getLunboGroupVO(lunbo_group_no.get());
        }
        return MediaManager.inst().getEmptyLunboGroupVO();
    }
}
