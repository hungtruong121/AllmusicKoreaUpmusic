//
//  cookieWebViewController.swift
//  Upmusic
//
//  Created by nough on 17/09/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import SwiftyJSON
import AZSClient
import GoogleSignIn


class WKCookieWebViewController2: BaseViewController, GIDSignInUIDelegate
     , WKScriptMessageHandler , WKUIDelegate , WKNavigationDelegate
{
    
    //    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
    //        print("\(message)")
    //    }
    //
    // start [ IMPORTED, UPMUSIC(APP) ]
    static let upmusicUrl = NSURL(string: "https://upmusic.azurewebsites.net")
    let upmusicUrlNoStatic = "https://upmusic.azurewebsites.net"
    let upmusicUserAgent = "UPMusicIOS"
    fileprivate let cellId = "cellId"
    
    var musicTracks : [MusicTrack] = []
    var clickedIndex : Int!
    var isFirstLoad : Bool! = true
    var activityIndicator = UIActivityIndicatorView()
    public var mainUrl: String = ""
    
    var pushUrl: String?
    var host = Def.Main
    let userDefaults = UserDefaults.standard
    
    var webView:WKWebView!
    var loadingView: UIView!
    var createWebView: WKWebView?
    
    // end [ IMPORTED, UPMUSIC(APP) ]
    
    // start [ AZURE ]
    // MARK : [FOR Azure Storage]
    
    /*
     
     음원 리소스의 경우 접근 허용 문자열이 필요한데 - 아마 모바일도 동일하게 동작할 것으로 보고 - SDK에서
     DefaultEndpointsProtocol=https;AccountName=upmalbum;AccountKey=Nc9PdgAOAaKP6QmSndhNjuqWhqltwrTbisCMxq/QCiZ5XRPfSN0sKAuCk+i4f6EkcrV8mPgdjQs/n0fArAvkoA==;EndpointSuffix=core.windows.net
     를 이용하여 권한을 취득(CloudStorageAccount)한 후 컨테이너명("upm-container-album")을 이용하여 음원을 가져오는 작업이 필요할 것입니다.
     
     영상 등 일반 공개리소스의 저장소 url은,
     https://upmresource.blob.core.windows.net/upm-container-resource 이하입니다.
     음원 리소스의 저장소 url은,
     https://upmalbum.blob.core.windows.net/upm-container-album 입니다.
     
     */
    
    let publicResourcesUrl = "https://upmresource.blob.core.windows.net/upm-container-resource"; // also Video
    let musicResourcesUrl = "https://upmalbum.blob.core.windows.net/upm-container-album";
    // MARK: Authentication
    
    // If using a SAS token, fill it in here.  If using Shared Key access, comment out the following line.
    var gereralResourcesContainerURL = "https://upmresource.blob.core.windows.net/upm-container-resource"
    var musicResourcesContainerURL = "https://upmalbum.blob.core.windows.net/upm-container-album"
    var usingSAS = true
    
    // If using Shared Key access, fill in your credentials here and un-comment the "UsingSAS" line:
    var connectionString = "DefaultEndpointsProtocol=https;AccountName=upmalbum;AccountKey=Nc9PdgAOAaKP6QmSndhNjuqWhqltwrTbisCMxq/QCiZ5XRPfSN0sKAuCk+i4f6EkcrV8mPgdjQs/n0fArAvkoA==;EndpointSuffix=core.windows.net"
    var gereralResourcesContainerName = "upm-container-resource"
    var musicResourcesContainerName = "upm-container-album"
    // var usingSAS = false
    
    // MARK: Properties
    
    var blobs = [AZSCloudBlob]()
    var container : AZSCloudBlobContainer!
    var continuationToken : AZSContinuationToken?
    // end [ AZURE ]
    
    // added
    
    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo,
                 completionHandler: @escaping (Bool) -> Void) {
        let alertController = UIAlertController(title: "", message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: "확인", style: .default, handler: { (action) in
            completionHandler(true)
        }))
        alertController.addAction(UIAlertAction(title: "취소", style: .default, handler: { (action) in
            completionHandler(false)
        }))
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    func webView(_ webView: WKWebView, runJavaScriptTextInputPanelWithPrompt prompt: String, defaultText: String?, initiatedByFrame frame: WKFrameInfo,
                 completionHandler: @escaping (String?) -> Void) {
        let alertController = UIAlertController(title: "", message: prompt, preferredStyle: .alert)
        alertController.addTextField { (textField) in
            textField.text = defaultText
        }
        alertController.addAction(UIAlertAction(title: "확인", style: .default, handler: { (action) in
            if let text = alertController.textFields?.first?.text {
                completionHandler(text)
            } else {
                completionHandler(defaultText)
            }
        }))
        
        alertController.addAction(UIAlertAction(title: "취소", style: .default, handler: { (action) in
            completionHandler(nil)
        }))
        
        self.present(alertController, animated: true, completion: nil)
    }
    public func webViewWebContentProcessDidTerminate(_ webView: WKWebView) {
        // 중복적으로 리로드가 일어나지 않도록 처리 필요.
        webView.reload()
    }
    
    // end of added
    
    
    
    var webConfig: WKWebViewConfiguration {
        get {
            print("[webConfig]")
            let webCfg = WKWebViewConfiguration()
            
            let userController = WKUserContentController()
            
            let webHandler = WebHandler()
            let preferences = WKPreferences()
            preferences.javaScriptEnabled = true
            webCfg.preferences = preferences
            
            userController.add(webHandler, name: "callbackFromJS")
            userController.add(self, name: "ios")
            
            
            webCfg.userContentController = userController;
            return webCfg;
        }
    }
    
    // Stop the UIActivityIndicatorView animation that was started when the user
    // pressed the Sign In button
    func signInWillDispatch(signIn: GIDSignIn!, error: NSError!) {
        activityIndicator.stopAnimating()
    }
    
    // Present a view that prompts the user to sign in with Google
    func signIn(signIn: GIDSignIn!,
                presentViewController viewController: UIViewController!) {
        self.present(viewController, animated: true, completion: nil)
    }
    
    // Dismiss the "Sign in with Google" view
    func signIn(signIn: GIDSignIn!,
                dismissViewController viewController: UIViewController!) {
        self.dismiss(animated: true, completion: nil)
    }
    
    
    @objc func moveUrl(_ userInfo: Notification) {
        
        print("==================== [start of moveUrl] =====================")
        
        pushUrl = ""
        pushUrl = userDefaults.string(forKey: "url")!
        userDefaults.set("", forKey: "url")
        
        webView = initJavascriptBridge()
        print("==================== [end of moveUrl] =====================")
    }
    
    func movePushUrl(_ url: String) {
        
        pushUrl = url
        
        userDefaults.set("", forKey: "url")
        
        webView = initJavascriptBridge()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.moveUrl), name: NSNotification.Name(rawValue: "movePushUrl"), object: nil)
        
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: "movePushUrl"), object: nil)
    }
    
    
    // NOT RECIEVE...
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        
        print("==================== [start of didReceive] =====================")
        guard let body = message.body as? String else {
            
            print("=========================================")
            return
        }
    
        print("=========================================")
        
