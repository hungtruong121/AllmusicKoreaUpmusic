//
//  String.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import Foundation

extension String {
    func toSecureHTTPS() -> String {
        return self.contains("https") ? self : self.replacingOccurrences(of: "http", with: "https")
    }
    
    func toSecureHTTPSofDomainChange() -> String {
        return self.contains("https://d1if7lu34hzu4p.cloudfront.net") ?
            self : self.replacingOccurrences(of: "http://upm-resources.s3-website.ap-northeast-2.amazonaws.com", with: "https://d1if7lu34hzu4p.cloudfront.net")
    }
    
    func toSecureHTTPSofDomainChangeForStreaming() -> String {
        return self.contains("https://s2tcs850o1mxx4.cloudfront.net") ?
            self : self.replacingOccurrences(of: "http://upm-resources.s3-website.ap-northeast-2.amazonaws.com", with: "https://s2tcs850o1mxx4.cloudfront.net")
    }
    
    
    func toGettingOnlyKeyName() -> String {
        return self.contains("http://s3.ap-northeast-2.amazonaws.com/upm-albums/") ?
            self.replacingOccurrences(of: "http://s3.ap-northeast-2.amazonaws.com/upm-albums/", with: "") : self
        
    }
    
    func replace(target: String, withString: String) -> String {
        return self.replacingOccurrences(of: target, with: withString, options: NSString.CompareOptions.literal, range: nil)
    }
    
}
