import {DeviceType} from "./device-type.enum";

export class Device {
  id: string;
  name: string;
  deviceType: DeviceType;
  createdBy: string;
  createdDate: string;
  schemaId: string;
  clientId: string;
  clientSecret: string;
}

export class BaseDevice {
  id: string;
  createdBy: string;
  schemaUrl: string;
  oauth: Oauth = new Oauth();
  ws: Ws = new Ws();

  public static of(sf: Device): BaseDevice {
    let location = window.location;
    let baseDevice = new BaseDevice();
    baseDevice.id = sf.id;
    baseDevice.createdBy = sf.createdBy;
    baseDevice.schemaUrl = location.origin + "/pi/schema/" + sf.schemaId;
    baseDevice.oauth.clintId = sf.clientId;
    baseDevice.oauth.clientSecret = sf.clientSecret;
    baseDevice.oauth.authorize = location.origin + '/auth/oauth/authorize';
    baseDevice.oauth.token = location.origin + '/auth/oauth/token';
    baseDevice.ws.url = (location.protocol === 'http:' ? 'ws:' : 'wss:') + "//" + location.host + "/echo/user/" + baseDevice.id;
    baseDevice.ws.notifyUrl = location.origin + '/pi/notify/' + baseDevice.id;
    return baseDevice
  }
}

export class Oauth {
  clintId: string;
  clientSecret: string;
  authorize: string;
  token: string;
}

export class Ws {
  url: string;
  notifyUrl: string;
}
