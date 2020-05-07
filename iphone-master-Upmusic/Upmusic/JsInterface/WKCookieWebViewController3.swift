//
//  WKCookieWebviewController3.swift
//  Upmusic
//
//  Created by nough on 01/10/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//


import UIKit
import WebKit
import Alamofire
import SwiftyJSON
import AZSClient
import GoogleSignIn

// 01. Oct. 2018 tested. >> Cookie working..
// session checking...
// function will be add..

class WKCookieWebViewController3: UIViewController, WKNavigationDelegate, WKScriptMessageHandler {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // create WKWebViewConfiguration
        let webViewConfig = WKWebViewConfiguration()
        
        let webHandler = WebHandler()
        let preferences = WKPreferences()
        preferences.javaScriptEnabled = true
        webViewConfig.preferences = preferences
        
        let userContentController = WKUserContentController();
        userContentController.add(self, name: "ios") // self working
        userContentController.add(webHandler, name: "callbackFromJS") // javascript handling.
        
        webViewConfig.userContentController = userContentController
        
        // init and load request in webview.
        webView = WKWebView(frame: self.view.frame, configuration:webViewConfig)
        webView.navigationDelegate = self
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        
        webView.customUserAgent = upmusicUserAgent // added.
        
        self.view.addSubview(webView)
        
        let request = URLRequest(url: webAddress)
        webView.load(request)
    }
    
    //
    // WKNavigationDelegate
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler:@escaping ((WKNavigationActionPolicy) -> Void)) {
        print("[decidePolicyForNavigationAction] with url:\(String(describing: navigationAction.request.url!))")
        
        if let url = navigationAction.request.url {
            // url request is outside of my website, then use safari to view address
            if webAddress.host != url.host {
                UIApplication.shared.open(url)
                decisionHandler(.cancel)
            }
        }
        decisionHandler(.allow)
    }
    
    func webView(_ webView: WKWebView,
                 decidePolicyFor navigationResponse: WKNavigationResponse,
                 decisionHandler: @escaping (WKNavigationResponsePolicy) -> Swift.Void)
    {
        
        let cookies = HTTPCookieStorage.shared.cookies
        for cookie in cookies! {
            print(cookie.description)
            print("found cookie " + cookie.name + " " + cookie.value)
        }
        decisionHandler(.allow)
    }
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
    
        print("[WKWebView] didCommit :::START:::")
        
        
        let cookies = HTTPCookieStorage.shared.cookies
        for cookie in cookies! {
            print(cookie.description)
            print("found cookie " + cookie.name + " " + cookie.value)
        }
        
        
        print("[WKWebView] didCommit :::END:::")
        
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        
        
        print("[WKWebView] didFinish :::START:::")
        
        
        let cookies = HTTPCookieStorage.shared.cookies
        for cookie in cookies! {
            print(cookie.description)
            print("found cookie " + cookie.name + " " + cookie.value)
        }
        
        print("[WKWebView] didFinish :::END:::")
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        print("Navigation error :\(error.localizedDescription)")
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        print("Loading error :\(error.localizedDescription)")
    }
    
    //
    // WKScriptMessageHandler
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        
        print("[userContentController] didReceive message::: \(message)")
    }
    
    var webView: WKWebView!
    let webAddress = URL(string:"https://upmusic.azurewebsites.net")!
    let upmusicUserAgent = "UPMusicIOS"
}
