//
//  Utils.swift
//  Upmusic
//
//  Created by nough on 26/04/2019.
//  Copyright © 2019 Nough / Ndvor. All rights reserved.
//

import Foundation
func LOG(_ msg: Any?, file: String = #file, function: String = #function, line: Int = #line) {
    #if DEBUG
    let fileName = file.split(separator: "/").last ?? ""
    let funcName = function.split(separator: "(").first ?? ""
    print("[\(fileName)] \(funcName)(\(line)): \(msg ?? "")")
    #endif
}
func TOAST(view: UIView, string: String) {
    let showingText = string
    let toastLabel = UILabel(
        frame: CGRect(x: ((view.frame.size.width/2)-150)
            , y: (view.frame.size.height-125)
            , width : 300
            , height : 35
    ))
    
    toastLabel.backgroundColor = UIColor.darkGray
    toastLabel.textColor = UIColor.white
    toastLabel.textAlignment = NSTextAlignment.center
    view.addSubview(toastLabel)
    toastLabel.text = showingText
    toastLabel.font = UIFont.boldSystemFont(ofSize: 18)
    toastLabel.alpha = 1.0
    toastLabel.layer.cornerRadius = 10;
    toastLabel.clipsToBounds = true
    
    UIView.animate(withDuration: 1.5, animations: {
        toastLabel.alpha = 0.0
    }, completion: {
        (isBool) -> Void in
        //self.dismiss(animated: true, completion: nil)
    })
}

public class Utils{
    internal static func getCurrScreenStoryBoard() -> UIStoryboard {
        let storyboard:UIStoryboard = UIStoryboard(name: "Main", bundle: Bundle.main)
        return storyboard;
    }
    
    internal static func getAppVersion () -> String {
        return Bundle.main.infoDictionary?["CFBundleShortVersionString"] as! String
    }
    
    internal static func getBuildVersion() -> String {
        return Bundle.main.infoDictionary?["CFBundleVersion"] as! String
    }
    
    internal static func getDeviceWidth () -> CGFloat {
        return UIScreen.main.bounds.size.width
    }
    
    internal static func getDeviceHeight () -> CGFloat {
        return UIScreen.main.bounds.size.height
    }
    
    internal static func getOSVersion () -> String {
        return UIDevice.current.systemVersion
    }
    
    internal static func isStringEmptyCheck(str:String) -> Bool{
        if(str.isEmpty || str.count == 0 || str == "") {
            return true
        } else {
            return false
        }
    }
    
    /** 가상 메모리 페이지 크기 가져오기 - returns: 가상 메모리 페이지 크기 */
    internal static func getVMPageSize() -> UInt {
        var pageSize: vm_size_t = 0
        let result = withUnsafeMutablePointer(to: &pageSize) {
            (size) -> kern_return_t in host_page_size(mach_host_self(), size)
            
        }
        guard result == KERN_SUCCESS else { return 0 }
        return UInt(pageSize)
        
    }
    
    /** 가상 메모리 데이터 가져오기 - returns: 가상 메모리 데이터 구조체 */
    internal static func getVMStatistics() -> vm_statistics {
        var vmstat = vm_statistics()
        var count = UInt32(MemoryLayout<vm_statistics>.size / MemoryLayout<integer_t>.size)
        let result = withUnsafeMutablePointer(to: &vmstat) {
            $0.withMemoryRebound(to: integer_t.self, capacity: 1) {
                return host_statistics(mach_host_self(), HOST_VM_INFO, host_info_t($0), &count)
                
            }
            
        }
        guard result == KERN_SUCCESS
            else {
                return vm_statistics()
                
        }
        return vmstat
        
    }
    /** 사용 가능한 메모리 가져오기 - returns: 사용가능한 메모리 */
    internal static func getFreeMemory() -> UInt {
        return UInt(getVMStatistics().free_count) * getVMPageSize()
        
    }
    internal static func setPreference(key:String, value:String) -> Void{
        if(!isStringEmptyCheck(str: value) && !isStringEmptyCheck(str: key)){
            UserDefaults.standard.setValue(value, forKey: key)
            UserDefaults.standard.synchronize()
        }
    }
    
    internal static func getPreference(str:String) -> String{
        if(isStringEmptyCheck(str: str)){
            return ""
        }
        
        let value = UserDefaults.standard.value(forKey: str)
        if(value != nil) {
            return value as! String
        } else {
            return ""
        }
    }
}