//        if(body == "getToken"){
        
            self.webView.evaluateJavaScript("document.cookie") { (object, error) in
                
                if let cookies: String = object as? String {
                    
                    print("=========================================")
                    
                    let split_space:[String] = cookies.components(separatedBy: " ")
                    
                    for i in split_space{
                        
                        let cookie = i.components(separatedBy: "=")
                        if(cookie[0] == "JSESSION"){
                            let id = cookie[1].replacingOccurrences(of: ";", with: "")
                            print("================   SAVE USER INFO    =========================")
                            self.userDefaults.setValue(id, forKey: Def.JSESSION)
                            print("================ - - - USER - - - - =========================")
                            return
                        }
                        
                    }
                    
                }
                
            }
            
            
            var JSESSION = ""
            
            if(self.userDefaults.string(forKey: Def.JSESSION) != nil){
                JSESSION = self.userDefaults.string(forKey: Def.JSESSION)!
            }
            
            
            
            var userID = ""
            
            if(self.userDefaults.string(forKey: Def.SIRMID) != nil){
                userID = self.userDefaults.string(forKey: Def.SIRMID)!
            }
            
            var token = ""
            
            if(self.userDefaults.string(forKey: Def.gcm_Token) != nil){
                token = self.userDefaults.string(forKey: Def.gcm_Token)!
            }
            
            var sendvalue = "\(token)&&\(userID)"
            
            if(!token.isEmpty && !userID.isEmpty){
                
                self.sendToken(forWebView: self.webView, token: sendvalue, completion:{_ in
                    print("SEND completion :: \(sendvalue)")
                })
                
            }
            
