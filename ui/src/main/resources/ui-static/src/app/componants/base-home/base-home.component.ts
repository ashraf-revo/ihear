import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {filter} from "rxjs/operators";

@Component({
  selector: 'js-base-home',
  templateUrl: './base-home.component.html',
  styleUrls: ['./base-home.component.css']
})
export class BaseHomeComponent implements OnInit {
  isAuth: boolean = false;

  constructor(private authService: AuthService) {
  }

  ngOnInit() {
    this.authService.onChange().subscribe(it => {
      this.isAuth = (it.isAuth == "true");
    });
  }

}
