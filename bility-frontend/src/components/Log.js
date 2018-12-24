import React, { Component } from 'react';

type LogEntry = {
  message: string,
  type: string
}

type Props = {
    entries: LogEntry[],
    pollingEndpoint: string,
    pollingRate: Number
}

type State = {

}

export default class Log extends Component<State, Props> {

  constructor(props) {
    super(props);
    this.state = {
    }
  }

  componentDidMount() {
    console.log(this.props);
  }

  generateLogs() {
    return this.props.entries.map((val, index) => {
      return (
      <div style={[styles.entry, styles[val.type]]}>
        {val.message}
      </div>);
    });
  }

  scrollToBottom() {

  }

  render() {
    return (
      <div style={styles.logContainer}>
        <div style={styles.log}>
          {this.generateLogs()}
        </div>
      </div>
    );
  }

}

const styles = {
  entry: {

  }
};