//        }else if(body == "logout"){
//
//            resetHistory()
//
//        }
        
        
        print("==================== [end of didReceive] =====================")
    }
    
    
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        
        let alertController = UIAlertController(title: nil, message: message, preferredStyle: .actionSheet)
        
        alertController.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action) in
            completionHandler()
        }))
        
        self.present(alertController, animated: true, completion: nil)
        
    }
    
    
    func initJavascriptBridge()->WKWebView{
        
        print("==================== [start of initJavascriptBridge] =====================")
//        let contentController = WKUserContentController();
//        contentController.add(self,name: "ios");
//        config.userContentController = contentController;
//        contentController.add(self, name: "callbackFromHandler")
        
        let config = webConfig
        let webView:WKWebView = WKWebView(frame:self.view.frame, configuration:config);
        
        var url:NSURL?
        
        url = NSURL(string:host);
//        if(pushUrl == ""){
//            url = NSURL(string:host);
//        }else{
//            url = NSURL(string:self.pushUrl!);
//        }
        print(url)
        webView.translatesAutoresizingMaskIntoConstraints = false
        webView.customUserAgent = self.upmusicUserAgent
        
        webView.uiDelegate = self
        webView.navigationDelegate = self
        
        URLCache.shared.removeAllCachedResponses()
        URLCache.shared.diskCapacity = 0
        URLCache.shared.memoryCapacity = 0
        
        let request = NSMutableURLRequest(url: url! as URL,
                                          cachePolicy: NSURLRequest.CachePolicy.reloadIgnoringCacheData,
                                          timeoutInterval: 30.0)
        
        webView.load(request as URLRequest);
        webView.autoresizingMask = [UIViewAutoresizing.flexibleHeight , UIViewAutoresizing.flexibleWidth]
        webView.sizeToFit()
        
        self.view.addSubview(webView);
        
        print("==================== [end of initJavascriptBridge] =====================")
        return webView;
    }
    
    func sendToken (forWebView webView: WKWebView, token: String, completion: @escaping (_ titleString: String?) -> Void) {
        
        webView.evaluateJavaScript("reciveiOS('\(token)')", completionHandler: { (innerHTML, error ) in
            
            completion(innerHTML as? String)
        })
        
    }
    
    func resetHistory() {
        
        print("==================== [start of resetHistory] =====================")
        
        HTTPCookieStorage.shared.removeCookies(since: Date.distantPast)
        
        WKWebsiteDataStore.default().fetchDataRecords(ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()) { records in
            records.forEach { record in
                WKWebsiteDataStore.default().removeData(ofTypes: record.dataTypes, for: [record], completionHandler: {})
                
            }
        }
        
        print("==================== [end of resetHistory] =====================")
    }
    
    
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        
        print("[WKWebView] didCommit :::START:::")
        
        
        print("[1] ===============================")
        let cookies = HTTPCookieStorage.shared.cookies
        for cookie in cookies! {
            print(cookie.description)
            print("found cookie " + cookie.name + " " + cookie.value)
        }
        
        print("[2] ===============================")
        webView.getCookies() { data in
            print("[-----------------------------]")
            print("\(webView.url?.absoluteString)")
            print(data)
            print("[-----------------------------]")
        }
        
        print("[WKWebView] didCommit :::END:::")
        
    }
    
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        
        print("[WKWebView] didFinish :::START:::")
        if(!activityIndicator.isHidden){
            activityIndicator.isHidden = true
        }
        
        // get all cookies
        webView.getCookies() { data in
            print("[-----------------------------]")
            print("\(webView.url?.absoluteString)")
            print(data)
            print("[-----------------------------]")
        }
        
        
        print("[WKWebView] didFinish :::END:::")
        
        
        
        
    }
    
    
    // not yet TEST
    func addUserScript(cookies: [HTTPCookie], userContentController: WKUserContentController) {
        for cookie in cookies {
            
            // Domain & cookie change (TRY)
            let cookieString = "document.cookie='JSESSION=TEST;path=/;domain=.test.co.kr;'"
            let cookieScript = WKUserScript(source: cookieString, injectionTime: .atDocumentStart, forMainFrameOnly: false)
            userContentController.addUserScript(cookieScript)
        }
    }
    
    
    
    
    
    private var httpCookieStore: WKHTTPCookieStore  {
        return WKWebsiteDataStore.default().httpCookieStore
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
    
    
    
    func webViewDidClose(_ webView: WKWebView) {
        
        print("==================== [start of webViewDidClose] =====================")
        if webView == createWebView {
            
            self.endLoading()
            createWebView?.removeFromSuperview()
            createWebView = nil
        }
        
        print("==================== [end of webViewDidClose] =====================")
    }
    
    func createWebView(config:WKWebViewConfiguration)->WKWebView{
        
        print("==================== [start of createWebView] =====================")
        config.allowsInlineMediaPlayback = true
        config.requiresUserActionForMediaPlayback = false
        
        let webView:WKWebView = WKWebView(frame:self.view.frame, configuration:config);
        webView.customUserAgent = self.upmusicUserAgent
        
        webView.uiDelegate = self
        webView.navigationDelegate = self
        
        webView.autoresizingMask = [UIViewAutoresizing.flexibleHeight , UIViewAutoresizing.flexibleWidth]
        webView.sizeToFit()
        
        self.view.addSubview(webView);
        
        print("==================== [end of createWebView] =====================")
        return webView;
    }
    
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        
        
        print("==================== [start of navigationAction] =====================")
        print()
        
        if navigationAction.targetFrame == nil {
            createWebView = createWebView(config: configuration)
            return createWebView
        }
        
        webView.load(navigationAction.request)
        print("==================== [end of navigationAction] =====================")
        return nil
        
    }
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        // show indicator
        
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        
        print("==================== [start of decidePolicyFor navigationResponse] =====================")
        
        
        guard
            let response = navigationResponse.response as? HTTPURLResponse,
            let url = navigationResponse.response.url
            else {
                decisionHandler(.cancel)
                return
        }
        
        if let headerFields = response.allHeaderFields as? [String: String] {
            let cookies = HTTPCookie.cookies(withResponseHeaderFields: headerFields, for: url)
            cookies.forEach { (cookie) in
                HTTPCookieStorage.shared.setCookie(cookie)
                print(cookie)
            }
        }
