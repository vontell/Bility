import React, { Component } from 'react';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import CheckCircle from '@material-ui/icons/CheckCircle';
import Cancel from '@material-ui/icons/Cancel';
import Keyboard from '@material-ui/icons/Keyboard';
import PanTool from '@material-ui/icons/PanTool';
import TouchApp from '@material-ui/icons/TouchApp';
import PhoneAndroid from '@material-ui/icons/PhoneAndroid';
import Typography from '@material-ui/core/Typography';

type Props = {
    issue: any,
    displayIssue: any,
}

type State = {

}

export default class TestResult extends Component<State, Props> {

  constructor(props) {
    super(props);
    this.state = {
    }
  }
  

  componentDidMount() {
    //console.log(this.props);
  }

  getStatusSpan() {
    if (this.props.issue.passes) {
      return <span style={styles.passes}>- PASSED <CheckCircle style={styles.statusIcon}/></span>
    }
    return <span style={styles.fails}>- FAILURE <Cancel style={styles.statusIcon}/></span>
  }

  getTypeSpan() {
    if (this.props.issue.type === "static") {
      return <span><PhoneAndroid style={styles.typeIcon}/></span>
    }
    return <span><TouchApp style={styles.typeIcon}/></span>
  }

  getInstanceLine() {
    if (this.props.issue.type === "static") {
      let times = this.props.issue.perceptifers.length;
      return (
        <div>
          <br />
          <em>This issue was found {times} time{times > 1 ? 's' : null} within this application.</em>
        </div>
      );
    }
    return null;
  }

  render() {
    return (
      <ExpansionPanel onMouseEnter={() => this.props.displayIssue(this.props.issue)}>
        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
          <p style={styles.issueHeading}>{this.getTypeSpan()} {this.props.issue.identifier} {this.getStatusSpan()}</p>
        </ExpansionPanelSummary>
        <ExpansionPanelDetails>
          <div>
            <p><b>Requirement: </b>{this.props.issue.shortDescription}</p>
            <p><b>Explanation: </b>{this.props.issue.instanceExplanation}</p>
            {!this.props.issue.passes &&
            <div></div>
            }
            {this.props.issue.suggestionExplanation &&
            <p><b>Suggestion: </b>{this.props.issue.suggestionExplanation}</p>
            }
            <p>To learn more about this guideline, visit the <a href={this.props.issue.extras.link} target="_blank">WCAG 2.0 principle reference</a></p>
            {this.getInstanceLine()}
          </div>
        </ExpansionPanelDetails>
      </ExpansionPanel>
    );
  }

}

const styles = {
  issueHeading: {

  },
  passes: {
    color: 'green',
    fontWeight: 'bold',
  },
  fails: {
    color: 'red',
    fontWeight: 'bold',
  },
  statusIcon: {
    marginBottom: -6,
    marginLeft: 8
  },
  typeIcon: {
    marginBottom: -6,
    marginLeft: 8
  }
};