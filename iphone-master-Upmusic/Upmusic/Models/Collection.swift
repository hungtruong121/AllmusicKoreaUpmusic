//
//  Collection.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import Foundation
import ObjectMapper

class Collection :  Mappable, Codable {
    
    required init?(map: Map) {
        
    }
    
    var createdAt: Int64?
    var id: Int64?
    var member: Member?
    var subject: String?
    var trackCnt: Int64?
    var updatedAt: Int64?
    
    func mapping(map: Map) {
        createdAt <- map["createdAt"]
        id <- map["id"]
        member <- map["member"]
        subject <- map["subject"]
        trackCnt <- map["trackCnt"]
        updatedAt <- map["updatedAt"]
    }
    
}
