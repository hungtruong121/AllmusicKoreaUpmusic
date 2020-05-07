//
//  ScannerViewController.swift
//  Upmusic
//
//  Created by nough on 10/08/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import AVFoundation
import AudioToolbox

class ScannerViewController: UIViewController, AVCaptureMetadataOutputObjectsDelegate {
    @IBOutlet weak var qrGuideView: UIView!
    @IBOutlet weak var centerMenuView: UIView!
    
    @IBOutlet weak var cameraView: UIView!
    @IBOutlet weak var homeButton: UIButton!
    
    @IBOutlet weak var titleMenuView: UIView!
    @IBOutlet weak var titleMypageView: UIView!
    @IBOutlet weak var menuCloseView: UIView!
    @IBOutlet weak var menuNotiView: UIView!
    @IBOutlet weak var menuLogoutView: UIView!
    
    @IBOutlet weak var menuQrView: UIView!
    @IBOutlet weak var menuWaitView: UIView!
    @IBOutlet weak var menuEventView: UIView!
    @IBOutlet weak var menuMypageView: UIView!
    @IBOutlet weak var cameraChildView: UIView!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var notiCountLabel: UILabel!
    @IBOutlet weak var notiCountView: UIView!
    let systemSoundId : SystemSoundID = 1016
    
    //captureSession manages capture activity and coordinates between input device and captures outputs
    var captureSession:AVCaptureSession?
    var videoPreviewLayer:AVCaptureVideoPreviewLayer?
    
    //Empty Rectangle with green border to outline detected QR or BarCode
    let codeFrame:UIView = {
        let codeFrame = UIView()
        codeFrame.layer.borderColor = UIColor.red.cgColor
        codeFrame.layer.borderWidth = 1.5
        codeFrame.frame = CGRect.zero
        codeFrame.translatesAutoresizingMaskIntoConstraints = false
        return codeFrame
    }()
    
    override func viewDidDisappear(_ animated: Bool) {
        captureSession?.stopRunning()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        self.initPreview()
        captureSession?.startRunning()
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("[yangs::SVC::viewDidLoad]")
        
        //print(cameraView.layer.bounds)
        //print(cameraChildView.layer.bounds)
        
        self.showGuideView(show: true)
        self.initControl()
        self.initMammaUI()
    }
    
