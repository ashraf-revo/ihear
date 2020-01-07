import {Injectable} from '@angular/core';
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class EventStreamService {

  constructor() {
  }

  public stream(url: string): Observable<Uint8Array> {
    return Observable.create(function (observer) {
      fetch(url).then((res) => {
        if (res.body) {
          const reader = res.body.getReader();
          reader.read().then(function processResult(result) {
            if (result.done) {
              observer.complete();
              return Promise.resolve();
            }
            observer.next(result.value);
            return reader.read().then(processResult);
          });
        }
      }).catch((err) => {
        observer.error(err)
      });
    });
  }
}