//
//        printCookie()
        
        print("==================== [end of decidePolicyFor navigationResponse] =====================")
        decisionHandler(.allow)
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        // dismiss indicato
        decisionHandler(.allow)
    }

    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("==================== [start of viewDidLoad] =====================")
        self.isFirstLoad = true
        
        
//        let manager = Manager(configuration: configuration)
//
//        manager.request(.GET, "http://example.com").response { x in
//            print(NSHTTPCookieStorage.sharedHTTPCookieStorage().cookiesForURL(NSURL(string: "http://example.com")!))
//        }
//
        print("viewDidLoad 1)")
        
        let configuration = URLSessionConfiguration.default
        configuration.httpCookieStorage = HTTPCookieStorage.shared
        
        print("viewDidLoad 2)")
//
//        // COOKIE SEARCH AS INDEX #
//        if (HTTPCookieStorage.shared.cookies(for: NSURL(string: upmusicUrlNoStatic)! as URL)?.count != 0) {
//            let sessionid = HTTPCookieStorage.shared.cookies(for: NSURL(string: upmusicUrlNoStatic)! as URL)?[0].value
//            print("cookies 0 ::: \(sessionid)")
//        }
//
        for cookie in HTTPCookieStorage.shared.cookies! {
            
            if (cookie.name == "upmusic-remember-me-cookie") {
                print("cookie.name::\(cookie.name), cookie.value::\(cookie.value), cookie.version::\(cookie.version)")
            }
            
        }
        
        
        print("viewDidLoad 3)")
        
        setupWebView()
        initAzureSetting()
        
        print("viewDidLoad 4)")
        
        printCookie()
        webView.load(URLRequest(url: URL(string: upmusicUrlNoStatic)!))
        
        
        print("==================== [end of viewDidLoad] =====================")
    }
    
    func setCookie(key: String, value: AnyObject) {
        let URL = upmusicUrlNoStatic
        let ExpTime = TimeInterval(60 * 60 * 24 * 365)
        
        let cookieProps: [HTTPCookiePropertyKey : Any] = [
            HTTPCookiePropertyKey.domain: URL,
            HTTPCookiePropertyKey.path: "/",
            HTTPCookiePropertyKey.name: key,
            HTTPCookiePropertyKey.value: value,
            HTTPCookiePropertyKey.secure: "TRUE",
            HTTPCookiePropertyKey.expires: NSDate(timeIntervalSinceNow: ExpTime)
        ]
        
        if let cookie = HTTPCookie(properties: cookieProps) {
            HTTPCookieStorage.shared.setCookie(cookie)
        }
    }
    
    
