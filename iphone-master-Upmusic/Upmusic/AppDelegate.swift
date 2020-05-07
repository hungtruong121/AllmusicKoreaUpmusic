//
//  AppDelegate.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import FBSDKCoreKit
import FBSDKLoginKit
import Firebase
import FirebaseCore
import FirebaseMessaging
import GoogleSignIn
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate
    ,GIDSignInDelegate
{
    /**
     @var.....
     */
    var window: UIWindow?
    var deviceToken: Data? = nil
    let gcmMessageIDKey = "gcm.message_id"
    
    let userDefaults = UserDefaults.standard
    
    private let kGoogleClientID = AuthCredentials.GOOGLE_CLIENT_ID // IF WANT GOOGLE-LOGIN
    private let kFacebookAppID = AuthCredentials.FACEBOOK_APP_ID
    
    @objc func kakaoSessionDidChangeWithNotification() {
        
        if(Define.useLoginKakao)
        {
            let isOpened = KOSession.shared().isOpen()
            if !isOpened {
                print("[yangs::AD::kakaoSessionDidChangeWithNotification] isOpened(\(isOpened))")
            }
            
            if(isOpened)
            {
                SnsManager.sharedInstance.loginType = LoginType.Kakao
                SnsManager.getKakaoLoginInfo(sync: false)
                if(!Define.useSnsAutoLogout) {
                    SnsManager.processLoginSuccessed()
                }
            }
            else
            {
                if(!Define.useSnsAutoLogout) {
                    SnsManager.processLogoutComplete()
                }
            }
        }
    }
    
    
    // START of [google-login]
    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
        if let err = error {
            print(" > [Google] Failed to log into Google: ", err)
            return
        }
        
        print(" > [Google] Successfully logged into Google", user)
        
        guard let idToken = user.authentication.idToken else { return }
        guard let accessToken = user.authentication.accessToken else { return }
        let credentials = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: accessToken)
        
        Auth.auth().signIn(with: credentials, completion: { (user, error) in
            if let err = error {
//                print("Failed to create a Firebase User with Google account: ", err)
                return
            }
            
            guard let uid = user?.uid else { return }
            print(" > [Google] Successfully logged into Firebase with Google", uid)
            
            SnsManager.sharedInstance.loginType = LoginType.Google
            SnsManager.sharedInstance.accessToken = idToken // Google USe id_token
            SnsManager.processLoginSuccessed()
            
        })
    }
    
    func sign(_ signIn: GIDSignIn!, didDisconnectWith user: GIDGoogleUser!, withError error: Error!) {
        // Perform any operations when the user disconnects from app here.
        // ...
    }
    // END of [google-login]
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        
        
        if(Define.useFirebase)
        {
            print("application launchOptions useFirebase")
            FirebaseApp.configure()
        }
        
        if(Define.useFcm)
        {
            print("application launchOptions useFcm")
            // [START set_messaging_delegate]
            Messaging.messaging().delegate = self as MessagingDelegate
            // [END set_messaging_delegate]
            
            // [START register_for_notifications]
            if #available(iOS 10.0, *) {
                // For iOS 10 display notification (sent via APNS)
                UNUserNotificationCenter.current().delegate = self as! UNUserNotificationCenterDelegate

                let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
                UNUserNotificationCenter.current().requestAuthorization(
                    options: authOptions,
                    completionHandler: {_, _ in })
            } else {
                let settings: UIUserNotificationSettings =
                    UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
                application.registerUserNotificationSettings(settings)
            }

            application.registerForRemoteNotifications()
            // [END register_for_notifications]
        }
        
        if (Define.useLoginGoogle) {
            
            print("application launchOptions useLoginGoogle")
            // USE ONLY-GOOGLE (MAY OCCUR-ERROR)
//            GIDSignIn.sharedInstance().clientID = kGoogleClientID
            // USE FIREBASE-GOOGLE
            GIDSignIn.sharedInstance().clientID = FirebaseApp.app()?.options.clientID
            GIDSignIn.sharedInstance().delegate = self
            print("\(String(describing:  GIDSignIn.sharedInstance().clientID))")
        }
        
        if(Define.useLoginNaver) {
            
            print("application launchOptions useLoginNaver")
            
            let instance = NaverThirdPartyLoginConnection.getSharedInstance()
            instance?.isInAppOauthEnable = true // --- 1
            instance?.isNaverAppOauthEnable = false // --- 2
            instance?.isOnlyPortraitSupportedInIphone() // --- 3
            // --- 4
            instance?.serviceUrlScheme = Define.kServiceAppUrlScheme
            instance?.consumerKey = Define.kConsumerKey
            instance?.consumerSecret = Define.kConsumerSecret
            instance?.appName = Define.kServiceAppName
        }
        if(Define.useLoginKakao) {
            
            print("application launchOptions useLoginKakao")
            
            NotificationCenter.default.addObserver(self,
                                                   selector: #selector(AppDelegate.kakaoSessionDidChangeWithNotification),
                                                   name: NSNotification.Name.KOSessionDidChange,
                                                   object: nil)
        }
        if(Define.useLoginFacebook) {
            
            print("application launchOptions useLoginFacebook")
            
            FBSDKApplicationDelegate.sharedInstance().application(application, didFinishLaunchingWithOptions: launchOptions)
        }

        
        UserDefaults.standard.set("true", forKey: "isFirstLoad")
        
        HTTPCookieStorage.shared.cookieAcceptPolicy = HTTPCookie.AcceptPolicy.always
        UINavigationBar.appearance().barStyle = .blackOpaque
        if let statusbar = UIApplication.shared.value(forKey: "statusBar") as? UIView {
            statusbar.backgroundColor = UIColor(red: 24.0/255.0, green: 30.0/255.0, blue: 51.0/255.0, alpha: 1.0)
        }
