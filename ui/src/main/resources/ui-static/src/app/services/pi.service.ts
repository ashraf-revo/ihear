import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from "@angular/common/http";
import {Stream} from "../models/stream";
import {Schema} from '../models/schema';
import {Device} from "../models/device";

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

  public findDevice(id: string): Observable<Device> {
    return this.http.get<Device>("/pi/device/" + id);
  }

  public saveSchema(schema: Schema): Observable<Schema> {
    return this.http.post<Schema>("/pi/schema", schema);
  }

  public saveStream(stream: Stream): Observable<Stream> {
    return this.http.post<Stream>("/pi/stream", stream);
  }

  public saveDevice(device: Device): Observable<Device> {
    return this.http.post<Device>("/pi/device", device);
  }

  findAllSchemas(): Observable<Schema[]> {
    return this.http.get<Schema[]>("/pi/schema");
  }

  findAllStreams(): Observable<Stream[]> {
    return this.http.get<Stream[]>("/pi/stream");
  }

  findAllDevices(): Observable<Device[]> {
    return this.http.get<Device[]>("/pi/device");
  }

  notify(id: string, body): Observable<Object> {
    return this.http.post("/pi/notify/" + id, body);
  }
}
