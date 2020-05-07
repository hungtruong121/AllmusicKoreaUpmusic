//
//  LoadingViewController.swift
//  yunstimeschedule
//
//  Created by cheche on 2018. 4. 23..
//  Copyright © 2018년 thecsy. All rights reserved.
//

import Foundation
import UIKit

class LoadingViewController: UIViewController{
    
    static let shared = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "LoadingViewController")
    
    @IBOutlet var loadingDialog :UIActivityIndicatorView!
    
    public var visble:Bool = false
    
    override func viewDidLoad() {
    
        loadingDialog.startAnimating()
        
    }
    
    func closeView(){
        self.view.removeFromSuperview()
    }
    
    override var shouldAutorotate: Bool {
        return true
    }
    
}

