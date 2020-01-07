import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {filter, map, mergeMap} from "rxjs/operators";
import {EventStreamService} from "../../services/event-stream.service";

declare var Player: any;

@Component({
  selector: 'js-player',
  templateUrl: './player.component.html',
  styleUrls: ['./player.component.css']
})
export class PlayerComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute, private eventStreamService: EventStreamService) {


  }

  ngOnInit() {
    var player = new Player();
    document.body.appendChild(player.canvas);


    this.activatedRoute.params.pipe(map((it: Params) => it['id']), filter(it => it), mergeMap(it => this.eventStreamService.stream('/streamer/video/' + it)))
      .subscribe((it: Uint8Array) => {
        console.log(it);
        player.decode(it)
      });

  }

}
