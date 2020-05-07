//
//  SplashView.swift
//  akccf
//
//  Created by nough on 11/02/2019.
//  Copyright © 2019 nough. All rights reserved.
//
import UIKit

class SplashView : UIView {
    
    @IBOutlet weak var logoImageView: UIImageView!
    
    @IBOutlet weak var progressView: UIProgressView!
    
    @IBOutlet weak var percentLabel1: UILabel!
    
    @IBOutlet weak var percentLabel2: UILabel!
    
    
    let MAX_TIME : Float = 10.0
    var currentTime : Float = 0.0
    
    class func instanceFromNib() -> UIView {
        
        return UINib(nibName: "SplashView", bundle: nil).instantiate(withOwner: nil, options: nil)[0] as! UIView
        
    }
    
    
    @IBAction func performClick() {
        progressView.setProgress(currentTime, animated: true)
        perform(#selector(updateProgress), with: nil, afterDelay: 1.0)
    }
    
    @objc func updateProgress() {
        currentTime = currentTime + 2.0
        progressView.progress = currentTime / MAX_TIME
        percentLabel1.text = "\(Int(currentTime) * 10)%"
        percentLabel2.text = "\(Int(currentTime) * 10)건 / 100 건"
        
        if (currentTime < MAX_TIME) {
            perform(#selector(updateProgress), with: nil, afterDelay: 1.0)
        } else {
            print("STOP")
            currentTime = 0.0
            // 시간이 다되면 저절로 꺼지도록
            self.removeFromSuperview()
        }
        
    }
    
}