//
//    @objc func appMovedToBackground() {
//
//        let authView = (storyboard?.instantiateViewController(withIdentifier: "ViewController")) as! ViewController
//        authView.isDisplayingApp = true
//        self.present(authView, animated: true, completion: nil)
//
//    }
//
    // MARK: - Private
    private func setupWebView() {
//
//        let notificationCenter = NotificationCenter.default
//        notificationCenter.addObserver(self, selector: #selector(appMovedToBackground), name: Notification.Name.UIApplicationDidEnterBackground, object: nil)
//
//        resetHistory()
        
        webView = WKWebView()
        
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
        webView.contentMode = .scaleToFill
        
        webView = initJavascriptBridge();
        
        
        activityIndicator = UIActivityIndicatorView()
        activityIndicator.center = self.view.center
//        activityIndicator.hidesWhenStopped = true
        activityIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyle.gray
        
        self.view.bringSubview(toFront: activityIndicator)
        
        self.webView!.uiDelegate = self
    }
    
    private func printCookie() {
        print("=====================Cookies=====================")
        HTTPCookieStorage.shared.cookies?.forEach {
            print($0)
        }
        print("=================================================")
    }
    
    // start [ IMPORTED, UPMUSIC(APP) ]
    
    func googleSignin() {
        
        GIDSignIn.sharedInstance().uiDelegate = self
        GIDSignIn.sharedInstance().signIn()
    }
    
    fileprivate func fetchUserOwnTracksPeek() {
        
        let URL = (upmusicUrlNoStatic + "/api/player/playlist") //.toSecureHTTPSofDomainChange()
        Alamofire.request(URL, method: .post).responseJSON { response in
            //            print("Request: \(String(describing: response.request))")   // original url request
            //            print("Response: \(String(describing: response.response))") // http url response
            //            print("Result: \(response.result)")                         // response serialization result
            
            switch response.result {
            case .success(let value):
                let json = JSON(value)
                //                print("JSON: \(json)")
                //                print("JSON: \(json["object"].array)")
                print("JSON: \(json["object"].array!.count)")
                
                //                self.musicTracks = json["object"].arrayObject as! [MusicTrack]
                
                self.musicTracks.removeAll()
                for index in 0...json["object"].array!.count-1 {
                    //                    print("OBJECT \(index) :::: \(json["object"].array![index].description)")
                    let track = MusicTrack(JSONString: json["object"].array![index].description)
                    //                    print("TRACK \(index) :::: \(track?.artistNick)")
                    self.musicTracks.append(track!)
                }
                
                // load music tracks & play
                
                let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
                let track = self.musicTracks[0]
                
                mainController?.peekPlayerDetails(track: track, playlistTracks: self.musicTracks, noPlay: true)
                
                mainController?.mediaPlayerDetailsView.originPlaylist = self.musicTracks
                mainController?.mediaPlayerDetailsView.playlistTableView.dataSource = self as UITableViewDataSource
                mainController?.mediaPlayerDetailsView.playlistTableView.delegate = self as UITableViewDelegate
                mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
                
                
            case .failure(let error):
                print(error)
            }
            
        }
        
    }
    
    fileprivate func fetchUserOwnTracks() {
        
        let URL = (upmusicUrlNoStatic + "/api/player/playlist") //.toSecureHTTPSofDomainChange()
        Alamofire.request(URL, method: .post).responseJSON { response in
            //            print("Request: \(String(describing: response.request))")   // original url request
            //            print("Response: \(String(describing: response.response))") // http url response
            //            print("Result: \(response.result)")                         // response serialization result
            
            switch response.result {
            case .success(let value):
                let json = JSON(value)
                //                print("JSON: \(json)")
                //                print("JSON: \(json["object"].array)")
                print("JSON: \(json["object"].array!.count)")
                
                //                self.musicTracks = json["object"].arrayObject as! [MusicTrack]
                
                self.musicTracks.removeAll()
                for index in 0...json["object"].array!.count-1 {
                    //                    print("OBJECT \(index) :::: \(json["object"].array![index].description)")
                    let track = MusicTrack(JSONString: json["object"].array![index].description)
                    //                    print("TRACK \(index) :::: \(track?.artistNick)")
                    self.musicTracks.append(track!)
                }
                
                // load music tracks & play
                
                
                let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
                let track = self.musicTracks[0]
                
                if (self.isFirstLoad == true) {
                    
                    mainController?.peekPlayerDetails(track: track, playlistTracks: self.musicTracks)
                    
                    mainController?.mediaPlayerDetailsView.originPlaylist = self.musicTracks
                    mainController?.mediaPlayerDetailsView.playlistTableView.dataSource = self as UITableViewDataSource
                    mainController?.mediaPlayerDetailsView.playlistTableView.delegate = self as UITableViewDelegate
                    mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
                    
                } else {
                    
                    mainController?.maximizePlayerDetails(track: track, playlistTracks: self.musicTracks)
                    
                    mainController?.mediaPlayerDetailsView.originPlaylist = self.musicTracks
                    mainController?.mediaPlayerDetailsView.playlistTableView.dataSource = self as UITableViewDataSource
                    mainController?.mediaPlayerDetailsView.playlistTableView.delegate = self as UITableViewDelegate
                    mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
                }
                
                
            case .failure(let error):
                print(error)
            }
            
        }
        
    }
    
    fileprivate func fetchAllTracks() {
        
        print("[fetchAllTracks]")
        let URL = (upmusicUrlNoStatic+"/api/track")//.toSecureHTTPSofDomainChange()
        Alamofire.request(URL, method: .post).responseArray { (response: DataResponse<[MusicTrack]>) in
            
            print("[RESULT] \(response)")
            
            let dataArray = response.result.value
            self.musicTracks = dataArray!
            
            let track = self.musicTracks[0]
            let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
            //            mainController?.maximizePlayerDetails(track: track, playlistTracks: self.musicTracks)
            mainController?.peekPlayerDetails(track: track, playlistTracks: self.musicTracks)
            
            mainController?.mediaPlayerDetailsView.originPlaylist = self.musicTracks
            
            mainController?.mediaPlayerDetailsView.playlistTableView.dataSource = self as! UITableViewDataSource
            mainController?.mediaPlayerDetailsView.playlistTableView.delegate = self as! UITableViewDelegate
            mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
            
            print("[Url!] : \(self.musicTracks[0].coverImageUrl?.toSecureHTTPSofDomainChange())")
            print("[Url!] : \(self.musicTracks[0].coverImageUrl)")
            //            self.tableView.reloadData()
        }
    }
    // end [ IMPORTED, UPMUSIC(APP) ]
    
    
    // start [ AZURE ]
    
    func initAzureSetting() {
        print("[initAzureSetting]")
        if (usingSAS) {
            var error: NSError?
            self.container = AZSCloudBlobContainer(url: URL(string: musicResourcesContainerURL)!, error: &error)
            if ((error) != nil) {
                print("Error in creating blob container object.  Error code = %ld, error domain = %@, error userinfo = %@", error!.code, error!.domain, error!.userInfo);
            }
        }
        else {
            //            do {
            let storageAccount : AZSCloudStorageAccount;
            try! storageAccount = AZSCloudStorageAccount(fromConnectionString: connectionString)
            let blobClient = storageAccount.getBlobClient()
            self.container = blobClient.containerReference(fromName: musicResourcesContainerName)
            
            let condition = NSCondition()
            var containerCreated = false
            
            self.container.createContainerIfNotExists { (error : Error?, created) -> Void in
                condition.lock()
                containerCreated = true
                condition.signal()
                condition.unlock()
            }
            
            condition.lock()
            while (!containerCreated) {
                condition.wait()
            }
            condition.unlock()
            //            } catch let error as NSError {
            //                print("Error in creating blob container object.  Error code = %ld, error domain = %@, error userinfo = %@", error.code, error.domain, error.userInfo);
            //            }
        }
        
        self.continuationToken = nil
        
    }
    // end [ AZURE ]
    
    
}



