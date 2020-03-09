import {Injectable} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class DefaultService {

  public url = '';
  private _lastRoute: NavigationEnd = null;
  private protectedUrl: string[] = ['schema', 'home', 'device'];
  private homeUrl: string = 'home';

  constructor() {
  }

  set lastRoute(value: NavigationEnd) {
    this._lastRoute = value;
  }

  isAccessible(router: Router, authService: AuthService): boolean {
    if (authService.getAuthUser().isAuth == null) {
      return true;
    } else if (authService.getAuthUser().isAuth === 'false' && this.protectedUrl.indexOf(this._lastRoute.url.split('/')[1]) !== -1) {
      this.goLogin();
      return false;
    } else if (authService.getAuthUser().isAuth === 'true' && (this._lastRoute.url === '/' || this._lastRoute.url === '')) {
      router.navigate(['/' + this.homeUrl]);
      return false;
    }
    return true;
  }

  public goLogin() {
    window.location.href = "/login?lastRoute=" + this._lastRoute.url;
  }

  get lastRoute(): NavigationEnd {
    return this._lastRoute;
  }


  handle(router: Router, authService: AuthService) {
    router.events.subscribe(it => {
      if (it instanceof NavigationEnd) {
        this.lastRoute = it;
        this.isAccessible(router, authService);
      }
    });
    authService.onChange().subscribe(it => {
      this.isAccessible(router, authService);
    });
  }
}
