import React, { Component } from 'react';
import Paper from '@material-ui/core/Paper';

type Props = {
    perceptifer: any,
}

type State = {

}

export default class TestHighlightDetail extends Component<State, Props> {

  constructor(props) {
    super(props);
    this.state = {
    }
  }

  componentDidMount() {
    
  }

  correctHex(hex) {
    while (hex.length < 6) {
      hex = "0" + hex;
    }
    while (hex.length < 8) {
      hex = "f" + hex;
    }
    return ('#' + hex.substring(2, 8) + hex.substring(0,2)).toUpperCase();
  }

  getUiInfo(perceptifer) {
    return {
      name: this._getPerceptInformation(perceptifer, "VIRTUAL_NAME", true),
      textColor: this._getPerceptInformation(perceptifer, "TEXT_COLOR", false),
      backgroundColor: this._getPerceptInformation(perceptifer, "BACKGROUND_COLOR", false),
      text: this._getPerceptInformation(perceptifer, "TEXT", false),
      fontSize: this._getPerceptInformation(perceptifer, "FONT_SIZE", false),
      fontStyle: this._getPerceptInformation(perceptifer, "FONT_STYLE", false),
      clickable: this._getPerceptInformation(perceptifer, "VIRTUALLY_CLICKABLE", true)
    }
  }

  _getPerceptInformation(perceptifer, type, virtual) {
    let ps = perceptifer.percepts;
    if (virtual) {
      ps = perceptifer.virtualPercepts
    }
    let valid = ps.filter((p) => p.type === type)
    if (valid.length > 0) {
      return valid[0].information
    }
  }

  render() {
    let info = this.getUiInfo(this.props.perceptifer);

    let elements = [];

    elements.push(
      <h3>Static Element Information</h3>
    )

    if (info.name) {
      elements.push(
        <div style={styles.detailItem}>
          <span style={styles.detailLabel}>View Type</span><br />
          <span style={styles.detailContent}>{info.name}</span>
        </div>
      )
    }

    if (info.textColor) {
      elements.push(
        <div style={styles.detailItem}>
          <span style={styles.detailLabel}>Text Color</span><br />
          <span style={styles.detailContent}>{'#' + info.textColor.colorHex}</span>
          <div style={{...styles.colorBox, ...{backgroundColor: this.correctHex(info.textColor.colorHex)}}}></div>
        </div>
      )
    }

    if (info.backgroundColor) {
      elements.push(
        <div style={styles.detailItem}>
          <span style={styles.detailLabel}>Background Color</span><br />
          <span style={styles.detailContent}>{'#' + info.backgroundColor.colorHex}</span>
          <div style={{...styles.colorBox, ...{backgroundColor: this.correctHex(info.backgroundColor.colorHex)}}}></div>
        </div>
      )
    }

    if (info.fontStyle) {
      elements.push(
        <div style={styles.detailItem}>
          <span style={styles.detailLabel}>Font Style</span><br />
          <span style={styles.detailContent}>{info.fontStyle}</span>
        </div>
      )
    }

    if (info.text) {
      elements.push(
        <div style={styles.detailItem}>
          <span style={styles.detailLabel}>Text</span><br />
          <span style={styles.detailContent}>{info.text}</span>
        </div>
      )
    }

    if (info.fontSize) {
      elements.push(
        <div style={styles.detailItem}>
          <span style={styles.detailLabel}>Font Size</span><br />
          <span style={styles.detailContent}>{info.fontSize}</span>
        </div>
      )
    }

    if (info.clickable) {
      elements.push(
        <div style={styles.detailItem}>
          <span style={styles.detailLabel}>Clickable</span><br />
          <span style={styles.detailContent}>{info.clickable}</span>
        </div>
      )
    }

    return (
      <div>
        <Paper style={styles.detail}>
          <div>
            {elements}
          </div>
        </Paper>
      </div>
    );
  }

}

const styles = {
  detail: {
    padding: 16,
  },
  colorSpan: {
    borderRadius: 4,
    padding: 4
  },
  detailContent: {

  },
  detailItem: {
    paddingBottom: 6
  },
  detailLabel: {
    fontWeight: 'bold',
  },
  colorBox: {
    border: '1px solid #000000',
    width: 10,
    height: 10,
    display: 'inline-block',
    marginLeft: 10
  }
};