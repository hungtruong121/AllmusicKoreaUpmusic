//
//  UserVO.swift
//  yunstimeschedule
//
//  Created by cheche on 2018. 4. 23..
//  Copyright © 2018년 thecsy. All rights reserved.
//

import Foundation

class UserVO: BaseVO {
    
    var status: String!
    var message: String!
    
    init(_ data: [String : Any]) {
        super.init()
        status = setValueString(data, "status")
        message = setValueString(data, "message")
    }
}

