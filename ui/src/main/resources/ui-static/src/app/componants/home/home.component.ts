import {Component, OnInit} from '@angular/core';
// import VideoConverter from 'h264-converter';
// import JMuxer from 'jmuxer';
// import Player from 'player';
declare var Player: any;

@Component({
  selector: 'js-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor() {
  }

  // ngOnInit() {
  // let jmuxer:JMuxer = new JMuxer({
  //     node: 'player',
  //     mode: 'video',
  //     flushingTime: 1000,
  //     fps: 30,
  //     debug: true
  //   });
  //   fetch('/streamer/video/5da0d0039c750d106725fce5').then((res) => {
  //     if (res.body) {
  //       const reader = res.body.getReader();
  //       reader.read().then(function processResult(result) {
  //         function decode(value) {
  //           jmuxer.feed({video: new Uint8Array(value)});
  //         }
  //
  //         if (result.done) {
  //           decode([]);
  //           console.log('Video Stream is done.');
  //           return Promise.resolve();
  //         }
  //         decode(result.value);
  //         return reader.read().then(processResult);
  //       });
  //     }
  //   }).catch((err) => {
  //     console.error('Video Stream Request error', err);
  //   });
  // }
  // ngOnInit(){}

  popupCenter(url, title, w, h) {

    // Fixes dual-screen position                         Most browsers      Firefox
    let dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : window.screenX;
    let dualScreenTop = window.screenTop != undefined ? window.screenTop : window.screenY;

    let width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    let height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    let systemZoom = width / window.screen.availWidth;
    let left = (width - w) / 2 / systemZoom + dualScreenLeft;
    let top = (height - h) / 2 / systemZoom + dualScreenTop;
    let newWindow = window.open(url, title, 'scrollbars=yes, width=' + w / systemZoom + ', height=' + h / systemZoom + ', top=' + top + ', left=' + left);

    newWindow.addEventListener("popstate", function(event){
      console.log(event);
    });
    // Puts focus on the newWindow
    if (window.focus) newWindow.focus();



  }

  ngOnInit() {
    //
    // var player = new Player();
    // document.body.appendChild(player.canvas);
    //
    //
    // fetch('/streamer/video/5da0d0039c750d106725fce5').then((res) => {
    //   if (res.body) {
    //     const reader = res.body.getReader();
    //     reader.read().then(function processResult(result) {
    //       function decode(value) {
    //         player.decode(value);
    //       }
    //
    //       if (result.done) {
    //         decode([]);
    //         console.log('Video Stream is done.');
    //         return Promise.resolve();
    //       }
    //       decode(result.value);
    //
    //       return reader.read().then(processResult);
    //     });
    //   }
    // }).catch((err) => {
    //   console.error('Video Stream Request error', err);
    // });
  }

  // ngOnInit() {
  //   const element = <HTMLVideoElement>document.getElementById('player');
  //   const converter = new VideoConverter(element, 20, 6);
  //   fetch('/streamer/video/5da0d0039c750d106725fce5').then((res) => {
  //     if (res.body) {
  //       const reader = res.body.getReader();
  //       reader.read().then(function processResult(result) {
  //         function decode(value) {
  //           converter.appendRawData(value);
  //         }
  //
  //         if (result.done) {
  //           decode([]);
  //           console.log('Video Stream is done.');
  //           return Promise.resolve();
  //         }
  //         decode(result.value);
  //
  //         return reader.read().then(processResult);
  //       });
  //       converter.play();
  //     }
  //   }).catch((err) => {
  //     console.error('Video Stream Request error', err);
  //   });
  // }

}
