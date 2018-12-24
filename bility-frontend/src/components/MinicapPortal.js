import React, { Component } from 'react';
import Button from '@material-ui/core/Button';

type Props = {
    websocketURL: string,
    portalStyle: any,
    restart: any,
}

type State = {
  errored: boolean
}

const BLANK_IMG =
  'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw=='

export default class MinicapPortal extends Component<State, Props> {

  constructor(props) {
    super(props);
    this.state = {
      errored: false
    }
  }

  componentDidMount() {
    console.log(this.props);
    this.createWebsocket();
  }

  createWebsocket() {
    const that = this;
    let canvas = document.getElementById('minicapCanvas')
        , g = canvas.getContext('2d')
    let ws = new WebSocket(this.props.websocketURL, 'minicap');
    ws.binaryType = 'blob'
    ws.onclose = function() {
      console.log('Minicap websocket closed');
    }
    ws.onerror = function() {
      console.log('Minicap websocket errored');
      that.setState({errored: true});
    }
    ws.onmessage = function(message) {
      that.setState({errored: false});
      var blob = new Blob([message.data], {type: 'image/jpeg'})
      var URL = window.URL || window.webkitURL
      var img = new Image()
      img.onload = function() {
        console.log(img.width, img.height)
        canvas.width = img.width
        canvas.height = img.height
        g.drawImage(img, 0, 0)
        img.onload = null
        img.src = BLANK_IMG
        img = null
        u = null
        blob = null
      }
      var u = URL.createObjectURL(blob)
      img.src = u
    }
    ws.onopen = function() {
      console.log('Minicap opened')
      ws.send('1920x1080/0')
    }
  }

  render() {
    const that = this;
    return (
      <div>
        {!this.state.errored &&
          <canvas id="minicapCanvas" style={this.props.portalStyle}>
          </canvas>
        }
        {this.state.errored &&
          <div style={styles.errorContainer}>
            <p style={styles.error}>Error connecting to device. Is the emulator on, and is minicap running?</p>
            <br />
            <Button
              onClick={() => that.props.restart()} >
              Try Again
            </Button>
          </div>
        }
      </div>
    );
  }

}

const styles = {
  errorContainer: {
    textAlign: 'center',
    padding: 16
  },
  error: {
    color: 'grey'
  }
};