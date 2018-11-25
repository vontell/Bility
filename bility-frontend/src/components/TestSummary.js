import React, { Component } from 'react';

type Props = {
  issueReport: any
}

type State = {

}

export default class TestSummary extends Component<State, Props> {

  constructor(props) {
    super(props);
    this.state = {
    }
  }
  

  componentDidMount() {
    console.log(this.props);
  }

  render() {

    let dynamicCount = this.props.issueReport.dynamicIssues.length;
    let staticCount = this.props.issueReport.staticIssues.length;

    return (
      <div>
        <h3>Test Summary</h3>
        {dynamicCount > 0 && 
          <span>Found a total of {dynamicCount} dynamic issues.<br /></span>}
        {dynamicCount === 0 &&
          <span>No dynamic issues found!<br /></span>}
        {staticCount > 0 && 
          <span>Found a total of {staticCount} static issues.<br /></span>}
        {staticCount === 0 &&
          <span>No static issues found!<br /></span>}
      </div>
    );
  }

}

const styles = {

};