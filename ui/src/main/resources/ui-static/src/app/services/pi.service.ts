import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Schema} from '../models/schema';
import {HttpClient} from "@angular/common/http";
import {Stream} from "../models/stream";

@Injectable({
  providedIn: 'root'
})
export class PiService {

  constructor(private http: HttpClient) {
  }

  public findSchema(id: string): Observable<Schema> {
    return this.http.get<Schema>("/pi/schema/" + id);
  }

  public findStream(id: string): Observable<Stream> {
    return this.http.get<Stream>("/pi/stream/" + id);
  }

  public saveSchema(schema: Schema): Observable<Schema> {
    return this.http.post<Schema>("/pi/schema", schema);
  }

  public saveStream(stream: Stream): Observable<Stream> {
    return this.http.post<Stream>("/pi/stream", stream);
  }

  findAllSchemas(): Observable<Schema[]> {
    return this.http.get<Schema[]>("/pi/schema");
  }

  findAllStreams(): Observable<Stream[]> {
    return this.http.get<Stream[]>("/pi/stream");
  }
}
