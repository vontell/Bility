import axios from 'axios';
import './constants';
import { SERVER_INFO, HOST } from './constants';

type TestServerConfig = {

}

const SERVER_POLL_RATE = 5000

export function pollTestServer(callback: (config: TestServerConfig) => any): TestServerConfig {
    axios.get(HOST + SERVER_INFO)
    .then(function(res) {
      callback(res.data);
      setTimeout(() => {
        pollTestServer(callback);
      }, SERVER_POLL_RATE);
    })
    .catch((reason) => {
      callback(null);
      setTimeout(() => {
        pollTestServer(callback);
      }, SERVER_POLL_RATE);
    });
    ;
}