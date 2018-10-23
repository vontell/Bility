import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import Button from '@material-ui/core/Button';
import axios from 'axios';

class App extends Component {


  componentDidMount() {
    axios.get('http://localhost:8080/information')
      .then(function(res) {
        console.log(res)
      });
  }

  render() {
    return (
      <Button variant="contained" color="primary">
        Hello World
      </Button>
    );
  }
}

export default App;
