import React, { Component } from 'react';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import CheckCircle from '@material-ui/icons/CheckCircle';
import Cancel from '@material-ui/icons/Cancel';
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
    console.log(this.props);
  }

  getStatusSpan() {
    if (this.props.issue.passes) {
      return <span style={styles.passes}>- PASSED <CheckCircle style={styles.statusIcon}/></span>
    } else {
      return <span style={styles.fails}>- FAILURE <Cancel style={styles.statusIcon}/></span>
    }
  }

  render() {
    return (
      <ExpansionPanel onMouseEnter={() => this.props.displayIssue(this.props.issue)}>
        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
          <p style={styles.issueHeading}>{this.props.issue.identifier} {this.getStatusSpan()}</p>
        </ExpansionPanelSummary>
        <ExpansionPanelDetails>
          <div>
            <p><b>Requirement: </b>{this.props.issue.shortDescription}</p>
            <p><b>Explanation: </b>{this.props.issue.instanceExplanation}</p>
            {!this.props.issue.passes &&
            <div></div>
            }
            <p>To learn more about this guideline, visit the <a href={this.props.issue.extras.link} target="_blank">WCAG 2.0 principle reference</a></p>
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
  }
};