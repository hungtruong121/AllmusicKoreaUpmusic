//
//  HTTPCookieStorage.swift
//  Upmusic
//
//  Created by nough on 13/09/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

// [USAGE]
//HTTPCookieStorage.restore()//앱실행시 호출하여 기존 쿠키를 불러온다.
//HTTPCookieStorage.save() //쿠키를 얻고 난후 호출하여 쿠키를 저장한다.
//HTTPCookieStorage.clear() //로그아웃이나 세션 만료시 저장된 쿠키를 삭제한다.(사이트주소가 바뀐다거나 하면서 쿠키가 누적될수 있다.)
extension HTTPCookieStorage {
    static func clear(){
        if let cookies = HTTPCookieStorage.shared.cookies {
            for cookie in cookies {
                HTTPCookieStorage.shared.deleteCookie(cookie)
            }
        }
    }
    static func save(){
        var cookies = [Any]()
        if let newCookies = HTTPCookieStorage.shared.cookies {
            for newCookie in newCookies {
                var cookie = [HTTPCookiePropertyKey : Any]()
                cookie[.name] = newCookie.name
                cookie[.value] = newCookie.value
                cookie[.domain] = newCookie.domain
                cookie[.path] = newCookie.path
                cookie[.version] = newCookie.version
                if let date = newCookie.expiresDate {
                    cookie[.expires] = date
                }
                cookies.append(cookie)
            }
            UserDefaults.standard.setValue(cookies, forKey: "cookies")
            UserDefaults.standard.synchronize()
        }
        
    }
    static func restore(){
        if let cookies = UserDefaults.standard.value(forKey: "cookies") as? [[HTTPCookiePropertyKey : Any]] {
            for cookie in cookies {
                if let oldCookie = HTTPCookie(properties: cookie) {
                    print("[FROM EXTENSION FOLDER] cookie loaded:\(oldCookie)")
                    HTTPCookieStorage.shared.setCookie(oldCookie)
                }
            }
        }
        
    }
    
    
}
