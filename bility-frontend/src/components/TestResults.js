import React, { Component } from 'react';
import TestResult from './TestResult';

type Props = {
    issueReport: any,
    displayIssue: any,
}

type State = {

}

export default class TestResults extends Component<State, Props> {

  constructor(props) {
    super(props);
    this.state = {
    }
  }

  componentDidMount() {
    console.log(this.props);
  }

  generateStaticIssuePanel() {

    let issues = []
    for (let issue of this.props.issueReport.staticIssues) {
      issues.push(<TestResult displayIssue={this.props.displayIssue} issue={issue}/>)
    }
    return (
      <div style={styles.issuePanel}>{issues}</div>
    );
  }

  render() {
    return (
      <div>
        {this.generateStaticIssuePanel()}
      </div>
    );
  }

}

const styles = {
  issuePanel: {

  }
};