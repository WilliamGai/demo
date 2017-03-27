package com.sincetimes.website.app.lunbo;
														
import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sincetimes.website.core.spring.controller.ControllerInterface;
@RestController
@Order(value = 5)
public class MediaController implements ControllerInterface {

	@RequestMapping("/get_lunbo")
	public Object get_lunbo(@RequestParam Optional<String> lunbo_group_no) {
		if(lunbo_group_no.isPresent()){
			return MediaManager.inst().getLunboGroupVO(lunbo_group_no.get());
		}
		return MediaManager.inst().getEmptyLunboGroupVO();
	}
}
