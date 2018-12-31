import React, { Component } from 'react';
import TestInfo from './components/TestInfo';
import './App.css';
import Button from '@material-ui/core/Button';
import LinearProgress from '@material-ui/core/LinearProgress';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import TextField from '@material-ui/core/TextField';
import Info from '@material-ui/icons/Info';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import axios from 'axios';
import TestResults from './components/TestResults';
import Grid from '@material-ui/core/Grid';
import TestImageDisplay from './components/TestImageDisplay';
import MenuItem from '@material-ui/core/MenuItem';
import Menu from '@material-ui/core/Menu';
import Paper from '@material-ui/core/Paper';
import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import TestHighlightDetail from './components/TestHighlightDetail';
import TestSummary from './components/TestSummary';
import ReactImageMagnify from 'react-image-magnify';
import TestDynamicDetail from './components/TestDynamicDetail';
import MinicapPortal from './components/MinicapPortal';
import AboutDialog from './components/static/AboutDialog';
import Dropzone from 'react-dropzone'
import Nexus5x from './components/emulators/Nexus5x';
import Log from './components/Log';



import {pollTestServer} from './utils/utils';

import AndroidImage from './img/android.png';

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
      aboutOpen: false,
      selectedProject: {
        folder: undefined
      },
      proctorConfig: {
        packageName: 'com.danielkim.soundrecorder',
        numActions: 300,
      },
      selectedTab: 0,
      hasGraph: false,
      portalKey: 0,
    }
  }

  componentDidMount() {
    this.getProject((proj) => this.setState({project: proj}));
    pollTestServer((server) => this.setState({bilityServer: server}));
  }

  // Methods for getting information from the server
  getProject(callback) {
    axios.get('http://localhost:8080/internal/getProjectInfo')
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

  downloadProject() {

    let result = {
      project: this.state.proctorConfig,
      issues: this.state.issueReport
    }

    var a = window.document.createElement('a');
    a.href = window.URL.createObjectURL(new Blob([JSON.stringify(result, null, 2)], {type: 'text/plain'}));
    a.download = 'bility.json';
    
    // Append anchor to body.
    document.body.appendChild(a);
    a.click();
    
    // Remove anchor from body
    document.body.removeChild(a);

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

  showAbout = event => {
    this.setState({ aboutOpen: true });
  };

  _getAppBar() {
    return(
      <AppBar position="static">
          <Toolbar>
            <IconButton style={styles.menuButton} color="inherit" aria-label="Menu">
              <MenuIcon />
            </IconButton>
            <Typography variant="h6" color="inherit" style={styles.grow}>
              Bility
            </Typography>
              <div>
                {this.state.bilityServer && 
                  <em>Connected v{this.state.bilityServer.version}</em>
                }
                {!this.state.bilityServer &&
                  <em>Connecting to server...</em>
                }
                <IconButton
                  aria-haspopup="false"
                  onClick={this.showAbout}
                  color="inherit">
                  <Info />
                </IconButton>
              </div>
          </Toolbar>
        </AppBar>
    );
  }

  onFolderDropped(files) {
    console.log(files);
    let path = files[0].path
    this.setState({selectedProject: {
      folder: path
    }});
  }

  _getFileDropper() {
    const that = this;
    return (
      <Nexus5x 
        getContents={(width: Float, height: Float, top: Float, left: Float) => {
          return (
            <div style={styles.dropzone}>
              <Dropzone
                onDrop={this.onFolderDropped.bind(that)} >
                {({getRootProps, getInputProps, isDragActive, isDragAccept, isDragReject}) => (
                  <div {...getRootProps()} style={styles.dropzone}>
                    <input {...getInputProps()} />
                      {!this.state.selectedProject.folder && 
                        <div style={styles.chooseContainer}>
                          <img src={AndroidImage} style={styles.platformImage}/>
                          <p>Drop Android project here</p>
                        </div>
                      }
                      {this.state.selectedProject.folder && 
                        <div>
                          <p>{this.state.selectedProject.folder}</p>
                        </div>
                      }
                      
                  </div>
                )}
              </Dropzone>
            </div>
            
          )
        }}
      />
    );
  }

  _getRealDeviceDisplay() {
    const that = this;
    return (
      <div style={{paddingLeft: '35%'}}>
        <div>
        <Nexus5x
          size={275}
          getContents={(width: Float, height: Float, top: Float, left: Float) => {
            return (
              <div>
                <MinicapPortal
                  restart={() => this.setState({portalKey: this.state.portalKey + 1})}
                  key={this.state.portalKey}
                  portalStyle={{
                    width: width,
                    height: height,
                  }}
                  websocketURL="ws://localhost:9002" />
              </div>
            )
          }}
        />
        </div>
      </div>
    )
  }

  _getIssueDetailPane() {
    return (
      <div>
        {this.state.displayed &&
          <div>
            <Grid container spacing={24}>
              <Grid item md={6} >
                <TestImageDisplay
                  onHighlightHover={(p) => this.setState({highlightedPerceptifer: p})}
                  imagePath={this.state.displayed.imagePath}
                  highlights={this.state.displayed.highlights}
                  originalData={this.state.displayed.originalData} />
              </Grid>
              <Grid item md={6}>
                {this.state.highlightedPerceptifer &&
                  <TestHighlightDetail
                    perceptifer={this.state.highlightedPerceptifer} />
                }
              </Grid>
            </Grid>
          </div>
        }
        {this.state.dynamicDisplay &&
          <TestDynamicDetail
            issue={this.state.dynamicDisplay} />
        }
        {(!this.state.dynamicDisplay && !this.state.displayed) &&
          <div style={styles.noIssueContainer}>
            <p style={styles.noIssue}>Hover over an issue to view more!</p>
          </div>
        }
      </div>
    );
  }

  _getTestProctor() {
    return (
      <div style={styles.proctorSection}>
        <TextField
          id="package-name"
          label="Package Name"
          value={this.state.proctorConfig.packageName}
          margin="normal"
          variant="outlined"
          style={styles.proctorEntry}
        />
        <TextField
          id="num-actions"
          label="Max Number of Actions"
          value={this.state.proctorConfig.numActions}
          margin="normal"
          variant="outlined"
          type="number"
          style={styles.proctorEntry}
        />
        <Button variant="contained" color="primary" onClick={() => this.startListeningLoop()}>
          {this.state.listening ? 'Stop Bility Test' : 'Start Bility Test'}
        </Button><br />
      </div>
    );
  }

  _getResultsPane() {
    return (
      <div>
        <Paper>
            <Tabs 
              indicatorColor="primary"
              textColor="primary"
              centered
              value={this.state.selectedTab} 
              onChange={(event, value) => {this.setState({selectedTab: value})}}>
              <Tab label="Live Device" />
              <Tab label="Result Detail" />
              <Tab label="App Diagram" />
              <Tab label="Log" />
            </Tabs>
        </Paper>
        <div style={styles.resultsPane}>
          <div style={{display: this.state.selectedTab === 0 ? null : 'none'}}>
            {this._getRealDeviceDisplay()}
          </div>
          <div style={{display: this.state.selectedTab === 1 ? null : 'none'}}>
            {this._getIssueDetailPane()}
          </div>
          <div style={{display: this.state.selectedTab === 2 ? null : 'none'}}>
            <div id="representation" style={styles.automatonGraph}>
              <div style={styles.noIssueContainer}>
                <p style={styles.noIssue}>No graph available yet.</p>
              </div>
            </div>
          </div>
          <div style={{display: this.state.selectedTab === 3 ? null : 'none'}}>
            <div style={styles.noIssueContainer}>
              <p style={styles.noIssue}>No logs available.</p>
            </div>
          </div>
        </div>
      </div>
        
    );
  }

  render() {
    return (
      <div style={styles.root}>
        {this._getAppBar()}
        <AboutDialog
          open={this.state.aboutOpen}
          handleClose={() => this.setState({aboutOpen: false})}
        />
        <div style={styles.appContainer}>
          <Grid container spacing={24}>
            <Grid item md={6} sm={12}>
              <Grid container spacing={24}>
                <Grid item md={6} sm={12}>
                  {this._getTestProctor()}
                </Grid>
                <Grid item md={6} sm={12}>
                  {this.state.issueReport && 
                    <div>
                      <TestSummary issueReport={this.state.issueReport}/><br />
                      <Button variant="contained" color="primary" onClick={() => this.downloadProject()}>Download Results</Button>
                    </div>
                  }
                </Grid>
              </Grid>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={this.state.showPassing}
                    onChange={(e, checked) => {console.log(checked); this.setState({showPassing: checked})}}
                  />
                }
                label="Show Passing Results"
              /><br />
              <Grid item md={12} sm={12}>
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
                    <div style={styles.noResultsContainer}>
                      <p style={styles.noResults}>No results have been received yet<br /><br />Click 'Start Bility Test' above to begin.</p>
                    </div>
                    
                  }
                </div>
                {this.state.listening &&
                  <LinearProgress variant="indeterminate"/>}
              </Grid>
            </Grid>
            <Grid item md={6} sm={12}>
              {this._getResultsPane()}
            </Grid>
          </Grid>

          <Grid container spacing={24}>
            
          </Grid>

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
      </div>
    );
  }
}

const GREY = 'grey'
const styles = {
  root: {
    flexGrow: 1,
  },
  appContainer: {
    padding: 32
  },
  issuePane: {
    maxHeight: 450,
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
    height: 500
  },
  menuButton: {
    marginLeft: -12,
    marginRight: 20,
  },
  grow: {
    flexGrow: 1,
  },
  dropzone: {
    height: '100%',
  },
  chooseContainer: {
    textAlign: 'center',
    paddingTop: 64
  },
  platformImage: {
    width: 80
  },
  proctorSection: {

  }, 
  proctorEntry: {
    width: '100%'
  },
  resultsPane: {
    marginTop: 16
  },
  noIssueContainer: {
    textAlign: 'center'
  },
  noIssue: {
    color: GREY,
    fontSize: 18,
    marginTop: 64
  },
  noResultsContainer: {
    textAlign: 'center',
  },
  noResults: {
    fontSize: 20,
    marginTop: 64,
    color: GREY,
    fontWeight: 'bold',
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
