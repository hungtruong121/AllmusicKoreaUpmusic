
extension MediaPlayerDetailsView {
    
    @objc func handlePan(gesture: UIPanGestureRecognizer) {
        if gesture.state == .changed {
            NSLog("[gesture][1-1] handlePan changed")
            handlePanChanged(gesture: gesture)
        } else if gesture.state == .ended {
            NSLog("[gesture][1-2] handlePan ended")
            handlePanEnded(gesture: gesture)
        }
    }
    
    func handlePanChanged(gesture: UIPanGestureRecognizer) {
        
        if (self.track != nil) {
            
            let translation = gesture.translation(in: self.superview)
            self.transform = CGAffineTransform(translationX: 0, y: translation.y)
            self.miniPlayerView.alpha = 1 + translation.y / 200
            self.maximizedStackView.alpha = -translation.y / 200
        }
    }
    
    func handlePanEnded(gesture: UIPanGestureRecognizer) {
        
        if (self.track != nil) {
            let translation = gesture.translation(in: self.superview)
            let velocity = gesture.velocity(in: self.superview)
            print("Ended:", velocity.y)
            
            UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.7, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
                self.transform = .identity
                if translation.y < -200 || velocity.y < -500 {
                    UIApplication.mainController()?.maximizePlayerDetails(track: nil)
                } else {
                    self.miniPlayerView.alpha = 1
                    self.maximizedStackView.alpha = 0
                }
            })
            }
    }
    
    @objc func handleTapMaximize() {
        
        if (self.track != nil) {
        UIApplication.mainController()?.maximizePlayerDetails(track: nil)
        }
    }
    
}

// NOT CURRENTLY USED>
extension MediaPlayerDetailsView : UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return playlistTracks.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! MusicTrackCell
        let track = playlistTracks[indexPath.row]
        cell.track = track
        return cell
    }
    
    
    // Moving cells
    
    func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCell.EditingStyle {
        return .none
    }
//
//    func tableView(_ tableView: UITableView, shouldIndentWhileEditingRowAt indexPath: IndexPath) -> Bool {
//        return true
//    }
////
//    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
//        return true
//    }
//
//    func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
//        return true
//    }
    
    
//    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
//        
//        print("[editingStyle] on MediaPlayerDetailsView+Gesture")
//        
//        if editingStyle == UITableViewCellEditingStyle.delete {
//            
////            playlistTracks.remove(at: indexPath.row)
////            // tell the table view to update with new data source
////            // tableView.reloadData()    Bad way!
////            tableView.deleteRows(at: [indexPath], with: UITableViewRowAnimation.automatic)
//        }
//    }
    
    func tableView(_ tableView: UITableView, moveRowAt sourceIndexPath: IndexPath, to destinationIndexPath: IndexPath) {
        
        print("[move] on MediaPlayerDetailsView+Gesture")
        
        let movedObject = self.playlistTracks[sourceIndexPath.row]
        playlistTracks.remove(at: sourceIndexPath.row)
        playlistTracks.insert(movedObject, at: destinationIndexPath.row)
        debugPrint("\(sourceIndexPath.row) => \(destinationIndexPath.row)")
        NSLog("\(sourceIndexPath.row) => \(destinationIndexPath.row)")
        // To check for correctness enable: self.tableView.reloadData()

        UserDefaults.standard.set(true, forKey: "isEdited")
        
//        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
//        mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()


        //
        //        let itemToMove = musicTracks[sourceIndexPath.row]
        //
        //        // delete item from source
        //        musicTracks.remove(at: sourceIndexPath.row)
        //        // move item to destination
        //        musicTracks.insert(itemToMove, at: destinationIndexPath.row)
        //
        //        print("[after move] : \(String(describing: self.musicTracks.toJSONString(prettyPrint: true)))")
        //
        
    }
}



