import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError} from "rxjs/operators";
import {DefaultService} from "./default.service";

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {

  constructor(private defaultService: DefaultService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(catchError(it => this.handleError(it)));
  }

  private handleError(err: HttpErrorResponse): Observable<any> {
    console.log(err);
    if ((err.status === 401 || err.status === 403) && !err.url.endsWith("/user")) {
      console.log("should route");
      this.defaultService.goLogin();
    }
    // handle your auth error or rethrow
    return throwError(err);
  }
}
