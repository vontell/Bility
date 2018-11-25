import React, { Component } from 'react';
import TestResult from './TestResult';

type Props = {
    issueReport: any,
    displayIssue: any,
    filters: any,
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
    //console.log(this.props);
  }

  _passesFilter(issue) {
    if (this.props.filters.showFailuresOnly && issue.passes) {
      return false;
    }
    if (this.props.filters.searchFilter !== undefined && 
          this.props.filters.searchFilter.trim() !== "" &&
          !issue.identifier.toLowerCase().includes(this.props.filters.searchFilter.toLowerCase())) {
      return false;
    }
    return true;
  }

  generateStaticIssuePanel() {

    let issues = []
    for (let issue of this.props.issueReport.staticIssues) {
      if (this._passesFilter(issue)) {
        issue['type'] = 'static'
        issues.push(<TestResult displayIssue={this.props.displayIssue} issue={issue}/>)
      }
    }
    for (let issue of this.props.issueReport.dynamicIssues) {
      if (this._passesFilter(issue)) {
        issue['type'] = 'dynamic'
        issues.push(<TestResult displayIssue={this.props.displayIssue} issue={issue}/>)
      }
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