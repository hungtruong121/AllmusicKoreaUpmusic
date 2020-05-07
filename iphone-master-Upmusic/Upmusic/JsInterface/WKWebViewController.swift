//
//  DetailViewController.swift
//  UpmusicTest
//
//  Created by Lihak Kang on 2018. 8. 5..
//  Copyright © 2018년 UPMusic. All rights reserved.
//

import UIKit
import WebKit
import Alamofire


// [NOT USED]
class WKWebViewController: UIViewController, WKNavigationDelegate, WKUIDelegate {

    static let upmusicUrl = NSURL(string: "https://upmusic.azurewebsites.net")
    let upmusicUrlNoStatic = "https://upmusic.azurewebsites.net"
    static let upmusicUserAgent = "UPMusicIOS"
    
    fileprivate let cellId = "cellId"
    let s3bucket = "upm-albums"
    let dataKey = ""
    
    // MARK : [FOR Azure Storage]
    
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
    
    var musicTracks : [MusicTrack] = []
    
    var clickedIndex : Int!
    var isFirstLoad : Bool!
    
    var webView: WKWebView!
    var activityIndicator = UIActivityIndicatorView()
    public var mainUrl: String = ""
    
    override var shouldAutorotate: Bool {
        ///< 회전 여부 설정
        return true
    }
    
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
            webCfg.userContentController = userController;
            return webCfg;
        }
    }
    
    
    ///Generates script to create given cookies
    func getJSCookiesString(cookies: [HTTPCookie]) -> String {
        var result = ""
        let dateFormatter = DateFormatter()
        dateFormatter.timeZone = NSTimeZone(abbreviation: "KST") as! TimeZone
        dateFormatter.dateFormat = "EEE, d MMM yyyy HH:mm:ss zzz"
        
        for cookie in cookies {
            result += "document.cookie='\(cookie.name)=\(cookie.value); domain=\(cookie.domain); path=\(cookie.path); "
            if let date = cookie.expiresDate {
                result += "expires=\(dateFormatter.string(from: date)); "
            }
            if (cookie.isSecure) {
                result += "secure; "
            }
            result += "'; "
        }
        return result
    }
    
    
    func initAzureSetting() {
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
    
    
    
//
//    /*
//     *   쿠키 셋팅을 통한 Session 유지
//     */
//    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
//        if let urlResponse = navigationResponse.response as? HTTPURLResponse,
//            let url = urlResponse.url,
//            let allHeaderFields = urlResponse.allHeaderFields as? [String : String] {
//            let cookies = HTTPCookie.cookies(withResponseHeaderFields: allHeaderFields, for: url)
////            HTTPCookieStorage.shared.setCookies(cookies , for: urlResponse.url!, mainDocumentURL: nil)
//            HTTPCookieStorage.shared.setCookies(cookies , for: WKWebViewController.upmusicPreviousUrl! as URL , mainDocumentURL: nil)
//            decisionHandler(.allow)
//        }
//    }
//
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation) {
        if let url = webView.url {
            
            print("[URL] : \(url.absoluteString)")
            webView.getCookies(for: url.host) { data in
                print("=========================================")
                print("\(url.absoluteString)")
                print(data)
            }
            
        }
        
        
//        if self.isFirstLoad { // 앱 처음 실행인지 판단.
//            self.isFirstLoad = false // 쿠키 생성을 다시 하지 않도록 처리.
//            if let string: String = Devg.getContents("COOKIE")
//            { // 저장된 쿠키 파일을 불러 옴.
//                // 쿠키를 생성하는 Javascript
//                let cookies: [String] = string.componentsSeparatedByString(" ")
//                var javascript: String = ""
//                for cookie: String in cookies {
//                    javascript = javascript + "document.cookie='\(cookie)';"
//                }
//                webView.evaluateJavaScript(javascript, completionHandler: { (object, error) in // 쿠키를 생성하는 Javascript를 실행.
//                    webView.reload() // 쿠기 생성 후 쿠키 정보가 반영된 웹페이지를 불러 올 수 있도록 새고 고침을 해줍니다.
//                })
//            }
//        }
//        else {
//            webView.evaluateJavaScript("document.cookie") { (object, error) in // 자바스크립트로 모든 쿠키를 가져와서
//                if let string: String = object as? String { // 스트링으로 변환 후 저장.
////                    Devg.createFile("COOKIE", contents: string) // 파일 저장하는 부분이므로 대체 하세요.
//                }
//            }
//        }
    }
    
    
    func storeCookies() {
        let cookiesStorage = HTTPCookieStorage.shared
        let userDefaults = UserDefaults.standard
        
        let serverBaseUrl = upmusicUrlNoStatic
        var cookieDict = [String : AnyObject]()
        
        for cookie in cookiesStorage.cookies(for: NSURL(string: serverBaseUrl)! as URL)! {
            cookieDict[cookie.name] = cookie.properties as AnyObject?
        }
        
        userDefaults.set(cookieDict, forKey: "cookiesKey")
    }
    
    func restoreCookies() {
        let cookiesStorage = HTTPCookieStorage.shared
        let userDefaults = UserDefaults.standard
        
        if let cookieDictionary = userDefaults.dictionary(forKey: "cookiesKey") {
            
            for (_, cookieProperties) in cookieDictionary {
                if let cookie = HTTPCookie(properties: cookieProperties as! [HTTPCookiePropertyKey : Any] ) {
                    cookiesStorage.setCookie(cookie)
                }
            }
        }
    }
    
    
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        decisionHandler(.allow)
    }
    
    
