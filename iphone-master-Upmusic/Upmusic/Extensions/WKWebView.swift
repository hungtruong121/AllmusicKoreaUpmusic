//
//  WKWebView.swift
//  Upmusic
//
//  Created by nough on 13/09/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//


import UIKit
import WebKit

extension WKWebView {
    
    private var httpCookieStore: WKHTTPCookieStore  {
        return WKWebsiteDataStore.default().httpCookieStore
    }
    
    func loadUrl(string: String) {
        if let url = URL(string: string) {
            if self.url?.host == url.host {
                self.reload()
            } else {
                load(URLRequest(url: url))
            }
        }
    }
    
    func load(_ request: URLRequest, with cookies: [HTTPCookie]) {
        var request = request
        let headers = HTTPCookie.requestHeaderFields(with: cookies)
        for (name, value) in headers {
            request.addValue(value, forHTTPHeaderField: name)
        }
        
        load(request)
    }
    
    func getCookies(for domain: String? = nil, completion: @escaping ([String : Any])->())  {
        var cookieDict = [String : AnyObject]()
        httpCookieStore.getAllCookies { (cookies) in
            for cookie in cookies {
                if let domain = domain {
                    if cookie.domain.contains(domain) {
                        cookieDict[cookie.name] = cookie.properties as AnyObject?
                    }
                } else {
                    cookieDict[cookie.name] = cookie.properties as AnyObject?
                }
            }
            completion(cookieDict)
        }
    }
}