    func initControl() {
        // 1. create a gesture recognizer (tap gesture)
        // 2. add the gesture recognizer to a view
        titleMenuView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(sender:))))
        titleMypageView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(sender:))))
        menuCloseView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(sender:))))
        menuNotiView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(sender:))))
        menuLogoutView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(sender:))))
        menuQrView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(sender:))))
        menuWaitView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(sender:))))
        menuEventView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(sender:))))
        menuMypageView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(sender:))))
    }
    
    func initMammaUI() {
        
        if let user_id = AppComm.getUserID() {
            self.emailLabel.text = user_id
        }
        else
        {
            // mjh : user_id 없으면, 메뉴 가리기 추가
            self.titleMenuView.isHidden = true
            self.titleMypageView.isHidden = true
        }
        self.initBadgeUI()
    }
    
    func initBadgeUI() {
        if let badge = AppComm.getBadgeInfo() {
            self.notiCountLabel.text = String(badge.push_cnt)
            if(badge.push_cnt==0) {
                self.notiCountView.isHidden = true
            }
            else {
                self.notiCountView.isHidden = false
            }
        }
        else {
            self.notiCountView.isHidden = true
        }
    }
    
    func initPreview() {
        //AVCaptureDevice allows us to reference a physical capture device (video in our case)
        let captureDevice = AVCaptureDevice.default(for: AVMediaType.video)
        
        if let captureDevice = captureDevice {
            
            do {
                captureSession = AVCaptureSession()
                
                // CaptureSession needs an input to capture Data from
                let input = try AVCaptureDeviceInput(device: captureDevice)
                captureSession?.addInput(input)
                
                // CaptureSession needs and output to transfer Data to
                let captureMetadataOutput = AVCaptureMetadataOutput()
                captureSession?.addOutput(captureMetadataOutput)
                
                //We tell our Output the expected Meta-data type
                captureMetadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
                captureMetadataOutput.metadataObjectTypes = [.code128, .qr,.ean13, .ean8, .code39, .upce, .aztec, .pdf417] //AVMetadataObject.ObjectType
                
                captureSession?.startRunning()
                
                //The videoPreviewLayer displays video in conjunction with the captureSession
                videoPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession!)
                videoPreviewLayer?.videoGravity = .resizeAspectFill
                videoPreviewLayer?.frame = cameraView.layer.bounds
                cameraView.layer.addSublayer(videoPreviewLayer!)
                
                //print(cameraView.layer.bounds)
                //print(cameraChildView.layer.bounds)
                
                //cameraView.bringSubview(toFront: cameraChildView)
            }
            catch {
                print("Error")
            }
        }
    }
    
    var isShowGuideView: Bool = false;
    func showGuideView(show: Bool)
    {
        isShowGuideView = show;
        if(show)
        {
            qrGuideView.isHidden = false;
            centerMenuView.isHidden = true;
        }
        else
        {
            qrGuideView.isHidden = true;
            centerMenuView.isHidden = false;
        }
    }
    
    // the metadataOutput function informs our delegate (the ScannerViewController) that the captureOutput emitted a new metaData Object
    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        if metadataObjects.count == 0 {
            print("no objects returned")
            return
        }
        
        let metaDataObject = metadataObjects[0] as! AVMetadataMachineReadableCodeObject
        guard let StringCodeValue = metaDataObject.stringValue else {
            return
        }
        
        view.addSubview(codeFrame)
        
        //transformedMetaDataObject returns layer coordinates/height/width from visual properties
        guard let metaDataCoordinates = videoPreviewLayer?.transformedMetadataObject(for: metaDataObject) else {
            return
        }
        
        //Those coordinates are assigned to our codeFrame
        codeFrame.frame = metaDataCoordinates.bounds
        //AudioServicesPlayAlertSound(systemSoundId)
        //infoLbl.text = StringCodeValue
        if let url = URL(string: StringCodeValue) {
            //infoLbl.text = url.absoluteString
            
            let storyboard: UIStoryboard = self.storyboard!
            let vc = storyboard.instantiateViewController(withIdentifier: "WebViewController") as! WebViewController
            vc.mainUrl = url.absoluteString
            present(vc, animated: true, completion: nil)
            
            captureSession?.stopRunning()
        }
    }
    
    @IBAction func onClickHome(_ sender: Any) {
        captureSession?.stopRunning()
        let storyboard: UIStoryboard = self.storyboard!
        let vc = storyboard.instantiateViewController(withIdentifier: "TestViewController") as! TestViewController
        present(vc, animated: true, completion: nil)
    }
    
    // 3. this method is called when a tap is recognized
    @objc func handleTap(sender: UITapGestureRecognizer) {
        switch(sender.view!)
        {
        case titleMenuView:
            showGuideView(show: !isShowGuideView)
            DispatchQueue.global(qos: .background).async {
                AppComm.mammaApiCall_getBadgeInfo(viewController: self)
                self.initBadgeUI()
            }
            break
        case titleMypageView:
            //AppUtils.openTestView(viewController: self)
            AppUtils.openWebView(viewController: self, urlString: Define.MYPAGE_URL)
            break
        case menuCloseView:
            showGuideView(show: true)
            break
        case menuNotiView:
            AppUtils.openWebView(viewController: self, urlString: Define.PUSH_URL)
            break
        case menuLogoutView:
            SnsManager.logout()
            break
        case menuQrView:
            showGuideView(show: true)
            break
        case menuWaitView:
            AppUtils.openWebView(viewController: self, urlString: Define.WAIT_URL)
            break
        case menuEventView:
            AppUtils.openWebView(viewController: self, urlString: Define.EVENT_URL)
            break
        case menuMypageView:
            AppUtils.openWebView(viewController: self, urlString: Define.MYPAGE_URL)
            break
        default:
            break
        }
    }
}
