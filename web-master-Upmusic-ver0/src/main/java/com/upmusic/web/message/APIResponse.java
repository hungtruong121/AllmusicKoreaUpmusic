package com.upmusic.web.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse {

	@Getter
	@Setter
	private boolean status;
	
	@Getter
	@Setter
	private String message;
	
	@Getter
	@Setter
	private Object object;
	
}
