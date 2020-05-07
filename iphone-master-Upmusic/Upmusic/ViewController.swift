//
//  ViewController.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
/*
 CAUTION!! [UNUSED THING!]   
 */
class ViewController: UIViewController, MNHeaderDelegate {
    func handleAdd() {
        print("MNHeaderDelegate fully functional. Go implement this in your apps now! woo!")
    }
    
    let header = MNHeader(title: "Tasks", subtitle: "4 Left")
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = .white
        
        view.addSubview(header)
        header.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        header.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        header.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        header.heightAnchor.constraint(equalToConstant: 120).isActive = true
        
        header.delegate = self
    }

    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

}

