package com.upmusic.web.message;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIRequest {

	@Getter
	@Setter
	private Map<String,String> params;
	
}
