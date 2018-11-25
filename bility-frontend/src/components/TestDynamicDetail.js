import React, { Component } from 'react';
import Paper from '@material-ui/core/Paper';

type Props = {
    issue: any,
}

type State = {

}

export default class TestDynamicDetail extends Component<State, Props> {

  constructor(props) {
    super(props);
    this.state = {
    }
  }

  componentDidMount() {
    console.log(this.props)
  }

  getUrlToImage(stateId) {
    let source = "http://localhost:8080/screens/hires-" + stateId + ".png";
    return source;
  }

  getMappingItem(mapping) {
    return (
      <div style={styles.dynamicContainer}>
        {mapping.hasOwnProperty('startState') && mapping.startState && 
          <img style={styles.stateImage} src={this.getUrlToImage(mapping.startState)} alt="The start state of this dynamic issue"/>}
        {(!mapping.hasOwnProperty('startState') || !mapping.startState) && 
          <div style={styles.stateMissing}>No start state specified</div> }
        <span style={styles.arrow}>&#8594;</span>
        {mapping.hasOwnProperty('endState') && mapping.endState && 
          <img style={styles.stateImage} src={this.getUrlToImage(mapping.endState)} alt="The start state of this dynamic issue"/>}
        {(!mapping.hasOwnProperty('endState') || !mapping.endState) && 
          <div style={styles.stateMissing}>No start state specified</div> }
      </div>
    );
  }

  render() {

    let m = [];
    for (let mapp of this.props.issue.mappings) {
      m.push(this.getMappingItem(mapp));
    }

    return (
      <div>
          <div>
            {m}
          </div>
      </div>
    );
  }

}

const styles = {
  dynamicContainer: {
    verticalAlign: 'middle'
  },
  stateImage: {
    width: 200,
    display: 'inline-block'
  },
  stateMissing: {
    width: 200,
    display: 'inline-block'
  },
  arrow: {
    fontSize: 50,
    display: 'inline-block'
  }
};