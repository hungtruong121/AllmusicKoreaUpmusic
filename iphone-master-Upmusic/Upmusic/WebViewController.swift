//
//  WebViewController.swift
//  Upmusic
//
//  Created by nough on 10/08/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit

class WebViewController: UIViewController {
    
    @IBOutlet weak var webView: UIWebView!
    var activityIndicator = UIActivityIndicatorView()
    
    public var mainUrl: String = ""
    
    override var shouldAutorotate: Bool {
        ///< 회전 여부 설정
        return true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
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
    
    func initWebView()
    {
        SnsManager.clearCache()
        ///< yangs : 앱실행할 때 clearCookies 수행하면 로그인 상태로 앱 종료 후 자동로그인 해제됨
        //SnsManager.clearCookies()
        
        self.webView.backgroundColor = UIColor.clear
        self.webView.delegate = self
    }
    
    func moveUrl(urlString: String)
    {
        print("[yangs::WVC::moveUrl] urlString(\(urlString))\n")
        if let url = URL(string: urlString) {
            let request = URLRequest(url: url)
            self.webView.loadRequest(request)
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
    
    func processMammaUrl(scheme: String)
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
    
    func commandMamma(scheme: String) -> Bool
    {
        let currentUrl = URL(string:scheme)!
        
        print("[yangs::WVC::commandMamma] scheme : \(scheme)")
        
        let components = URLComponents(url: currentUrl, resolvingAgainstBaseURL: false)!
        /*if let queryItems = components.queryItems {
         print("[yangs::WVC::commandMamma] queryItems : \(queryItems)")
         }*/
        
        if( (currentUrl.scheme=="http" || currentUrl.scheme=="https"))
        {
            if(currentUrl.path==Define.SCHEME_MOVE_LOGIN)
            {
                print("[yangs::WVC::commandMamma] Define.SCHEME_MOVE_LOGIN")
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
        self.webView.stringByEvaluatingJavaScript(from: function)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
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
}

extension WebViewController: UIWebViewDelegate {
    func webView(_ webView: UIWebView, shouldStartLoadWith request: URLRequest, navigationType: UIWebViewNavigationType) -> Bool {
        
        print("[yangs::WebViewController::webView] shouldStartLoadWith : \(request)")
        
        processMammaUrl(scheme: request.url!.absoluteString)
        
        if(commandMamma(scheme: request.url!.absoluteString)) {
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
