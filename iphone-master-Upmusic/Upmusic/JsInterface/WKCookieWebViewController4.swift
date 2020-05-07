//
//  WKCookieWebViewController4.swift
//  Upmusic
//
//  Created by nough on 03/10/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import SwiftyJSON

class WKCookieWebViewController4: UIViewController, WKNavigationDelegate {
    
    static let upmusicUrl = NSURL(string: "https://upmusic.azurewebsites.net")
    let upmusicUrlNoStatic = "https://upmusic.azurewebsites.net"
    let upmusicUserAgent = "UPMusicIOS"
    
    var isFirstLoad: Bool = true
    var webView: WKWebView!
    var webConfig: WKWebViewConfiguration {
        get {
            NSLog("[webConfig]:::WKWebViewConfiguration setting...")
            NSLog("-----------------------------------------")
            let webCfg = WKWebViewConfiguration()
            let userController = WKUserContentController()
            
            let webHandler = WebHandler()
            let preferences = WKPreferences()
            preferences.javaScriptEnabled = true
            webCfg.preferences = preferences
            
            userController.add(webHandler, name: "callbackFromJS")
            //            userController.add(self, name: "ios")
            
            webCfg.applicationNameForUserAgent = upmusicUserAgent
            webCfg.userContentController = userController;
            return webCfg;
        }
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        NSLog("[viewDidLoad]:::WKCookieWebViewController4 setting...")
        NSLog("-----------------------------------------")
        setup_web_view()
        check_user_session_id_exists()
        retrieve_cookies()
        
    }
    
    // 0. init.
    private func setup_web_view() {
        
        NSLog("[setup_web_view]:::setting...")
        NSLog("-----------------------------------------")
        webView = WKWebView(frame: self.view.bounds, configuration: webConfig )
        view.addSubview(webView)
        
        let views: [String: Any] = ["webView": webView]
        
        view.addConstraints(NSLayoutConstraint.constraints(
            withVisualFormat: "H:|-0-[webView]-0-|",
            options: [],
            metrics: nil,
            views: views))
        view.addConstraints(NSLayoutConstraint.constraints(
            withVisualFormat: "V:|-0-[webView]-0-|",
            options: [],
            metrics: nil,
            views: views))
        
        
        webView.navigationDelegate = self
        
        // TODO :::::::: [JS] or [COOKIE]........
        webView.customUserAgent = upmusicUserAgent
        
    }
    
    
    // [load]
    func opensite()
    {
        
        let addr = upmusicUrlNoStatic
        let url = URL(string: addr)!
        let request = URLRequest(url: url)
        NSLog("[opensite]::webView loaded")
        NSLog("-----------------------------------------")
        webView.load(request)
    }
    
    
    // 1.
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        // dismiss indicator
        NSLog("-----------------------------------------")
        NSLog("[WKWebView]:::[1]::decidePolicyFor navigationAction")
        NSLog("-----------------------------------------")
        decisionHandler(.allow)
    }
    
    // 2.
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        
        NSLog("[WKWebView]:::[2]::didStartProvisionalNavigation")
        NSLog("-----------------------------------------")
        
        check_user_session_id_exists()
        
        let ds = WKWebsiteDataStore.default()
        let cookies = ds.httpCookieStore
        cookies.getAllCookies { (cookies: [HTTPCookie]) in
            for cookie in cookies {
                NSLog(" > [Known Cookie]: cookie.name : \(cookie.name), cookie.value : \(cookie.value)" )
            }
        }
        NSLog("-----------------------------------------")
        NSLog("[Starting to load]")
    }
    
    // 서버 리다이렉트가 발생하는 경우에만 발생하며 이 경우,
    // 다시 decidePolicyForNavigationAction delegate 호출이 발생할 수 있다.
    func webView(_ webView: WKWebView, didReceiveServerRedirectForProvisionalNavigation navigation: WKNavigation!) {
        
        NSLog("[WKWebView]:::[2-1]::didReceiveServerRedirectForProvisionalNavigation")
        NSLog("-----------------------------------------")
        
        
    }
    
    // 3.
    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        
        NSLog("[WKWebView]:::[3]::decidePolicyFor navigationResponse")
        NSLog("-----------------------------------------")
//        guard
//            let response = navigationResponse.response as? HTTPURLResponse,
//            let url = navigationResponse.response.url
//            else {
//                decisionHandler(.cancel)
//                return
//        }
//
        NSLog("[WKWebView]:::[3-1]::decidePolicyFor navigationResponse")
        
        // NOT WORKING