//
//        HTTPCookieStorage.restore()//앱실행시 호출하여 기존 쿠키를 불러온다.
        
        window = UIWindow()
        window?.rootViewController = MainController()
        window?.makeKeyAndVisible()

//        self.window = UIWindow(frame: UIScreen.main.bounds)
//        let navController = UINavigationController()
//        navController.navigationBar.barTintColor = UIColor(red: 24.0/255.0, green: 30.0/255.0, blue: 51.0/255.0, alpha: 1.0)
    
//      // IF YOU NEED NAVBAR LOGO.
//        let logo = #imageLiteral(resourceName: "logo")
//        let imageView = UIImageView(image: logo)
//        let bannerWidth = UINavigationBar().frame.size.width
//        let bannerHeight = UINavigationBar().frame.size.height
//        let bannerX = bannerWidth / 2 - logo.size.width / 2
//        let bannerY = bannerHeight / 2 - logo.size.height / 2
//        imageView.frame = CGRect(x: bannerX, y: bannerY, width: bannerWidth, height: bannerHeight)
//        navController.navigationItem.titleView = imageView

//        let mainController = MainController(nibName: nil, bundle: nil) //ViewController = Name of your controller
//        navController.viewControllers = [mainController]
//        self.window!.rootViewController = navController
//        self.window?.makeKeyAndVisible()
        
        return true
    }
    
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        
        if(Define.useLoginKakao) {
            KOSession.handleDidBecomeActive()
        }
        if(Define.useLoginFacebook) {
            FBSDKAppEvents.activateApp()
        }
    }
    
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        if(Define.useLoginKakao) {
            KOSession.handleDidEnterBackground()
        }
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    func application(application: UIApplication, didReceiveRemoteNotification userInfo: [NSObject : AnyObject], fetchCompletionHandler completionHandler: (UIBackgroundFetchResult) -> Void) {
        
        if(Define.useFcmForground)
        {
            ///< iOS 9 이하에서 Foreground 푸시 수신 처리
            // Let FCM know about the message for analytics etc.
            Messaging.messaging().appDidReceiveMessage(userInfo)
            // handle your message
            
            print("[application::didReceiveRemoteNotification] Define.useFcmForground(true) iOS 10 under")
            
            // Print full message.
            print(userInfo)
            
            AppUtils.processPushMessage(userInfo: userInfo)
        }
    }
    

    
    func application(_ application: UIApplication, open url: URL, sourceApplication: String?, annotation: Any) -> Bool {
        
        // NICE @@@@@@@@@@@@@@@@@@@@@@@@@@
//        if let scheme = url.scheme {
//            if scheme.hasPrefix(IAMPortPay.sharedInstance.appScheme ?? "") {
//
//                print("[scheme NICE] 1")
//
//                return IAMPortPay.sharedInstance.application(application, open: url, sourceApplication: sourceApplication, annotation: annotation)
//            }
//        }
        
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        if(Define.useLoginKakao)
        {
            print("[application]  : open Define.useLoginKakao")
            if KOSession.handleOpen(url) {
                return true
            }
        }
        
        if(Define.useLoginGoogle) {
            print("[application]  : open Define.useLoginGoogle")
            return GIDSignIn.sharedInstance().handle(url,
                                                     sourceApplication: sourceApplication,
                                                     annotation: annotation)
        }
        
        
        return true
    }
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey : Any]) -> Bool {
        
        print("[application] UIApplicationOpenURLOptionsKey url(\(url))")
        
//        // NICE @@@@@@@@@@@@@@@@@@@@@@@@@@
//        if let scheme = url.scheme {
//            if scheme.hasPrefix(IAMPortPay.sharedInstance.appScheme ?? "") {
//
//                print("[scheme NICE] 2")
//
//                return IAMPortPay.sharedInstance.application(app, open: url, options: options)
//            }
//        }
        
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        if(Define.useLoginNaver) {
            print("[useLoginNaver]")
            if let scheme = url.scheme {
                if scheme == Define.kServiceAppUrlScheme {
                    let result = NaverThirdPartyLoginConnection.getSharedInstance().receiveAccessToken(url)
                    if result == CANCELBYUSER {
                        //print(result)
                        print("[application] UIApplicationOpenURLOptionsKey CANCELBYUSER(\(result))")
                        return false
                    }
                    else if result != SUCCESS {
                        print("[application] UIApplicationOpenURLOptionsKey Naver login Failed(\(result))")
                        return false
                    } else {
                        print("[application] UIApplicationOpenURLOptionsKey Naver login success(\(result))")
                        return true
                    }
                }
            }
        }
        if(Define.useLoginKakao) {
            print("[useLoginKakao]")
            if KOSession.handleOpen(url) {
                return true
            }
        }
        if(Define.useLoginFacebook) {
            
            print("[useLoginFacebook]")
            let res = FBSDKApplicationDelegate.sharedInstance().application(app, open: url as URL!, sourceApplication: options[UIApplicationOpenURLOptionsKey.sourceApplication] as! String, annotation: options[UIApplicationOpenURLOptionsKey.annotation])
            if(res) {
                return true
            }
        }
        if(Define.useLoginGoogle) {
            print("[useLoginGoogle]")
            return GIDSignIn.sharedInstance().handle(url,
                                              sourceApplication:options[UIApplicationOpenURLOptionsKey.sourceApplication] as? String,
                                              annotation: [:])
        }
        
        return false
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        self.deviceToken = deviceToken
        var hexString: String = ""
        for byte in deviceToken {
            hexString.append(String(Int(byte), radix: 16))
        }
        print("didRegisterForRemoteNotificationsWithDeviceToken=\(hexString)")
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("didFailToRegisterForRemoteNotificationsWithError=\(error)")
    }
    
    
    

}

