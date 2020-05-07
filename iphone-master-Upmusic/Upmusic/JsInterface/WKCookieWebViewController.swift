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
//import WKCookieWebView // IF NEED GIT-ORIGIN-FILE // CURRENTLY USED OTHER-COOKIEVIEW

class WKCookieWebViewController: UIViewController, GIDSignInUIDelegate
//    ,WKNavigationDelegate, WKUIDelegate, WKScriptMessageHandler
{
//    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
//        print("\(message)")
//    }
//
    // start [ IMPORTED, UPMUSIC(APP) ]
    static let upmusicUrl = NSURL(string: "https://upmusic.azurewebsites.net")
    let upmusicUrlNoStatic = "https://upmusic.azurewebsites.net"
    static let upmusicUserAgent = "UPMusicIOS"
    
    fileprivate let cellId = "cellId"
    
    var musicTracks : [MusicTrack] = []
    var clickedIndex : Int!
    var isFirstLoad : Bool! = true
    var activityIndicator = UIActivityIndicatorView()
    public var mainUrl: String = ""
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
//            userController.add(self, name: "ios")
            
            webCfg.userContentController = userController;
            return webCfg;
        }
    }
    
    lazy var webView: WKCookieWebView2 = {
        let webView: WKCookieWebView2 = WKCookieWebView2(frame: self.view.bounds, configuration: webConfig )
        webView.translatesAutoresizingMaskIntoConstraints = false
        webView.customUserAgent = WKWebViewController.upmusicUserAgent
//        webView.navigationDelegate = self
//        webView.uiDelegate = self
        return webView
    }()
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.isFirstLoad = true
        setupWebView()
        initAzureSetting()
        
        printCookie()
        
        webView.getCookies() { data in
            print("[-----------------------------]")
            print("\(self.webView.url?.absoluteString)")
            print(data)
            print("[-----------------------------]")
        }
        
        webView.load(URLRequest(url: URL(string: upmusicUrlNoStatic)!))
        
        
    }
    
    
    // MARK: - Private
    private func setupWebView() {
        
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
        
    }
    
    private func printCookie() {
        print("=====================Cookies=====================")
        HTTPCookieStorage.shared.cookies?.forEach {
            print($0)
        }
        print("=================================================")
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
extension WKCookieWebViewController: UITableViewDelegate, UITableViewDataSource {
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