// start [ IMPORTED, UPMUSIC(APP) ] // FOR Playlist..
extension WKCookieWebViewController2: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let activityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: .whiteLarge)
        activityIndicatorView.color = .darkGray
        activityIndicatorView.startAnimating()
        return activityIndicatorView
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return musicTracks.isEmpty ? 200 : 0
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let track = self.musicTracks[indexPath.row]
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.maximizePlayerDetails(track: track, playlistTracks: self.musicTracks)
        
        
        //        let window  = UIApplication.shared.keyWindow
        //        let playerDetailsView = Bundle.main.loadNibNamed("MediaPlayerDetailsView", owner: self, options: nil)?.first as! UIView
        //        playerDetailsView.frame = self.view.frame
        //        window?.addSubview(playerDetailsView)
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return musicTracks.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! MusicTrackCell
        let track = musicTracks[indexPath.row]
        cell.track = track
        
        cell.artistNameLabel.tag = indexPath.row
        cell.trackNameLabel.tag = indexPath.row
        cell.podcastImageView.tag = indexPath.row
        cell.PlayButton.tag = indexPath.row
        cell.MoreButton.tag = indexPath.row
        
        
        cell.PlayButton.addTarget(self, action: #selector(playButtonClicked(sender:)), for: .touchUpInside)
        cell.MoreButton.addTarget(self, action: #selector(moreButtonClicked(sender:)), for: .touchUpInside)
        
        cell.MoreButton.isHidden = true
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56.3
    }
    
    @objc func playButtonClicked (sender: UIButton){
        clickedIndex = sender.tag
        print("[playButtonClicked] clickedIndex : \(clickedIndex)")
        
        let track = self.musicTracks[clickedIndex]
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.maximizePlayerDetails(track: track, playlistTracks: self.musicTracks)
        
    }
    
    
    @objc func moreButtonClicked (sender: UIButton){
        clickedIndex = sender.tag
        print("[moreButtonClicked] clickedIndex : \(clickedIndex)")
        
    }
    
    
    
}
// end [ IMPORTED, UPMUSIC(APP) ]