// [START ios_10_message_handling]
@available(iOS 10, *)
extension AppDelegate : UNUserNotificationCenterDelegate {
    
    // Receive displayed notifications for iOS 10 devices.
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        
        let userInfo = notification.request.content.userInfo
        
        // Print message ID.
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        
        // Print full message.
        print(userInfo)
        
        // Change this to your preferred presentation option
        completionHandler([.alert, .sound, .badge])
        
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        
        
        if(userInfo["url"] != nil){
            
            let pushUrl:String = userInfo["url"] as! String
            
            if(pushUrl != "" && !pushUrl.isEmpty){
                
                self.userDefaults.setValue(pushUrl, forKey: "url")
                
                if(UIApplication.shared.applicationState == .active){
                    
                    if(UIApplication.shared.topMostViewController()?.restorationIdentifier == "MainViewController"){
                        
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "movePushUrl"), object: nil)
                        
                    }else{
                        
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "intent"), object: nil)
                        
                    }
                    
                }else{
                    
                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: "intent"), object: nil)
                    
                }
                
            }
            
        }
        
        completionHandler()
    }
}

extension AppDelegate : MessagingDelegate {
    // [START refresh_token]
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        print("Firebase registration token: \(fcmToken)")
        self.userDefaults.setValue(fcmToken, forKey: Def.gcm_Token)
    }
    
    func messaging(_ messaging: Messaging, didReceive remoteMessage: MessagingRemoteMessage) {
        print("Received data message: \(remoteMessage.appData)")
    }
}


extension UIApplication {
    func topMostViewController() -> UIViewController? {
        return self.keyWindow?.rootViewController?.topMostViewController()
    }
}

extension UIViewController {
    func topMostViewController() -> UIViewController {
        if self.presentedViewController == nil {
            return self
        }
        if let navigation = self.presentedViewController as? UINavigationController {
            
            return navigation.visibleViewController!.topMostViewController()
        }
        if let tab = self.presentedViewController as? UITabBarController {
            if let selectedTab = tab.selectedViewController {
                return selectedTab.topMostViewController()
            }
            return tab.topMostViewController()
        }
        return self.presentedViewController!.topMostViewController()
    }
}
