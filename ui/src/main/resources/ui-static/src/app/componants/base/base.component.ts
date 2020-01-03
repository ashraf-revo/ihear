import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {AuthService} from "../../services/auth.service";
import {DefaultService} from "../../services/default.service";

@Component({
  selector: 'js-base',
  templateUrl: './base.component.html',
  styleUrls: ['./base.component.css']
})
export class BaseComponent implements OnInit {

  constructor(public router: Router,
              private userService: UserService,
              private authService: AuthService,
              private defaultService: DefaultService) {
    this.defaultService.handle(this.router, this.authService);
  }

  ngOnInit() {
    this.userService.currentUser().subscribe(it => this.authService.setAuth(it, 'true'), it => this.authService.setAuth(null, 'false'));
  }

}
