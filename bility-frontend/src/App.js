import React, { Component } from 'react';
import TestInfo from './components/TestInfo';
import './App.css';
import Button from '@material-ui/core/Button';
import LinearProgress from '@material-ui/core/LinearProgress';
import axios from 'axios';
import TestResults from './components/TestResults';
import Grid from '@material-ui/core/Grid';
import TestImageDisplay from './components/TestImageDisplay';
import Paper from '@material-ui/core/Paper';
import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import TextField from '@material-ui/core/TextField';
import TestHighlightDetail from './components/TestHighlightDetail';
import TestSummary from './components/TestSummary';
import ReactImageMagnify from 'react-image-magnify';
import TestDynamicDetail from './components/TestDynamicDetail';



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
      issueDetail: null,
      showPassing: false,
      searchField: undefined,
      highlightedPerceptifer: null,
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
      showDot(res.data.automatonGraph);
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

    if (issue.type === "dynamic"){
      this.setState({
        displayed: null,
        highlightedPerceptifer: null,
        dynamicDisplay: issue
      });
    } else {
      let perceptifer = issue.perceptifers[0];
      let source = "http://localhost:8080/screens/hires-" + perceptifer.parentId + ".png";
  
      let locations = perceptifer.percepts.filter((p) => p.type === "LOCATION");
      let sizes = perceptifer.percepts.filter((p) => p.type === "SIZE");
  
      if (locations.length > 0 && sizes.length > 0) {
        let highlight = {
          location: locations[0].information,
          size: sizes[0].information,
        }
        this.setState({
          dynamicDisplay: null,
          highlightedPerceptifer: null,
          displayed: {
            imagePath: source,
            highlights: [highlight],
            originalData: perceptifer
          }
        });
      }
    }

    

  }

  

  _getFilters() {
    return {
      showFailuresOnly: !this.state.showPassing,
      searchFilter: this.state.searchField
    }
  }

  render() {
    return (
      <div style={styles.root}>
        <Grid container spacing={24}>
          <Grid item md={6} sm={12}>
            <img src={'https://raw.githubusercontent.com/vontell/BilityBuildSystem/master/Design/logo.png?token=AHrh03WmzGbw1n-aev3RhfHl1pD-thw3ks5cAgluwA%3D%3D'} 
              alt="Logo for the Bility Project"
              style={styles.bility}/>
            {this.state.project &&
                <TestInfo info={this.state.project}></TestInfo>
            }
            <Button variant="contained" color="primary" onClick={() => this.startListeningLoop()}>
              {this.state.listening ? 'Stop Bility Test' : 'Start Bility Test'}
            </Button><br />

            <FormControlLabel
              control={
                <Checkbox
                  checked={this.state.showPassing}
                  onChange={(e, checked) => {console.log(checked); this.setState({showPassing: checked})}}
                />
              }
              label="Show Passing Results"
            /><br />
          </Grid>
          <Grid item md={6} sm={12}>
            {this.state.issueReport && 
              <TestSummary issueReport={this.state.issueReport}/>}
          </Grid>
        </Grid>

        <Grid container spacing={24}>
          <Grid item md={6} sm={12}>
            <div>
              {this.state.issueReport &&
              <div>
                <TextField
                  id="issue-filter"
                  label="Filtered Search"
                  value={this.state.searchField}
                  onChange={(e) => this.setState({searchField: e.target.value})}
                  margin="normal"
                  variant="filled"
                  style={styles.filterSearch} />
                <div style={styles.issuePane}>
                  <TestResults 
                    filters={this._getFilters()}
                    displayIssue={(issue) => this.displayIssue(issue)}
                    issueReport={this.state.issueReport}
                  />
                </div>
              </div>
                
              }
              {!this.state.issueReport &&
                <p>No results have been received yet</p>
              }
            </div>
            {this.state.listening &&
              <LinearProgress variant="indeterminate"/>}
          </Grid>
          <Grid item md={6} sm={12}>
            <Grid container spacing={24}>
              <Grid item md={12} sm={12} lg={6}>
                {this.state.displayed &&
                  <TestImageDisplay
                    onHighlightHover={(p) => this.setState({highlightedPerceptifer: p})}
                    imagePath={this.state.displayed.imagePath}
                    highlights={this.state.displayed.highlights}
                    originalData={this.state.displayed.originalData} />
                }
                {this.state.dynamicDisplay &&
                  <TestDynamicDetail
                    issue={this.state.dynamicDisplay} />
                }
                {(!this.state.displayed && !this.state.dynamicDisplay) &&
                  <div>
                    <p>Hover over an issue to see more.</p>
                  </div>
                }
              </Grid>
              <Grid item md={12} sm={12} lg={6}>
                {this.state.highlightedPerceptifer &&
                  <TestHighlightDetail
                    perceptifer={this.state.highlightedPerceptifer} />
                }
              </Grid>
            </Grid>
          </Grid>
        </Grid>

        <div id="representation" style={styles.automatonGraph}></div>

        { /*<ReactImageMagnify {...{
            smallImage: {
                alt: 'Automaton generated during this run',
                isFluidWidth: true,
                enlargedImagePosition: 'over',
                src: 'http://localhost:8080/screens/auto.png'
            },
            largeImage: {
                src: 'http://localhost:8080/screens/auto.png',
                enlargedImagePosition: 'over',
                width: 1200,
                height: 1800
            }
        }} /> */}
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
  },
  bility: {
    height: 100
  },
  filterSearch: {
    width: '100%',
    marginTop: 0
  },
  automatonGraph: {
    width: '100%',
    height: 800
  }
}

function showDot(DOTstring) {
  // provide data in the DOT language
  var parsedData = window.vis.network.convertDot(DOTstring);

  var data = {
    nodes: parsedData.nodes,
    edges: parsedData.edges
  }

  var options = parsedData.options;

  // you can extend the options like a normal JSON variable:
  options['physics'] = {
    enabled: true,
    repulsion: {
        centralGravity: 0.0,
        springLength: 335,
        springConstant: 0.01,
        nodeDistance: 500,
        damping: 0.41
    },
    solver: 'repulsion'
  }

  // create a network
  var network = new window.vis.Network(document.getElementById('representation'), data, options);
}

export default App;
