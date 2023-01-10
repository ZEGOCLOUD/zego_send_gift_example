//
//  ViewController.swift
//  virtual_gift_demo
//
//  Created by zego on 2023/1/9.
//

import UIKit
import ZegoUIKitPrebuiltLiveAudioRoom
import ZegoUIKitSDK
import SVGAPlayer

class ViewController: UIViewController {
    
    let appID: UInt32 = <#YourAppID#>
    let appSign: String = <#YourAppSign#>
    
    var userName: String?
    
    var liveAudioRoomVC: ZegoUIKitPrebuiltLiveAudioRoomVC?
    
    lazy var svgPlayer: SVGAPlayer = {
        let player: SVGAPlayer = SVGAPlayer()
        player.clearsAfterStop = true
        player.loops = 1
        return player
    }()
    
    let parser: SVGAParser = SVGAParser()
    
    @IBOutlet weak var roomIDTextField: UITextField! {
        didSet {
            let roomID: UInt32 = arc4random() % 999999
            roomIDTextField.text = String(format: "%d", roomID)
        }
    }
    
    
    @IBOutlet weak var UserIDTextField: UITextField! {
        didSet {
            let userID: String = String(format: "%d", arc4random() % 999999)
            UserIDTextField.text = userID
        }
    }
    

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    
    @IBAction func startLiveAudio(_ sender: Any) {
        let config: ZegoUIKitPrebuiltLiveAudioRoomConfig = ZegoUIKitPrebuiltLiveAudioRoomConfig.host()
        let layoutConfig: ZegoLiveAudioRoomLayoutConfig = ZegoLiveAudioRoomLayoutConfig()
        layoutConfig.rowSpecing = 5
        let rowConfig1: ZegoLiveAudioRoomLayoutRowConfig = ZegoLiveAudioRoomLayoutRowConfig()
        rowConfig1.alignment = .spaceAround
        rowConfig1.count = 4
        layoutConfig.rowConfigs = [rowConfig1, rowConfig1]
        layoutConfig.rowSpecing = 0
        config.layoutConfig = layoutConfig
        let liveAudioVC: ZegoUIKitPrebuiltLiveAudioRoomVC = ZegoUIKitPrebuiltLiveAudioRoomVC(appID, appSign: appSign, userID: self.UserIDTextField.text ?? "", userName: userName ?? "", roomID: self.roomIDTextField.text ?? "", config: config)
        liveAudioVC.modalPresentationStyle = .fullScreen
        liveAudioVC.delegate = self
        let backgroundView: LiveAudioHeaderView = LiveAudioHeaderView(frame: CGRect(x: 0, y: 0, width: liveAudioVC.view.frame.width, height: liveAudioVC.view.frame.height))
        backgroundView.setHeadContent("Live Audio Room", roomID: self.roomIDTextField.text ?? "", image: UIImage())
        liveAudioVC.setBackgroundView(backgroundView)
        self.present(liveAudioVC, animated: true)
        self.liveAudioRoomVC = liveAudioVC
        self.liveAudioRoomVC?.view.addSubview(self.svgPlayer)
    }
    
    @IBAction func joinLiveAudio(_ sender: Any) {
        let config: ZegoUIKitPrebuiltLiveAudioRoomConfig = ZegoUIKitPrebuiltLiveAudioRoomConfig.audience()
        let layoutConfig: ZegoLiveAudioRoomLayoutConfig = ZegoLiveAudioRoomLayoutConfig()
        layoutConfig.rowSpecing = 5
        let rowConfig1: ZegoLiveAudioRoomLayoutRowConfig = ZegoLiveAudioRoomLayoutRowConfig()
        rowConfig1.alignment = .spaceAround
        rowConfig1.count = 4
        layoutConfig.rowConfigs = [rowConfig1, rowConfig1]
        layoutConfig.rowSpecing = 0
        config.layoutConfig = layoutConfig
        let liveAudioVC: ZegoUIKitPrebuiltLiveAudioRoomVC = ZegoUIKitPrebuiltLiveAudioRoomVC(appID, appSign: appSign, userID: self.UserIDTextField.text ?? "", userName: userName ?? "", roomID: self.roomIDTextField.text ?? "", config: config)
        liveAudioVC.modalPresentationStyle = .fullScreen
        liveAudioVC.delegate = self
        let backgroundView: LiveAudioHeaderView = LiveAudioHeaderView(frame: CGRect(x: 0, y: 0, width: liveAudioVC.view.frame.width, height: liveAudioVC.view.frame.height))
        backgroundView.setHeadContent("Live Audio Room", roomID: String(format: "ID: %@", self.roomIDTextField.text ?? ""), image: UIImage())
        let giftButton: UIButton = UIButton()
        giftButton.setTitle("sendGift", for: .normal)
        giftButton.addTarget(self, action: #selector(sendGift), for: .touchUpInside)
        liveAudioVC.addButtonToMenuBar(giftButton, role: .audience)
        liveAudioVC.addButtonToMenuBar(giftButton, role: .speaker)
        liveAudioVC.setBackgroundView(backgroundView)
        self.present(liveAudioVC, animated: true)
        ZegoUIKit.shared.addEventHandler(self)
        self.liveAudioRoomVC = liveAudioVC
        self.liveAudioRoomVC?.view.addSubview(self.svgPlayer)
    }
    
    @objc func sendGift() {
        ZegoUIKit.shared.sendInRoomCommand("GIFT", toUserIDs: ["101"]) { errorCode in
            if errorCode == 0 {
                self.svgPlayer.frame = CGRect.init(x: 0, y: UIScreen.main.bounds.size.height - 400, width: UIScreen.main.bounds.size.width, height: 300)
                    self.parser.parse(withNamed: "sports-car", in: nil) { videoItem in
                    self.svgPlayer.videoItem = videoItem
                    self.svgPlayer.startAnimation()
                }
            }
        }
    }

}

extension ViewController: ZegoUIKitPrebuiltLiveAudioRoomVCDelegate,ZegoUIKitEventHandle {
    
    func onLeaveLiveAudio() {
        
    }
    
    func onInRoomTextMessageReceived(_ messages: [ZegoSignalingInRoomTextMessage], roomID: String) {
        if let message = messages.first,
           let senderUserID = message.senderUserID
        {
            if senderUserID != self.UserIDTextField.text {
                self.svgPlayer.frame = CGRect.init(x: 0, y: UIScreen.main.bounds.size.height - 400, width: UIScreen.main.bounds.size.width, height: 300)
                self.parser.parse(withNamed: "sports-car", in: nil) { videoItem in
                    self.svgPlayer.videoItem = videoItem
                    self.svgPlayer.startAnimation()
                }
            }
        }
    }
    
    func onInRoomCommandReceived(_ fromUser: ZegoUIKitUser, command: String) {
        if fromUser.userID != self.UserIDTextField.text {
            self.svgPlayer.frame = CGRect.init(x: 0, y: UIScreen.main.bounds.size.height - 400, width: UIScreen.main.bounds.size.width, height: 300)
            self.parser.parse(withNamed: "sports-car", in: nil) { videoItem in
                    self.svgPlayer.videoItem = videoItem
                    self.svgPlayer.startAnimation()
            }
        }
    }
    
    
}

