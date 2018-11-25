import React, { Component } from 'react';
import TestResult from './TestResult';

type Props = {
    imagePath: any,
    originalSize: any,
    highlights: any,
    originalData: any,
    onHighlightHover: any,
}

type State = {

}

export default class TestImageDisplay extends Component<State, Props> {

  constructor(props) {
    super(props);
    this.state = {
    }
  }

  componentDidMount() {
    console.log(this.props);
  }

  getScaleFactor() {
      return 500.0/1940.0
  }

  getTopOffset() {
      return 0//48.0 * this.getScaleFactor();
  }

  getPadding() {
      return 3.0
  }

  generateHighlights() {
      let highlights = []
      if (this.props.highlights) {
        for (let h of this.props.highlights) {
            let s = {
                top: h.location.top * this.getScaleFactor() + this.getTopOffset() - this.getPadding(),
                left: h.location.left * this.getScaleFactor() - this.getPadding(),
                width: h.size.width * this.getScaleFactor() + (this.getPadding()),
                height: h.size.height * this.getScaleFactor() + (this.getPadding()),
                position: 'absolute',
                border: '3px solid #ff993f',
                borderRadius: 3,
            }
            highlights.push(
                <div style={s} onMouseEnter={() => this.props.onHighlightHover(this.props.originalData)}></div>
            );
          }
          return highlights
      }
  }

  render() {
    return (
      <div style={styles.imageContainer}>
        <img src={this.props.imagePath} style={styles.screenshot} alt="A screenshot of your application that was tested"></img>
        {this.generateHighlights()}
      </div>
    );
  }

}

const styles = {
    screenshot: {
        height: 500,
    },
    imageContainer: {
        position: 'relative'
    },
    highlight: {
        position: 'absolute',
        background: 'red'
    }
};