//    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
////        if navigationAction.navigationType == .linkActivated  {
////            if let newURL = navigationAction.request.url,
////                let host = newURL.host , !host.hasPrefix(upmusicUrlNoStatic) &&
////                UIApplication.shared.canOpenURL(newURL) &&
////                UIApplication.shared.openURL(newURL) {
////                print(newURL)
////                print("Redirected to browser. No need to open it locally")
////                decisionHandler(.cancel)
////            } else {
////                print("Open it locally")
////                decisionHandler(.allow)
////            }
////        } else {
////            print("not a user click")
////            decisionHandler(.allow)
////        }
//
//
//        let headerKeys = navigationAction.request.allHTTPHeaderFields?.keys
//        let hasCookies = headerKeys?.contains("Cookie") ?? false
//
//        if hasCookies {
//            decisionHandler(.allow)
//        } else {
//            let cookies = HTTPCookie.requestHeaderFields(with: HTTPCookieStorage.shared.cookies ?? [])
//
//            var headers = navigationAction.request.allHTTPHeaderFields ?? [:]
//            for cookie in cookies {
//                headers[cookie.key] = cookie.value
//            }
//
//            var req = navigationAction.request
//            req.allHTTPHeaderFields = headers
//            webView.load(req)
//
//            decisionHandler(.cancel)
//        }
//    }
    
    
    func addUserScript(cookies: [HTTPCookie], userContentController: WKUserContentController) {
        for cookie in cookies {
            //eg. //JSESSIONID=E85439F1A4A3B4AB6E5CF766B2B1B694;
            let cookieString = "document.cookie='JSESSIONID=;path=/;domain=.test.co.kr;'"
            let cookieScript = WKUserScript(source: cookieString, injectionTime: .atDocumentStart, forMainFrameOnly: false)
            userContentController.addUserScript(cookieScript)
        }
    }
    
    
    func configureView() {
        print("configureView ")
        self.webView = WKWebView( frame: self.view.frame, configuration: webConfig)
        self.webView.customUserAgent = WKWebViewController.upmusicUserAgent
        self.webView.navigationDelegate = self
        self.webView.uiDelegate = self
        
        var requestObj = URLRequest(url: WKWebViewController.upmusicUrl! as URL)
        
//        // 쿠키사용해서 로그인 유지 [NOT WORKING]
//        var cookies = HTTPCookie.requestHeaderFields(with: HTTPCookieStorage.shared.cookies(for: WKWebViewController.upmusicUrl! as URL)!)
//        if let value = cookies["Cookie"] {
//            print("cookie? : \(value)")
//            requestObj.addValue(value, forHTTPHeaderField: "Cookie")
//        }
//
        
        self.webView.load(requestObj)
        
        self.view.addSubview(self.webView)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        print("viewDidLoad")
        // Do any additional setup after loading the view, typically from a nib.
        
        configureView()
        fetchAllTracks()
        
        
        print("[yangs::WebViewController::viewDidLoad] start\n")
        
        AppUtils.sharedInstance.webViewController = self
        
        if(Define.isNetworkCheck)
        {
            DispatchQueue.main.async() {
                self.checkNetwork(viewController: self)
                
                if(Define.isUpdateCheck)
                {
                    self.checkUpdate(viewController: self)
                }
            }
        }
        
        initWebView()
        moveUrl(urlString: self.mainUrl)
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func callbackFromNative(msg: String) {
        self.webView?.evaluateJavaScript("callbackFromNative('\(msg)')", completionHandler: nil)
        
        
    }
    
    /*
     webView.evaluateJavaScript("function getCookie(name) {  var value = \"; \" + document.cookie;  var parts = value.split(\"; \" + name + \"=\");  if (parts.length == 2) return parts.pop().split(\";\").shift();}; getCookie('basket_cnt');") { (object, error) in
     print(object)
     }
     */
    
    
    fileprivate func fetchAllTracks() {
        
        print("[fetchAllTracks]")
        
        let URL = (upmusicUrlNoStatic+"/api/track").toSecureHTTPSofDomainChange()
        Alamofire.request(URL).responseArray { (response: DataResponse<[MusicTrack]>) in
            
            let dataArray = response.result.value
            self.musicTracks = dataArray!
            
            let track = self.musicTracks[1]
            let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
//            mainController?.maximizePlayerDetails(track: track, playlistTracks: self.musicTracks)
            mainController?.peekPlayerDetails(track: track, playlistTracks: self.musicTracks)
            
            mainController?.mediaPlayerDetailsView.originPlaylist = self.musicTracks
            
            mainController?.mediaPlayerDetailsView.playlistTableView.dataSource = self as! UITableViewDataSource
            mainController?.mediaPlayerDetailsView.playlistTableView.delegate = self as! UITableViewDelegate
            mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
            
            
            
            //print("[Url!] : \(self.musicTracks[0].coverImageUrl?.toSecureHTTPSofDomainChange())")
            //            print("[Url!] : \(self.musicTracks[0].coverImageUrl)")
//            self.tableView.reloadData()
        }
    }
    
    
    
    
    
    
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo,
                 completionHandler: @escaping () -> Void) {
        let alertController = UIAlertController(title: "", message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: "확인", style: .default, handler: { (action) in
            completionHandler()
        }))
        
        self.present(alertController, animated: true, completion: nil)
    }
    
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
    
    
    
    
    
    // added
    func initWebView()
    {
        SnsManager.clearCache()
        ///< yangs : 앱실행할 때 clearCookies 수행하면 로그인 상태로 앱 종료 후 자동로그인 해제됨
        //SnsManager.clearCookies()
        
        self.webView.backgroundColor = UIColor.clear
//        self.webView.delegate = self
    }
    
    
    
    func moveUrl(urlString: String)
    {
        print("[yangs::WVC::moveUrl] urlString(\(urlString))\n")
        if let url = URL(string: urlString) {
            let request = URLRequest(url: url)
//            self.webView.loadRequest(request)
        }
    }
    
    func showLoading(show: Bool)
    {
        if(!Define.isWebviewLoading) {
            return
        }
        
        if(show)
        {
            // 1. 가운데 로딩 이미지를 띄워주면서
            if( activityIndicator.isAnimating ) {
                activityIndicator.removeFromSuperview()
            }
            let lodingWidth:CGFloat = 1000
            activityIndicator = UIActivityIndicatorView(activityIndicatorStyle: UIActivityIndicatorViewStyle.gray)
            activityIndicator.frame = CGRect(x: view.frame.midX - (CGFloat)(lodingWidth/2), y: view.frame.midY - (CGFloat)(lodingWidth/2) , width: lodingWidth, height: lodingWidth)
            activityIndicator.hidesWhenStopped = true
            activityIndicator.startAnimating()
            view.addSubview(activityIndicator)
            
            // 2. 상단 status bar에도 activity indicator가 나오게 할 것이다.
            UIApplication.shared.isNetworkActivityIndicatorVisible = true;
        }
        else
        {
            // 1. 가운데 로딩 이미지 제거
            activityIndicator.removeFromSuperview()
            
            // 2. 상단 status bar 제거
            UIApplication.shared.isNetworkActivityIndicatorVisible = false
        }
    }
    
    
    
    func checkUpdate(viewController: UIViewController)
    {
        if AppUtils.isUpdateAvailable()
        {
            let appStoreVersion = AppUtils.getAppStoreVersion()!;
            let message = "앱스토어에 최신버전(v\(appStoreVersion))이 업데이트 되었습니다.\n업데이트 하시겠습니까?"
            let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
                let url = URL(string: "itms-apps://itunes.apple.com/app/id\(Define.appleAppID)")
                if UIApplication.shared.canOpenURL(url!) == true  {
                    UIApplication.shared.open(url!, options: [:], completionHandler: nil)
                }
            }))
            alert.addAction(UIAlertAction(title: "취소", style: .cancel, handler: { action in
                //exit(0)
            }))
            
            viewController.present(alert, animated: true)
        }
    }
    
    func checkNetwork(viewController: UIViewController)
    {
        if ReachabilityCustom.isConnectedToNetwork()
        {
            
        }
        else
        {
            let alert = UIAlertController(title: "안내", message: "네트워크에 연결할 수 없습니다.\n네트워크 연결을 확인하고 다시 실행해주세요.", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
                exit(0)
            }))
            viewController.present(alert, animated: true)
        }
    }
    
    func processUrl(scheme: String)
    {
        let currentUrl = URL(string:scheme)!
        
        print("[yangs::WVC::processMammaUrl] scheme : \(scheme)")
        
        let components = URLComponents(url: currentUrl, resolvingAgainstBaseURL: false)!
        /*if let queryItems = components.queryItems {
         print("[yangs::WVC::processMammaUrl] queryItems : \(queryItems)")
         }*/
        
        if( (currentUrl.scheme=="http" || currentUrl.scheme=="https") &&
            (currentUrl.host==Define.HOST) &&
            (currentUrl.port==Define.PORT || currentUrl.port==nil))
        {
            if(currentUrl.path==Define.SCHEME_MOVE_LOGIN)
            {
                print("[yangs::WVC::processMammaUrl] Define.SCHEME_MOVE_LOGIN")
                DispatchQueue.global(qos: .background).async {
                    SnsManager.saveFacebookLoginInfo()
                }
            }
        }
    }
    
    func commandUrl(scheme: String) -> Bool
    {
        let currentUrl = URL(string:scheme)!
        
        print("[yangs::WVC::commandUrl] scheme : \(scheme)")
        
        let components = URLComponents(url: currentUrl, resolvingAgainstBaseURL: false)!
        /*if let queryItems = components.queryItems {
         print("[yangs::WVC::commandMamma] queryItems : \(queryItems)")
         }*/
        
        if( (currentUrl.scheme=="http" || currentUrl.scheme=="https"))
        {
            if(currentUrl.path==Define.SCHEME_MOVE_LOGIN)
            {
                print("[yangs::WVC::commandUrl] Define.SCHEME_MOVE_LOGIN")
                DispatchQueue.global(qos: .background).async {
                    //SnsManager.saveFacebookLoginInfo()
                }
                //return true;
            }
            //            if(currentUrl.path==Define.SCHEME_MOVE_SCANNER)
            //            {
            //                if(SnsManager.sharedInstance.isLogin==false) {
            //                    SnsManager.processLoginSuccessed()
            //                    // mjh :  로그인 안해도 QR코드 화면 나오게 하도록 수정.
            //                    AppUtils.openQrView(viewController: self)
            //                }
            //                else {
            //                    AppUtils.openQrView(viewController: self)
            //                }
            //                return true;
            //            }
            //            else
            if(currentUrl.path==Define.SCHEME_MOVE_EVENT)
            {
                AppUtils.openWebView(viewController: self, urlString: Define.EVENT_URL)
                return true;
            }
            else if(currentUrl.path==Define.SCHEME_MOVE_NAVER_LOGIN)
            {
                SnsManager.loginNaver(login: true)
                return true;
            }
            else if(currentUrl.path==Define.SCHEME_MOVE_KAKAO_LOGIN)
            {
                SnsManager.loginKakao(login: true, trial: 1)
                
                return true;
            }
            else if(currentUrl.path==Define.SCHEME_MOVE_FACEBOOK_LOGIN)
            {
                SnsManager.loginFacebook(login: true)
                return true;
            }
            
            //print(currentUrl.scheme)
            //print(currentUrl.host)
            //print(currentUrl.port)
            //print(currentUrl.path)
            //print(dict)
        }
        
        return false;
    }
    
    func commandWeb2App(scheme: String) -> Bool
    {
        //print("[yangs::WebViewController::commandWeb2App] scheme(\(scheme))")
        //let scheme2 = "jscall:showMessage:It's%20Message"
        let splits = scheme.split(separator: ":").map(String.init)
        var command = ""
        var param01 = ""
        var param02 = ""
        var param03 = ""
        for (index, element) in splits.enumerated() {
            if(index==0 && element != "jscall") {
                return false
            }
            if(index==1){ command=element.removingPercentEncoding! }
            if(index==2){ param01=element.removingPercentEncoding! }
            if(index==3){ param02=element.removingPercentEncoding! }
            if(index==4){ param03=element.removingPercentEncoding! }
        }
        
        if(command=="showMessage") {
            let alert = UIAlertController(title: "메시지", message: param01, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "확인", style: .default, handler: nil))
            self.present(alert, animated: true)
            return true
        }
        
        return false
    }
    
    func callJavascript(function: String)
    {
        print("[yangs::WebViewController::callJavascript] function : \(function)")
//        self.webView.stringByEvaluatingJavaScript(from: function)
    }

    
}



