import React, { Component } from 'react';

type Props = {
  info: any,
}

type State = {

}

export default class TestInfo extends Component<State, Props> {

  constructor(props) {
    super(props);
    this.state = {
    }
  }

  componentDidMount() {
    console.log(this.props.info);
  }

  render() {
    return (
      <div>
        <p style={styles.lineItem}>
          <span style={styles.label}>Package: </span><span>{this.props.info.packageName}</span>
        </p>
        <p style={styles.lineItem}>
          <span style={styles.label}>Number of attempts: </span><span>{this.props.info.numRuns}</span>
        </p>
        <p style={styles.lineItem}>
          <span style={styles.label}>Total actions: </span><span>{this.props.info.maxActions}</span>
        </p>
      </div>
    );
  }

}

const styles = {
  label: {
    fontWeight: 'bold',
  },
  lineItem: {

  }
};