//        if let headerFields = response.allHeaderFields as? [String: String] {
//            let cookies = HTTPCookie.cookies(withResponseHeaderFields: headerFields, for: url)
//            cookies.forEach { (cookie) in
//                HTTPCookieStorage.shared.setCookie(cookie)
//                NSLog("cookie : \(cookie)")
//            }
//        }
        
        NSLog("[WKWebView]:::[3-2]::decidePolicyFor navigationResponse")
        NSLog("-----------------------------------------")
        decisionHandler(.allow)
    }
    
    
    
    // 4.
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        
        NSLog("[WKWebView]:::[4]::didCommit")
        NSLog("-----------------------------------------")
    }
    
    // didCommit 이후에 에러가 발생하는 경우
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        
        NSLog("[WKWebView]:::[4-1]::didFail")
        NSLog("-----------------------------------------")
        NSLog("** Error" + error.localizedDescription)
    }
    
    
    // 5.
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        NSLog("[WKWebView]:::[5]::didFinish")
        NSLog("-----------------------------------------")
        
        let ds = WKWebsiteDataStore.default()
        let cookies = ds.httpCookieStore
        cookies.getAllCookies { (cookies: [HTTPCookie]) in
            var cookieDict = [String : AnyObject]()
            for cookie in cookies {
                NSLog("[Saved cookie]: \(cookie.name)")
                cookieDict[cookie.name] = cookie.properties as AnyObject?
                
                if (cookie.name == "JSESSIONID") {
                    NSLog(" > [JSESSIONID] : \(cookie.value)")
                    UserDefaults.standard.set(cookie.value, forKey: "JSESSIONID")
//                    UserDefaults.standard.set(cookie, forKey: "JSESSIONID_COOKIE") // ERROR
                }
                
                if (cookie.name == "upmusic-remember-me-cookie") {
                    NSLog(" > [upmusic-remember-me-cookie] : \(cookie.value)")
                }
                
            }
            UserDefaults.standard.set(cookieDict, forKey: "cookiez")
            NSLog("-----------------------------------------")
        }
    }
    
    // custom [Upmusic-server]
    func check_user_session_id_exists() {
        
        NSLog("[check_user_session_id_exists]:::function called")
        NSLog("-----------------------------------------")
        
        if (isKeyPresentInUserDefaults(key: "JSESSIONID")) {
            
            let session_id = UserDefaults.standard.string(forKey: "JSESSIONID")
            let token = UserDefaults.standard.string(forKey: "TOKEN")
            
            NSLog("[isKeyPresentInUserDefaults] JSESSIONID  : \(session_id), TOKEN : \(token)")
            
            requestCheckSession(sessionID: session_id ?? "", tokenValue: token ?? "")
            
            let cookie = HTTPCookie(properties: [
                .domain: "upmusic.azurewebsites.net",
                .path: "/",
                .name: "JSESSIONID",
                .value: session_id,
                .secure: "TRUE",
                .expires: NSDate(timeIntervalSinceNow: 31556926)
                ])!
            webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
            
        } else {
            NSLog("[isKeyPresentInUserDefaults] nil...")
        }
        
        NSLog("-----------------------------------------")
        
    }
    
    func isKeyPresentInUserDefaults(key: String) -> Bool {
        return UserDefaults.standard.object(forKey: key) != nil
    }
    
    func retrieve_cookies()
    {
        // EXCEPT JSESSION COOKIES......
        NSLog("[retrieve_cookies]:::function called.")
        NSLog("-----------------------------------------")
        
        let ds = WKWebsiteDataStore.default()
        let cookies = ds.httpCookieStore
        let userDefaults = UserDefaults.standard
        
        if let cookieDict = userDefaults.dictionary(forKey: "cookiez") {
            
            var cookies_left = 0
            for (_, cookieProperties) in cookieDict {
                if let _ = HTTPCookie(properties: cookieProperties as! [HTTPCookiePropertyKey : Any] ) {
                    cookies_left += 1
                }
            }
            for (_, cookieProperties) in cookieDict {
                if let cookie = HTTPCookie(properties: cookieProperties as! [HTTPCookiePropertyKey : Any] ) {
                    
                    cookies.setCookie(cookie, completionHandler: {
                        cookies_left -= 1
                        
                        NSLog(" > [\(cookies_left)] LEFT ::::\(cookie.name)")
                        if cookies_left == 0 {
                            NSLog("[All Saved Cookies Printed]")
                            NSLog("-----------------------------------------")
                            self.opensite()
                        }
                    })
                }
            }
        } else {
            NSLog("[No Saved Cookies]...")
            NSLog("-----------------------------------------")
            opensite()
        }
    }
    
    
//    func setCookies(cookies: HTTPCookie){
//        Alamofire.SessionManager.default.session.configuration.httpCookieStorage?.setCookies([cookies], for: URL, mainDocumentURL: nil)
//    }
    
    
    func requestCheckSession(sessionID: String, tokenValue :String) {
        
        NSLog("[requestCheckSession]")
        NSLog("-----------------------------------------")
        var URL = upmusicUrlNoStatic + "/api/auth/check_session" //.toSecureHTTPSofDomainChange()
        
        let headers: HTTPHeaders = [
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        let parameters = [
            "token" : tokenValue
        ]
        
//        Alamofire.SessionManager.default.session.configuration.httpCookieStorage?
//            .setCookies([cookie], for: NSURL(string: URL) as! URL, mainDocumentURL: nil)
        
        Alamofire.request(URL, method: .post
            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            ).responseJSON { response in
                
                NSLog("[rCS] > \(response.result)")
                NSLog("[rCS] > \(response.result.value)")
                NSLog("-----------------------------------------")
                
                switch response.result {
                
                case .success(let value):
                    let json = JSON(value)
                    if (json["status"].description == "true") {
                        
                        NSLog("[TRUE] JSON: \(json["message"].description)")
                        
                        // 로컬 로그인 제외 >> json["message"].description 의 값을 세션id로 저장 ()
                        // eg. ANDROID :  cookieManager.setCookie(DEFAULT_URL, "JSESSIONID=" + apiResponse.getMessage());
                    }
                    
                    if (json["status"].description == "false") {
                        NSLog("[FALSE] JSON: \(json)")
                    }
                    
                case .failure(let error):
                    
                    print(error)
                }
        }
    }
    
}
