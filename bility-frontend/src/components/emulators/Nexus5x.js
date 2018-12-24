import React, { Component } from 'react';
import Nexus5xImg from '../../img/phoneFrame.png';
import { relative } from 'path';

type Props = {
    getContents: (width: Float, height: Float, top: Float, left: Float) => any,
    size: any
}

type State = {

}

export default class Nexus5x extends Component<State, Props> {

  constructor(props) {
    super(props);
    this.state = {
    }
  }

  componentDidMount() {
    console.log(this.props.info);
  }

  render() {
    let styles = styleGen(this.props.size || 200);
    return (
      <div style={styles.frameContainer}>
          <img src={Nexus5xImg} style={styles.frame} alt="Nexus 5 frame"></img>
          <div style={styles.screenBlocker}></div>
          <div style={styles.content}>
            {this.props.getContents(styles.content.width, styles.content.height, styles.content.top, styles.content.left)}
          </div>
      </div>
    );
  }

}

const styleGen = (size) => {
    return {
        frameContainer: {
            position: 'relative'
        },
        frame: {
            width: size,
        },
        screenBlocker: {
            position: 'absolute',
            background: 'white',
            width: size * 0.89,
            height: size * 1.6,
            top: 41,
            left: 10
        },
        content: {
            position: 'absolute',
            background: 'white',
            width: size * 0.89,
            height: size * 1.6,
            top: 41,
            left: 10
        }
    }
};