extension WKWebViewController: UITextViewDelegate {
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        guard let text = textField.text else { return true }
        let newLength = text.characters.count + string.characters.count - range.length
        return newLength <= 15
    }
    
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        guard let str = textView.text else { return true }
        let newLength = str.characters.count + text.characters.count - range.length
        return newLength <= 200
    }
    
}


extension WKWebViewController: UITableViewDelegate, UITableViewDataSource {
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





extension WKWebViewController: UIWebViewDelegate {
    func webView(_ webView: UIWebView, shouldStartLoadWith request: URLRequest, navigationType: UIWebViewNavigationType) -> Bool {
        
        print("[yangs::WebViewController::webView] shouldStartLoadWith : \(request)")
        
        processUrl(scheme: request.url!.absoluteString)
        
        if(commandUrl(scheme: request.url!.absoluteString)) {
            showLoading(show: false)
            return false
        }
        
        if(commandWeb2App(scheme: request.url!.absoluteString)) {
            showLoading(show: false)
            return false
        }
        
        return true
    }
    
    func webViewDidStartLoad(_ webView: UIWebView) {
        showLoading(show: true)
    }
    
    func webViewDidFinishLoad(_ webView: UIWebView) {
        showLoading(show: false)
    }
    
    @IBAction func onClickHome(_ sender: Any) {
        let storyboard: UIStoryboard = self.storyboard!
        let vc = storyboard.instantiateViewController(withIdentifier: "TestViewController") as! TestViewController
        present(vc, animated: true, completion: nil)
    }
    
    
    
}

