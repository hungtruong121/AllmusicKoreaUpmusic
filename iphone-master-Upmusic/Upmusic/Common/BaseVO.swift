//
//  BaseVO.swift
//  yunstimeschedule
//
//  Created by cheche on 2018. 4. 23..
//  Copyright © 2018년 thecsy. All rights reserved.
//

import Foundation

class BaseVO {
    func setValueString(_ data: [String : Any], _ key:String) -> String{
        if let item = data[key] as? String {
            return item
        } else {
            return ""
        }
    }
    
    func setValueInt(_ data: [String : Any], _ key:String) -> Int{
        if data[key] != nil {
            return data[key] as! Int
        } else {
            return 0
        }
    }
}

