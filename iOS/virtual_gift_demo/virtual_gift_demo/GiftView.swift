//
//  GiftView.swift
//  virtual_gift_demo
//
//  Created by Kael Ding on 2023/11/6.
//

import UIKit
import QGVAPlayer

class GiftView: UIView {
    override init(frame: CGRect) {
        super.init(frame: frame)
        
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    func show(_ name: String) {
        show(name, container: nil)
    }
    
    func show(_ name: String, container: UIView?) {
        container?.addSubview(self)
        hwd_enterBackgroundOP = .pauseAndResume
        let path = Bundle.main.path(forResource: name, ofType: nil)
        setMute(true)
        playHWDMP4(path, delegate: self)
    }
}

extension GiftView: HWDMP4PlayDelegate {
    func viewDidStartPlayMP4(_ container: UIView!) {
        print("1. viewDidStartPlayMP4: \(container.description)")
    }
    
    func viewDidStopPlayMP4(_ lastFrameIndex: Int, view container: UIView!) {
        print("2. viewDidStopPlayMP4, lastFrameIndex: \(lastFrameIndex), container: \(container.description)")
    }
    
    func viewDidFinishPlayMP4(_ totalFrameCount: Int, view container: UIView!) {
        print("3. viewDidFinishPlayMP4, totalFrameCount: \(totalFrameCount), container: \(container.description)")
        DispatchQueue.main.async {
            self.removeFromSuperview()
        }
    }
    
    func viewDidFailPlayMP4(_ error: Error!) {
        print("4. viewDidFailPlayMP4, error: \(error.debugDescription)")
        DispatchQueue.main.async {
            self.removeFromSuperview()
        }
    }
}
