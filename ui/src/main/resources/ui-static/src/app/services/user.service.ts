import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {User} from "../models/user";
import {HttpClient} from "@angular/common/http";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient:HttpClient) {
  }

  currentUser(): Observable<User> {
    return this.httpClient.get( '/auth/user').pipe(map(it => it["user"]));
  }
}
