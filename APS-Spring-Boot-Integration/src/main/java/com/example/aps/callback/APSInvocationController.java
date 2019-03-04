package com.example.aps.callback;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.aps.model.APSInstance;

@RestController
class APSCallbackController {


	@RequestMapping
	(method = RequestMethod.POST, value="/apsInstance/fullname")
	
	@ResponseBody
	void callBackFullName(@RequestBody CallBackInstance instance) {
		System.out.println("Return value from APS >> "+instance.getFullName());
	}

}