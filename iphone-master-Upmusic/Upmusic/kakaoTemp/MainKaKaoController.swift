//
//  MainKaKaoController.swift
//  Upmusic
//
//  Created by nough on 21/07/2018.
//  Copyright © 2018 Nough / Ndvor All rights reserved.
//


import FirebaseAuth

class MainKaKaoController: UIViewController {
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var emailTextView: UITextView!
    @IBOutlet weak var displayNameTextView: UITextView!
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var navigationTitleItem: UINavigationItem!
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if let user = Auth.auth().currentUser {
            updateUserProfileUI(user: user)
        } else {
            self.dismiss(animated: true)
        }
    }
    
    /**
     Update profile UI with user information stored on Firebase (email, display name, and profile image)
     */
    func updateUserProfileUI(user: User) {
        self.emailTextView.text = String(format: "Email: %@", user.email!)
        self.displayNameTextView.text = String.init(format: "DisplayName: %@", user.displayName!)
        if let photoURL = user.photoURL {
            profileImageView.sd_setImage(with: photoURL, placeholderImage: #imageLiteral(resourceName: "placeholder.png"), options: [], completed: { (image, error, cacheType, url) in
                if let error = error {
                    print("error in loading profile image. \(error)")
                }
            })
        }
    }
    
    @IBAction func logoutButtonClicked(_ sender: UIButton) {
        do {
            try Auth.auth().signOut()
            KOSession.shared().close()
            self.dismiss(animated: true)
        } catch let error {
            print("Error signing out: \(error)")
        }
    }
}
