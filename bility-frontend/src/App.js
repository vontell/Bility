import React, { Component } from 'react';
import TestInfo from './components/TestInfo';
import './App.css';
import Button from '@material-ui/core/Button';
import LinearProgress from '@material-ui/core/LinearProgress';
import axios from 'axios';
import TestResults from './components/TestResults';
import Grid from '@material-ui/core/Grid';
import TestImageDisplay from './components/TestImageDisplay';



class App extends Component {

  intervalId = 0;

  constructor(props) {
    super(props);
    this.state = {
      project: null,
      bilityServer: null,
      listening: false,
      currentAction: null,
      representation: null,
      issueReport: null,
      numUnexplored: null,
      displayed: null,
    }
  }


  componentDidMount() {
    this.getServerInformation((info) => this.setState({bilityServer: info}));
    this.getProject((proj) => this.setState({project: proj}));
  }

  // Methods for getting information from the server
  getProject(callback) {
    axios.get('http://localhost:8080/internal/getProjectInfo')
    .then(function(res) {
      callback(res.data);
    });
  }

  getServerInformation(callback) {
    axios.get('http://localhost:8080/information')
    .then(function(res) {
      callback(res.data);
    });
  }

  startListeningLoop() {
    if (this.state.listening) {
      clearInterval(this.intervalId);
      this.setState({listening: false});
    } else {
      this.reloadReportInfo();
      this.setState({listening: true});
      this.intervalId = setInterval(() => {
        this.reloadReportInfo();
      }, 1000);
    }
    
  }

  reloadReportInfo() {
    axios.get('http://localhost:8080/internal/getFrontendReport')
    .then((res) => {
      console.log(res.data);
      this.setState({
        currentAction: res.data.action,
        numUnexplored: res.data.numUnexplored,
        representation: res.data.automatonGraph,
        issueReport: res.data.issueReport
      });
    });
  }

  displayIssue(issue) {
    console.log(issue);

    let perceptifer = issue.perceptifers[0];
    let source = "http://localhost:8080/screens/hires-" + perceptifer.parentId + ".png";
    let highlight = {
      location: perceptifer.percepts.filter((p) => p.type === "LOCATION")[0].information,
      size: perceptifer.percepts.filter((p) => p.type === "SIZE")[0].information,
    }
    this.setState({
      displayed: {
        imagePath: source,
        highlights: [highlight]
      }
    });

  }

  render() {
    return (
      <div style={styles.root}>
        {this.state.project &&
            <TestInfo info={this.state.project}></TestInfo>
        }
        <Button variant="contained" color="primary" onClick={() => this.startListeningLoop()}>
          {this.state.listening ? 'Stop Bility Test' : 'Start Bility Test'}
        </Button>
        <Grid container spacing={24}>
          <Grid item md={6} sm={12}>
            <div style={styles.issuePane}>
              {this.state.issueReport &&
                <TestResults 
                  displayIssue={(issue) => this.displayIssue(issue)}
                  issueReport={this.state.issueReport}
                />
              }
              {!this.state.issueReport &&
                <p>No results have been received yet</p>
              }
            </div>
            {this.state.listening &&
              <LinearProgress variant="indeterminate"/>}
          </Grid>
          <Grid item md={6} sm={12}>
            {this.state.displayed &&
              <TestImageDisplay
                imagePath={this.state.displayed.imagePath}
                highlights={this.state.displayed.highlights} />
            }
            {!this.state.displayed &&
              <div>
                <p>Hover over an issue to see more.</p>
              </div>
            }
          </Grid>
        </Grid>
      </div>
    );
  }
}

const styles = {
  root: {
    flexGrow: 1,
    padding: 32
  },
  issuePane: {
    maxHeight: 500,
    overflow: 'scroll',
    marginTop: 12,
  }
}

export default App;
